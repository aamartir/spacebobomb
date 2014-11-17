package com.weapons;

import com.ship.effects.Explosion;
import spaceGame.SpaceObject;

public class Weapon extends SpaceObject
{
	public static final String PATH 			   	  = "/resources/Weapons/";
	
	public static final String MISSILE_IMG 		   	  = "missileBlue.png";
	public static final String PLASMA_BOMB_IMG 	   	  = "plasmaBomb.png";
	public static final String SEEK_MISSILE_IMG    	  = "seekMissile.png";

	public static final int    MISSILE_DMG 		   	  = 10;
	public static final int    PLASMA_BOMB_DMG 	   	  = 60;
	public static final int    SEEK_MISSILE_DMG 	  = 80;

	public static final int    MISSILE_EXPL_RAD 	  = 30;
	public static final int    PLASMA_BOMB_EXPL_RAD   = 70;
	public static final int    SEEK_MISSILE_EXPL_RAD  = 50;
	public static final double SEEK_MISSILE_TURN_RATE = 0.1;

	public static final double MISSILE_VEL 		   	  = 0.4;
	public static final double PLASMA_BOMB_VEL 	   	  = 0.3;
	public static final double SEEK_MISSILE_VEL    	  = 0.25;
	
	private Explosion explObj;
	private int dmg;
	
	public Weapon(String type, int x, int y, double vel, double angle, int dmg)
	{
		super(PATH, type, x, y, vel, angle);
		this.dmg = dmg;
	}
	
	public int getDmg() 
	{
		return dmg;
	}

	public void setDmg(int val) 
	{
		this.dmg = val;
	}
}
