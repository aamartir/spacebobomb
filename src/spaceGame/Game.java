package spaceGame;

// For Drawing
import javax.swing.JFrame;

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
import java.awt.event.WindowEvent;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseListener;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseListener;
import java.util.ArrayList;

import com.space.Asteroid;
import com.weapons.Weapon;

public class Game extends JFrame //implements ActionListener, MouseListener
{
	private static final long serialVersionUID = 1L;
	public static int screenWidth;
	public static int screenHeight;

	// Timing
	private double dt;
	private long lastTime;

	// FPS
	private static final int timeSliceDuration = 1000;
	public static final int framesPerSec = 70;
	public static final int msPerFrame = ((int) 1000.0/framesPerSec);
	
	private long currentTimeSlice;
	private long nextTimeSlice;
	private int framesInCurrentTimeSlice;
	private int framesInLastTimeSlice;
	
	// Space objects
	private PlayerShip playerShip;
	private SpaceCamera camera;
	private ArrayList<SpaceShip> enemies;
	private ArrayList<Asteroid> asteroids;

	private boolean inGame;
	private Thread logicThread;
	private Thread renderThread;
	
	public static Game game;
	private BufferStrategy bufferStrategy;
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
		screenWidth= gd.getDisplayMode().getWidth();
		screenHeight = gd.getDisplayMode().getHeight();
		/*screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;*/
		pack(); //this.setSize( w, h );
		
		// System.out.println( "Screen size: " + screenWidth + "x" + screenHeight );
		
		this.setResizable( false );
		this.setBackground( Color.black );
		this.setFocusable( true );
		this.setVisible( true );

		// Add keyListener
		addKeyListener( new TAdapter() );
		//addMouseListener(this);

