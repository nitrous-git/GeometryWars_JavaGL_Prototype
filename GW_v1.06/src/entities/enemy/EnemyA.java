package entities.enemy;

import static org.lwjgl.opengl.GL11.*;

import java.util.ArrayList;

import org.newdawn.slick.opengl.Texture;

import entities.particle_system.OscParticle;
import helpers.TextureHelper;

/**
 * EnemyA
 * Blue Rectangle main enemy 
 * is a follower -- 50Pts
 */
public class EnemyA extends EnemyEntity{
	
	
	public ArrayList<OscParticle> deathParticleSystem = new ArrayList<>();
	OscParticle oscParticle;

	public EnemyA(double x, double y, double width, double height) {
		super(x, y, width, height);
		texHelper = new TextureHelper();
		super.enemyTex = texHelper.loadTexture("res/EnemyA.png", "PNG");
		super.setEnemyID("EnemyA");
		setHealth(0);
	}
	


    @Override
    public void update(int delta, double player_x, double player_y){
    	
        double distance_x = player_x - this.x;
        double distance_y = player_y - this.y;
        double enemy_angle = Math.atan2(distance_y, distance_x);
        setEnemyAngle(enemy_angle);
    	
		comp_x = vel_x*Math.cos(-enemy_angle);
		this.x += delta * comp_x;
		comp_y = vel_y*Math.sin(-enemy_angle);
		this.y -= delta * comp_y;
    }
    
	
	@Override
	public void draw() {
		glColor3f(1.0f, 1.0f, 1.0f);
		glEnable(GL_BLEND);

		drawQuadTex( (float)x, (float)y, (float)width, (float)height, (float)super.enemy_angle, super.enemyTex );
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
	
	
	
	/*
	// ---- Death particle system -- should be in external class, in package particle_system
	public void generateDeathParticle() {
		// do particle stuff
		int particleAngle = 0;
		
		for (int i = 0; i < 8; i++) {
			oscParticle = new OscParticle(x, y, 4, 4);
			oscParticle.setXVelocity(Math.cos(Math.toRadians(particleAngle)));
			oscParticle.setYVelocity(Math.sin(Math.toRadians(particleAngle)));
			
			
			deathParticleSystem.add(oscParticle);
			particleAngle += 45;
			
		}
	}
	
	public void updateDeathParticle(int delta) {
		if (!deathParticleSystem.isEmpty()) {
			for (int i = 0; i < deathParticleSystem.size(); i++) {
				deathParticleSystem.get(i).update(delta);
				if (deathParticleSystem.get(i).lifetime <= 0) {
					deathParticleSystem.remove(i);
				}
			}
		}
	}
	
	
	public void drawDeathParticle() {
		if (!deathParticleSystem.isEmpty()) {
			for (OscParticle oscParticle : deathParticleSystem) {
				oscParticle.draw();
			}
		}
	}
	*/
	
}
