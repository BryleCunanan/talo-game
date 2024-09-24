package utils;

import main.Game;

public class Constants {
	
	public static final float GRAVITY = 0.04f * Game.SCALE;
	public static final int ANI_SPEED = 25;
	
	public static class Projectiles {
		public static final int TOTEM_BALL_DEFAULT_WIDTH = 15;
		public static final int TOTEM_BALL_DEFAULT_HEIGHT = 15;
		
		public static final int TOTEM_BALL_WIDTH = (int)(Game.SCALE * TOTEM_BALL_DEFAULT_WIDTH );
		public static final int TOTEM_BALL_HEIGHT = (int)(Game.SCALE * TOTEM_BALL_DEFAULT_HEIGHT );
		public static final float SPEED = 1.2f * Game.SCALE;
	}
	
	public static class ObjectConstants {
		public static final int RED_POTION = 0;
		public static final int BLUE_POTION = 1;
		public static final int BARREL = 2;
		public static final int BOX = 3;
		public static final int SPIKE = 4;
		public static final int TOTEM_LEFT = 5;
		public static final int TOTEM_RIGHT = 6;
		
		public static final int RED_POTION_VALUE = 15;
		public static final int BLUE_POTION_VALUE = 40;
		
		public static final int CONTAINER_WIDTH_DEFAULT = 40;
		public static final int CONTAINER_HEIGHT_DEFAULT = 30;
		public static final int CONTAINER_WIDTH = (int)(CONTAINER_WIDTH_DEFAULT * Game.SCALE);
		public static final int CONTAINER_HEIGHT = (int)(CONTAINER_HEIGHT_DEFAULT * Game.SCALE);
		
		public static final int POTION_WIDTH_DEFAULT = 12;
		public static final int POTION_HEIGHT_DEFAULT = 16;
		public static final int POTION_WIDTH = (int)(POTION_WIDTH_DEFAULT * Game.SCALE);
		public static final int POTION_HEIGHT = (int)(POTION_HEIGHT_DEFAULT * Game.SCALE);
		
		public static final int SPIKE_WIDTH_DEFAULT = 32;
		public static final int SPIKE_HEIGHT_DEFAULT = 32;
		public static final int SPIKE_WIDTH = (int)(SPIKE_WIDTH_DEFAULT * Game.SCALE);
		public static final int SPIKE_HEIGHT = (int)(SPIKE_HEIGHT_DEFAULT * Game.SCALE);
		
		public static final int TOTEM_WIDTH_DEFAULT = 32;
		public static final int TOTEM_HEIGHT_DEFAULT = 32;
		public static final int TOTEM_WIDTH = (int)(TOTEM_WIDTH_DEFAULT * Game.SCALE);
		public static final int TOTEM_HEIGHT = (int)(TOTEM_HEIGHT_DEFAULT * Game.SCALE);
		
		public static int GetSpriteAmount(int object_type) {
			switch (object_type) {
			case RED_POTION, BLUE_POTION:
				return 7;
			case BARREL, BOX:
				return 8;
			case TOTEM_LEFT, TOTEM_RIGHT:
				return 6;
			}
			
			return 1;
		}
	}
	
	public static class EnemyConstants {
		public static final int SWEEPER = 0;
		
		public static final int IDLE = 0;
		public static final int RUNNING = 1;
		public static final int ATTACK = 2;
		public static final int HIT = 3;
		public static final int DEAD = 4;
		
		public static final int SWEEPER_WIDTH_DEFAULT = 132;
		public static final int SWEEPER_HEIGHT_DEFAULT = 50;
		public static final int SWEEPER_WIDTH = (int)(SWEEPER_WIDTH_DEFAULT   * Game.SCALE);
		public static final int SWEEPER_HEIGHT = (int)(SWEEPER_HEIGHT_DEFAULT  * Game.SCALE);
		
		public static final int SWEEPER_DRAWOFFSET_X = (int)(31.5 * Game.SCALE);
		public static final int SWEEPER_DRAWOFFSET_Y = (int)(15 * Game.SCALE);
		