		// init game
		initGame();
	}

	// Fixes an issue where bufferstrategy does not allocate the buffer.
	// See: http://stackoverflow.com/questions/3435994/buffers-have-not-been-created-whilst-creating-buffers
	public void addNotify()
	{
		super.addNotify();
		
		// Double buffering
		this.createBufferStrategy( 2 );
	}
	
	// Game loop
	public void initGame()
	{
		// Initialize spaceships and everything else
		initSpaceShips();
		initAsteroids();
		
		// Initialize camera
		camera = new SpaceCamera( playerShip.getPosX(), playerShip.getPosY(), screenWidth, screenHeight, playerShip );
		
		// Start logic and rendering threads
		inGame = true; // Has to be called before start of threads.
		
		renderThread = new Thread( new GameRender() );
		logicThread = new Thread( new GameLogic() );
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
		
		// 1a. Update enemies' motion
		for( SpaceShip ship : enemies )
			ship.updateSpaceShipMotion( dt );
		
		for( Asteroid asteroid : asteroids )
			asteroid.updateAsteroidMotion( dt );
		
		// 2. Check collisions
		// ...
		
		// 3. Move accordingly on next iteration
		// ...
		
		// Last. Move Camera to follow its target
		camera.updatePosition();
	}
	
	// Draw game stuff
	public void gameDraw()
	{
		bufferStrategy = this.getBufferStrategy();
		graphics = null;

		try
		{
			// Get the graphics object from the imagebuffer
			graphics = bufferStrategy.getDrawGraphics();
			
			// Clear screen (draw and fill black rectangle)
			graphics.clearRect( 0, 0, screenWidth, screenHeight );
			
			// Set clipping region (only what's inside this area will be drawn)
			graphics.setClip( 0, 0, screenWidth, screenHeight );
			
			// Draw fps. All static text and graphics are drawn before the translate function.
			updateFPS();
			drawFPS( graphics, getLastFPS() );
			
			// Draw msg "Escape to exit game"
			drawScreenMessage( graphics, "Press Escape key to exit game", 14, 20, 60, Color.YELLOW );
						
			// Translate screen so that player is always in the center (This should be done before all objects are drawn, so that
			// everything is translated properly).
			//((Graphics2D)graphics).translate( -playerShip.getPosX() + getWidth()/2.0, -playerShip.getPosY() + getHeight()/2.0 );
			((Graphics2D)graphics).translate( -camera.getPosX() + camera.getViewportWidth()/2.0, 
					                          -camera.getPosY() + camera.getViewportHeight()/2.0 );
			
			// Draw our little spaceship friend
			playerShip.drawSpaceShip( graphics );
			
			// Draw enemy space ships
			for( SpaceShip ship : enemies )
			{
				if( ship.isWithinViewport(camera.getViewportMinX(), camera.getViewportMinY(), 
	                                      camera.getViewportMaxX(), camera.getViewportMaxY()) )
				{
					ship.drawSpaceShip( graphics );
				}
			}
			
			// Draw asteroids
			for( Asteroid asteroid : asteroids )
			{
				if( asteroid.isWithinViewport(camera.getViewportMinX(), camera.getViewportMinY(), 
						                      camera.getViewportMaxX(), camera.getViewportMaxY()) )
				{
					asteroid.drawAsteroid( graphics );
				}
			}
			
			// Draw other stuff
			// TODO
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
		System.out.println( "Initializing spaceships..." );
		
		// Initialize player spaceship in the middle of the screen
		playerShip = new PlayerShip( screenWidth/2, screenHeight/2 );
		
		// Initialize enemie arraylist.
		enemies = new ArrayList<SpaceShip>();
		
		// Add some enemies at random locations (test)
		for( int i = 0; i < 5; i++ )
			EnemyShip.createEnemyShip( enemies, 0, 0, 10, 0, 0, screenWidth, screenHeight );
	}
	
	public void initAsteroids()
	{
		System.out.println( "Generating asteroids..." );
		
		asteroids = new ArrayList<Asteroid>();
		
		// Create a couple of asteroids
		for( int i = 0; i < 20; i++ )
		{
			Asteroid.createRandomAsteroid( asteroids, 
					                       Asteroid.ASTEROID_01, 
					                       0, 0, screenWidth, screenHeight );
		}
	 }

	public void moveSpaceShipWeapons(SpaceShip aShip)
	{
		/*
		// Move the missiles from the array
		weaponArr = aShip.getWeaponArr();
		weaponCount = weaponArr.size();
			
		//System.out.println(missileArr.size());
		
		int i = 0;
		while(i < weaponCount && i >= 0)
		{
			thisWeapon = weaponArr.get(i);
			
			// If the missile is outside the boundaries, don't draw it anymore
			if(!isWithinBounds(thisWeapon))
			{
				weaponArr.remove(thisWeapon);
				weaponCount--;
				
				if(thisWeapon instanceof Missile)
					aShip.decreaseMissileCounter();
			}
			else if(!thisWeapon.isDestroyed())
				thisWeapon.move();
			
			i++;
		}
		*/
	}
	
	public void moveAsteroids()
	{
		/*
		int i = 0;
		while(i < asteroidArr.size() && i >= 0)
		{
			thisAsteroid = asteroidArr.get(i);
			if(isWithinBounds(thisAsteroid) && !thisAsteroid.isDestroyed())
				thisAsteroid.move();
			else if(thisAsteroid.isDestroyed() && !thisAsteroid.getExplObj().isVisible() || !isWithinBounds(thisAsteroid))
			{
				asteroidArr.remove(i);
				i--;
			}
			
			i++;
		}
		*/
	}
	
	public void moveAllSpaceShips() // Including user's craft
	{
		/*
		for(int i = 0; i < spaceShipArr.size(); i++)
		{
			spaceShip = spaceShipArr.get(i); //missiles move regardless
			moveSpaceShipWeapons(spaceShip); // weapons
			
			if(!spaceShip.isDestroyed()) // Only draw if still alive
			{
				if(spaceShip instanceof AlienShip)
				{
					aShip = (AlienShip) spaceShip;
					
					if(aShip.distanceWithRespectTo(craft) < 180 && !craft.isDestroyed() && !aShip.isEscaping())
						aShip.followAndDestroy(craft);
					else if(aShip.isEscaping())
						aShip.escapeFrom(craft);
					else if(!isWithinBounds(aShip))
						aShip.followOther(craft);
					else
					{
						aShip.niceBehavior(); // Not following the user
						aShip.cruise(craft, randomGenerator, WIDTH, HEIGHT);
					}
				}
				
				spaceShip.move();
			}	
			else
				spaceShipArr.remove(i);
		}
		*/
	}
	
	private void drawShipWeapons(Graphics g, ArrayList<Weapon> arr)
	{
		/*
		int m = 0;
		while(m < arr.size() && m >= 0)
		{
			thisWeapon = arr.get(m);
				
			if(thisWeapon.isVisible()) // draw if visible
			{
				thisWeapon.draw(g2d);
				
				if(thisWeapon instanceof SeekMissile)
					((SeekMissile) thisWeapon).getTrailObject().drawTrail(g2d);
			}
			else if(thisWeapon.isDestroyed() && thisWeapon.getExplObj() != null)
			{
				if(thisWeapon.getExplObj().isVisible())
					thisWeapon.drawExplosion(g2d);
			}
			else
			{
				arr.remove(m);
				m--;
			}
			
			m++;
				
		}
		*/
	}

	public void drawAsteroids( Graphics g )
	{
		/*
		if(mousePointer == null)
			mousePointer = MouseInfo.getPointerInfo().getLocation();
				
		int i = 0;
		while(i < asteroidArr.size() && i >= 0)
		{
			thisAsteroid = asteroidArr.get(i);
			
			if(!thisAsteroid.isDestroyed()) // if asteroid is not destroyed
			{
				thisAsteroid.draw(g2d);
				
				// Draw target if mouse is hovering above it
				if(thisAsteroid.containsPoint(mousePointer) && !thisAsteroid.isSelected())
				{
					thisAsteroid.setMouseHover();
				}
				else if(!thisAsteroid.isSelected())
				{
					thisAsteroid.unsetMouseHover();
				}
			}
			else if(thisAsteroid.getExplObj() != null) // it is destroyed
			{
				if(thisAsteroid.getExplObj().isVisible())
					thisAsteroid.drawExplosion(g2d);
			}
			else
			{
				asteroidArr.remove(i);
				i--;
			}
			
			i++;
		}
		*/
	}
	
	public static boolean isWithinBounds(SpaceObject obj)
	{
		int x = (int) obj.getPosX();
		int y = (int) obj.getPosY();
		
		if(x + obj.getImgWidth() >= 0 && x <= WIDTH && y + obj.getImgHeight() >= 0 && y <= HEIGHT)
			return true;
		else
			return false;
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
