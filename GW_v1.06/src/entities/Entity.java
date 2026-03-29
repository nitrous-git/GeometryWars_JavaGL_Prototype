package entities;

import java.awt.Rectangle;
import org.newdawn.slick.opengl.Texture;

public class Entity implements EntityInterface {
	
	public double x;
	public double y;
	protected double width;
	protected double height;
	public double vel_x;
	public double vel_y;
	public double mass;
	public Rectangle hitbox = new Rectangle();
	
	// ------- constructor -------- //
	public Entity(double x, double y, double width, double height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	
	// ------ instantiate  ------- //
	@Override
	public void setLocation(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public void setX(double x) {
		this.x = x;
	}

	@Override
	public void setY(double y) {
		this.y = y;
	}

	@Override
	public void setWidth(double width) {
		this.width = width;
	}

	@Override
	public void setHeight(double height) {
		this.height = height;
	}

	@Override
	public double getX() {
		return x;
	}

	@Override
	public double getY() {
		return y;
	}

	@Override
	public double getWidth() {
		return width;
	}	

	@Override
	public double getHeight() {
		return height;
	}

	@Override
	public boolean intersects(Entity other) {
		hitbox.setBounds((int)x, (int)y, (int)width, (int)height);
		return hitbox.intersects(other.getX(), other.getY(), other.getWidth(), other.getHeight());
	}
	
	@Override
	public boolean contains(double x, double y) {
		//hitbox.setBounds((int)x, (int)y, (int)width, (int)height);
		return hitbox.contains(x, y);
	}
	
	// ------- do physics ------- //
	@Override
	public double getXVelocity() {
		return vel_x;
	}
	
	@Override
	public double getYVelocity() {
		return vel_y;
	}
	
	@Override
	public void setXVelocity(double vel_x) {
		this.vel_x = vel_x;
	}
	
	@Override
	public void setYVelocity(double vel_y) {
		this.vel_y = vel_y;
	}
	
	@Override
	public void update(int delta) {
		this.x += delta * vel_x;
		this.y += delta * vel_y;
	}
	
	
	// ------------ Override in child class --------------//
	@Override
	public void draw() {}
	
	@Override
	public void drawQuadTex(float x, float y, float width, float height, float rot, Texture texture) {}

	
	// ------------ Override in Enemy CLASS --------------//
	@Override
	public void update(int delta, double player_x, double player_y) {}
}
