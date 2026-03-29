package entities;

import helpers.TextureHelper;

import static org.lwjgl.opengl.GL11.*;

import java.awt.Rectangle;

import org.newdawn.slick.opengl.Texture;

public class Bullet extends Entity {
	
	TextureHelper texHelper;
    private double bullet_angle; 
    public double comp_x, comp_y;
    public Texture bulletTex;

	public Bullet(double x, double y, double width, double height) {
		super(x, y, width, height);
		super.setXVelocity(0.8);
		super.setYVelocity(0.8);
		texHelper = new TextureHelper();
		bulletTex = texHelper.loadTexture("res/DoubleBullet_Alpha.png", "PNG");
		hitbox = new Rectangle();
	}
	
    public void setBulletAngle(double bullet_angle) {
        this.bullet_angle = bullet_angle;
    }
    
    public double getBulletAngle() {
        return bullet_angle;
    }
	
	@Override
	public void update(int delta) {
		comp_x = vel_x*Math.cos(-bullet_angle);
		this.x += delta * comp_x;
		comp_y = vel_y*Math.sin(-bullet_angle);
		this.y -= delta * comp_y;
		hitbox.setBounds((int)x, (int)y, (int)width, (int)height);
	}
	
	
	@Override
	public void draw() {
		glColor3f(1.0f, 0.5f, 0.0f);
		glEnable(GL_BLEND);

		drawQuadTex( (float)x, (float)y, (float)width, (float)height, (float)bullet_angle, bulletTex );
	}
	
	@Override
	public void drawQuadTex(float x, float y, float width, float height, float rot, Texture texture) {
		texture.bind();
		glLoadIdentity();
	
		// rotate bullet in direction of velocity - convert rad to deg
		rot = (float)((bullet_angle*180)/3.14)+270;
		
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
