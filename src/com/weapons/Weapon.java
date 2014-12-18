package com.weapons;

import Mathematics.Vector2D;

import com.ship.effects.Shockwave;
import spaceGame.SpaceObject;
import spaceGame.SpaceShip;

public class Weapon extends SpaceObject
{
	public static final String MISSILE_IMG 		   	  = "Weapons/missileBlue.png";
	public static final String PLASMA_BOMB_IMG 	   	  = "Weapons/plasmaBomb.png";
	public static final String SEEK_MISSILE_IMG    	  = "Weapons/seekMissile.png";

	// Missiles
	public static final int    MISSILE_DMG 		   	  = 10;
	public static final int    MISSILE_EXPL_RAD 	  = 30;
	public static final double MISSILE_VEL 		   	  = 0.4;
	public static final double DISTANCE_DURATION      = 600.0;
	
	private SpaceShip sourceSpaceShip;
	private Shockwave shockWave;
	private int dmg;
	private double initialX;
	private double initialY;
	private boolean destroyed;
	
	public Weapon( SpaceShip source,
				   String type, 
			       double posX, double posY, 
			       double v_x, double v_y, 
			       double initialAngle, double rotDegPerSec,
			       double mass,
			       int dmg )
	{
		super( type, posX, posY, v_x, v_y, initialAngle, rotDegPerSec, mass );
		this.dmg = dmg;
		this.sourceSpaceShip = source;
		this.destroyed = false;
		
		// keep track of the initial location
		initialX = posX;
		initialY = posY;
	}
	
	public void updateWeaponMotion( double dt )
	{
		if( Vector2D.getDistanceBetween2Points(initialX, initialY, super.getPosX(), super.getPosY() ) < DISTANCE_DURATION )
		{
			super.updateSpaceObjectMotion( MISSILE_VEL, 0, 0, 0, dt );
		}
		else
		{
			// Missile will be removed from array, next time logic function is called (in Game.java)
			destroy();
		}
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
