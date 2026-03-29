package entities;

import helpers.TextureHelper;
import entities.particle_system.ParticleSystem_Thruster;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.opengl.Texture;


public class Player extends Entity {
	
	TextureHelper texHelper;
	private int health;
	public Texture playerTex;
	public boolean aimUP, aimDOWN, steerL, steerR;
	public boolean keyReleased = true;
	// player rotation
	public int rotation = 0;  // angle of rotation for velocity 
	public double rotSpeed = 0;
	public double push = 0;
	// thruster particle
	public ParticleSystem_Thruster ps_Thruster;
	int score_points = 0;

 
	public Player(double x, double y, double width, double height) {
		super(x, y, width, height);
		texHelper = new TextureHelper();
		playerTex = texHelper.loadTexture("res/playerDOWN_64x64.png", "PNG");
		ps_Thruster = new ParticleSystem_Thruster();
		setHealth(6);
	}
	
	public void setScore(int score_points) {
		this.score_points = score_points;
	}
	
	public int getScore() {
		return score_points;
	}
	

	
	public void setHealth(int health) {
		if (health >= 0) {
			this.health = health;
		}else {
			health = 0;
		}
	}
	
	public int getHealth() {
		return health;
	}
	
	
	@Override
	public void draw() {
		glColor3f(1.0f, 1.0f, 1.0f);
		glEnable(GL_BLEND);
		drawQuadTex( (float)x, (float)y, (float)width, (float)height, (float)rotation, playerTex );
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
	

	
	public void applyThruster() {
		if (push <= 0.30) {
			push += 0.01;
		}
	}
	
	public void decelerate() {
		if (push > 0) {
			push -= 0.01;
		}
		if (push <= 0.01) {
			push = 0;
		}
	}
	
	

	@Override
	public void update(int delta) {
		//System.out.println(ps_Thruster.particleSysOsc.size());
		//System.out.println(thrustForce);
		
		this.x += delta * vel_x * push;
		this.y += delta * vel_y * push;
		
		if (steerL) {
			rotation += delta * -rotSpeed;
		}
		if (steerR) {
			rotation += delta * rotSpeed;
		}
		
		if (aimUP) {
			applyThruster();
			setYVelocity(Math.sin(Math.toRadians(rotation))); 
			setXVelocity(Math.cos(Math.toRadians(rotation)));
		}
		
		if (keyReleased) {
			decelerate();
		}
		
		
	}
	
	
	public void generatePS_Thruster() {
		ps_Thruster.generateLinearParticule(x, y, -vel_x, -vel_y);
		ps_Thruster.generateOscParticule(x, y, -vel_x, -vel_y);
		ps_Thruster.generateOscInvertParticule(x, y, -vel_x, -vel_y);
	}	
	

}
