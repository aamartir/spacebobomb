package com.weapons;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import javax.swing.ImageIcon;

import com.ship.effects.Explosion;

import spaceGame.SpaceObject;

public class Missile extends Weapon
{
	public Missile(int x, int y, double vel, double angle)
	{
		super(Weapon.MISSILE_IMG, x, y, vel + Weapon.MISSILE_VEL, angle, Weapon.MISSILE_DMG);
		
		super.getExplObj().setColor(Color.blue);
		super.getExplObj().setEndRadius(Weapon.MISSILE_EXPL_RAD);
	}		
}
