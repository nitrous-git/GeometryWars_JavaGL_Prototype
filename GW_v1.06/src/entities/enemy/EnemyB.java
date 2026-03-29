package entities.enemy;

import helpers.TextureHelper;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;
import java.util.Random;

/**
 * EnemyB
 * Spinner/ filler enemy -- 25Pts
 * is not a follower -- does smooth rotation and bounce of wall 
 * choose between two texture if instantiate 
 * 0 : purple spinner 1 : red spinner
 */
public class EnemyB extends EnemyEntity {
	
	// screen size for collisions
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 960;
	public static final int SUB_WIDTH = 1200;
	public static final int SUB_HEIGHT = 880;
	
	Random rand = new Random();
	protected int randomTex;
	protected int rotation = 0;
	protected double rotSpeed = 0.2;
	
	public EnemyB(double x, double y, double width, double height) {
		super(x, y, width, height);
		texHelper = new TextureHelper();
		randomTex = rand.nextInt(2);
		switch (randomTex) {
			case 0:
				super.enemyTex = texHelper.loadTexture("res/EnemyB_a.png", "PNG");
				break;
			case 1:
				super.setWidth(44);
				super.setHeight(44);
				super.enemyTex = texHelper.loadTexture("res/EnemyB_b.png", "PNG");
				break;
			default:
				break;
		}
		super.setEnemyID("EnemyB");
		setHealth(0);
	}
	
	
	
	
	public void checkCollision() {
	    // spinner collided window  
		if (getX() >= SUB_WIDTH - getWidth()/2) {
			setXVelocity(-getXVelocity());
		}
		if (getX() <= 40 + getWidth()/2) {
			setXVelocity(-getXVelocity());
		}
		if (getY() >= -40 + SUB_HEIGHT - getHeight()/2) {
			setYVelocity(-getYVelocity());
		}
		if (getY() <= 40 + getHeight()/2) {
			setYVelocity(-getYVelocity());
		}
	
	}
	
	
    
    @Override
    public void update(int delta){
    	rotation += delta * rotSpeed; // smooth rotation around his axis
		this.x += delta * vel_x;
		this.y -= delta * vel_y;
		// spinner collided window
		checkCollision();
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
