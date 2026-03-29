package entities.particle_system;

import java.util.ArrayList;
import java.util.Random;

import entities.enemy.EnemyC;
import entities.enemy.EnemyEntity;

public class BlackholeSystem {
	
	static final int ORBITER_SIZE = 4;
	static final int BLACKHOLE_SIZE = 52;
	protected static final int n_particle = 25;
	protected double x, y;
	Random rand = new Random();
	protected int min_x, max_x, min_y, max_y;
	protected int offset = 100;
	// color for orbit particle 
	protected float r, g, b;
	// variables for newton eq -- gravity attraction
	protected double distance_x, distance_y, comp_x, comp_y; 
	protected double magnitude;
	protected double grav_force;
	protected double accel;
	protected double vel_x = 0;
	protected double vel_y = 0;
	// object 
	public ArrayList<EnemyEntity> systemArray = new ArrayList<EnemyEntity>();
	public EnemyC blackholeEntityC;
	OrbitParticle orbitParticle;
	public boolean isAlive = true;
	
	// ----- constructor ---- //
	public BlackholeSystem(double x, double y) {
		this.x = x;
		this.y = y;
		// pick a random color for orbiter
		setRandColor();
		generateOrbitParticle(x, y);
		blackholeEntityC = new EnemyC(x, y, BLACKHOLE_SIZE, BLACKHOLE_SIZE);
		systemArray.add(blackholeEntityC);
	}
	
	public void generateOrbitParticle(double x, double y) {
		for (int i = 0; i < n_particle; i++) {
			//setRandColor();
			// generate random initial orbit position
			max_x = (int)x + offset;
			min_x = (int)x - offset;
			int randXPos = rand.nextInt(max_x-min_x) + min_x;
			max_y = (int)y + offset;
			min_y = (int)y - offset;
			int randYPos = rand.nextInt(max_y-min_y) + min_y;		
			
			orbitParticle = new OrbitParticle(randXPos, randYPos, ORBITER_SIZE, ORBITER_SIZE, r, g, b);
			systemArray.add(orbitParticle);
		}
	}
	
	// pick a random color
	public void setRandColor() {
		this.r = (float) rand.nextInt(255) / 255;
		this.g = (float) rand.nextInt(255) / 255;
		this.b = (float) rand.nextInt(255) / 255;
	}
	
	// calclate gravity attraction
	public void calculateNewton() {
		for (EnemyEntity n_1 : systemArray) {
			for (EnemyEntity n_2 : systemArray) {
				distance_x = n_1.getX() - n_2.getX();
				distance_y = n_1.getY() - n_2.getY();
				magnitude = Math.sqrt( Math.pow(distance_x, 2) + Math.pow(distance_y, 2) );
				
				if (magnitude < 8) {
					magnitude = 8;
				}

				grav_force = 0.125*(n_1.mass*n_2.mass) / (Math.pow(magnitude, 2));
				// solve for acceleration : F=ma
				accel = grav_force/n_1.mass;
				// component of velocity
				comp_x = distance_x/magnitude;
				comp_y = distance_y/magnitude;
				// update velocity
				n_1.vel_x -= accel*comp_x;
				n_1.vel_y -= accel*comp_y;
			}
		}
	}
	
	
	public void applyNewton(int delta) {
		for (EnemyEntity n : systemArray) {
			n.x += n.vel_x;
			n.y += n.vel_y ;
		}
	}
	
	public void updateBlackholeSystem(int delta) {
		calculateNewton();
		applyNewton(delta);
		for (EnemyEntity n : systemArray) {
			n.draw();
		}
	}


	
}
