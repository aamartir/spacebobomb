package com.ship.effects;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;

import javax.swing.ImageIcon;

public class Target implements Runnable
{
	public static final String PATH = "/resources/";
	
	public static final String RED_TARGET = "targetRed.png";
	public static final String BLUE_TARGET = "targetBlue.png";
	public static final String GREEN_TARGET = "targetGreen.png";

	private Image img;
	private double angle;
	private boolean visible;
	
	private static AffineTransform transf;
	private Thread T;
	
	public Target()
	{
		setImage(BLUE_TARGET);
		visible = false;
		angle = 0;
	}
	
	public void setImage(String type)
	{
		try
		{
			img = new ImageIcon(getClass().getResource(PATH + "" + type)).getImage();
		}
		catch(Exception e){}
	}
	
	public void draw(Graphics2D g2d, int x, int y, int shipWidth, int shipHeight)
	{
		if(visible)
		{
			int dx = Math.abs(img.getWidth(null) - shipWidth)/2;
			int dy = Math.abs(img.getHeight(null) - shipHeight)/2;
			
			transf = new AffineTransform();
			transf.rotate(angle*Math.PI/180, x + shipWidth/2, y + shipHeight/2);
			transf.translate(x - dx, y - dy);

			g2d.drawImage(img, transf, null);
		}
	}
	
	public void activate()
	{
		if(!visible)
		{
			visible = true;
			
			T = new Thread(this);
			T.start();
		}
	}
	
	public boolean isActive()
	{
		return visible && T.isAlive();
	}
	
	public void rest()
	{
		visible = false;
		angle = 0;
	}
	
	public void run()
	{
		while(visible)
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
