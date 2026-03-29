package warping_grid;

public class Particle {

    public double currentX;
	public double currentY;
	double oldX;
	double oldY;
	double damping;
    boolean isMovable;

    public Particle(double posX, double posY, boolean isMovable){
        // start everything at the same place, no velocity is calculated
        this.currentX = posX;
        this.currentY = posY;
        this.oldX = posX;
        this.oldY = posY;
        this.damping = 0.5;
        this.isMovable = isMovable;
    }

    // update with verlet integration (only position)
    // velocity is calculated from difference from (current position - old position)
    public void updatePos(double delta){
        if (isMovable) {
            double futureX = currentX;
            double futureY = currentY;
            
            futureX += (currentX - oldX) * damping * delta;
            futureY += (currentY - oldY) * damping * delta;


            oldX = currentX;
            oldY = currentY;
            currentX = futureX;
            currentY = futureY;
        }
    }

}
