package com.weapons;

import java.awt.Color;
import javax.swing.ImageIcon;

import com.ship.effects.SpaceShipTrail;

import spaceGame.AlienShip;
import spaceGame.SpaceObject;

public class SeekMissile extends Weapon implements Runnable
{
	private SpaceObject target;
	private SpaceShipTrail trail;
	private Thread T;
	
	public SeekMissile(int x, int y, double vel, double angle, SpaceObject target)
	{
		super(Weapon.SEEK_MISSILE_IMG, x, y, vel + Weapon.SEEK_MISSILE_VEL, angle, Weapon.SEEK_MISSILE_DMG);
		super.setMaxTurningRate(Weapon.SEEK_MISSILE_TURN_RATE);
		
		super.getExplObj().setColor(Color.red);
		super.getExplObj().setEndRadius(Weapon.SEEK_MISSILE_EXPL_RAD);

		this.target = target;
		trail = new SpaceShipTrail(3);
		
		T = new Thread(this);
		T.start();
	}
	
	public SpaceShipTrail getTrailObject()
	{
		return trail;
	}
	
	public void move()
	{
		// Set angle
		if(target != null)
			super.rotate(AlienShip.getClosestAngle(this.getAngle(), this.angleWithRespectTo(target)));
		
		super.move();
	}

	@Override
	public void run() 
	{
		while(!this.isDestroyed() && this.isVisible())
		{
			try
			{
				trail.addTrailComponent(Color.gray, 
								       (int) (getPosX() + getImgWidth()/2 - 5 - getImgHeight()/2.5*Math.cos(getAngleRad())),
								       (int) (getPosY() + getImgHeight()/2 - 5 - getImgHeight()/2.5*Math.sin(getAngleRad())));
				
				Thread.sleep(40);
			}
			catch(InterruptedException e)
			{
				
			}
		}
	}
}
