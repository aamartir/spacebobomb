package spaceGame;

import java.awt.Color;
import java.awt.event.KeyEvent;

public class PlayerShip extends SpaceShip
{	
	private long xp;
	private long nextLvlXP;
	private long lastLvlXP;
	private int lvl;
	
	private double lastKill_time;
	private double killDt;
	public static final int KILL_TIME_SEC = 18;
	private int COMBO_COUNT;
	
	public PlayerShip(int x, int y)
	{
		super(SpaceShip.PATH, SpaceShip.SPACESHIP_BLUE, x, y, 0, -90);

		lvl = 0;
		xp = 1;
		nextLvlXP = 100;
		lastLvlXP = 0;
		
		// Keeps count of combo kills
		lastKill_time = System.currentTimeMillis()/1000.0;
		COMBO_COUNT = 0;
	}
	
	public long getXP()
	{
		return xp;
	}
	
	public long getNextLvlXP()
	{
		return nextLvlXP;
	}
	
	public long getLastLvlXP()
	{
		return lastLvlXP;
	}
	
	public int getLevel()
	{
		return lvl;
	}
	
	public void increaseLevel()
	{
		lvl++;
		lastLvlXP = nextLvlXP;
		nextLvlXP *= 2;
		
		super.newMessage("LEVEL UP!", new Color(0, 0, 255));
		super.incrementNumOfMissiles();
		super.increaseMaxLife(10);
	}
	
	public void addXP(int val)
	{
		xp += val;
		super.newMessage("+" + val + " XP", new Color(0, 255, 0));
		
		if(xp > getNextLvlXP()) // Every 100 xp points, player increases level
			increaseLevel();
	}
	
	public void keyPressed(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_A)
			super.setRotation(-super.getMaxTurningRate());
		if(key == KeyEvent.VK_D)
			super.setRotation(super.getMaxTurningRate());
		if(key == KeyEvent.VK_W)
			super.setVelocity(SHIP_MAX_VEL);
		if(key == KeyEvent.VK_S)
			super.setVelocity(-SHIP_MAX_VEL);
		if(key == KeyEvent.VK_SPACE)
			super.fireMissile();
	}
	
	public void keyReleased(KeyEvent e)
	{
		int key = e.getKeyCode();
		
		switch(key)
		{
			/*case KeyEvent.VK_B:
				super.stopBoosters();
				break;*/
			case KeyEvent.VK_A:
				super.setRotation(0);
			case KeyEvent.VK_D:
				super.setRotation(0);
				break;
			case KeyEvent.VK_W:
				super.setVelocity(0); break;
			case KeyEvent.VK_S:
				super.setVelocity(0); break;
		}
	}
	
	public void updateLastKillTime(final double val_sec)
	{
		killDt = val_sec - lastKill_time;
		lastKill_time = val_sec;

		if(killDt <= KILL_TIME_SEC)
		{
			COMBO_COUNT++;
		
			if(COMBO_COUNT >= 2)
				KILL_SOUND(COMBO_COUNT);
		}
		else
			COMBO_COUNT = 0;
	}
	
	public final double getLastKillTime()
	{
		return lastKill_time;
	}
	
	private void KILL_SOUND(int counter)
	{
		switch(counter)
		{
			case 2:
				SFX_Player.playSound(SFX_Player.MALE_SOUND_PATH, SFX_Player.DOUBLE_KILL);
				break;
			case 3:
				SFX_Player.playSound(SFX_Player.MALE_SOUND_PATH, SFX_Player.TRIPLE_KILL);
				break;
			default:
				SFX_Player.playRandomCommentatorSound();
		}
	}
}
