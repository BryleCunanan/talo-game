package entities;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import gamestates.Playing;
import levels.Level;
import levels.LevelManager;
import main.Game;
import utils.LoadSave;
import static utils.Constants.EnemyConstants.*;

public class EnemyManager {

	private Playing playing;
	private LevelManager levelManager;
	private BufferedImage[][] sweeperArr;
	private ArrayList<Sweeper> sweepers = new ArrayList<>();
	
	public EnemyManager(Playing playing, LevelManager levelManager) {
		this.playing = playing;
		this.levelManager = levelManager;
		loadEnemyImgs();
	}

	public void loadEnemies(Level level) {
		sweepers = level.getSweepers();
		System.out.println("size of sweepers: " + sweepers.size());
		
	}

	public void update(int[][] lvlData, Player player) {
		boolean isAnyActive = false;
		for(Sweeper s: sweepers)
			if (s.isActive()) {
				s.update(lvlData, player);
				isAnyActive = true;
			}
		if(!isAnyActive)
			if ((int)(player.getHitbox().x + player.getHitbox().width + 1) >= (levelManager.getCurrentLevel().getLevelData()[0].length * Game.TILES_SIZE) - (1 * Game.TILES_SIZE))
				playing.setLevelCompleted(true);
		
	} 
	
	public void draw(Graphics g, int xLvlOffset) {
		drawSweeper(g, xLvlOffset);
		
	}
	
	private void drawSweeper(Graphics g, int xLvlOffset) {
		for(Sweeper s: sweepers) 
			if (s.isActive()) {
			g.drawImage(sweeperArr[s.getEnemyState()][s.getAniIndex()],
					(int)(s.getHitbox().x - xLvlOffset - SWEEPER_DRAWOFFSET_X) + s.flipX(), (int)(s.getHitbox().y - SWEEPER_DRAWOFFSET_Y),
					SWEEPER_WIDTH * s.flipW(), SWEEPER_HEIGHT, null);
//			s.drawHitbox(g, xLvlOffset);
//			s.drawAttackBox(g, xLvlOffset);
		}
		
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		for(Sweeper s: sweepers)
			if (s.isActive())
				if(s.getCurrentHealth() > 0)
					if(attackBox.intersects(s.getHitbox())) {
						s.hurt(10);
						return;
					}
	}

	private void loadEnemyImgs() {
		sweeperArr = new BufferedImage[5][10];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.SWEEPER_SPRITE);
		for(int j = 0; j < sweeperArr.length; j++)
			for(int i = 0; i < sweeperArr[j].length; i++)
				sweeperArr[j][i] = temp.getSubimage(i * SWEEPER_WIDTH_DEFAULT, j * SWEEPER_HEIGHT_DEFAULT, SWEEPER_WIDTH_DEFAULT, SWEEPER_HEIGHT_DEFAULT);
		
	}

	public void resetAllEnemies() {
		for(Sweeper s : sweepers)
			s.resetEnemy();
		
	} 
}
