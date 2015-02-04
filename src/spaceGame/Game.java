package spaceGame;

// For Drawing
import javax.swing.JFrame;

import panel.MiniMap;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseListener;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseListener;
import java.util.ArrayList;

import com.ship.effects.Shockwave;
import com.ship.effects.RotatingTrianglesTarget;
import com.space.Asteroid;
import com.space.StarField;
import com.weapons.Weapon;

public class Game extends JFrame implements MouseListener //implements ActionListener
{
	private static final long serialVersionUID = 1L;
	public static int screenWidth;
	public static int screenHeight;

	// Timing
	private double dt;
	private long lastTime;

	// FPS
	private static final int timeSliceDuration = 1000;
	public  static final int framesPerSec = 70;
	public  static final int msPerFrame = ((int) 1000.0/framesPerSec);
	
	private static long currentTimeSlice;
	private static long nextTimeSlice;
	private static int framesInCurrentTimeSlice;
	private static int framesInLastTimeSlice;
	
	// Debug Statistics (rendering)
	private int spaceShipsRendered;
	private int asteroidsRendered;
	private int weaponsRendered;
	
	// Space objects
	public  static PlayerShip playerShip;
	private static SpaceCamera camera;
	private static SpaceObjectSelector selector;
	private static MiniMap miniMap;
	private static StarField starField;
	private static ArrayList<EnemyShip> enemies;
	private static ArrayList<Asteroid> asteroids;

	// Other game variables
	public static Game game;
	public static Grid grid;
	private static boolean inGame;
	private static SpaceObject objSelected;
	
	// Re-usable variables for Collision detection
	Quadrant q       = null;
	Object[] objInQ  = null;
	SpaceObject obj1 = null;
	SpaceObject obj2 = null;
	
	// Game Threads
	private static Thread logicThread;
	private static Thread renderThread;
	
	// Graphics variables
	private BufferStrategy bufferStrategy;
	private RenderingHints renderHints;
	private Graphics graphics;
	
	public static void main( String[] args )
	{
		game = new Game();
	}
	
	public Game()
	{
		// this.setTitle( "Space bob omb" );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		// Remove borders from screen (it looks nicer this way).
		this.setUndecorated( true );
		
		// Set windows size to full screen
		getContentPane().setPreferredSize( Toolkit.getDefaultToolkit().getScreenSize() );
		GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
		screenWidth = gd.getDisplayMode().getWidth();
		screenHeight = gd.getDisplayMode().getHeight();
		pack(); //this.setSize( w, h );
		
		// System.out.println( "Screen size: " + screenWidth + "x" + screenHeight );
		
		this.setResizable( false );
		this.setBackground( Color.black );
		this.setFocusable( true );
		this.setVisible( true );

		// Add keyListener
		addKeyListener( new TAdapter() );
		addMouseListener( this );

		// init game
		initGame();
	}

	// Fixes an issue where bufferstrategy does not allocate the buffer.
	// See: http://stackoverflow.com/questions/3435994/buffers-have-not-been-created-whilst-creating-buffers
	public void addNotify()
	{
		super.addNotify();
		
		// Double buffering (allocate 2 buffers).
		this.createBufferStrategy( 2 );
	}
	
	// Game loop
	public void initGame()
	{
		// Initialize collision grid
		grid = new Grid( 5, screenWidth, screenHeight );
		
		// Initialize spaceships and everything else
		initSpaceShips();
		initAsteroids();
		
		// Initialize camera (Point to the center of the object)
		camera = new SpaceCamera( playerShip.getPosX() + playerShip.getImgWidth()/2.0, 
				                  playerShip.getPosY() + playerShip.getImgHeight()/2.0, 
				                  screenWidth, 
				                  screenHeight, 
				                  playerShip );
		
		// Init object selector
		selector = new SpaceObjectSelector();
		
		// Initialize minimap
		miniMap = new MiniMap( screenWidth, screenHeight, 100, 255 );
		
		// Initialize starField
		starField = new StarField( 100, -screenWidth, -screenHeight, 2*screenWidth, 2*screenHeight );
		
		// Start logic and rendering threads
		inGame = true; // Has to be called before start of threads.
		
		// Instantiate threads (render and logic)
		renderThread = new Thread( new GameRender() );
		logicThread = new Thread( new GameLogic() );
		
		// Thread priority
		logicThread.setPriority( Thread.MAX_PRIORITY );
		//renderThread.setPriority( Thread.MAX_PRIORITY );
		
		renderThread.start();
		logicThread.start();

		// Used for fps
		nextTimeSlice = System.currentTimeMillis() + timeSliceDuration;
		lastTime = System.nanoTime();
	}
	
