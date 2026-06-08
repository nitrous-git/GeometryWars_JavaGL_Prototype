package game;

public class GameTime {
	// Normal tick.
	public static final int CAP = 20;

	private long lastFrameTime;
	private int delta;

	// Verlet / frame limiter tick.
	long lastLoopTime = System.nanoTime();

	final int TARGET_FPS = 60;
	final long OPTIMAL_TIME = 1_000_000_000L / TARGET_FPS;

	public void setUpTimer() {
		lastFrameTime = getTime();
		lastLoopTime = System.nanoTime();
	}

	public long getTime() {
		return System.nanoTime() / 1_000_000L;
	}

	public int getDelta() {
		long currentTime = getTime();

		delta = (int) (currentTime - lastFrameTime);

		if (delta > CAP) {
			delta = CAP;
		}

		if (delta < 0) {
			delta = 0;
		}

		lastFrameTime = currentTime;

		return delta;
	}

	public double getVerletDelta() {
		long now = System.nanoTime();
		long updateLength = now - lastLoopTime;

		lastLoopTime = now;

		return updateLength / (double) OPTIMAL_TIME;
	}
}