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

import static org.lwjgl.opengl.GL11.*;
import org.lwjgl.opengl.*;
import org.newdawn.slick.Color;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import java.awt.Font;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.JOptionPane;

public class Game {
	
	// ---- var & const ---- //
	public static final int WIDTH = 1280;
	public static final int SUB_WIDTH = 1200;
	public static final int HEIGHT = 960;
	public static final int SUB_HEIGHT = 880;
	static final int PLAYER_SIZE = 52;   // 64
	boolean isRunning = true;
	int[] mouse_pos;
	GameTime gt = new GameTime();
	public Player player;
	Grid warpingGrid; 
	// thruster particle
	boolean openThruster = false;
	// font render 
	FontRenderer fr_timesNew = new FontRenderer("Roboto", Font.BOLD, 20);
	// instantiate weapon manager 
	WeaponManager weaponManager = new WeaponManager();
	// declare background 
	Background bg;
	
	
	// do enemy stuff -- should be moved to WaveManager class
    int MAX_ENEMY = 4;
    boolean canSpawnWave = true;
	public int enemy_size = 52;     // 64
	int[] enemy_pos;
	int waveCounter = 0;
	int iterativeWaveCounter = 0;
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

	
	// ---- CONSTRUCTOR ---- //
	public Game() {
		setUpDisplay();
		setUpOpenGL();
		setUpEntities();
		gt.setUpTimer();
		// ------------ MAIN GAME LOOP ------------ // 
		while (isRunning) {
			render();
			logic(gt.getDelta(), gt.getVerletDelta());
			input();       
			Display.update();
			Display.sync(60);
			if (Display.isCloseRequested()) {
				isRunning = false;
			}
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
		Display.destroy();
	}
	
	
	//  ------------ SETTING OPENGL ------------ // 
	// ---------------------------------------------------------------------- //
	private void setUpDisplay() {
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle("Geometry Wars");
			Display.create();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// set up the font (UI)
		fr_timesNew.setUpFont();
	}
	
	private void setUpOpenGL() {
		glMatrixMode(GL_PROJECTION);
		glLoadIdentity();
		glOrtho(0, WIDTH, HEIGHT, 0, 1, -1);
		glMatrixMode(GL_MODELVIEW);
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
		
		fr_timesNew.drawString(20, 0, "SCORE : "+ String.valueOf(player.getScore()), Color.gray); // uses alpha 
        
        glDisable(GL_TEXTURE_2D);
		glDisable(GL_BLEND);
	}
	
	private void logic(int delta, double dt) {
		checkCollision();
		checkUpgrade();
		//checkLose();
		
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
        
        // update death particle for enemy -- handle the render also
        for (int i = 0; i < ps_DeathArrayList.size(); i++) {
        	ps_DeathArrayList.get(i).updateDeathParticle(delta);
        	if (!ps_DeathArrayList.get(i).isAlive) {
        		ps_DeathArrayList.remove(i);
			}
		}
        
        if (!blackholeArrayList.isEmpty()) {
			for (BlackholeSystem bhSystem : blackholeArrayList) {
				bhSystem.updateBlackholeSystem(delta);
				
			    // warping the grid on blackhole
		        for (int i = 1; i < warpingGrid.row-1; i++) {
		            for (int j = 1; j < warpingGrid.column-1; j++) {
		            	if (bhSystem.blackholeEntityC.hitbox.contains(warpingGrid.particleArray[i][j].currentX, warpingGrid.particleArray[i][j].currentY)) {
		            		warpingGrid.particleArray[i][j].currentX -= 40;
	                        warpingGrid.particleArray[i][j].currentY -= 40;
						}
		            }
		        }
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
	    
	    // collide bullet with enemy -- for now ... 
	    for (int i = 0; i < weaponManager.bulletArray.size(); i++) {
			for (int j = 0; j < enemyArray.size(); j++) {
				if (weaponManager.bulletArray.get(i).intersects(enemyArray.get(j)) && enemyArray.get(j).getHealth() <= 0) {
					weaponManager.bulletArray.remove(i);
					
					addPlayerScore(enemyArray.get(j));
					
					ParticleSystem_Death ps_DeathSys = new ParticleSystem_Death();
					ps_DeathArrayList.add(ps_DeathSys);
					
					ps_DeathSys.setX(enemyArray.get(j).getX());
					ps_DeathSys.setY(enemyArray.get(j).getY());
							
					//ps_Death.generateDeathParticle(enemyArray.get(j).getX(), enemyArray.get(j).getY());
					ps_DeathSys.thread.start();
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
					
					ParticleSystem_Death ps_DeathSys = new ParticleSystem_Death();
					ps_DeathArrayList.add(ps_DeathSys);
					
					ps_DeathSys.setX(blackholeArrayList.get(j).blackholeEntityC.getX());
					ps_DeathSys.setY(blackholeArrayList.get(j).blackholeEntityC.getY());
							
					ps_DeathSys.thread.start();
					blackholeArrayList.remove(j);
				}else if(weaponManager.bulletArray.get(i).intersects(blackholeArrayList.get(j).blackholeEntityC) 
						&& blackholeArrayList.get(j).blackholeEntityC.getHealth() > 0) {
					
					blackholeArrayList.get(j).blackholeEntityC.setHealth( blackholeArrayList.get(j).blackholeEntityC.getHealth() - 1 );
					weaponManager.bulletArray.remove(i);
					break;
				}
			}
		}
	    
	    
	    // warping the grid on collisons with bullet 
        for (int i = 1; i < warpingGrid.row-1; i++) {
            for (int j = 1; j < warpingGrid.column-1; j++) {
            	for (Bullet b : weaponManager.bulletArray) {
                    if (b.contains(warpingGrid.particleArray[i][j].currentX, warpingGrid.particleArray[i][j].currentY)) {

                    	warpingGrid.particleArray[i][j].currentX += b.comp_x * 10;
                        warpingGrid.particleArray[i][j].currentY -= b.comp_y * 10;

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
		// ---- KEY INPUT ---- //
		while (Keyboard.next()) {	
			if (Keyboard.getEventKey() == Keyboard.KEY_UP || Keyboard.getEventKey() == Keyboard.KEY_DOWN) {
				if (Keyboard.getEventKeyState()) {
					player.aimUP = true;
					player.keyReleased = false;
					
					// open particle thruster
					openThruster = true;
					
				} else {
					// keyReleased
					resetKeyInput();
				}
			}
			
			if (Keyboard.getEventKey() == Keyboard.KEY_LEFT) {
				if (Keyboard.getEventKeyState() ) {
					// keyPressed
					player.steerL = true;
					player.rotSpeed = 0.3;
				} else {
					// keyReleased
					player.steerL = false;
					player.rotSpeed = 0.0;
				}
			}
			if (Keyboard.getEventKey() == Keyboard.KEY_RIGHT) {
				if (Keyboard.getEventKeyState()) {
					// keyPressed
					player.steerR = true;
					player.rotSpeed = 0.3;
				} else {
					// keyReleased
					player.steerR = false;
					player.rotSpeed = 0.0;
				}
			}
		}
		
		// ---- MOUSE INPUT ---- //
		while (Mouse.next()) {
			// left mouse button : 0
			if (Mouse.getEventButton() == 0) {
				if (Mouse.getEventButtonState()) {
					// mousePressed
			        mouse_pos = new int[2];
			        mouse_pos[0] = Mouse.getEventX();
			        mouse_pos[1] = HEIGHT - Mouse.getEventY();
			        weaponManager.bulletSpawner(player.getX(), player.getY(), mouse_pos);
				}else {
					// mouseReleased
					weaponManager.resetShoot();
				}
			}
		}

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
                	//enemyArray.add(new EnemyC(rand.nextInt(WIDTH-enemy_size/2), rand.nextInt(HEIGHT-enemy_size/2), enemy_size, enemy_size));
                	//blackholeArrayList.add( new BlackholeSystem((double)rand.nextInt(SUB_WIDTH-enemy_size/2), (double)rand.nextInt(SUB_HEIGHT-enemy_size/2)));
                	blackholeArrayList.add( new BlackholeSystem((double)rand.nextInt(SUB_WIDTH-enemy_size/2 - 800), (double)rand.nextInt(SUB_HEIGHT-enemy_size/2 - 600)));
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
        // use enemyArray.isEmpty() for perfect loop
    	if (enemyArray.size() == 2 && !canSpawnWave) {
            waveCounter++;
            canSpawnWave = true;
            // possibilities of iterative waves 
            randSkip = rand.nextInt(10);
            iterativeWaveCounter = 0;
        }
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
    
    
}
