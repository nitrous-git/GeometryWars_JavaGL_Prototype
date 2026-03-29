package helpers;

import java.io.InputStream;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

public class TextureHelper {
	
	public TextureHelper(){}
	
	public Texture loadTexture(String path, String fileType) {
		Texture texture = null;
		InputStream inputStream = ResourceLoader.getResourceAsStream(path);
		try {
			texture = TextureLoader.getTexture(fileType, inputStream);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return texture;
	}
}
