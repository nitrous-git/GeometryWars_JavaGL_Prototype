package warping_grid;

import java.util.ArrayList;
import static org.lwjgl.opengl.GL11.*;


public class Grid {
    
    int particleDistance = 20; // same as the lengthAtRest = 15
    //public int n_particle = 60; // number of particle
    // number of particle adjusted 
    
    // screen size 1280x960  
    // sub grid is 1200x880
    
    public int row = 45; //960/20 = 48   --- 880/20=44        adjust to height   
    public int column = 61;  //1280/20 = 64  ---- 1200/20=60  adjust to width

    int startX = 40;  
    int startY = 40;

    public Particle[][] particleArray = new Particle[row][column];
    ArrayList<Spring> springArray = new ArrayList<Spring>();

    public Grid(){
        initParticle();
        initSpring();
    }

    private void initParticle() {
        for(int i = 0; i < row; i++){
            for(int j = 0; j < column; j++){
                this.particleArray[i][j] = new Particle(startX + (particleDistance * j), startY + (particleDistance * i), 
                i == 0 && (j > 0) || i == row-1 && (j > 0) || j == 0 && (i > 0) || j == column-1 && (i > 0) ? false : true);
            }
        }
    }

    private void initSpring() {
        for (int i = 0; i < row; i++){
            for (int j = 0; j < column; j++){
                if (j != column-1) {
                    springArray.add(new Spring(particleArray[i][j], particleArray[i][j + 1]));
                }
                if (i != row-1) {
                    springArray.add(new Spring(particleArray[i][j], particleArray[i + 1][j]));
                }
            }
        }
    }

    // update the grid 
    public void updateGrid(double delta) {
        // update springs
        for (Spring s: springArray) {
            for (int i = 0; i < column; i++) {
                s.solve();
            }
        }
        // update position of particle
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                if (particleArray[i][j].isMovable) {
                    particleArray[i][j].updatePos(delta);
                }
            }
        }
    }
    
    
    // use this draw method
    public void draw() {
		for (Spring s : springArray) {
			drawLines(s.startParticle.currentX, s.startParticle.currentY, s.endParticle.currentX, s.endParticle.currentY);
		}
	}

    
    
    // draw the only lines(springs) of the grid
    public void drawLines(double x1, double y1, double x2, double y2) {
    	//glLineWidth(1);
    	glColor3f(0, 0, 0.75f);
    	glBegin(GL_LINES);{
	        glVertex2f((float)x1,(float)y1);
	        glVertex2f((float)x2,(float)y2);
    	}
        glEnd();
	}

    // BLACK HOLE HELPER
    public void applyBlackholeWarp(double centerX, double centerY, double radius, double strength) {
        double radiusSq = radius * radius;

        for (int i = 1; i < row - 1; i++) {
            for (int j = 1; j < column - 1; j++) {
                Particle p = particleArray[i][j];

                if (!p.isMovable) {
                    continue;
                }

                double dx = centerX - p.currentX;
                double dy = centerY - p.currentY;
                double distSq = dx * dx + dy * dy;

                if (distSq <= 0.0001 || distSq > radiusSq) {
                    continue;
                }

                double dist = Math.sqrt(distSq);

                // Direction from the grid particle toward the black hole center.
                double dirX = dx / dist;
                double dirY = dy / dist;

                // Stronger near the center, weaker near the edge.
                double normalizedDistance = dist / radius;
                double falloff = 1.0 - normalizedDistance;
                falloff = falloff * falloff;

                double displacement = strength * falloff;

                double moveX = dirX * displacement;
                double moveY = dirY * displacement;

                p.currentX += moveX;
                p.currentY += moveY;

                // Important with Verlet:
                // Move old position too, otherwise this injects a huge artificial velocity.
                p.oldX += moveX;
                p.oldY += moveY;
            }
        }
    }

}
