package entities.enemy;

import helpers.TextureHelper;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;



/**
 * EnemyC
 * Black hole enemy - 100Pts 
 * spawn in random location 
 * 
 */
public class EnemyC extends EnemyEntity {
	
	protected int rotation = 0;

	public EnemyC(double x, double y, double width, double height) {
		super(x, y, width, height);
		texHelper = new TextureHelper();
		super.enemyTex = texHelper.loadTexture("res/EnemyC.png", "PNG");
		//setXVelocity(0.01);
		//setYVelocity(0.01);
		setEnemyID("EnemyC");
		setHealth(10);
		super.mass = 200;
		hitbox = new Rectangle();
		hitbox.setBounds((int)x, (int)y, (int)width, (int)height);
	}

	
	@Override
	public void draw() {
		glColor3f(1.0f, 1.0f, 1.0f);
		glEnable(GL_BLEND);
		drawQuadTex( (float)x, (float)y, (float)width, (float)height, (float)rotation, super.enemyTex );
	}
	
	@Override
	public void drawQuadTex(float x, float y, float width, float height, float rot, Texture texture) {
		texture.bind();
		glLoadIdentity();
		
		glPushMatrix();{
			
			width /= 2;
			height /= 2;
			
			glTranslatef(x, y, 0);
			glRotatef(rot, 0, 0, 1);
			
			glBegin(GL_QUADS); {
				
				glTexCoord2f(0, 0); 
				glVertex2f(-width, -height);
				glTexCoord2f(1, 0); 
				glVertex2f(-width, height);
				glTexCoord2f(1, 1); 
				glVertex2f(width, height);
				glTexCoord2f(0, 1); 
				glVertex2f(width, -height);
			}
			glEnd();
		}
		glPopMatrix();
	}

	
}
