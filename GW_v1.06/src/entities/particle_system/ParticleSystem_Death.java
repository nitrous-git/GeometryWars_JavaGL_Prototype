package entities.particle_system;

import java.util.ArrayList;
import java.util.Random;

public class ParticleSystem_Death implements Runnable {

	public ArrayList<OscParticle> deathParticleSystem;
	OscParticle oscParticle;
	// color
	float r, g, b;
	// location
	private double x;
	private double y;
	// initial angle
	int initAngle = 0;
	Random rand = new Random();
	int loopCounter = 0;
	public Thread thread = new Thread(this);
	
	public boolean isAlive = true;
	
	public ParticleSystem_Death() {
		deathParticleSystem = new ArrayList<>();
		setRandColor();
	}
	
	// ---- Death particle system 
	public void generateDeathParticle(double x, double y, int initAngle) {
		// do particle stuff
		//int particleAngle = 0;
		for (int i = 0; i < 8; i++) {

			oscParticle = new OscParticle(x, y, 4, 4, r, g, b);
			oscParticle.setXVelocity(Math.cos(Math.toRadians(initAngle)) * 5);
			oscParticle.setYVelocity(Math.sin(Math.toRadians(initAngle)) * 5);
			
			oscParticle.lifetime = 50; // use a setter .... because OOP bro ... 
			deathParticleSystem.add(oscParticle);
			//particleAngle += 45;
			initAngle += 45;
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
			drawDeathParticle();
		} else if (deathParticleSystem.isEmpty()) {
			isAlive = false;
		}
	}
	
	
	
	public void drawDeathParticle() {
		if (!deathParticleSystem.isEmpty()) {
			for (OscParticle oscParticle : deathParticleSystem) {
				oscParticle.draw();
			}
		}
	}
	
	
	// pick a random color
	public void setRandColor() {
		this.r = (float) rand.nextInt(255) / 255;
		this.g = (float) rand.nextInt(255) / 255;
		this.b = (float) rand.nextInt(255) / 255;
	}

	@Override
	public void run() {
		while (loopCounter<30) {
			loopCounter++;
			if (loopCounter%10==0) {
				generateDeathParticle(this.x, this.y, rand.nextInt(360));
				try {
					Thread.sleep(50);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	
	
	
	// set location
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}
	
	
}
