package entities;

import static utils.Constants.PlayerConstants.*;
import static utils.HelpMethods.*;
import static utils.Constants.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import audio.AudioPlayer;
import gamestates.Playing;
import main.Game;
import utils.LoadSave;


public class Player extends Entity {

	private BufferedImage[][] animations;

	private boolean moving = false, attacking = false;
	private boolean left, right, jump;
	private int [][] lvlData;
	private float xDrawOffset = 1* Game.SCALE;
	private float yDrawOffset = 2* Game.SCALE;
	
	//JUMPING & GRAVITY
	private float jumpSpeed = -2.25f * Game.SCALE;
	private float fallSpeedAfterCollision = 0.5f * Game.SCALE;
	
	//STATUS BAR UI
	private BufferedImage statusBarImg;
	
	private int statusBarWidth = (int)(192 * Game.SCALE);
	private int statusBarHeight = (int)(58 * Game.SCALE);
	private int statusBarX = (int)(10 * Game.SCALE);
	private int statusBarY = (int)(10 * Game.SCALE);
	
	private int healthBarWidth = (int)(150 * Game.SCALE);
	private int healthBarHeight = (int)(4 * Game.SCALE);
	private int healthBarXStart = (int)(34 * Game.SCALE);
	private int healthBarYStart = (int)(14 * Game.SCALE);
	private int healthWidth = healthBarWidth;
	
	private int manaBarWidth = (int)(104 * Game.SCALE);
	private int manaBarHeight = (int)(2 * Game.SCALE);
	private int manaBarXStart = (int)(44 * Game.SCALE);
	private int manaBarYStart = (int)(34 * Game.SCALE);
	private int manaWidth = healthBarWidth;
	private int manaMaxValue = 200;
	private int manaValue = manaMaxValue;
	
	private int flipX = 0;
	private int flipW = 1;
	
	private boolean attackChecked = false;
	private Playing playing;
	
	private int tileY = 0;
	private boolean specialAttackActive;
	private int specialAttackTick;
	private int manaGrowSpeed = 15;
	private int manaGrowTick;
	
	public Player(float x, float y, int width, int height, Playing playing) {
		super(x, y, width, height);
		this.playing = playing;
		this.state = IDLE;
		this.maxHealth = 100;
		this.currentHealth = maxHealth;
		this.walkSpeed = 1.0f * Game.SCALE;
		LoadAnimations();
		initHitbox(15, 24);
		initAttackBox();
	} 
	