	// Game logic 
	public void gameLogic( double dt )
	{
		// 1. Update player's motion
		playerShip.updateSpaceShipMotion( dt );
		
		// Update spaceship messages
		playerShip.updateSpaceShipStatusMessages( dt );
		
		// Move our player's weapons
		for( int i = 0; i < playerShip.getWeaponsFired().size(); i++ )
		{
			if( playerShip.getWeaponsFired().get(i).isDestroyed() )
			{
				playerShip.getWeaponsFired().remove( i );
				continue;
			}
			else
			{
				// Update the motion of each weapon
				playerShip.getWeaponsFired().get(i).updateWeaponMotion( dt );
			}
		}
		
		// 1a. Update enemies' motion
		for( EnemyShip enemy : enemies )
		{
			// Update AI and motion (motion is updated automatically within AI method).
			enemy.AI( dt );
			
			// Update enemy status messages (motion)
			enemy.updateSpaceShipStatusMessages( dt );
			
			// For every enemy ship, update motion of weapons fired. 
			for( int i = 0; i < enemy.getWeaponsFired().size(); i++ )
			{
				if( enemy.getWeaponsFired().get(i).isDestroyed() )
				{
					enemy.getWeaponsFired().remove( i );
					continue;
				}
				else
				{
					// Update the motion of each weapon
					enemy.getWeaponsFired().get(i).updateWeaponMotion( dt );
				}
			}
		}
		
		// Update asteroid dynamics
		for( Asteroid asteroid : asteroids )
		{
			asteroid.updateAsteroidMotion( dt );
		}
		
		// Update shockwave and explosions
		Shockwave.updateShockwaves( dt );
		
		// 2. Check collisions (Tile based)
		// For every visible tile
		// Get all the objects, and see if they are colliding with each other.
		for( int g = 0; g < grid.size(); g++ )
		{
			q = grid.get(g);
			
			// Ignore quadrant if there are no objects within it
			if( q.getNumOfObjectsInQuadrant() < 2 )
				continue;
			
			// Get kth quadrant and the objects inside it
			objInQ = q.getObjectsInQuadrant();
			
			for( int i = 0; i < objInQ.length - 1; i++ )
			{
				obj1 = ((SpaceObject) objInQ[i]);
				if( obj1.isDestroyed() || !obj1.isVisible() )
					continue;
				
				for( int j = i + 1; j < objInQ.length; j++ )
				{
					obj2 = ((SpaceObject) objInQ[j]);
					
					if( obj2.isDestroyed() || !obj2.isVisible() )
						continue;
					
					if( obj1.isCollidingWith(obj2) )
					{
						//System.out.println( "Collision between " + obj1.getObjectID() + " and " + obj2.getObjectID() + "." );
						
						obj1.destroySpaceObject( true );
						obj2.destroySpaceObject( true );
					}
				}
			}
		}
		
		// 3. Move accordingly on next iteration
		// ...
		
		// Update starField based on player's velocity
		if( Math.abs(playerShip.getVelocityX()) > 0 || 
		    Math.abs(playerShip.getVelocityY()) > 0 )
		{
			starField.moveStarField( -playerShip.getVelocityX(), -playerShip.getVelocityY() );
		}
		
		// Last. Move Camera to follow its target
		camera.updatePosition();
	}
	
