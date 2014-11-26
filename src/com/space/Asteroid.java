package com.space;

import java.awt.Graphics;
import java.util.ArrayList;

import spaceGame.SpaceObject;

public class Asteroid extends SpaceObject
{
	public static final double ASTEROID_MAX_SPEED 		 = 0.4;
	public static final double ASTEROID_MAX_TURNING_RATE = 0.4;
	public static final double ASTEROID_MASS             = 1.0;
			
	public static final String ASTEROID_01 = "asteroid01.png";
	public static final String ASTEROID_02 = "asteroid02.png";
	public static final String ASTEROID_03 = "asteroid03.png";
	public static final String ASTEROID_04 = "asteroid04.png";
	
	public Asteroid( String asteroidType, double x, double y, double v_x, double v_y, double initialAngle, double rotationDegPerSec, double mass )
	{
		// Super constructor
		super( asteroidType, x, y, v_x, v_y, initialAngle, rotationDegPerSec, ASTEROID_MASS );
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
	
	public static void createAsteroid( ArrayList<Asteroid> asteroidArr, String asteroidType, double x, double y, double v_x, double v_y, double initialAngle, double rotationDegPerSec, double mass )
	{
		asteroidArr.add( new Asteroid( asteroidType, x, y, v_x, v_y, initialAngle, rotationDegPerSec, mass) );
	}
}
