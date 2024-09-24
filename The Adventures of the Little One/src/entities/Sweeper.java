package entities;

import static utils.Constants.EnemyConstants.*;

import java.awt.geom.Rectangle2D;

import static utils.Constants.Directions.*;
import main.Game;
public class Sweeper extends Enemy{

	// ATTACKBOX
	private int attackBoxOffsetX;
	
	public Sweeper(float x, float y) {
		super(x, y, SWEEPER_WIDTH, SWEEPER_HEIGHT, SWEEPER);
		initHitbox(18, 30);
		initAttackBox();
	}
	

	private void initAttackBox() {
		attackBox = new Rectangle2D.Float(x, y, (int)(47 * Game.SCALE), (int)hitbox.height);
		attackBoxOffsetX = (int)(Game.SCALE * 30);
		
	}



	public void update(int[][] lvlData,Player player) {
		updateBehavior(lvlData, player);
		updateAnimationTick();
		updateAttackBox();
		
	}
	
	private void updateAttackBox() {
		if(walkDir == RIGHT) {
			attackBox.x = hitbox.x + hitbox.width + (int)(2 * Game.SCALE);
		}else if(walkDir == LEFT) {
			attackBox.x = hitbox.x - hitbox.width - (int)(32 * Game.SCALE);
		}
		
		attackBox.y = hitbox.y + (int)(Game.SCALE);
		
	}


	private void updateBehavior(int[][] lvlData, Player player) {
		if(firstUpdate)
			firstUpdateCheck(lvlData);
		
		if(inAir)
			updateInAir(lvlData);
		else {
			switch (state) {
			case IDLE:
				newState(RUNNING);
				break;
			case RUNNING:
				if(canSeePlayer(lvlData, player)) {
					turnTowardsPlayer(player);
					System.out.println("Enemy Saw player!");
				if(IsPlayerCloseForAttack(player))
					newState(ATTACK);
				}
				
				move(lvlData);
				break;
			case ATTACK:
				if(aniIndex == 0)
					attackChecked = false;
				
				if(aniIndex == 5 && !attackChecked)
					checkPlayerHit(attackBox,player);
				break;
			case HIT:
				break;
			}
		}
	}
	
	
	public int flipX() {
		if(walkDir == RIGHT)
			return 0;
		else
			return (int)(81 * Game.SCALE);
	}
	
	public int flipW() {
		if(walkDir == LEFT)
			return -1;
		else
			return 1;
	}
}
