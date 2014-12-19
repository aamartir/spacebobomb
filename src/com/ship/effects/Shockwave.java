package com.ship.effects;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

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
	private double rateOfExpansion;
	
	private double alpha;
	private Color  shockwaveColor;
	private double dissipationRate;
	private double shockwaveDamage;
	
	private static ArrayList<Shockwave> shockwaves;
	
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
		visible 		= true;
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
	
	public static void updateShockwaves( double dt )
	{
		if( shockwaves != null )
		{
			for( Shockwave shock : shockwaves )
			{
				if( !shock.isVisible() )
					continue;
				
				shock.currRadius += shock.rateOfExpansion * dt;
				if(shock.currRadius >= shock.endRadius )
				{
					// As soon as the max radius has been reaches, wave begins to dissipate/disappear 
					shock.alpha -= shock.dissipationRate * dt;
					
					if( shock.alpha <= 0 )
					{
						shock.alpha = 0;
						shock.visible = false;
					}
				}
				
				shock.rateOfExpansion -= EXPANSION_DECELERATION * dt;
				if( shock.rateOfExpansion <= 0 )
					shock.rateOfExpansion = 0;
			}
		}
	}
	
	public static void newShockwave( double posX, double posY )
	{
		if( shockwaves == null )
			shockwaves = new ArrayList<Shockwave>();
		
		shockwaves.add( new Shockwave(posX, posY) );
	}
	
	public static void drawShockwaves( Graphics g )
	{
		Shape s;
		
		if( shockwaves != null )
		{
			for( int i = 0; i < shockwaves.size(); i++ )
			{
				if( !shockwaves.get(i).isVisible() )
				{
					shockwaves.remove(i);
					continue;
				}
				
				s = new Ellipse2D.Double( shockwaves.get(i).posX - shockwaves.get(i).currRadius/2.0, 
						                  shockwaves.get(i).posY - shockwaves.get(i).currRadius/2.0, 
						                  shockwaves.get(i).currRadius, shockwaves.get(i).currRadius );
				
				((Graphics2D)g).setColor( new Color(shockwaves.get(i).shockwaveColor.getRed(), 
													shockwaves.get(i).shockwaveColor.getGreen(), 
													shockwaves.get(i).shockwaveColor.getBlue(), 
						                            (int) shockwaves.get(i).alpha) );
				
				((Graphics2D)g).fill( s );
			}
		}
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