	// Draw game stuff
	public void gameDraw()
	{
		bufferStrategy = this.getBufferStrategy();
		graphics = null;
		
		/* Enable Antialiasing (may reduce performance) */
		renderHints = new RenderingHints( RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON );
		renderHints.put( RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY );
		
		try
		{
			// Get the graphics object from the imagebuffer
			graphics = bufferStrategy.getDrawGraphics();
			
			// Set antialiasing ON (with render hints)
			((Graphics2D) graphics).setRenderingHints( renderHints );
			
			// Clear screen (draw and fill black rectangle)
			graphics.clearRect( 0, 0, screenWidth, screenHeight );
			
			// Set clipping region (only what's inside this area will be drawn)
			graphics.setClip( 0, 0, screenWidth, screenHeight );
			
			// Draw fps. All static text and graphics are drawn before the translate function.
			updateFPS();
			drawFPS( graphics, getLastFPS() );
			
			// Draw statistical variables
			drawScreenMessage( graphics, "Spaceships rendered: " + spaceShipsRendered, 14, 20, 60, Color.YELLOW );
			drawScreenMessage( graphics, "Asteroids rendered: "  + asteroidsRendered,  14, 20, 80, Color.YELLOW );
			drawScreenMessage( graphics, "Weapons rendered: "    + weaponsRendered,    14, 20, 100, Color.YELLOW );
			
			// Draw msg "Escape to exit game"
			drawScreenMessage( graphics, "Press Escape key to exit game", 14, 20, 120, Color.YELLOW );
			
			// Draw starfield
			starField.drawStarField( graphics );
						
			// Draw collision grid for testing purposes
			grid.drawGrid( graphics );
			
			// The minimap is the last thing to draw
			miniMap.drawMiniMap( graphics );
								
			// Clear statistical variables
			weaponsRendered = 0;
			spaceShipsRendered = 0;
			asteroidsRendered = 0;
			
			// Translate screen so that player is always in the center (This should be done before all objects are drawn, so that
			// everything is translated properly).
			//((Graphics2D)graphics).translate( -playerShip.getPosX() + getWidth()/2.0, -playerShip.getPosY() + getHeight()/2.0 );
			((Graphics2D)graphics).translate( -camera.getPosX() + camera.getViewportWidth()/2.0, 
					                          -camera.getPosY() + camera.getViewportHeight()/2.0 );
			
			// Draw our little spaceship friend
			playerShip.drawSpaceShip( graphics );
			spaceShipsRendered++;
			
			// Draw weapons fired of player ship
			for( Weapon weapon : playerShip.getWeaponsFired() )
			{
				// Draw weapon if within viewport
				if( weapon.isWithinViewport( camera.getViewportMinX(), camera.getViewportMinY(), 
                        					 camera.getViewportMaxX(), camera.getViewportMaxY()) &&
                    !weapon.isDestroyed() )
				{
					weaponsRendered++;
					weapon.drawSpaceObject( graphics );
				}
			}
			
			// Draw enemy space ships
			for( SpaceShip ship : enemies )
			{
				if( ship.isWithinViewport(camera.getViewportMinX(), camera.getViewportMinY(), 
	                                      camera.getViewportMaxX(), camera.getViewportMaxY()) )
				{
					spaceShipsRendered++;
					ship.drawSpaceShip( graphics );
				}
				
				// Draw weapons fired if within viewport as well
				for( Weapon weapon : ship.getWeaponsFired() )
				{
					if( weapon.isWithinViewport( camera.getViewportMinX(), camera.getViewportMinY(), 
                            					 camera.getViewportMaxX(), camera.getViewportMaxY()) && 
                        !weapon.isDestroyed() )
        			{
						weaponsRendered++;
						weapon.drawSpaceObject( graphics );
                    }
				}
			}
			
			// Draw asteroids
			for( Asteroid asteroid : asteroids )
			{
				if( asteroid.isWithinViewport(camera.getViewportMinX(), camera.getViewportMinY(), 
						                      camera.getViewportMaxX(), camera.getViewportMaxY()) &&
				    asteroid.isVisible() )
				{
					asteroidsRendered++;
					asteroid.drawAsteroid( graphics );
				}
			}
			
			// Draw shockwaves and explosions
			Shockwave.drawShockwaves( graphics );

			// Draw other stuff
			// TODO
			
			// Draw mouse selector box
			/*
			if( selector.isVisible() )
				selector.drawSelector( (Graphics2D) graphics );
			*/
						
		}
		finally
		{
			// Dispose of the graphics object when you are done with it
			graphics.dispose();
		}
		
		// Show contents of backbuffer onto the screen (This will flip buffers automatically)
		bufferStrategy.show();
		
		//Tell the System to do the Drawing now, otherwise it can take a few extra ms until 
        //Drawing is done which looks very jerky
        Toolkit.getDefaultToolkit().sync();	
	}

	private void updateFPS()
	{
		currentTimeSlice = System.currentTimeMillis();
		if( currentTimeSlice >= nextTimeSlice )
		{
			nextTimeSlice += timeSliceDuration;
			framesInLastTimeSlice = framesInCurrentTimeSlice;
			framesInCurrentTimeSlice = 0;
		}
		
		framesInCurrentTimeSlice++;
	}
	
	private int getLastFPS()
	{
		return (int)(framesInLastTimeSlice*(1000/timeSliceDuration));
	}
		
