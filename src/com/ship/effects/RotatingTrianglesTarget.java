package com.ship.effects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

import spaceGame.SpaceObject;

public class RotatingTrianglesTarget implements Runnable
{
	public static final String RED_TARGET = "targetRed.png";
	public static final String BLUE_TARGET = "targetBlue.png";
	public static final String GREEN_TARGET = "targetGreen.png";

	private ImageIcon img;
	private double angle;
	private boolean runAnimation;
	
	private static AffineTransform transf;
	private Thread T;
	
	public RotatingTrianglesTarget()
	{
		runAnimation = false;
		angle = 0;
	}
	
	public void setTargetImage( String imgString )
	{
		try
		{
			img = SpaceObject.getImgResource( imgString );
		}
		catch(Exception e)
		{
			System.out.println( "Target image does not exist" );
		}
	}
	
	public Image getTargetImg()
	{
		return img.getImage();
	}
	
	public void runAnimation()
	{
		angle = 0;
		runAnimation = true;
		T = new Thread( this );
		T.start();
	}
	
	public void drawTarget( Graphics2D g2d, AffineTransform lastTransform, double x, double y )
	{
		transf = new AffineTransform();
		
		g2d.rotate( Math.toRadians(angle), 
				    x,// - img.getImage().getWidth(null)/2.0, 
				    y );// - img.getImage().getHeight(null)/2.0 ); 
		
		transf.setToIdentity();
		transf.translate( x - img.getImage().getWidth(null)/2.0, 
				          y - img.getImage().getHeight(null)/2.0 );
		
		// draw image
		g2d.drawImage( getTargetImg(), transf, null );
		
		// Restore transform
		g2d.setTransform( lastTransform );
	}
	
	public boolean isActive()
	{
		return T.isAlive();
	}
	
	public void stopAnimation()
	{
		runAnimation = false;
		T.stop();
	}
	
	public void run()
	{
		while( runAnimation )
		{
			try
			{
				angle = ++angle % 360;
				Thread.sleep(30);
			}
			catch(Exception e)
			{
				
			}
		}
	}
}
