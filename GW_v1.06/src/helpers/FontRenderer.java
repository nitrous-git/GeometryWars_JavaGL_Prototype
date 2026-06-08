package helpers;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBTTAlignedQuad;
import org.lwjgl.stb.STBTTPackContext;
import org.lwjgl.stb.STBTTPackedchar;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBTruetype.*;

public class FontRenderer {
	private static final int BITMAP_WIDTH = 512;
	private static final int BITMAP_HEIGHT = 512;

	private static final int FIRST_CHAR = 32;
	private static final int CHAR_COUNT = 96;

	private final String fontPath;
	private final float fontSize;

	private int textureId;
	private STBTTPackedchar.Buffer charData;

	public FontRenderer(String fontPath, float fontSize) {
		this.fontPath = fontPath;
		this.fontSize = fontSize;
	}

	public void setUpFont() {
		ByteBuffer fontBuffer = loadResourceAsByteBuffer(fontPath);

		ByteBuffer bitmap = BufferUtils.createByteBuffer(BITMAP_WIDTH * BITMAP_HEIGHT);
		charData = STBTTPackedchar.malloc(CHAR_COUNT);

		STBTTPackContext context = STBTTPackContext.malloc();

		if (!stbtt_PackBegin(context, bitmap, BITMAP_WIDTH, BITMAP_HEIGHT, 0, 1)) {
			context.free();
			throw new IllegalStateException("Failed to initialize STB font packing.");
		}

		stbtt_PackSetOversampling(context, 2, 2);

		boolean packed = stbtt_PackFontRange(
				context,
				fontBuffer,
				0,
				fontSize,
				FIRST_CHAR,
				charData
		);

		stbtt_PackEnd(context);
		context.free();

		if (!packed) {
			throw new IllegalStateException("Failed to pack STB font range: " + fontPath);
		}

		textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);

		glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

		glTexImage2D(
				GL_TEXTURE_2D,
				0,
				GL_ALPHA,
				BITMAP_WIDTH,
				BITMAP_HEIGHT,
				0,
				GL_ALPHA,
				GL_UNSIGNED_BYTE,
				bitmap
		);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

		glBindTexture(GL_TEXTURE_2D, 0);
	}

	public void drawString(int x, int y, String text, float r, float g, float b, float a) {
		if (text == null || text.isEmpty()) {
			return;
		}

		if (textureId == 0 || charData == null) {
			throw new IllegalStateException("FontRenderer.setUpFont() must be called before drawString().");
		}

		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

		glBindTexture(GL_TEXTURE_2D, textureId);

		glColor4f(r, g, b, a);

		FloatBuffer xBuffer = BufferUtils.createFloatBuffer(1);
		FloatBuffer yBuffer = BufferUtils.createFloatBuffer(1);

		xBuffer.put(0, x);
		yBuffer.put(0, y);

		STBTTAlignedQuad quad = STBTTAlignedQuad.malloc();

		glBegin(GL_QUADS);

		for (int i = 0; i < text.length(); i++) {
			char character = text.charAt(i);

			if (character == '\n') {
				xBuffer.put(0, x);
				yBuffer.put(0, yBuffer.get(0) + fontSize);
				continue;
			}

			if (character < FIRST_CHAR || character >= FIRST_CHAR + CHAR_COUNT) {
				continue;
			}

			stbtt_GetPackedQuad(
					charData,
					BITMAP_WIDTH,
					BITMAP_HEIGHT,
					character - FIRST_CHAR,
					xBuffer,
					yBuffer,
					quad,
					true
			);

			glTexCoord2f(quad.s0(), quad.t0());
			glVertex2f(quad.x0(), quad.y0());

			glTexCoord2f(quad.s1(), quad.t0());
			glVertex2f(quad.x1(), quad.y0());

			glTexCoord2f(quad.s1(), quad.t1());
			glVertex2f(quad.x1(), quad.y1());

			glTexCoord2f(quad.s0(), quad.t1());
			glVertex2f(quad.x0(), quad.y1());
		}

		glEnd();

		quad.free();

		glBindTexture(GL_TEXTURE_2D, 0);

		glColor4f(1f, 1f, 1f, 1f);
	}

	public void destroy() {
		if (textureId != 0) {
			glDeleteTextures(textureId);
			textureId = 0;
		}

		if (charData != null) {
			charData.free();
			charData = null;
		}
	}

	private ByteBuffer loadResourceAsByteBuffer(String path) {
		String normalizedPath = normalizePath(path);

		// 1. Try classpath resource first.
		try (InputStream inputStream = FontRenderer.class
				.getClassLoader()
				.getResourceAsStream(normalizedPath)) {

			if (inputStream != null) {
				return inputStreamToByteBuffer(inputStream);
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to read classpath font resource: " + normalizedPath, e);
		}

		// 2. Fallback for IntelliJ/dev mode.
		Path[] candidates = new Path[]{
				Path.of(normalizedPath),
				Path.of("src").resolve(normalizedPath),
				Path.of("GW_v1.06").resolve(normalizedPath),
				Path.of("GW_v1.06").resolve("src").resolve(normalizedPath)
		};

		for (Path candidate : candidates) {
			if (Files.exists(candidate)) {
				try {
					byte[] bytes = Files.readAllBytes(candidate);

					ByteBuffer buffer = BufferUtils.createByteBuffer(bytes.length);
					buffer.put(bytes);
					buffer.flip();

					return buffer;
				} catch (IOException e) {
					throw new RuntimeException("Failed to read font file: " + candidate.toAbsolutePath(), e);
				}
			}
		}

		StringBuilder message = new StringBuilder();

		message.append("Font resource not found: ").append(path).append("\n");
		message.append("Normalized path: ").append(normalizedPath).append("\n");
		message.append("Working directory: ").append(Path.of("").toAbsolutePath()).append("\n");
		message.append("Tried classpath resource:\n");
		message.append("- ").append(normalizedPath).append("\n");
		message.append("Tried file paths:\n");

		for (Path candidate : candidates) {
			message.append("- ").append(candidate.toAbsolutePath()).append("\n");
		}

		throw new RuntimeException(message.toString());
	}

	private String normalizePath(String path) {
		String normalizedPath = path.replace("\\", "/");

		if (normalizedPath.startsWith("/")) {
			normalizedPath = normalizedPath.substring(1);
		}

		return normalizedPath;
	}

	private ByteBuffer inputStreamToByteBuffer(InputStream inputStream) throws IOException {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

		byte[] buffer = new byte[8192];
		int bytesRead;

		while ((bytesRead = inputStream.read(buffer)) != -1) {
			outputStream.write(buffer, 0, bytesRead);
		}

		byte[] bytes = outputStream.toByteArray();

		ByteBuffer byteBuffer = BufferUtils.createByteBuffer(bytes.length);
		byteBuffer.put(bytes);
		byteBuffer.flip();

		return byteBuffer;
	}
}