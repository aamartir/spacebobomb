package com.space;

import java.awt.Color;
import java.util.Random;

import javax.swing.ImageIcon;
import com.ship.effects.Explosion;
import spaceGame.SpaceObject;

public class Asteroid extends SpaceObject
{
	private int dmg;
	private int innerAsteroids;
	private int lvl;
	private static Random randomGenerator;
	
	public static final String PATH 					= "/resources/";
	public static final double ASTEROID_MAX_VEL 		= 0.3;
	public static final double ASTEROID_MAX_ROTATION 	= 0.5;
	
	public Asteroid(String imgFilename, int x, int y, double vel, double angle, double rateDeg, int dmg, int lvl)
	{
		super(PATH, imgFilename, x, y, vel, angle);
		
		super.setRotationRateDeg(rateDeg);
		super.getExplObj().setColor(Color.gray);
		
		randomGenerator = new Random();

		this.dmg = dmg;
		this.lvl = lvl;
		
		if(lvl > 1)
			this.innerAsteroids = randomGenerator.nextInt(3) + 2;
		else
			this.innerAsteroids = 0;
	}
	
	public int getAsteroidDmg()
	{
		return dmg;
	}
	
	public int getAsteroidLvl()
	{
		return this.lvl;
	}
	
	public void setAsteroidLvl(int val)
	{
		this.lvl = val;
	}
	
	public int getInnerAsteroids()
	{
		return innerAsteroids;
	}
	
	public void setInnerAsteroids(int val)
	{
		this.innerAsteroids = val;
	}
}
