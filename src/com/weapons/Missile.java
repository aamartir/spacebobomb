package com.weapons;

import spaceGame.SpaceShip;

public class Missile extends Weapon
{
	public static final int    MISSILE_DMG 		   	  = 20;
	public static final int    MISSILE_EXPL_RAD 	  = 30;
	public static final double MISSILE_VEL 		   	  = 0.4;
	public static final double MISSILE_DISTANCE       = 800.0;
	
	public Missile( SpaceShip sourceSpaceShip, double posX, double posY, double v_x, double v_y, double initialAngle )
	{
		super( sourceSpaceShip,
			   Weapon.MISSILE_IMG, 
			   SpaceShip.MISSILE_OBJ_TYPE,
			   posX, 
			   posY,  
			   v_x + Math.cos( Math.toRadians(initialAngle) ) * MISSILE_VEL, 
			   v_y + Math.sin( Math.toRadians(initialAngle) ) * MISSILE_VEL,
			   initialAngle, 0,
			   1.0,
			   MISSILE_DMG );
		
		//super.getExplObj().setColor(Color.blue);
		//super.getExplObj().setEndRadius(Weapon.MISSILE_EXPL_RAD);
	}		
}
