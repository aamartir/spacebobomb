package com.space;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;

import spaceGame.Game;

public class StarField
{
	public static final double MAX_DISTANCE  = 5.0;
	public static final double STAR_MAX_SIZE = 4.0;
	
	private static ArrayList<Star> starArr;
	private static Random randomGenerator;
	private static int maxStarsInView;
	
	public StarField( int maxStars, int minX, int minY, int width, int height )
	{
		starArr = new ArrayList<Star>();
		randomGenerator = new Random();
		maxStarsInView = maxStars;

		for( int i = 0; i < maxStarsInView; i++ )
		{
			newStar( randomGenerator.nextDouble() * width + minX,
					 randomGenerator.nextDouble() * height + minY );
		}
	}
	
	public void addStar( Star star )
	{
		starArr.add( star );
	}
	
	public void newStar( double x, double y )
	{
		double distance = randomGenerator.nextDouble() * MAX_DISTANCE;
		double size = STAR_MAX_SIZE * Math.exp(-(distance*distance));
		
		addStar( new Star(x, y, distance, size) );
	}
	
	public void moveStarField( double velX, double velY )
	{
		for( Star star : starArr )
		{
			star.incrStarPosition( 5.0*velX*Math.exp(-star.getStarDistance()),
					               5.0*velY*Math.exp(-star.getStarDistance()) );
		}
	}
		
	public void drawStarField( Graphics g )
	{
		for( Star star : starArr )
		{
			if( !isWithinBoundaries(star) )
				continue;
			
			star.drawStar( (Graphics2D) g );
		}
	}
	
	private boolean isWithinBoundaries( Star aStar )
	{
		double x = aStar.getPosX();
		double y = aStar.getPosY();

		if( x > 0 && x < Game.screenWidth && y > 0 && y < Game.screenHeight )
			return true;
		
		return false;
	}
}
