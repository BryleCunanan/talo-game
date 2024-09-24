package objects;

import main.Game;

public class Totem extends GameObject{

	private int tileY;
	
	public Totem(int x, int y, int objType) {
		super(x, y, objType);
		tileY = y / Game.TILES_SIZE;
		initHitbox(32,32);
	}

	public void update() {
		if(doAnimation)
			updateAnimationTick();
	}
	
	public int getTileY() {
		return tileY;
	}
}
