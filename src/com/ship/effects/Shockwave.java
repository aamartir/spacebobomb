package com.ship.effects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;

import spaceGame.EnemyShip;
import spaceGame.SpaceShip;

public class Shockwave
{
	public static final double MAX_RATE_OF_EXPANSION  = 0.15;
	public static final double EXPANSION_DECELERATION = 0.00005;
	public static final double MIN_EXPANSION_RADIUS   = 0.0;	
	public static final double MAX_EXPANSION_RADIUS   = 5.0;
	public static final double MAX_DISSIPATION_RATE   = 0.05;
	public static final double INITIAL_ALPHA_VALUE    = 150.0;
	public static final double SHOCKWAVE_MAX_DAMAGE   = 20.0; 
	
	private double  posX;
	private double  posY;
	private boolean visible;
	
	private double startRadius;
	private double endRadius;
	private double currRadius;
	private double deltaRadius;
	private double rateOfExpansion;
	
	private double alpha;
	private Color  shockwaveColor;
	private double dissipationRate;
	private double shockwaveDamage;
	
	public Shockwave( double x, double y )
	{
		posX = x;
		posY = y;
		
		alpha           = INITIAL_ALPHA_VALUE;
		startRadius     = MIN_EXPANSION_RADIUS;
		endRadius       = MAX_EXPANSION_RADIUS;
		currRadius      = startRadius;
		rateOfExpansion = MAX_RATE_OF_EXPANSION;
		dissipationRate = MAX_DISSIPATION_RATE;
		shockwaveDamage = SHOCKWAVE_MAX_DAMAGE;
		shockwaveColor  = Color.RED;
	}
	
	public void setShockwaveColor( Color c )
	{
		shockwaveColor = c;
	}
	
	public void setRateOfExpansion( double newVal )
	{
		rateOfExpansion = newVal;
	}
	
	public void setEndRadius( double val )
	{
		endRadius = val;
	}
	
	public void updateExplosionMotion( double dt )
	{
		currRadius += rateOfExpansion * dt;
		if(currRadius >= endRadius )
		{
			// As soon as the max radius has been reaches, wave begins to dissipate/disappear 
			alpha -= dissipationRate * dt;
			
			if( alpha <= 0 )
			{
				alpha = 0;
				visible = false;
			}
		}
		
		rateOfExpansion -= EXPANSION_DECELERATION * dt;
		if( rateOfExpansion <= 0 )
			rateOfExpansion = 0;
	}
	
	public void drawExplotion( Graphics2D g2d )
	{
		Shape s = new Ellipse2D.Double( posX - currRadius/2.0, posY - currRadius/2.0, currRadius, currRadius );
		
		g2d.setColor( new Color(shockwaveColor.getRed(), 
								shockwaveColor.getGreen(), 
								shockwaveColor.getBlue(), 
				                (int) alpha) );
		
		g2d.fill( s );
	}
		
	public void setVisible(boolean visible)
	{
		this.visible = visible;
	}
	
	public boolean isVisible()
	{
		return visible;
	}

	public double getPosX() 
	{
		return posX;
	}

	public void setPosX(double posX) 
	{
		this.posX = posX;
	}

	public double getPosY() 
	{
		return posY;
	}

	public void setPosY(double posY) 
	{
		this.posY = posY;
	}
}
