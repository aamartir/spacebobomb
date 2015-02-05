package spaceGame;

import java.awt.Color;

public class PlayerShip extends SpaceShip
{	
	public PlayerShip( int x, int y )
	{
		super( SpaceShip.SPACESHIP_01, SpaceObject.PLAYERSHIP_OBJ_TYPE, x, y, 0, 0, 0, SpaceShip.SPACESHIP_MASS );
	}
	
	/*
	public void increaseLevel()
	{
		lvl++;
		lastLvlXP = nextLvlXP;
		nextLvlXP *= 2;
		
		//super.newMessage("LEVEL UP!", new Color(0, 0, 255));
		//super.incrementNumOfMissiles();
		//super.increaseMaxLife(10);
	}
	*/
	
	public void addXP(int val)
	{
		//xp += val;
		//super.newMessage("+" + val + " XP", new Color(0, 255, 0));
		
		//if(xp > getNextLvlXP()) // Every 100 xp points, player increases level
		//	increaseLevel();
	}

	private void KILL_SOUND( int counter )
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
