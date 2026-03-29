package entities;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;

import helpers.TextureHelper;

public class Background extends Entity {
	
	TextureHelper texHelper;
	public Texture backgroundTex;
	public int rotation = 0;  // unused 

	public Background(double x, double y, double width, double height) {
		super(x, y, width, height);
		texHelper = new TextureHelper();
		backgroundTex = texHelper.loadTexture("res/bg_1280x960.png", "PNG");
	}
	
	@Override
	public void draw() {
		
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glColor3f(1.0f, 1.0f, 1.0f);
		
		drawQuadTex( (float)x, (float)y, (float)width, (float)height, (float)rotation, backgroundTex );
		glDisable(GL_BLEND);
		glDisable(GL_TEXTURE_2D);
		
	}
	
	@Override
	public void drawQuadTex(float x, float y, float width, float height, float rot, Texture texture) {
		texture.bind();

		glPushMatrix();{
			
			width *= 2;
			height *= 2;
			
			glBegin(GL_QUADS); {
				
				glTexCoord2f(0, 0); 
				glVertex2f(0, 0);
				glTexCoord2f(1, 0); 
				glVertex2f(width, 0);
				glTexCoord2f(1, 1); 
				glVertex2f(width, height);
				glTexCoord2f(0, 1); 
				glVertex2f(0, height);
				
			}
			glEnd();
		}
		glPopMatrix();
	} 
}
