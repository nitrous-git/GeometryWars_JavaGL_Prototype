package manager;

import entities.Bullet;

import java.util.ArrayList;

public class WeaponManager {
	
	// ---- var & const ---- //
	static final int BULLET_SIZE = 28;
	public ArrayList<Bullet> bulletArray = new ArrayList<Bullet>();
	Bullet lastBullet;
	boolean canShoot = true;
	public int weaponIndex = 1;
	
	public WeaponManager() {}

	//-----SHOOT-----//
	// ---------------------------------------------------------------------- //
	public void bulletSpawner(double player_x, double player_y, int[] mouse_pos) {
		if (canShoot) {
            double distance_x;
            double distance_y;
            double bullet_angle;
            
            switch (weaponIndex) {
				case 1:
					// single shot shooting mode
					lastBullet = new Bullet(player_x, player_y, BULLET_SIZE+8, BULLET_SIZE);
					bulletArray.add(lastBullet);
					
                    distance_x = mouse_pos[0] - lastBullet.getX();
                    distance_y = mouse_pos[1] - lastBullet.getY();
                    bullet_angle = Math.atan2(distance_y, distance_x); // angle is in radians
                    lastBullet.setBulletAngle(bullet_angle);
                    
                    canShoot = false;
					break;
				case 2:
                    // initial deviation
                    double deviation = 0.2;
                    // 3 burst shooting mode
                    for (int i = 0; i < 3; i++) {
    					lastBullet = new Bullet(player_x, player_y, BULLET_SIZE+8, BULLET_SIZE);
    					bulletArray.add(lastBullet);
    					
                        distance_x = mouse_pos[0] - lastBullet.getX();
                        distance_y = mouse_pos[1] - lastBullet.getY();
                        bullet_angle = Math.atan2(distance_y, distance_x); // angle is in radians
                        lastBullet.setBulletAngle(bullet_angle + deviation);
                        
                        // decrease the deviation up to -0.2
                        deviation -= 0.2;
					}
                    
                    canShoot = false;
					break;
					
				default:
					break;
			}
		}
	}
	
	public void resetShoot() {
		canShoot = true;
	}
	
	
	// update shooting
	public void update(int delta) {
		if (!bulletArray.isEmpty()) {
			for (Bullet bullet : bulletArray) {
				bullet.update(delta);
			}
		}
	}
	
	// draw shooting bullet
	public void draw() {
        if (!bulletArray.isEmpty()) {
            for (Bullet bullet : bulletArray) {
                bullet.draw(); // uses alpha 
            } 
        }
	}
}
