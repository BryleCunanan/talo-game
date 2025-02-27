package gamestates;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import entities.EnemyManager;
import entities.Player;
import levels.LevelManager;
import main.Game;
import objects.ObjectManager;
import ui.GameOverOverlay;
import ui.LevelCompletedOverlay;
import ui.PauseOverlay;
import utils.LoadSave;
import static utils.Constants.Environment.*;

public class Playing extends State implements Statemethods {

	private Player player;
	private LevelManager levelManager;
	private EnemyManager enemyManager;
	private ObjectManager objectManager;
	private PauseOverlay pauseOverlay;
	private GameOverOverlay gameOverOverlay;
	private LevelCompletedOverlay levelCompletedOverlay;
	private boolean paused = false;
	
	private int xLvlOffset;
	private int leftBorder = (int) (0.4 * Game.GAME_WIDTH);
	private int rightBorder = (int) (0.6 * Game.GAME_WIDTH);
	private int maxLvlOffsetX;
	 
	private BufferedImage backgroundImg, cloudOne, cloudTwo, cloudThree;
	
	private boolean gameOver;
	private boolean lvlCompleted = false;
	private boolean playerDying;
	
	public Playing(Game game) {
		super(game);
		initClasses();
		
		backgroundImg = LoadSave.GetSpriteAtlas(LoadSave.PLAYING_BG_IMG);
		cloudOne = LoadSave.GetSpriteAtlas(LoadSave.CLOUD_1);
		cloudTwo = LoadSave.GetSpriteAtlas(LoadSave.CLOUD_2);
		cloudThree = LoadSave.GetSpriteAtlas(LoadSave.CLOUD_3);
		
		calcLvlOffset();
		loadStartLevel();
	}
	
	public void loadNextLevel() {
		levelManager.loadNextLevel();
		player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
		resetAll();
	}

	private void loadStartLevel() {
		enemyManager.loadEnemies(levelManager.getCurrentLevel());
		objectManager.loadObjects(levelManager.getCurrentLevel());
		
	}


	private void calcLvlOffset() {
		maxLvlOffsetX = levelManager.getCurrentLevel().getLvlOffset();
		
	}



	private void initClasses() {
	levelManager = new LevelManager(game);
	enemyManager = new EnemyManager(this, levelManager);
	objectManager = new ObjectManager(this);
	
	player = new Player(200, 200, (int)(30 *Game.SCALE), (int)(27*Game.SCALE), this);
	player.loadLvlData(levelManager.getCurrentLevel().getLevelData());
	player.setSpawn(levelManager.getCurrentLevel().getPlayerSpawn());
	pauseOverlay = new PauseOverlay(this);
	gameOverOverlay = new GameOverOverlay(this);
	levelCompletedOverlay = new LevelCompletedOverlay(this);
	player.resetAll();
	}

	@Override
	public void update() {
		if(paused) {
			pauseOverlay.update();
		} else if(lvlCompleted) {
			levelCompletedOverlay.update();
		} else if(gameOver) {
			gameOverOverlay.update();
		} else if(playerDying) {
			player.update();
		} else {
			levelManager.update();
			objectManager.update(levelManager.getCurrentLevel().getLevelData(), player);
			player.update();
			enemyManager.update(levelManager.getCurrentLevel().getLevelData(), player);
			checkCloseToBorder();
			
		}
	}
		


	private void checkCloseToBorder() {
		int playerX = (int) player.getHitbox().x;
		int diff = playerX - xLvlOffset;
		
		if(diff> rightBorder)
			xLvlOffset += diff - rightBorder;
		else if(diff < leftBorder)
			xLvlOffset += diff - leftBorder;
		
		if(xLvlOffset > maxLvlOffsetX)
			xLvlOffset = maxLvlOffsetX;
		else if(xLvlOffset < 0)
			xLvlOffset = 0;
		
	}



	@Override
	public void draw(Graphics g) {
		
		g.drawImage(backgroundImg, 0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT, null);
		drawClouds(g);
		
		
		levelManager.draw(g, xLvlOffset);
		objectManager.draw(g, xLvlOffset);
		player.render(g, xLvlOffset);
		enemyManager.draw(g, xLvlOffset);
		
		
		if(paused) {
			g.setColor(new Color(0,0,0,100));
			g.fillRect(0, 0, Game.GAME_WIDTH, Game.GAME_HEIGHT);
			pauseOverlay.draw(g);
		} else if(gameOver)
			gameOverOverlay.draw(g);
		else if (lvlCompleted)
			levelCompletedOverlay.draw(g);
	}



