package warping_grid;

public class Spring {
    
    Particle startParticle, endParticle;
    double magnitude; 
    double lengthAtRest; 
    double k; 

    public Spring(Particle startParticle, Particle endParticle){
        this.startParticle = startParticle;
        this.endParticle = endParticle;
        this.lengthAtRest = 20;   // lengthAtRest = 15
        this.k = 0.009;//0.005; //0.009;
    }

    private double getMagnitude(){
        this.magnitude = Math.sqrt(Math.pow(endParticle.currentX - startParticle.currentX, 2) +  Math.pow(endParticle.currentY - startParticle.currentY, 2));
        return magnitude;
    }

    public void solve(){
        double dx = startParticle.currentX - endParticle.currentX;
        double dy = startParticle.currentY - endParticle.currentY;
        double mag = getMagnitude();
        
        if (magnitude != 0) {
            double ds = (lengthAtRest - mag)/mag;

            double offsetX = ( k*( dx * ds ) ) / 2;
            double offsetY = ( k*( dy * ds ) ) / 2;

            if (startParticle.isMovable) {
                startParticle.currentX += offsetX;
                startParticle.currentY += offsetY;
            }
            if (endParticle.isMovable) {
                endParticle.currentX -= offsetX;
                endParticle.currentY -= offsetY;
            }
        }
    }
    

}