	public void setSpawn(Point spawn) {
		this.x = spawn.x;
		this.y = spawn.y;
		hitbox.x = x;
		hitbox.y = y;
	}
	
	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int)(20 * Game.SCALE), (int)(20 * Game.SCALE));
		resetAttackBox();
	}

	public void update() {
		updateHealthBar();
		updateManaBar();
		
		if(currentHealth <= 0) {
			if(state != DEAD) {
				state = DEAD;
				aniTick = 0;
				aniIndex = 0;
				playing.setPlayerDying(true);
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.DIE);
			}else if(aniIndex == GetSpriteAmount(DEAD) - 1 && aniTick >= ANI_SPEED - 1) {
				playing.setGameOver(true);
				playing.getGame().getAudioPlayer().stopSong();
				playing.getGame().getAudioPlayer().playEffect(AudioPlayer.GAMEOVER);
			} else
				updateAnimationTick();
			
			return;
		}
		
		
		updateAttackBox();
		
		updatePos();
		
		if(moving) {
			checkPotionTouched();
			checkSpikesTouched();
			tileY = (int)(hitbox.y / Game.TILES_SIZE);
			if(specialAttackActive) {
				specialAttackTick ++;
				if(specialAttackTick >= 35) {
					specialAttackTick = 0;
					specialAttackActive = false;
				}
			}
		}
		if(attacking || specialAttackActive)
			checkAttack();
		updateAnimationTick();
		setAnimation();
	}
	
	private void checkSpikesTouched() {
		playing.checkSpikeTouched(this);
		
	}

	private void checkPotionTouched() {
		playing.checkPotionTouched(hitbox);
		
	}

	private void checkAttack() {
		if(attackChecked || aniIndex != 0)
			return;
		attackChecked = true;
		
		if(specialAttackActive)
			attackChecked = false;
		
		playing.checkEnemyHit(attackBox);
		playing.checkObjectHit(attackBox);
		playing.getGame().getAudioPlayer().playAttackSound();
		
	}

	private void updateAttackBox() {
		if(right && left) {
			if(flipW == 1)	{
				attackBox.x = hitbox.x + hitbox.width + (int)(2 * Game.SCALE);
			} else {
				attackBox.x = hitbox.x - hitbox.width - (int)(7 * Game.SCALE);
			}
		}else if(right || (specialAttackActive && flipW == 1)) {
			attackBox.x = hitbox.x + hitbox.width + (int)(2 * Game.SCALE);
		}else if(left || (specialAttackActive && flipW == -1)) {
			attackBox.x = hitbox.x - hitbox.width - (int)(7 * Game.SCALE);
		}
		
		attackBox.y = hitbox.y + (int)(Game.SCALE * 3);
		
	}

	private void updateHealthBar() {
		healthWidth = (int)((currentHealth / (float)maxHealth) * healthBarWidth);
		
	}
	
	private void updateManaBar() {
		manaWidth = (int)((manaValue / (float)manaMaxValue) * manaBarWidth);
		
		manaGrowTick++;
		if(manaGrowTick >= manaGrowSpeed) {
			manaGrowTick = 0;
			changeMana(1);
		}
	}

	public void render(Graphics g, int lvlOffset) {
		g.drawImage(animations[state][aniIndex], (int)(hitbox.x - xDrawOffset) - lvlOffset + flipX, (int)(hitbox.y - yDrawOffset), width * flipW, height, null);
//		drawHitbox(g, lvlOffset);
//		drawAttackBox(g, lvlOffset);
		drawUI(g);
	}

	private void drawUI(Graphics g) {
		//UI
		g.drawImage(statusBarImg, statusBarX, statusBarY, statusBarWidth, statusBarHeight, null);
		
		//HEALTH BAR
		g.setColor(new Color(220, 73, 73));
		g.fillRect(healthBarXStart + statusBarX, healthBarYStart + statusBarY, healthWidth, healthBarHeight);
		
		//MANA BAR
		g.setColor(new Color(87, 87, 231));
		g.fillRect(manaBarXStart+statusBarX, manaBarYStart + statusBarY, manaWidth, manaBarHeight);
	}

	private void updateAnimationTick() {

		aniTick++;
		if(aniTick >= ANI_SPEED) {
			aniTick = 0;
			aniIndex++;
			if (aniIndex>= GetSpriteAmount(state)) {
				aniIndex = 0;
				attacking = false;
				attackChecked = false;
				if (state == HIT) {
					newState(IDLE);
					airSpeed = 0f;
					if (!IsFloor(hitbox, 0, lvlData))
						inAir = true;
				}
			}
		}
		
	}

	private void setAnimation() {
		int startAni = state;
		
		if (state == HIT)
			return;
		
		if(moving) {
			state = RUNNING;
		} else {
			state = IDLE;
		}
		
		if(inAir) {
			if(airSpeed <0)
				state = JUMP;
			else
				state = FALLING;
		}
		
		if(specialAttackActive) {
			state = ATTACK_1;
			aniIndex = 1;
			aniTick = 0;
			return;
		}
		
		if(attacking) {
			state = ATTACK_1;
			if(startAni != ATTACK_1) {
				aniIndex = 0;
				aniTick = 0;
				return;
			}
		}
		
		if(startAni != state) {
			resetAniTick();
		}
	}

	private void resetAniTick() {
		aniTick = 0;
		aniIndex = 0;
	}

	private void updatePos() {
		moving = false;
		
		if(jump)
			jump();
		
		if(!inAir)
			if(!specialAttackActive)
					if((!left && !right) || (left && right))
						return;
		
		float xSpeed = 0;
		
		if(left && !right) {
			xSpeed = -walkSpeed;
			flipX = (int)(18 * Game.SCALE);
			flipW = -1;
		}
		
		if(right && !left) {
			xSpeed = walkSpeed;
			flipX = 0;
			flipW = 1;
		}
		
		if(specialAttackActive) {
			if((!left && !right) || (left && right)) {
					if(flipW == -1)
						xSpeed = -walkSpeed;
					else
						xSpeed = walkSpeed;
			}
			
			xSpeed *= 3;
		}
		
		if(!inAir) 
			if(!IsEntityOnFloor(hitbox, lvlData))
				inAir = true;
		
		if(inAir && !specialAttackActive) {
			
			if(CanMoveHere(hitbox.x, hitbox.y + airSpeed, hitbox.width, hitbox.height, lvlData)) {
				hitbox.y += airSpeed;
				airSpeed += GRAVITY;
				updateXPos(xSpeed);
			} else {
				hitbox.y = GetEntityYPosUnderRoofOrAboveFloor(hitbox, airSpeed);
				if(airSpeed > 0)
					resetInAir();
				else
					airSpeed = fallSpeedAfterCollision;
				updateXPos(xSpeed);
			}
			
		}else {
			updateXPos(xSpeed);
		}
		moving = true;
		
	}
	
	private void jump() {
		if(inAir)
			return;
		playing.getGame().getAudioPlayer().playEffect(AudioPlayer.JUMP);
		inAir = true;
		airSpeed = jumpSpeed;
		
	}

	private void resetInAir() {
		inAir = false;
		airSpeed = 0;
	}

	private void updateXPos(float xSpeed) {
		if(CanMoveHere(hitbox.x+xSpeed, hitbox.y, hitbox.width, hitbox.height, lvlData)) {
			hitbox.x += xSpeed;
		} else {
			hitbox.x = GetEntityXPosNextToWall(hitbox, xSpeed);
			if(specialAttackActive) {
				specialAttackActive = false;
				specialAttackTick = 0;
			}
		}
	}

	public void changeHealth(int value) {
		if (value < 0) {
			if (state == HIT)
				return;
			else
				newState(HIT);
		}
		
		currentHealth += value;
		
		if(currentHealth <=0) {
			currentHealth = 0;
		}else if(currentHealth >= maxHealth)
			currentHealth = maxHealth;
	}
	
	public void changeMana(int value) {
		manaValue += value;
		
		if(manaValue >= manaMaxValue) {
			manaValue = manaMaxValue;
		}else if(manaValue <= 0)
			manaValue = 0;
	}
	
	public void kill() {
		currentHealth = 0;
		
	}
	
	private void LoadAnimations() {
		
			BufferedImage img = LoadSave.GetSpriteAtlas(LoadSave.PLAYER_ATLAS);
			
			animations = new BufferedImage[9][5];
			
			for(int j = 0; j < animations.length; j++)
				for(int i = 0; i < animations[j].length; i++)
					animations[j][i] = img.getSubimage(i*20, j*18, 20, 18);
			
			statusBarImg = LoadSave.GetSpriteAtlas(LoadSave.STATUS_BAR);
	}

	public void loadLvlData(int[][] lvlData) {
		this.lvlData = lvlData;
		if(!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
	}
	
	public void resetDirBooleans() {
		left = false;
		right = false;
	}
	
	public void setAttacking(boolean attacking) {
		this.attacking = attacking;
	}
	
	public boolean isLeft() {
		return left;
	}

	public void setLeft(boolean left) {
		this.left = left;
	}

	public boolean isRight() {
		return right;
	}

	public void setRight(boolean right) {
		this.right = right;
	}
	
	public void setJump(boolean jump) {
		this.jump = jump;
	}

	public void resetAll() {
		resetDirBooleans();
		inAir = false;
		attacking = false;
		moving = false;
		airSpeed = 0f;
		state = IDLE;
		currentHealth = maxHealth;
		hitbox.x = x;
		hitbox.y = y;
		resetAttackBox();
		
		if(!IsEntityOnFloor(hitbox, lvlData))
			inAir = true;
		
	}
	
	private void resetAttackBox() {
		if(flipW == 1)	{
			attackBox.x = hitbox.x + hitbox.width + (int)(2 * Game.SCALE);
		} else {
			attackBox.x = hitbox.x - hitbox.width - (int)(7 * Game.SCALE);
		}
	}
	
	public int getTileY() {
		return tileY;
	}

	public void specialAttack() {
		if(specialAttackActive)
			return;
		if(manaValue>=60) {
			specialAttackActive = true;
			changeMana(-60);
		}
		
	}
	
}
