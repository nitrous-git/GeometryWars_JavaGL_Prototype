package game;

import org.lwjgl.Sys;

public class GameTime {
	
	// normal tick 
	public static final int CAP = 20;
	private long lastFrame;
	int delta;
	
	public void setUpTimer() {
		lastFrame = getTime();
	}
	
	public long getTime() {
		return (Sys.getTime() * 1000) / Sys.getTimerResolution();
	}
	
	public int getDelta() {
		long currentTime = getTime();
		
		
		// CAPPING THE DELTA
		if (delta <= CAP) {
			delta = (int) (currentTime - lastFrame);
		}
		if (delta > CAP) {
			delta = CAP;
		}
		
		lastFrame = getTime();
		return delta;
	}
	
	
	// verlet tick 
    long lastLoopTime = System.nanoTime();
    final int TARGET_FPS = 60;
    final long OPTIMAL_TIME = 1000000000 / TARGET_FPS;   
	
    public double getVerletDelta() {
        long now = System.nanoTime();
        long updateLength = now - lastLoopTime;
        lastLoopTime = now;
        double dt = updateLength / ((double)OPTIMAL_TIME);
        return dt;
	}
	
	
}