	public void initSpaceShips()
	{
		System.out.println( "Generating spaceships..." );
		
		// Initialize player spaceship in the middle of the screen
		playerShip = new PlayerShip( screenWidth/2, screenHeight/2 );
		
		// Initialize enemie arraylist.
		enemies = new ArrayList<EnemyShip>();
		
		// Add some enemies at random locations (test)
		for( int i = 0; i < 50; i++ )
		{
			EnemyShip.createEnemyShip( enemies, 0, 0, 10, 0, 0, 2*screenWidth, 2*screenHeight );
			enemies.get( i ).followSpaceShip( playerShip );
		}
		
		//System.out.println( "done" );
	}
	
	public void initAsteroids()
	{
		System.out.println( "Generating asteroids..." );
		
		asteroids = new ArrayList<Asteroid>();
		
		// Create some asteroids
		for( int i = 0; i < 10; i++ )
		{
			Asteroid.createRandomAsteroid( asteroids, 
					                       Asteroid.ASTEROID_01, 
					                       0, 0, 2*screenWidth, 2*screenHeight );
		}
		//System.out.println( "done" );
	 }

	public static boolean isWithinBounds( SpaceObject obj )
	{
		int x = (int) obj.getPosX();
		int y = (int) obj.getPosY();
		
		if(x + obj.getImgWidth() >= 0 && x <= WIDTH && y + obj.getImgHeight() >= 0 && y <= HEIGHT)
			return true;
		else
			return false;
	}
	
	public static StarField getStarField()
	{
		return starField;
	}
	
	// Return all enemies for now (Need only to return objects within viewable area)
	public static ArrayList<EnemyShip> getEnemiesWithinViewport()
	{
		return enemies;
	}
	
	public static ArrayList<EnemyShip> getAllEnemies()
	{
		return enemies;
	}
	
	// Return all asteroids for now (Need only to return objects within viewable area)
	public static ArrayList<Asteroid> getAsteroidsWithinViewport()
	{
		return asteroids;
	}
	
	public static ArrayList<Asteroid> getAllAsteroids()
	{
		return asteroids;
	}
	
	public static SpaceCamera getCamera()
	{
		return camera;
	}
	
	public static SpaceShip getPlayer()
	{
		return playerShip;
	}
	
	public void drawGameInfo( Graphics g )
	{
		/*
		// Draw Life and Missiles Left on the upper left corner
		g2d.setColor(Color.white);
		g2d.setFont(new Font("ARIAL", Font.BOLD, 20));
		g2d.drawString("Life ", 140, 30);
		drawBar(g2d, 20, 20, 100, 10, (double) craft.getLife()/(double) craft.getMaxLife(), Color.red, Color.green);
		
		// Missiles Left
		g2d.setColor(Color.white);
		g2d.drawString("Missiles Left ", 140, 55);
		drawBar(g2d, 20, 45, 100, 10, (double) craft.getNumOfMissilesLeft()/ (double) craft.getMaxMissiles(), Color.red, Color.green);
		
		// Experience Gained
		g2d.setColor(Color.white);
		g2d.drawString("XP", 140, 80);
		drawBar(g2d, 20, 70, 100, 10, (double) (craft.getXP()-craft.getLastLvlXP()) / (double) craft.getNextLvlXP(), Color.red, Color.green);
		
		// Fuel Left
		g2d.setColor(Color.white);
		g2d.drawString("Fuel", 140, 105);
		drawBar(g2d, 20, 95, 100, 10, craft.getFuel()/craft.getFuelCapacity(), Color.red, Color.green);
		//System.out.println(craft.getFuel());

		// Aliens Left
		g2d.setColor(Color.white);
		g2d.drawString("Level " + gameLevel, WIDTH-150, 30);
		g2d.drawString("Aliens Left: " + alienCount, WIDTH-150, 60);
		
		// Frame per second
		//g2d.setColor(Color.white);
		//g2d.setFont(new Font("ARIAL", Font.PLAIN, 14));
		//g2d.drawString("Frame Rate: " + Math.round(frameRate) + " fps.", 20, 130);
		*/
	}
	
	public void drawScreenMessage( Graphics g, String str, int textSize, int x, int y, Color c )
	{
		g.setColor( c );
		g.setFont( new Font("ARIAL", Font.PLAIN, textSize) );
		g.drawString( str, x, y );
	}
	
	public void drawBar( Graphics g, int x, int y, int width, int height, double val, Color baseColor, Color topColor )
	{
		// Base color
		g.setColor(baseColor);
		g.fillRect(x, y, width, height);
		
		// Top Color
		g.setColor(topColor);
		g.fillRect(x, y, (int) (width*val), height);
	}
	
