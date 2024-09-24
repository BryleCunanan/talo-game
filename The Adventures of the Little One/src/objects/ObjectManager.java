package objects;

import java.awt.Graphics;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import entities.Player;
import gamestates.Playing;
import levels.Level;
import main.Game;
import utils.LoadSave;
import static utils.Constants.ObjectConstants.*;
import static utils.HelpMethods.CanTotemSeePlayer;
import static utils.HelpMethods.IsProjectileHittingLevel;
import static utils.Constants.Projectiles.*; 

public class ObjectManager {
	
	private Playing playing;
	private BufferedImage[][] potionImgs, containerImgs;
	private BufferedImage[] totemImgs;
	private BufferedImage spikeImg, ballImg;
	private ArrayList<Potion> potions;
	private ArrayList<GameContainer> containers;
	private ArrayList<Spike> spikes;
	private ArrayList<Totem> totems;
	private ArrayList<Projectile> projectiles = new ArrayList<>();
	
	public ObjectManager(Playing playing) {
		this.playing = playing;
		loadImgs();		
	}
	
	public void checkSpikesTouched(Player p) {
		for(Spike s : spikes)
			if(s.getHitbox().intersects(p.getHitbox()))
				p.kill();
				
	}
	
	public void checkObjectTouched(Rectangle2D.Float hitbox) {
		for(Potion p : potions)
			if(p.isActive()) {
				if(hitbox.intersects(p.getHitbox())) {
					p.setActive(false);
					applyEffectToPlayer(p);
				}
			}
	}
	
	public void applyEffectToPlayer(Potion p) {
		if(p.getObjType() == RED_POTION)
			playing.getPlayer().changeHealth(RED_POTION_VALUE);
		else
			playing.getPlayer().changeMana(BLUE_POTION_VALUE);
	}
	public void checkObjectHit(Rectangle2D.Float attackBox) {
		
		for(GameContainer gc : containers)
			if(gc.isActive() && !gc.doAnimation) {
				if(gc.getHitbox().intersects(attackBox)) {
					gc.setAnimation(true);
					
					if(gc.getObjType() == BARREL) {
						potions.add(new Potion((int)(gc.getHitbox().x + gc.getHitbox().width / 2), (int) (gc.getHitbox().y), BLUE_POTION));
					}else if(gc.getObjType() == BOX) {
						potions.add(new Potion((int)(gc.getHitbox().x + gc.getHitbox().width / 2), (int) (gc.getHitbox().y - gc.getHitbox().height/2), RED_POTION));
					}
					return;
				}
			}
	}

	public void loadObjects(Level newLevel) {
		potions = new ArrayList<>(newLevel.getPotions());
		containers = new ArrayList<>(newLevel.getContainers());
		spikes = newLevel.getSpikes();
		totems = newLevel.getTotems();
		projectiles.clear();
	}
	
	private void loadImgs() {
		BufferedImage potionSprite = LoadSave.GetSpriteAtlas(LoadSave.POTION_ATLAS);
		potionImgs = new BufferedImage[2][7];
		
		for(int j =0; j < potionImgs.length; j++)
			for(int i = 0; i < potionImgs[j].length; i++)
				potionImgs[j][i] = potionSprite.getSubimage(12 * i, 16 * j, 12, 16);
		
		BufferedImage containerSprite = LoadSave.GetSpriteAtlas(LoadSave.CONTAINER_ATLAS);
		containerImgs = new BufferedImage[2][8];
		
		for(int j =0; j < containerImgs.length; j++)
			for(int i = 0; i < containerImgs[j].length; i++)
				containerImgs[j][i] = containerSprite.getSubimage(40 * i, 30 * j, 40, 30);
		
		spikeImg = LoadSave.GetSpriteAtlas(LoadSave.TRAP_ATLAS);
		
		totemImgs = new BufferedImage[6];
		BufferedImage temp = LoadSave.GetSpriteAtlas(LoadSave.TOTEM_ATLAS);
		
		for(int i = 0; i < totemImgs.length; i++)
			totemImgs[i] = temp.getSubimage(i * 32, 0, 32, 32);
		
		ballImg = LoadSave.GetSpriteAtlas(LoadSave.TOTEM_BALL);
	}
	
	public void update(int[][] lvlData, Player player) {
		for(Potion p: potions)
			if(p.isActive())
				p.update();
		for(GameContainer gc : containers)
			if(gc.isActive())
				gc.update();
		
		updateTotems(lvlData, player);
		updateProjectiles(lvlData, player);
	}
	
