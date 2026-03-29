package entities;

import org.newdawn.slick.opengl.Texture;

public interface EntityInterface {
	public void draw();
	public void update(int delta);
	public void update(int delta, double player_x, double player_y);
	public void setLocation(double x, double y);
	public void setX(double x);
	public void setY(double y);
	public void setWidth(double width);
	public void setHeight(double height);
	public double getX();
	public double getY();
	public double getWidth();
	public double getHeight();
	public boolean intersects(Entity other);
	public boolean contains(double x, double y);
	public double getXVelocity();
	public double getYVelocity();
	public void setXVelocity(double vel_x);
	public void setYVelocity(double vel_y);
	public void drawQuadTex(float x, float y, float width, float height, float rot, Texture texture);
}