		public static int GetSpriteAmount(int enemy_type, int enemy_state) {
			
			switch(enemy_type) {
			case SWEEPER:
				switch(enemy_state) {
				case IDLE:
					return 6;
				case RUNNING:
					return 8;
				case ATTACK:
					return 10;
				case HIT:
					return 2;
				case DEAD:
					return 5;
				}
			}
			
			return 0;
			
		}
		
		public static int GetMaxHealth(int enemy_type) {
			switch (enemy_type) {
			case SWEEPER:
				return 50;
			default:
				return 1;
			}
		}
		
		public static int GetEnemyDamage(int enemy_type) {
			switch (enemy_type) {
			case SWEEPER:
				return 15;
			default:
				return 0;
			}
		}
	}
	
	public static class Environment {
		public static final int CLOUD_1_WIDTH_DEFAULT = 768;
		public static final int CLOUD_1_HEIGHT_DEFAULT = 448;
		public static final int CLOUD_1_WIDTH = (int)(CLOUD_1_WIDTH_DEFAULT * Game.SCALE);
		public static final int CLOUD_1_HEIGHT = (int)(CLOUD_1_HEIGHT_DEFAULT * Game.SCALE);
		
		public static final int CLOUD_2_WIDTH_DEFAULT = 717;
		public static final int CLOUD_2_HEIGHT_DEFAULT = 448;
		public static final int CLOUD_2_WIDTH = (int)(CLOUD_2_WIDTH_DEFAULT * Game.SCALE);
		public static final int CLOUD_2_HEIGHT = (int)(CLOUD_2_HEIGHT_DEFAULT * Game.SCALE);
		
		public static final int CLOUD_3_WIDTH_DEFAULT = 717;
		public static final int CLOUD_3_HEIGHT_DEFAULT = 448;
		public static final int CLOUD_3_WIDTH = (int)(CLOUD_3_WIDTH_DEFAULT * Game.SCALE);
		public static final int CLOUD_3_HEIGHT = (int)(CLOUD_3_HEIGHT_DEFAULT * Game.SCALE);
	}
	public static class UI {
		public static class Buttons {
			public static final int B_WIDTH_DEFAULT = 140;
			public static final int B_HEIGHT_DEFAULT = 56;
			public static final int B_WIDTH = (int) (B_WIDTH_DEFAULT * Game.SCALE);
			public static final int B_HEIGHT = (int) (B_HEIGHT_DEFAULT * Game.SCALE);
		}
		
		public static class PauseButtons {
			public static final int SOUND_SIZE_DEFAULT = 42;
			public static final int SOUND_SIZE = (int)(SOUND_SIZE_DEFAULT*Game.SCALE);
		}
		
		public static class URMButtons {
			public static final int URM_DEFAULT_SIZE = 56;
			public static final int URM_SIZE = (int)(URM_DEFAULT_SIZE * Game.SCALE);
		}
		
		public static class VolumeButtons {
			public static final int VOLUME_DEFAULT_WIDTH = 28;
			public static final int VOLUME_DEFAULT_HEIGHT = 44;
			public static final int SLIDER_DEFAULT_WIDTH = 215;
			
			public static final int VOLUME_WIDTH = (int)(VOLUME_DEFAULT_WIDTH * Game.SCALE);
			public static final int VOLUME_HEIGHT = (int)(VOLUME_DEFAULT_HEIGHT * Game.SCALE);
			public static final int SLIDER_WIDTH = (int)(SLIDER_DEFAULT_WIDTH * Game.SCALE);
		}
	}
	
	public static class Directions {
		public static final int LEFT = 0;
		public static final int UP = 1;
		public static final int RIGHT = 2;
		public static final int DOWN = 3;
	}
	
	public static class PlayerConstants {
		public static final int IDLE = 0;
		public static final int RUNNING = 1;
		public static final int JUMP = 2;
		public static final int FALLING = 3;
		public static final int GROUND = 4;
		public static final int HIT = 5;
		public static final int ATTACK_1= 6;
		public static final int DEAD = 7;
		
		
		public static int GetSpriteAmount(int player_action) {
			
			switch(player_action) {
			
			case RUNNING:
				return 4;
			case IDLE:
			case DEAD:
				return 5;
			case JUMP:
			case FALLING:
			case GROUND:
				return 1;
			case HIT:
				return 2;
			case ATTACK_1:
				return 3;
			default:
				return 1;
			
			}
		}
	}
	
}
