package com.space;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.HashMap;

import spaceGame.SpaceObject;

public class Asteroid extends SpaceObject
{
	public static final double ASTEROID_MAX_SPEED 		 = 0.05;
	public static final double ASTEROID_MAX_TURNING_RATE = 0.4;
	public static final double ASTEROID_COLLISION_DAMAGE = 50.0;
	public static final double ASTEROID_MASS             = 1.0;
			
	public static final String ASTEROID_01 = "asteroid01.png";
	public static final String ASTEROID_02 = "asteroid02.png";
	public static final String ASTEROID_03 = "asteroid03.png";
	public static final String ASTEROID_04 = "asteroid04.png";
	
	public Asteroid( String asteroidType, double x, double y, double v_x, double v_y, double initialAngle, double rotationDegPerSec, double mass )
	{
		// Super constructor
		super( asteroidType, SpaceObject.ASTEROID_OBJ_TYPE, x, y, v_x, v_y, initialAngle, rotationDegPerSec, ASTEROID_MASS );
		super.setRotationRateDegPerSec( rotationDegPerSec );
	}
	
	public void updateAsteroidMotion( double dt )
	{
		super.updateSpaceObjectMotion( ASTEROID_MAX_SPEED, ASTEROID_MAX_TURNING_RATE, 0, 0, dt );
	}
	
	public void drawAsteroid( Graphics g )
	{
		super.drawSpaceObject( g );
	}
	
	public static Asteroid createRandomAsteroid( String asteroidType,
			                                     double minX, double minY,
			                                     double maxX, double maxY )
	{
		double mass = SpaceObject.randGenerator.nextDouble()*Asteroid.ASTEROID_MASS;
		
		double posX = SpaceObject.randGenerator.nextDouble()*(maxX - minX) + minX;
		double posY = SpaceObject.randGenerator.nextDouble()*(maxY - minY) + minY;
		
		double velX = SpaceObject.randGenerator.nextDouble()*(2*ASTEROID_MAX_SPEED) - ASTEROID_MAX_SPEED;
		double velY = SpaceObject.randGenerator.nextDouble()*(2*ASTEROID_MAX_SPEED) - ASTEROID_MAX_SPEED;
		
		double initialAngle = SpaceObject.randGenerator.nextDouble() * 180;
		double rotationDegPerSec = SpaceObject.randGenerator.nextDouble() * (2*ASTEROID_MAX_TURNING_RATE) - ASTEROID_MAX_TURNING_RATE;
		
		return new Asteroid( asteroidType, posX, posY, velX, velY, initialAngle, rotationDegPerSec, mass );
	}
	
	public static void createAsteroid( ArrayList<Asteroid> arr, 
			                           String asteroidType, 
			                           double x, double y, 
			                           double v_x, double v_y, 
			                           double initialAngle, double rotationDegPerSec, 
			                           double mass )
	{
		arr.add( new Asteroid( asteroidType, x, y, v_x, v_y, initialAngle, rotationDegPerSec, mass) );
	}
	
	//public static void createRandomAsteroid( ArrayList<Asteroid> arr, 
	public static void createRandomAsteroid( HashMap<Integer, SpaceObject> map, 
											 String asteroidType, 
											 double minX, double minY, double maxX, double maxY )
	{
		double posX = SpaceObject.randGenerator.nextDouble()*(maxX - minX) + minX;
		double posY = SpaceObject.randGenerator.nextDouble()*(maxY - minY) + minY;
		
		double velX = SpaceObject.randGenerator.nextDouble()*(2*ASTEROID_MAX_SPEED) - ASTEROID_MAX_SPEED;
		double velY = SpaceObject.randGenerator.nextDouble()*(2*ASTEROID_MAX_SPEED) - ASTEROID_MAX_SPEED;
		
		double initialAngle = SpaceObject.randGenerator.nextDouble() * 180;
		double rotationDegPerSec = SpaceObject.randGenerator.nextDouble() * (2*ASTEROID_MAX_TURNING_RATE) - ASTEROID_MAX_TURNING_RATE;
		
		// Add random asteroid
		//arr.add( new Asteroid(asteroidType, posX, posY, velX, velY, initialAngle, rotationDegPerSec, ASTEROID_MASS) );
		//map.put( new Asteroid(asteroidType, posX, posY, velX, velY, initialAngle, rotationDegPerSec, ASTEROID_MASS) );
	}
}
