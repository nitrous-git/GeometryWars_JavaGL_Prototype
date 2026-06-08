package game;

import entities.Background;
import entities.Bullet;
import entities.Player;
import entities.enemy.EnemyA;
import entities.enemy.EnemyB;
import entities.enemy.EnemyC;
import entities.enemy.EnemyD;
import entities.enemy.EnemyEntity;
import entities.particle_system.BlackholeSystem;
import entities.particle_system.ParticleSystem_Death;
import helpers.FontRenderer;
import manager.WeaponManager;
import warping_grid.Grid;

import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class Game {
	
	// ---- var & const ---- //
	public static final int WIDTH = 1280; //1280
	public static final int SUB_WIDTH = 1200; //1200
	public static final int HEIGHT = 960; //960
	public static final int SUB_HEIGHT = 880; //880
	static final int PLAYER_SIZE = 52;   // 64
	boolean isRunning = true;
	int[] mouse_pos;
	GameTime gt = new GameTime();
	public Player player;
	Grid warpingGrid; 
	// thruster particle
	boolean openThruster = false;
	// font render 
	FontRenderer fr_timesNew = new FontRenderer("res/ExoSpace.ttf", 30f);
	// instantiate weapon manager 
	WeaponManager weaponManager = new WeaponManager();
	// declare background 
	Background bg;

	private Window window;
	
	
	// do enemy stuff -- should be moved to WaveManager class
    int MAX_ENEMY = 4;
    boolean canSpawnWave = true;
	public int enemy_size = 52;     // 64
	int[] enemy_pos;
	int waveCounter = 0;
	int iterativeWaveCounter = 0;
	int nextWaveEnemyThreshold = 2;
    // ------ array 01 for spawn location (corner of screen) ------ //
    ArrayList<Point> spawnArray = new ArrayList<Point>(Arrays.asList(new Point(40+enemy_size/2,40+enemy_size/2), 
                                                                     new Point(SUB_WIDTH-enemy_size/2,40+enemy_size/2), 
                                                                     new Point(40+enemy_size/2,-40+SUB_HEIGHT-enemy_size/2),
                                                                     new Point(SUB_WIDTH-enemy_size/2,SUB_HEIGHT-enemy_size/2)));
    // ------ array 02 for spawn location (half of screen) ------ //
    ArrayList<Point> spawnArray02 = new ArrayList<Point>(Arrays.asList(new Point(40+(SUB_WIDTH/2),40+enemy_size/2), 
                                                                     new Point(40+(SUB_WIDTH/2),-40+SUB_HEIGHT-enemy_size/2), 
                                                                     new Point(40+enemy_size,40+(SUB_HEIGHT/2)-(enemy_size/2)),
                                                                     new Point(SUB_WIDTH-enemy_size/2,(SUB_HEIGHT/2))));
    // ------ array 03 for spawn location (iterative spawn) ------ //
    ArrayList<Point> spawnArray03 = new ArrayList<Point>(Arrays.asList(new Point(140+enemy_size/2,140+enemy_size/2), 
                                                                     new Point(-140+SUB_WIDTH-enemy_size/2,140+enemy_size/2), 
                                                                     new Point(140+enemy_size/2,-140+SUB_HEIGHT-enemy_size/2),
                                                                     new Point(-140+SUB_WIDTH-enemy_size/2,-140+SUB_HEIGHT-enemy_size/2)));
    ArrayList<EnemyEntity> enemyArray = new ArrayList<EnemyEntity>();
    Random rand = new Random();
    int min_x, max_x, min_y, max_y;
    int offset = 100;
    int randSkip = 1;
    ArrayList<ParticleSystem_Death> ps_DeathArrayList = new ArrayList<ParticleSystem_Death>();
    ArrayList<BlackholeSystem> blackholeArrayList = new ArrayList<BlackholeSystem>();

	// game over
	private boolean gameOver = false;
	private boolean replayMouseWasDown = false;
	private GameOverPanel gameOverPanel;

	// ---- CONSTRUCTOR ---- //
	public Game() {
		setUpDisplay();
		setUpOpenGL();
		setUpEntities();
		gt.setUpTimer();
		// ------------ MAIN GAME LOOP ------------ // 
		while (isRunning && !window.shouldClose()) {
			render();
			logic(gt.getDelta(), gt.getVerletDelta());
			input();

			window.update();


			try {
				Long sleepTimeLong = (gt.lastLoopTime-System.nanoTime() + gt.OPTIMAL_TIME)/1000000;

				if (sleepTimeLong<0) {
					sleepTimeLong = (long)0;
				}

				Thread.sleep(sleepTimeLong);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		fr_timesNew.destroy();
		window.destroy();
	}
	
	
	//  ------------ SETTING OPENGL ------------ // 
	// ---------------------------------------------------------------------- //
	private void setUpDisplay() {
		window = new Window(WIDTH, HEIGHT, "Geometry Wars");
		window.create();

		fr_timesNew.setUpFont();
	}

	private void setUpOpenGL() {
		glEnable(GL_COLOR_MATERIAL);
		glEnable(GL_TEXTURE_2D);
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
	}

	
	
	// ---------- INIT ENTITIES ---------- // 
	// ---------------------------------------------------------------------- //
	private void setUpEntities() {
		player = new Player(500, 500, PLAYER_SIZE, PLAYER_SIZE);
		warpingGrid = new Grid();
		bg = new Background(0, 0, WIDTH, HEIGHT);

		gameOverPanel = new GameOverPanel();
	}


	// ------ INSIDE MAIN GAME LOOP ------- //
	// ---------------------------------------------------------------------- //
	/*	
	*	 Render method  
	*	 Don't break the order
	*   -Draw all opaque objects first.
	*	-Sort all the transparent objects.
	*	-Draw all the transparent objects in sorted order.
	**/
	private void render() {
		glClear( GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT );
		bg.draw();

		// draw the grid
		warpingGrid.draw();
		
		// draw player and thruster particle
		player.ps_Thruster.draw(); // opaque texture -- for now ...
		
		glEnable(GL_TEXTURE_2D);
		
		player.draw(); // uses alpha 
		
		// draw weapons attack
		weaponManager.draw(); // uses alpha 
	
		// draw enemy wave
        if (!enemyArray.isEmpty()) {
            for (EnemyEntity enemy : enemyArray) {
                enemy.draw();
            }
        }
		
		fr_timesNew.drawString(20, 30, "SCORE : "+ player.getScore(), 1f, 1f, 1f, 1.0f); // uses alpha

		if (gameOver) {
			gameOverPanel.draw(fr_timesNew);
		}

        glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}

	private void logic(int delta, double dt) {
		if (gameOver) {
			warpingGrid.updateGrid(dt);
			updateDeathParticles(delta);
			return;
		}

		checkCollision();
		checkUpgrade();

		if (gameOver) {
			updateDeathParticles(delta);
			return;
		}

		// update the grid -- using a second tick
		warpingGrid.updateGrid(dt);

		// update weapons attack
		weaponManager.update(delta);

		// update thruster particle
		if (openThruster) {
			player.generatePS_Thruster();
			player.ps_Thruster.updateLinear(delta);
			player.ps_Thruster.updateOsc(delta);
		}

		if (!openThruster) {
			player.ps_Thruster.updateLinear(delta);
			player.ps_Thruster.updateOsc(delta);
		}

		// update player
		player.update(delta);

		// update enemy and waves
		updateEnemy(delta);
		resetWave();
		generateWave();

		updateDeathParticles(delta);

		if (!blackholeArrayList.isEmpty()) {
			for (BlackholeSystem bhSystem : blackholeArrayList) {
				bhSystem.updateBlackholeSystem(delta);

				double blackholeX = bhSystem.blackholeEntityC.getX();
				double blackholeY = bhSystem.blackholeEntityC.getY();

				double warpRadius = 140.0;
				double warpStrength = 8.0;

				warpingGrid.applyBlackholeWarp(blackholeX, blackholeY, warpRadius, warpStrength);
			}
		}
	}

	private void updateDeathParticles(int delta) {
		for (int i = ps_DeathArrayList.size() - 1; i >= 0; i--) {
			ParticleSystem_Death psDeath = (ParticleSystem_Death) ps_DeathArrayList.get(i);

			psDeath.updateDeathParticle(delta);

			if (!psDeath.isAlive) {
				ps_DeathArrayList.remove(i);
			}
		}
	}
	
	public void checkCollision() throws IndexOutOfBoundsException {
	    
	    // player collide window
	    if (player.getX() >= 40 + SUB_WIDTH - player.getWidth()/2) {
			player.setX( 40 + SUB_WIDTH - player.getWidth()/2 );
		}
	    if (player.getX() <= 40+player.getWidth()/2) {
	    	player.setX(40+player.getHeight()/2);
		}
    	if (player.getY() >= 40 + SUB_HEIGHT - player.getHeight()/2) {
			player.setY( 40 + SUB_HEIGHT - player.getHeight()/2 );
		}
	    if (player.getY() <= 40 + player.getHeight()/2) {
	    	player.setY(40 + player.getHeight()/2);
		}


		if (checkPlayerHitByEnemy()) {
			triggerGameOver();
			return;
		}
	    
	    // collide bullet with enemy -- for now ... 
	    for (int i = 0; i < weaponManager.bulletArray.size(); i++) {
			for (int j = 0; j < enemyArray.size(); j++) {
				if (weaponManager.bulletArray.get(i).intersects(enemyArray.get(j)) && enemyArray.get(j).getHealth() <= 0) {
					weaponManager.bulletArray.remove(i);
					
					addPlayerScore(enemyArray.get(j));

					spawnDeathParticles(enemyArray.get(j).getX(), enemyArray.get(j).getY());

					enemyArray.remove(j);
					break;
				}else if(weaponManager.bulletArray.get(i).intersects(enemyArray.get(j)) && enemyArray.get(j).getHealth() > 0) {
					//System.out.println("ENEMY " + j + " HAS " + enemyArray.get(j).getHealth());
					enemyArray.get(j).setHealth( enemyArray.get(j).getHealth() - 1 );
					weaponManager.bulletArray.remove(i);
					break;
				}
			}
		}
	    
	    
	    // colllide bullet with black hole
	    for (int i = 0; i < weaponManager.bulletArray.size(); i++) {
			for (int j = 0; j < blackholeArrayList.size(); j++) {
				if (weaponManager.bulletArray.get(i).intersects(blackholeArrayList.get(j).blackholeEntityC) 
					&& blackholeArrayList.get(j).blackholeEntityC.getHealth() <= 0) {
					weaponManager.bulletArray.remove(i);

					spawnDeathParticles(enemyArray.get(j).getX(), enemyArray.get(j).getY());

					blackholeArrayList.remove(j);
				}else if(weaponManager.bulletArray.get(i).intersects(blackholeArrayList.get(j).blackholeEntityC) 
						&& blackholeArrayList.get(j).blackholeEntityC.getHealth() > 0) {
					
					blackholeArrayList.get(j).blackholeEntityC.setHealth( blackholeArrayList.get(j).blackholeEntityC.getHealth() - 1 );
					weaponManager.bulletArray.remove(i);
					break;
				}
			}
		}


		// Warp the grid around active bullets.
		double bulletWarpRadius = 42.0;
		double bulletWarpRadiusSq = bulletWarpRadius * bulletWarpRadius;
		double bulletWarpStrength = 14.0;

		for (int i = 1; i < warpingGrid.row - 1; i++) {
			for (int j = 1; j < warpingGrid.column - 1; j++) {
				for (Bullet b : weaponManager.bulletArray) {
					double dx = warpingGrid.particleArray[i][j].currentX - b.getX();
					double dy = warpingGrid.particleArray[i][j].currentY - b.getY();

					double distSq = dx * dx + dy * dy;

					if (distSq <= bulletWarpRadiusSq) {
						double dist = Math.sqrt(distSq);
						double normalizedDistance = dist / bulletWarpRadius;
						double falloff = 1.0 - normalizedDistance;
						falloff = falloff * falloff;

						double moveX = b.comp_x * bulletWarpStrength * falloff;
						double moveY = -b.comp_y * bulletWarpStrength * falloff;

						warpingGrid.particleArray[i][j].currentX += moveX;
						warpingGrid.particleArray[i][j].currentY += moveY;

						break;
					}
				}
			}
		}
	    
	}
	
	public void checkLose() {
		if (player.getHealth() <= 0) {
	      int result = JOptionPane.showConfirmDialog(null, "         *** YOU LOSE *** \n                Replay ?", "GEOMETRY WARS", JOptionPane.OK_CANCEL_OPTION);
	      if (result == JOptionPane.OK_OPTION) {
	        player.setX( WIDTH/2 - 120/2 );
	        player.setY( HEIGHT - 20 );
	        player.setHealth(6);
	      }
	      if (result == JOptionPane.CANCEL_OPTION) {
	        System.exit(0);
	      }
		}
	}


	// ------------ HANDLE INPUT ------------- //
	// ---------------------------------------------------------------------- //
	private void input() {
		long handle = window.getHandle();

		// Escape closes the game.
		if (glfwGetKey(handle, GLFW_KEY_ESCAPE) == GLFW_PRESS) {
			window.requestClose();
			isRunning = false;
			return;
		}

		if (gameOver) {
			handleGameOverInput(handle);
			return;
		}

		// Thruster / movement.
		boolean thrustPressed =
				glfwGetKey(handle, GLFW_KEY_UP) == GLFW_PRESS ||
						glfwGetKey(handle, GLFW_KEY_DOWN) == GLFW_PRESS;

		if (thrustPressed) {
			player.aimUP = true;
			player.keyReleased = false;
			openThruster = true;
		} else {
			resetKeyInput();
		}

		// Rotation left.
		if (glfwGetKey(handle, GLFW_KEY_LEFT) == GLFW_PRESS) {
			player.steerL = true;
			player.rotSpeed = 0.3;
		} else {
			player.steerL = false;
		}

		// Rotation right.
		if (glfwGetKey(handle, GLFW_KEY_RIGHT) == GLFW_PRESS) {
			player.steerR = true;
			player.rotSpeed = 0.3;
		} else {
			player.steerR = false;
		}

		if (!player.steerL && !player.steerR) {
			player.rotSpeed = 0.0;
		}

		// Mouse shooting.
		if (glfwGetMouseButton(handle, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS) {
			double[] mouseX = new double[1];
			double[] mouseY = new double[1];

			glfwGetCursorPos(handle, mouseX, mouseY);

			mouse_pos = new int[2];
			mouse_pos[0] = (int) window.toGameX(mouseX[0]);
			mouse_pos[1] = (int) window.toGameY(mouseY[0]);

			weaponManager.bulletSpawner(player.getX(), player.getY(), mouse_pos);
		} else {
			weaponManager.resetShoot();
		}
	}

	private void handleGameOverInput(long handle) {
		boolean replayMouseDown = glfwGetMouseButton(handle, GLFW_MOUSE_BUTTON_LEFT) == GLFW_PRESS;

		if (replayMouseDown && !replayMouseWasDown) {
			double[] mouseX = new double[1];
			double[] mouseY = new double[1];

			glfwGetCursorPos(handle, mouseX, mouseY);

			double gameMouseX = window.toGameX(mouseX[0]);
			double gameMouseY = window.toGameY(mouseY[0]);

			if (gameOverPanel.isReplayButtonHit(gameMouseX, gameMouseY)) {
				resetGameAfterGameOver();
			}
		}

		replayMouseWasDown = replayMouseDown;
	}

	public void resetKeyInput() {
		player.steerL = false;
		player.steerR = false;
		player.aimUP = false;
		//player.aimDOWN = false;
		//player.setXVelocity(0);
		//player.setYVelocity(0);
		player.rotSpeed = 0.0;
		player.keyReleased = true;
		openThruster = false;
	}
	
	
	

    // ------ ENEMY & WAVES ------ //
	// do enemy stuff -- should be moved to WaveManager class
    public void updateEnemy(int delta) {
        for (EnemyEntity enemy : enemyArray) {
            switch (enemy.getEnemyID()) {
				case "EnemyA":
					enemy.update(delta, player.getX(), player.getY());
					break;
				case "EnemyB":
					enemy.update(delta);
					break;
				case "EnemyC":
					enemy.update(delta);
					break;
				case "EnemyD":
					enemy.update(delta, player.getX(), player.getY());
					break;
				default:
					break;
			}	
        }
    }
	

    public void generateWave() {
        if (canSpawnWave) {
        	if (randSkip != 0) {
        		
                for (int i = 0; i < MAX_ENEMY; i++) {
                	// control generation of different Enemy type A or B
	        		enemyArray.add(new EnemyA((int)spawnArray.get(i).getX(), (int)spawnArray.get(i).getY(), enemy_size, enemy_size));
	                if (waveCounter%2==0 && waveCounter!=0) {
	                	enemyArray.add(new EnemyB((int)spawnArray02.get(i).getX(), (int)spawnArray02.get(i).getY(), enemy_size, enemy_size));
	                }
                } 
                if (waveCounter%4==0 && waveCounter!=0) {
					blackholeArrayList.add(createRandomBlackholeInsideGrid());
                } 
                canSpawnWave = false;
			}
        	if (randSkip == 0) {
        		iterativeWave();
			}
        }
        
    }


    public void iterativeWave() {
    	iterativeWaveCounter++;
    	if (iterativeWaveCounter%20 == 0) {
    		for (int i = 0; i < MAX_ENEMY; i++) {
    			max_x = (int)spawnArray03.get(i).getX() + offset;
    			min_x = (int)spawnArray03.get(i).getX() - offset;
    			int randXPos = rand.nextInt(max_x-min_x) + min_x;
    			max_y = (int)spawnArray03.get(i).getY() + offset;
    			min_y = (int)spawnArray03.get(i).getY() - offset;
    			int randYPos = rand.nextInt(max_y-min_y) + min_y;
    			enemyArray.add(new EnemyD(randXPos, randYPos, enemy_size, enemy_size));
    		}
		}
    	// spawn 15 enemy each corner (300/20=15)
    	// 4 corner = 60 enemy on screen 
    	if (iterativeWaveCounter >= 200) {
    		canSpawnWave = false;
		}
    	
    }

	public void resetWave() {
		if (!canSpawnWave && enemyArray.size() <= nextWaveEnemyThreshold) {
			waveCounter++;
			canSpawnWave = true;

			randSkip = rand.nextInt(10);
			iterativeWaveCounter = 0;

			nextWaveEnemyThreshold = rollNextWaveEnemyThreshold();
		}
	}

	private int rollNextWaveEnemyThreshold() {
		if (waveCounter < 4) {
			return randomIntInclusive(1, 2);
		}

		if (waveCounter < 10) {
			return randomIntInclusive(1, 3);
		}

		return randomIntInclusive(2, 4);
	}

    public void addPlayerScore(EnemyEntity enemy) {
        switch (enemy.getEnemyID()) {
			case "EnemyA":
				player.setScore(player.getScore()+50);
				break;
			case "EnemyB":
				player.setScore(player.getScore()+25);
				break;
			case "EnemyC":
				player.setScore(player.getScore()+100);
				break;
			case "EnemyD":
				player.setScore(player.getScore()+50);
				break;
			default:
				break;
		}		
	}
    
    // --- check for weapon upgrade
    public void checkUpgrade() {
    	if (player.getScore() > 4000) {
			weaponManager.weaponIndex = 2;
		}
	}


	// Black hole spawn helpers
	private BlackholeSystem createRandomBlackholeInsideGrid() {
		int gridLeft = 40;
		int gridTop = 40;
		int gridRight = gridLeft + SUB_WIDTH;
		int gridBottom = gridTop + SUB_HEIGHT;

		int blackholeRadius = enemy_size / 2;

		// Extra margin so the black hole does not spawn too close to the grid border.
		int borderPadding = 120;

		int minX = gridLeft + blackholeRadius + borderPadding;
		int maxX = gridRight - blackholeRadius - borderPadding;

		int minY = gridTop + blackholeRadius + borderPadding;
		int maxY = gridBottom - blackholeRadius - borderPadding;

		int x = randomIntInclusive(minX, maxX);
		int y = randomIntInclusive(minY, maxY);

		return new BlackholeSystem((double) x, (double) y);
	}

	private int randomIntInclusive(int min, int max) {
		if (max < min) {
			return min;
		}

		return rand.nextInt(max - min + 1) + min;
	}

	// Game Over Helpers
	private boolean checkPlayerHitByEnemy() {
		for (int i = 0; i < enemyArray.size(); i++) {
			EnemyEntity enemy = (EnemyEntity) enemyArray.get(i);

			if (centeredRectIntersects(
					player.getX(), player.getY(), player.getWidth() * 0.75, player.getHeight() * 0.75,
					enemy.getX(), enemy.getY(), enemy.getWidth() * 0.85, enemy.getHeight() * 0.85
			)) {
				return true;
			}
		}

		for (int i = 0; i < blackholeArrayList.size(); i++) {
			BlackholeSystem bhSystem = (BlackholeSystem) blackholeArrayList.get(i);

			if (centeredRectIntersects(
					player.getX(), player.getY(), player.getWidth() * 0.75, player.getHeight() * 0.75,
					bhSystem.blackholeEntityC.getX(), bhSystem.blackholeEntityC.getY(),
					bhSystem.blackholeEntityC.getWidth() * 0.9,
					bhSystem.blackholeEntityC.getHeight() * 0.9
			)) {
				return true;
			}
		}

		return false;
	}

	private boolean centeredRectIntersects(
			double ax, double ay, double aw, double ah,
			double bx, double by, double bw, double bh
	) {
		double aLeft = ax - aw / 2.0;
		double aRight = ax + aw / 2.0;
		double aTop = ay - ah / 2.0;
		double aBottom = ay + ah / 2.0;

		double bLeft = bx - bw / 2.0;
		double bRight = bx + bw / 2.0;
		double bTop = by - bh / 2.0;
		double bBottom = by + bh / 2.0;

		return aLeft < bRight
				&& aRight > bLeft
				&& aTop < bBottom
				&& aBottom > bTop;
	}

	private void triggerGameOver() {
		if (gameOver) {
			return;
		}

		gameOver = true;

		player.setHealth(0);
		openThruster = false;
		resetKeyInput();

		killAllEnemiesOnScreen();

		weaponManager.bulletArray.clear();

		canSpawnWave = false;
	}

	private void killAllEnemiesOnScreen() {
		for (int i = enemyArray.size() - 1; i >= 0; i--) {
			EnemyEntity enemy = (EnemyEntity) enemyArray.get(i);
			spawnDeathParticles(enemy.getX(), enemy.getY());
			enemyArray.remove(i);
		}

		for (int i = blackholeArrayList.size() - 1; i >= 0; i--) {
			BlackholeSystem bhSystem = (BlackholeSystem) blackholeArrayList.get(i);
			spawnDeathParticles(bhSystem.blackholeEntityC.getX(), bhSystem.blackholeEntityC.getY());
			blackholeArrayList.remove(i);
		}
	}

	private void spawnDeathParticles(double x, double y) {
		ParticleSystem_Death psDeathSys = new ParticleSystem_Death();

		psDeathSys.setX(x);
		psDeathSys.setY(y);

		psDeathSys.generateDeathParticle(x, y, rand.nextInt(360));

		ps_DeathArrayList.add(psDeathSys);

		psDeathSys.thread.start();
	}

	// Reset game for replay
	private void resetGameAfterGameOver() {
		gameOver = false;
		replayMouseWasDown = false;

		enemyArray.clear();
		blackholeArrayList.clear();
		ps_DeathArrayList.clear();
		weaponManager.bulletArray.clear();

		player.setX(WIDTH / 2.0);
		player.setY(HEIGHT / 2.0);
		player.setHealth(6);
		player.setScore(0);
		player.rotation = 0;
		player.push = 0;
		player.setXVelocity(0);
		player.setYVelocity(0);

		openThruster = false;
		resetKeyInput();

		warpingGrid = new Grid();

		waveCounter = 0;
		iterativeWaveCounter = 0;
		randSkip = 1;
		canSpawnWave = true;
	}
}