	private void drawClouds(Graphics g) {
		
		for(int i = 0; i < 4; i++) {
			g.drawImage(cloudOne, i * CLOUD_1_WIDTH - (int)(xLvlOffset * 0.3), 0, CLOUD_1_WIDTH, CLOUD_1_HEIGHT, null);
			g.drawImage(cloudTwo, i * CLOUD_2_WIDTH - (int)(xLvlOffset * 0.5), 0, CLOUD_2_WIDTH, CLOUD_2_HEIGHT, null);
			g.drawImage(cloudThree, i * CLOUD_3_WIDTH - (int)(xLvlOffset * 0.7), 0, CLOUD_3_WIDTH, CLOUD_3_HEIGHT, null);
		}
	}

	public void resetAll() {
		//RESET PLAYING, ENEMY, LEVEL, ETC
		gameOver = false;
		paused = false;
		lvlCompleted = false;
		playerDying = false;
		player.resetAll();
		enemyManager.resetAllEnemies();
		objectManager.resetAllObjects();
		
	}
	
	public void setGameOver(boolean gameOver) {
		this.gameOver = gameOver;
	}
	
	public void checkObjectHit(Rectangle2D.Float attackBox) {
		objectManager.checkObjectHit(attackBox);
		
	}
	
	public void checkEnemyHit(Rectangle2D.Float attackBox) {
		enemyManager.checkEnemyHit(attackBox);
	}

	public void checkPotionTouched(Rectangle2D.Float hitbox) {
		objectManager.checkObjectTouched(hitbox);
	}
	
	public void checkSpikeTouched(Player p) {
		objectManager.checkSpikesTouched(player);
		
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		if(!gameOver) {
			if(e.getButton() == MouseEvent.BUTTON1)
				player.setAttacking(true);
			else if(e.getButton() == MouseEvent.BUTTON3)
				player.specialAttack();
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pauseOverlay.mousePressed(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mousePressed(e);
		}else {
			gameOverOverlay.mousePressed(e);
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pauseOverlay.mouseReleased(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseReleased(e);
		} else {
			gameOverOverlay.mouseReleased(e);
		}
	}



	@Override
	public void mouseMoved(MouseEvent e) {
		if(!gameOver) {
			if(paused)
				pauseOverlay.mouseMoved(e);
			else if(lvlCompleted)
				levelCompletedOverlay.mouseMoved(e);
		} else {
			gameOverOverlay.mouseMoved(e);
		}
	}



	@Override
	public void keyPressed(KeyEvent e) {
		if(gameOver)
			gameOverOverlay.keyPressed(e);
		else
			switch(e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(true);
				break;
			case KeyEvent.VK_D:
				player.setRight(true);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(true);
				break;
			case KeyEvent.VK_P:
				paused = !paused;
				break;
			default:
				break;
		}
		
		if(!gameOver) {
			if(e.getKeyCode() == KeyEvent.VK_J)
				player.setAttacking(true);
			else if(e.getKeyCode() == KeyEvent.VK_K)
				player.specialAttack();
		}
		
	}

	public void mouseDragged(MouseEvent e) {
		if(!gameOver)
			if(paused)
				pauseOverlay.mouseDragged(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(!gameOver)
			switch(e.getKeyCode()) {
			case KeyEvent.VK_A:
				player.setLeft(false);
				break;
			case KeyEvent.VK_D:
				player.setRight(false);
				break;
			case KeyEvent.VK_SPACE:
				player.setJump(false);
				break;
			default:
				break;
			}
		
	}

	public void setLevelCompleted(boolean levelCompleted) {
		this.lvlCompleted = levelCompleted;
		if(levelCompleted)
			game.getAudioPlayer().lvlCompleted();
	}
	
	
	public void setMaxLvlOffset(int lvlOffset) {
		this.maxLvlOffsetX = lvlOffset;
	}
	
	public void unpauseGame() {
		paused = false;
	}
	
	public void windowFocusLost() {
		player.resetDirBooleans();
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public EnemyManager getEnemyManager() {
		return enemyManager;
	}
	
	public ObjectManager getObjectManager() {
		return objectManager;
	}

	public LevelManager getLevelManager() {
		return levelManager;
	}

	public void setPlayerDying(boolean playerDying) {
		this.playerDying = playerDying;
		
	}

}
