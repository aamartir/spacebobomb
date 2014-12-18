package com.ship.effects;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

public class StatusMessage
{	
	public static final Color   STATUS_MSG_RED_COLOR     = new Color( 255, 0, 0 );
	public static final Color   STATUS_MSG_GREEN_COLOR   = new Color( 0, 255, 0 );
	public static final Color   STATUS_MSG_BLUE_COLOR    = new Color( 0, 0, 255 );
	
	private static final Font   STATUS_MSG_DEFAULT_FONT  =  new Font( "ARIAL", Font.BOLD, 18 );
	
	private static final double STATUS_MSG_VEL           =  0.05;
	private static final double STATUS_MSG_TOTAL_DELTA_Y =  30.0;
	private static final double STATUS_MSG_DELTA_ALPHA   =  0.1;
	
	private static int msgCounter;
	
	private double initialX;
	private double initialY;
	private double posX; // Current x
	private double posY; // Current y
	private String msgString;
	
	private Color  msgColor;
	private double alpha;
	private int    fontSize;
	
	private int    id;
	private boolean completed;
	
	public StatusMessage( double posX, double posY, String msg, Color c )
	{
		this.initialX = posX;
		this.initialY = posY;
		this.posX = posX;
		this.posY = posY;
		
		this.msgString = msg;
		this.msgColor = c;
		this.alpha = 255.0;
		this.id = ++msgCounter;
		this.completed = false;
	}
	
	public int getMessageID()
	{
		return id;
	}
	
	public boolean isCompleted()
	{
		return completed;
	}
	
	public void drawMessage( Graphics g )
	{
		if( !completed && alpha > 0 )
		{
			g.setColor( new Color(msgColor.getRed(), msgColor.getGreen(), msgColor.getBlue(), (int) alpha));
			g.setFont( STATUS_MSG_DEFAULT_FONT );
			g.drawString( msgString, (int)posX, (int)posY );
		}
	}
	
	public void updateStatusMessage( double dt )
	{
		if( posY > (initialY - STATUS_MSG_TOTAL_DELTA_Y) )
			posY -= STATUS_MSG_VEL * dt;
		else if( !completed )
		{
			alpha -= ( STATUS_MSG_DELTA_ALPHA * dt );
			
			// Message should be deleted when alpha reaches 0
			if( alpha <= 0 )
			{
				msgCounter--;
				completed = true;
			}
		}
	}
}
