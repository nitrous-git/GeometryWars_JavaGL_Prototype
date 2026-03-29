package entities.particle_system;

import entities.Entity;

import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

public class LinearParticle extends Entity{
	
	public double lifetime;
	public Color color = Color.orange;
	
	public LinearParticle(double x, double y, double width, double height) {
		super(x, y, width, height);
		this.lifetime = 10;
	}
	
	@Override
	public void update(int delta) {
		this.x += delta * vel_x;
		this.y += delta * vel_y;
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
		glColor3f(1.0f, 1.0f, 0.0f);
		if (lifetime > 0) {
			glRectd(x, y, x+width, y+height);
		}
	}
}
