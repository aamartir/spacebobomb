package panel;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;

import com.space.Asteroid;

import spaceGame.EnemyShip;
import spaceGame.Game;

public class MiniMap 
{
	private static final int MAP_BORDER = 10;
	private static final int MAP_OBJECT_RADIUS = 5;
	
	private int posX;
	private int posY;
	private double scaleX;
	private double scaleY;
	private int mapRadius;
	private int viewportWidth;
	private int viewportHeight;
	private double zoomFactor;
	private int alpha; // Transparency (affects everything drawn to map)
	private Color mapBackgroundColor;
	private Color playerColor;
	private Color spaceshipColor;
	private Color asteroidColor;

	public MiniMap( int viewportWidth, int viewportHeight, 
			        int mapRadius,
			        int alpha )
	{
		this.viewportWidth = viewportWidth;
		this.viewportHeight = viewportHeight;
		this.mapRadius = mapRadius;
		posX = viewportWidth - 2*mapRadius - MAP_BORDER;
		posY = MAP_BORDER;
		zoomFactor = 1.0;
		
		// Blueish semi-transparent color
		this.alpha         = alpha;
		mapBackgroundColor = new Color( 0, 50, 200, alpha );
		playerColor        = new Color( 0, 200, 0, alpha );
		spaceshipColor     = new Color( 200, 0, 0, alpha );
		asteroidColor      = new Color( 150, 150, 150, alpha );
	}
	
	public void drawMiniMap( Graphics g )
	{
		// Draw background
		g.setColor( mapBackgroundColor );
		g.fillOval( posX, posY, 2*mapRadius, 2*mapRadius );
		
		// Draw player ship in the middle
		g.setColor( playerColor );
		g.fillOval( (int)(posX + mapRadius - MAP_OBJECT_RADIUS),
				    (int)(posY + mapRadius - MAP_OBJECT_RADIUS), 
				    MAP_OBJECT_RADIUS, MAP_OBJECT_RADIUS ); 
		
		// Draw all enemies nearby
		for( EnemyShip ship : Game.getEnemiesWithinViewport() )
		{
			if( ship.distanceWithRespectTo( Game.getPlayer() ) <= viewportHeight/2.0 )
			{
				g.setColor( spaceshipColor );
				g.fillOval( (int)((ship.getPosX() - Game.getCamera().getViewportMinX())/viewportWidth*2*mapRadius + posX), 
							(int)((ship.getPosY() - Game.getCamera().getViewportMinY())/viewportHeight*2*mapRadius + posY), 
							MAP_OBJECT_RADIUS, MAP_OBJECT_RADIUS );
			}
			
		}
		
		// Draw asteroids nearby
		for( Asteroid asteroid : Game.getAsteroidsWithinViewport() )
		{
			if( asteroid.distanceWithRespectTo( Game.getPlayer() ) <= viewportHeight/2.0 )
			{
				g.setColor( asteroidColor );
				g.fillOval( (int)((asteroid.getPosX() - Game.getCamera().getViewportMinX())/viewportWidth*2*mapRadius + posX), 
							(int)((asteroid.getPosY() - Game.getCamera().getViewportMinY())/viewportHeight*2*mapRadius + posY), 
							MAP_OBJECT_RADIUS, MAP_OBJECT_RADIUS );
			}
		}
	}
}
