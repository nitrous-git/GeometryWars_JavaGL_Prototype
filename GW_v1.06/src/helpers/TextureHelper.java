package helpers;

import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.stb.STBImage.*;

public class TextureHelper {

	public TextureHelper() {
	}

	public Texture loadTexture(String path, String fileType) {
		return loadTexture(path);
	}

	public Texture loadTexture(String path) {
		ByteBuffer textureData = loadResourceAsByteBuffer(path);

		try (MemoryStack stack = MemoryStack.stackPush()) {
			IntBuffer widthBuffer = stack.mallocInt(1);
			IntBuffer heightBuffer = stack.mallocInt(1);
			IntBuffer channelsBuffer = stack.mallocInt(1);

			stbi_set_flip_vertically_on_load(false);

			ByteBuffer imageBuffer = stbi_load_from_memory(
					textureData,
					widthBuffer,
					heightBuffer,
					channelsBuffer,
					4
			);

			if (imageBuffer == null) {
				throw new RuntimeException(
						"Failed to decode texture: " + path + "\nReason: " + stbi_failure_reason()
				);
			}

			int width = widthBuffer.get(0);
			int height = heightBuffer.get(0);

			int textureId = glGenTextures();
			glBindTexture(GL_TEXTURE_2D, textureId);

			glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

			glTexImage2D(
					GL_TEXTURE_2D,
					0,
					GL_RGBA,
					width,
					height,
					0,
					GL_RGBA,
					GL_UNSIGNED_BYTE,
					imageBuffer
			);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
			glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

			glBindTexture(GL_TEXTURE_2D, 0);

			stbi_image_free(imageBuffer);

			return new Texture(textureId, width, height);
		}
	}

	private ByteBuffer loadResourceAsByteBuffer(String path) {
		String normalizedPath = normalizePath(path);

		// 1. First try classpath resource.
		try (InputStream inputStream = TextureHelper.class
				.getClassLoader()
				.getResourceAsStream(normalizedPath)) {

			if (inputStream != null) {
				return inputStreamToByteBuffer(inputStream);
			}

		} catch (IOException e) {
			throw new RuntimeException("Failed to read classpath resource: " + normalizedPath, e);
		}

		// 2. Fallback for development mode / direct file execution.
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
					throw new RuntimeException("Failed to read texture file: " + candidate.toAbsolutePath(), e);
				}
			}
		}

		StringBuilder message = new StringBuilder();
		message.append("Texture resource not found: ").append(path).append("\n");
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