	public void drawFPS( Graphics g, int fps )
	{
		drawScreenMessage( g, fps + " fps", 14, 20, 40, Color.YELLOW );
	}
	
	class GameLogic extends Thread
	{
		public void run()
		{
			while( inGame )
			{
				dt = (System.nanoTime() - lastTime)/1000000.0; // Time in milliseconds
				lastTime = System.nanoTime();
				
				// Game logic here
				gameLogic( dt );

				try
				{
					// Sleepy time
					Thread.sleep( msPerFrame );
				}
				catch( InterruptedException ie ) 
				{ 
					System.out.println( ie.toString() );
				}
				finally
				{
					// Clean up
				}
			}
		}
	}
	
	class GameRender extends Thread
	{
		public void run()
		{
			while( inGame )
			{
				// Draw everything at a specific fps
				gameDraw();
					
				try
				{
					// Sleepy time
					Thread.sleep( msPerFrame );
				}
				catch( InterruptedException ie ) 
				{
					System.out.println( ie.toString() );
				}
				finally
				{
					// Clean up
				}
			}
			
			// Close game window and exit program.
			game.dispatchEvent( new WindowEvent(game, WindowEvent.WINDOW_CLOSING) );
			System.exit( 0 );
		}
	}
	
	public void mouseClicked( MouseEvent e )
	{
		
	}
	
	public void mouseReleased( MouseEvent e )
	{
		//selector.setVisible( true );
		selector.setCoordinates( e.getX() - SpaceObjectSelector.SELECTOR_SIZE/2.0 + camera.getViewportMinX() + playerShip.getImgWidth()/2.0, 
				                 e.getY() - SpaceObjectSelector.SELECTOR_SIZE/2.0 + camera.getViewportMinY() + playerShip.getImgHeight()/2.0);
		
		//System.out.println( "(" + e.getY() + "," + e.getX() + ")." + camera.getViewportMinY() + ". playerY: " + playerShip.getPosY() );
		
		// Get the selected tile
		// Within that tile, get all objects, and find which object is selected
		
		// ... 
		
		if( objSelected != null )
		{
			objSelected.unselect();
			objSelected = null;
		}
		
		for( SpaceShip aShip : enemies )
		{
			if( !aShip.isWithinViewport(camera.getViewportMinX(), camera.getViewportMinY(), 
                                        camera.getViewportMaxX(), camera.getViewportMaxY()) )
			{
				continue;
			}
			
			if( selector.isSelecting(aShip) )
			{
				objSelected = aShip;
				aShip.select();
			}
		}
			
		// Select other objects
		if( objSelected == null )
		{
			for( Asteroid asteroid : asteroids )
			{
				if( !asteroid.isWithinViewport(camera.getViewportMinX(), camera.getViewportMinY(), 
                        					   camera.getViewportMaxX(), camera.getViewportMaxY()) )
				{
					continue;
				}
				
				if( selector.isSelecting(asteroid) )
				{
					objSelected = asteroid;
					asteroid.select();
				}
			}
		}
		
		// Select something on the screen
		// TODO
	}
	
	public void mouseEntered( MouseEvent e )
	{
		
	}
	
	public void mousePressed( MouseEvent e )
	{
		
	}
	
	public void mouseExited( MouseEvent e )
	{
		
	}
	
	public class TAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			
			// Accelerate
			if(key == KeyEvent.VK_W)
				playerShip.setSpaceShipThrust( playerShip.getMaxThrust() );
			else if(key == KeyEvent.VK_A)
				playerShip.setSpaceShipAngularThrust( -playerShip.getMaxAngularThrust());
			else if(key == KeyEvent.VK_D)
				playerShip.setSpaceShipAngularThrust( playerShip.getMaxAngularThrust() );
			else if( key == KeyEvent.VK_ESCAPE )
				inGame = false;
			else if( key == KeyEvent.VK_SPACE )
				playerShip.fireMissile();
			else if( key == KeyEvent.VK_K ) // This is to test different features
				playerShip.decreaseLife(10);
		}
		
		public void keyReleased(KeyEvent e)
		{
			int key = e.getKeyCode();
			
			//System.out.println( "Key released: " + key );
			
			if( key == KeyEvent.VK_A || key == KeyEvent.VK_D )
				playerShip.setSpaceShipAngularThrust( 0 );
			else if( key == KeyEvent.VK_W )
				playerShip.setSpaceShipThrust( 0 );
		}
	}
}