	private void updateProjectiles(int[][] lvlData, Player player) {
		for(Projectile p : projectiles)
			if(p.isActive()) {
				p.updatePos();
				
				if(p.getHitbox().intersects(player.getHitbox())) {
					player.changeHealth(-25);
					p.setActive(false);
				}else if(IsProjectileHittingLevel(p, lvlData)) {
					p.setActive(false);
				}
			}
		
	}

	private boolean isPlayerInRange(Totem t, Player player) {
		int absValue = (int) Math.abs(player.getHitbox().x - t.getHitbox().x);
		return absValue <= Game.TILES_SIZE * 25;
	}
	
	private boolean isPlayerInFrontOfTotem(Totem t, Player player) {
		if(t.getObjType() == TOTEM_LEFT) {
			if(t.getHitbox().x > player.getHitbox().x)
				return true;
		} else if(t.getHitbox().x < player.getHitbox().x)
			return true;
		return false;
	}
	
	private void updateTotems(int[][] lvlData, Player player) {
		for(Totem t : totems) {
			if(!t.doAnimation)
				if(t.getTileY() == player.getTileY()) {
					if(isPlayerInRange(t, player)) {
						System.out.println("Player in Range");
						if(isPlayerInFrontOfTotem(t, player)) {
							System.out.println("Player in front");
							if(CanTotemSeePlayer(lvlData, player.getHitbox(), t.getHitbox(), t.getTileY())) {
								System.out.println("sees player");
								t.setAnimation(true);
							}
						}
					}
				}
			t.update();
			if(t.getAniIndex() == 3 && t.getAniTick () == 0)
				shootTotem(t);
		}
	}


	private void shootTotem(Totem t) {
		int dir = 1;
		if(t.getObjType() == TOTEM_LEFT)
			dir = -1;
		
		projectiles.add(new Projectile((int)t.getHitbox().x, (int)t.getHitbox().y, dir));
		
	}

	public void draw(Graphics g, int xLvlOffset) {
		drawPotions(g, xLvlOffset);
		drawContainers(g, xLvlOffset);
		drawTraps(g, xLvlOffset);
		drawTotems(g,xLvlOffset);
		drawProjectiles(g,xLvlOffset);
	}

	private void drawProjectiles(Graphics g, int xLvlOffset) {
		for(Projectile p : projectiles)
			if(p.isActive()) 
					
				g.drawImage(ballImg,(int)(p.getHitbox().x -xLvlOffset),(int)(p.getHitbox().y), TOTEM_BALL_WIDTH, TOTEM_BALL_HEIGHT, null);
						
	}
	
	private void drawTotems(Graphics g, int xLvlOffset) {
		for(Totem t : totems) {
			int x = (int)(t.getHitbox().x -xLvlOffset);
			int width = TOTEM_WIDTH;
			
			if(t.getObjType() == TOTEM_RIGHT) {
				x += width;
				width *= -1;
			}
			g.drawImage(totemImgs[t.getAniIndex()], x, (int)(t.getHitbox().y), width, TOTEM_HEIGHT, null);
		}
	}

	private void drawTraps(Graphics g, int xLvlOffset) {
		for(Spike s : spikes)
			g.drawImage(spikeImg, (int)(s.getHitbox().x - xLvlOffset), (int)(s.getHitbox().y - s.getyDrawOffset()), SPIKE_WIDTH, SPIKE_HEIGHT, null);
		
	}

	private void drawContainers(Graphics g, int xLvlOffset) {
		for(GameContainer gc : containers)
			if(gc.isActive()) {
				int type = 0;
				if(gc.getObjType() == BARREL)
					type = 1;
				
				g.drawImage(containerImgs[type][gc.getAniIndex()], (int)(gc.getHitbox().x - gc.getxDrawOffset() - xLvlOffset), (int)(gc.getHitbox().y - gc.getyDrawOffset()), CONTAINER_WIDTH, CONTAINER_HEIGHT, null);
			}
	}

	private void drawPotions(Graphics g, int xLvlOffset) {
		for(Potion p: potions)
			if(p.isActive()) {
				int type = 0;
				if(p.getObjType() == RED_POTION)
					type = 1;
				
				g.drawImage(potionImgs[type][p.getAniIndex()], (int)(p.getHitbox().x - p.getxDrawOffset() - xLvlOffset),(int)(p.getHitbox().y - p.getyDrawOffset()), POTION_WIDTH, POTION_HEIGHT, null);
			}
		
	}
	
	public void resetAllObjects() {
		loadObjects(playing.getLevelManager().getCurrentLevel());
		
		for(Potion p: potions)
			p.reset();
			
		for(GameContainer gc : containers)
			gc.reset();
		
		for(Totem t : totems)
			t.reset();
	}

}
