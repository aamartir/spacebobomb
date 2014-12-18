package com.weapons;

import spaceGame.SpaceShip;

public class Missile extends Weapon
{
	public Missile( SpaceShip sourceSpaceShip, double posX, double posY, double v_x, double v_y, double initialAngle )
	{
		super( sourceSpaceShip,
			   Weapon.MISSILE_IMG, 
			   posX, posY,  
			   v_x + Math.cos( Math.toRadians(initialAngle) ) * Weapon.MISSILE_VEL, 
			   v_y + Math.sin( Math.toRadians(initialAngle) ) * Weapon.MISSILE_VEL,
			   initialAngle, 0,
			   1.0,
			   Weapon.MISSILE_DMG );
		
		//super.getExplObj().setColor(Color.blue);
		//super.getExplObj().setEndRadius(Weapon.MISSILE_EXPL_RAD);
	}		
}
