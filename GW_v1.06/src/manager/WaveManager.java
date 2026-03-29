package manager;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;

import entities.enemy.EnemyEntity;

public class WaveManager {
	
	// ---- var & const ---- //
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 960;
    int MAX_ENEMY = 4;
    boolean canSpawnWave = true;
	public int enemy_size;
	int[] enemy_pos;
    // ------ array 01 for spawn location (corner of screen) ------ //
    ArrayList<Point> spawnArray = new ArrayList<Point>(Arrays.asList(new Point(0,0), 
                                                                     new Point(WIDTH-enemy_size,0), 
                                                                     new Point(0,HEIGHT-enemy_size),
                                                                     new Point(WIDTH-enemy_size,HEIGHT-enemy_size)));
    // ------ array 02 for spawn location (center of screen) ------ //
    ArrayList<Point> spawnArray02 = new ArrayList<Point>(Arrays.asList(new Point((WIDTH/2)-(enemy_size/2),0), 
                                                                     new Point((WIDTH/2)-(enemy_size/2),HEIGHT), 
                                                                     new Point(0,(HEIGHT/2)-(enemy_size/2)),
                                                                     new Point(WIDTH,(HEIGHT/2)-(enemy_size/2))));
    ArrayList<EnemyEntity> enemyArray = new ArrayList<EnemyEntity>();
    
    
    // ---- empty constructor ---- //													
    public WaveManager() {}
	
	
    
    
    
    
    
    
    
    
    
    
    
    
    
    public void updateWave(int delta) {
		for (EnemyEntity enemyEntity : enemyArray) {
			enemyEntity.update(delta);
		}
	}
	
	
}
