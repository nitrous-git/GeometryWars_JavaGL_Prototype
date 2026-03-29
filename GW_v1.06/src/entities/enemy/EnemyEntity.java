package entities.enemy;

import org.newdawn.slick.opengl.Texture;

import entities.Entity;
import helpers.TextureHelper;

/**
 * Enemy
 * This parent class will not be used on runtime. 
 * Parent of enemy type A-B-C-D
 * 
 */
public class EnemyEntity extends Entity {
	
	TextureHelper texHelper;
	protected Texture enemyTex;
    protected double enemy_angle;
    protected double comp_x;
    protected double comp_y;
    protected String enemy_id = "enemy";
    protected int health;
	
	public EnemyEntity(double x, double y, double width, double height) {
		super(x, y, width, height);
		super.setXVelocity(0.1);
		super.setYVelocity(0.1);
	}
	
	public void setEnemyID(String enemy_id) {
		this.enemy_id = enemy_id;
	}
	
    public void setEnemyAngle(double enemy_angle) {
        this.enemy_angle = enemy_angle;
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
	
	public String getEnemyID() {
		return enemy_id;
	}
	
}
