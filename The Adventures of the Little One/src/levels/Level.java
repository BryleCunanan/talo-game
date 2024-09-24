package levels;

import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Sweeper;
import main.Game;
import objects.GameContainer;
import objects.Potion;
import objects.Spike;
import objects.Totem;
import utils.HelpMethods;

import static utils.HelpMethods.GetLevelData;
import static utils.HelpMethods.GetSweepers;
import static utils.HelpMethods.GetPlayerSpawn;


public class Level {

	private BufferedImage img;
	private int[][] lvlData;
	private ArrayList<Sweeper> sweepers;
	private ArrayList<Potion> potions;
	private ArrayList<Spike> spikes;
	private ArrayList<GameContainer> containers;
	private ArrayList<Totem> totems;
	private int lvlTilesWide;
	private int maxTilesOffset;
	public int maxLvlOffsetX;
	private Point playerSpawn;
			
	public Level(BufferedImage img) {
		this.img = img;
		createLevelData();
		createEnemies();
		createPotions();
		createContainers();
		createSpikes();
		createTotems();
		calcLvlOffsets();
		calcPlayerSpawn();
	}

	private void createTotems() {
		totems = HelpMethods.GetTotem(img);
	}

	private void createSpikes() {
		spikes = HelpMethods.GetSpikes(img);
	}

	private void createContainers() {
		containers = HelpMethods.GetContainers(img);
	}

	private void createPotions() {
		potions = HelpMethods.GetPotions(img);
		
	}

	private void calcPlayerSpawn() {
		playerSpawn = GetPlayerSpawn(img);
		
	}
	
	private void calcLvlOffsets() {
		lvlTilesWide = img.getWidth();
		maxTilesOffset = lvlTilesWide - Game.TILES_IN_WIDTH;
		maxLvlOffsetX = Game.TILES_SIZE * maxTilesOffset;
	}

	private void createEnemies() {
		sweepers = GetSweepers(img);
		
	}

	private void createLevelData() {
		lvlData = GetLevelData(img);
		
	}

	public int getSpriteIndex(int x, int y) {
		return lvlData[y][x];
	}
	
	public int[][] getLevelData() {
		return lvlData;
	}
	
	public int getLvlOffset() {
		return maxLvlOffsetX;
	}
	
	public ArrayList<Sweeper> getSweepers() {
		return sweepers;
	}
	
	public Point getPlayerSpawn() {
		return playerSpawn;
	}
	
	public ArrayList<Potion> getPotions() {
		return potions;
	}
	
	public ArrayList<GameContainer> getContainers() {
		return containers;
	}
	
	public ArrayList<Spike> getSpikes() {
		return spikes;
	}
	
	public ArrayList<Totem> getTotems() {
		return totems;
	}
}
