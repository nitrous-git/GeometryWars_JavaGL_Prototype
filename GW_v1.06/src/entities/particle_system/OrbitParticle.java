package entities.particle_system;

import static org.lwjgl.opengl.GL11.*;

import entities.enemy.EnemyEntity;

public class OrbitParticle extends EnemyEntity {
	
	public double lifetime;
	// color
	protected float r, g, b;
	
	public OrbitParticle(double x, double y, double width, double height, float r, float g, float b) {
		super(x, y, width, height);
		this.r = r;
		this.g = g;
		this.b = b;
		super.mass = 2;
		setEnemyID("Orbiter");
	}
	
	
	public void updateLifetime() {
		lifetime -= 1;
		if (lifetime <= 0) {
			lifetime = 0;
		}
	}
	
	@Override
	public void draw() {
		glColor3f(r, g, b);
		glRectd(x, y, x+width, y+height);
	}
	

}
