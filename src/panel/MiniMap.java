package panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Iterator;
import java.util.Map;

import Mathematics.MyMath;

import com.space.Asteroid;

import spaceGame.EnemyShip;
import spaceGame.Game;
import spaceGame.SpaceObject;

public class MiniMap 
{
	private static final int MAP_BORDER = 10;
	private static final int MAP_OBJECT_RADIUS = 2;
	
	private int mapLeftMargin;
	private int mapTopMargin;
	private double scaleX;
	private double scaleY;
	private int mapRadius;
	private int viewportWidth;
	private int viewportHeight;
	private double mapHorizon;
	private int alpha; // Transparency (affects everything drawn to map)
	private Color mapBackgroundColor;
	private Color playerColor;
	private Color spaceshipColor;
	private Color asteroidColor;

	private static Iterator<Map.Entry<Integer, SpaceObject>> it;
	private static SpaceObject obj;
	
	public MiniMap( int viewportWidth, int viewportHeight, 
			        int mapRadius,
			        int alpha )
	{
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
		this.mapRadius = mapRadius;
		mapLeftMargin = viewportWidth - 2*mapRadius - MAP_BORDER;
		mapTopMargin = MAP_BORDER;
		mapHorizon = Math.max( viewportWidth, viewportHeight )/2.0;
		
		// Blueish semi-transparent color
		this.alpha         = alpha;
		mapBackgroundColor = new Color( 0,    50, 200, alpha );
		playerColor        = new Color( 0,   200,   0, alpha );
		spaceshipColor     = new Color( 200,   0,   0, alpha );
		asteroidColor      = new Color( 150, 150, 150, alpha );
	}
	
	// k (percentage increase) is a value [0 to 1]
	public void increaseMapHorizon( double k )
	{
		if( k > 0 && k <= 1 )
			mapHorizon += k*mapHorizon;
	}
	
	public void descreaseMapHorizon( double k )
	{
		if( k > 0 && k <= 1 )
			mapHorizon -= k*mapHorizon;
	}
	
	public double getMapHorizon()
	{
		return mapHorizon;
	}
	
	public void setMapColor( Color newColor )
	{
		mapBackgroundColor = new Color( newColor.getRed(), 
				                        newColor.getGreen(),
				                        newColor.getBlue(), 
				                        alpha );
	}
	
	public void drawMiniMap( Graphics g )
	{
		// Draw background
		g.setColor( mapBackgroundColor );
		g.fillOval( mapLeftMargin, mapTopMargin, 2*mapRadius, 2*mapRadius );
		
		// Draw player ship in the middle
		g.setColor( playerColor );
		g.fillOval( (int)(mapLeftMargin + mapRadius - MAP_OBJECT_RADIUS),
				    (int)(mapTopMargin + mapRadius - MAP_OBJECT_RADIUS), 
				    2*MAP_OBJECT_RADIUS, 2*MAP_OBJECT_RADIUS ); 

		// Iterate through all object in collection
		it = Game.spaceObjects.entrySet().iterator();
		while( it.hasNext() )
		{
			obj = it.next().getValue();
			if( obj.distanceWithRespectTo(Game.getPlayer()) >= mapHorizon ||
				obj.isDestroyed() || !obj.isVisible() )
				continue;
			
			if( obj.getObjectType() == SpaceObject.ENEMYSHIP_OBJ_TYPE )
				g.setColor( spaceshipColor );
			else if( obj.getObjectType() == SpaceObject.ASTEROID_OBJ_TYPE )
				g.setColor( asteroidColor );
			else // Don't draw anything else
				continue;
			
			g.fillOval( (int) MyMath.map(obj.getPosX() - Game.getCamera().getPosX(),
					                    -mapHorizon, mapHorizon, 
					                     mapLeftMargin, mapLeftMargin + mapRadius*2.0 ), 
					    (int) MyMath.map(obj.getPosY() - Game.getCamera().getPosY(),
							            -mapHorizon, mapHorizon, 
							             mapTopMargin, mapTopMargin + mapRadius*2.0 ), 
					     2*MAP_OBJECT_RADIUS, 2*MAP_OBJECT_RADIUS );
			
		}
	}
}
