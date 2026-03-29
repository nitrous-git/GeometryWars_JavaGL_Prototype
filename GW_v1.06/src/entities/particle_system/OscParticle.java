package entities.particle_system;

import static org.lwjgl.opengl.GL11.*;

import entities.Entity;

public class OscParticle extends Entity{
	
	public double lifetime;
	// color
	float r, g, b;
	// particle osc movement
	public int rotation = 0;
	public double rotSpeed = 0.8;
	public double amplitude = 1.4;

	public OscParticle(double x, double y, double width, double height, float r, float g, float b) {
		super(x, y, width, height);
		this.lifetime = 34;
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	@Override
	public void update(int delta) {
		this.x += vel_x;
		this.y += vel_y;
		updateLifetime();
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
		if (lifetime > 0) {
			glRectd(x, y, x+width, y+height);
		}
	}
	
	

}