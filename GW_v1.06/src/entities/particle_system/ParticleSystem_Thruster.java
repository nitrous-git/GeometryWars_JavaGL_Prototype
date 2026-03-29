package entities.particle_system;

import java.util.ArrayList;

public class ParticleSystem_Thruster {

	// --- var & const --- //
	static final int PARTICLE_SIZE = 3;
	public LinearParticle linearParticle;
	public OscParticle oscParticle;
	public ArrayList<LinearParticle> particleSysLinear = new ArrayList<LinearParticle>();
	public ArrayList<OscParticle> particleSysOsc = new ArrayList<OscParticle>();
	double x, y;
	int timerCountLinear = 0;
	int timerCountOsc = 0;
	public int rotation = 0;
	public double rotSpeed = 0.8;
	public double amplitude = 1.5;
	
	// --- empty constructor --- //
	public ParticleSystem_Thruster(){}
	
	// --- generate particle system --- //
	public void generateLinearParticule(double x, double y, double vel_x, double vel_y) {
		timerCountLinear++;
		if (timerCountLinear % 10 == 0) {
			linearParticle = new LinearParticle(x, y, PARTICLE_SIZE, PARTICLE_SIZE);
			linearParticle.setXVelocity(vel_x);
			linearParticle.setYVelocity(vel_y);
			particleSysLinear.add(linearParticle);
		}
		if (timerCountLinear >= 100) {
			timerCountLinear = 0;
		}
	}
	
	public void generateOscParticule(double x, double y, double vel_x, double vel_y) {
		timerCountOsc++;
		if (timerCountOsc % 1 == 0) {
			oscParticle = new OscParticle(x, y, PARTICLE_SIZE, PARTICLE_SIZE, 1.0f, 0.3f, 0.0f);
			// move y
			if (Math.abs(vel_y) >= 0.2) {
				oscParticle.setXVelocity(Math.cos(Math.toRadians(rotation*1.4))*amplitude);
				oscParticle.setYVelocity(vel_y);
			}
			// move x
			if (Math.abs(vel_x) >= 0.2) {
				oscParticle.setXVelocity(vel_x);
				oscParticle.setYVelocity(Math.cos(Math.toRadians(rotation*1.4))*amplitude);
			}
			particleSysOsc.add(oscParticle);
		}
		if (timerCountOsc >= 100) {
			timerCountOsc = 0;
		}
	}
	
	public void generateOscInvertParticule(double x, double y, double vel_x, double vel_y) {
		timerCountOsc++;
		if (timerCountOsc % 1 == 0) {
			oscParticle = new OscParticle(x, y, PARTICLE_SIZE, PARTICLE_SIZE, 1.0f, 0.3f, 0.0f);
			// move y
			if (Math.abs(vel_y) >= 0.2) {
				oscParticle.setXVelocity(-1*Math.cos(Math.toRadians(rotation*1.4))*amplitude);
				oscParticle.setYVelocity(vel_y);
				
			}
			// move x
			if (Math.abs(vel_x) >= 0.2) {
				oscParticle.setXVelocity(vel_x);
				oscParticle.setYVelocity(-1*Math.cos(Math.toRadians(rotation*1.4))*amplitude);
			}
			particleSysOsc.add(oscParticle);
		}
		if (timerCountOsc >= 100) {
			timerCountOsc = 0;
		}
	}
	
	
	// --- update particle system --- //
	public void updateLinear(int delta) {
		for (int i = 0; i < particleSysLinear.size(); i++) {
			particleSysLinear.get(i).update(delta);
			if (particleSysLinear.get(i).lifetime <= 0) {
				particleSysLinear.remove(i);
			}
		}
	
	}
	
	public void updateOsc(int delta) {
		rotation += delta * rotSpeed;
		//System.out.println(rotation);
		for (int i = 0; i < particleSysOsc.size(); i++) {
			particleSysOsc.get(i).update(delta);
			if (particleSysOsc.get(i).lifetime <= 0) {
				particleSysOsc.remove(i);
			}
		}
	}
	
	// --- draw particle system --- //
	public void draw() {
		if (!particleSysLinear.isEmpty()) {
			for (LinearParticle linearParticle : particleSysLinear) {
				linearParticle.draw();
			}
		}
		if (!particleSysOsc.isEmpty()) {
			for (OscParticle oscParticle : particleSysOsc) {
				oscParticle.draw();
			}
		}
	}
	
}
