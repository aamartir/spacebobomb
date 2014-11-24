package spaceGame;

// For Drawing
import javax.swing.JFrame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.image.BufferStrategy;
import java.awt.Color;
import java.awt.Font;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

//import java.awt.event.ActionListener;
//import java.awt.event.MouseListener;
//import java.awt.event.ActionListener;
//import java.awt.event.MouseListener;
import java.util.ArrayList;

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
	long currentTimeSlice;
	long nextTimeSlice;
	int framesInCurrentTimeSlice;
	int framesInLastTimeSlice;
	
	private PlayerShip playerShip;
	private boolean inGame;
	
	public static void main( String[] args )
	{
		new Game();
	}
	
	public Game()
	{
		//this.setTitle( "Space bob omb" );
		this.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );
		
		// Remove borders from screen (it looks nicer this way).
		this.setUndecorated( true );
		
		// Set windows size to full screen
		getContentPane().setPreferredSize( Toolkit.getDefaultToolkit().getScreenSize() );
		screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width;
		screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height;
		pack(); //this.setSize( w, h );
		
		this.setResizable( false );
		this.setBackground( Color.black );
		this.setFocusable( true );
		this.setVisible( true );
	
		// Tell swing to stop rasterizing
		setIgnoreRepaint( true );
		
		// Double buffering
		this.createBufferStrategy( 2 );
		
		// Add keyListener
		addKeyListener(new TAdapter());
		//addMouseListener(this);

		// Game loop (stays here for ever)
		gameLoop();
	}

	// Game loop
	public void gameLoop()
	{
		inGame = true;
		
		// Initialize spaceships
		initSpaceShips();

		// Used for fps
		nextTimeSlice = System.currentTimeMillis() + timeSliceDuration;
		
		// game loop
		while( inGame )
		{
			dt = (System.nanoTime() - lastTime)/1000000000.0; // Time in milliseconds
			lastTime = System.nanoTime();
			
			gameLogic( getLastFPS()*dt );
			gameDraw();
		}
	}
	
	// Game logic 
	public void gameLogic( double dt )
	{
		// 1. Move all objects
		playerShip.updateSpaceShipMotion( dt );
		
		// 2. Check collisions
		// ...
		
		// 3. Move accordingly on next iteration
		// ...
	}
	
	// Draw game stuff
	public void gameDraw()
	{
		BufferStrategy bf = this.getBufferStrategy();
		Graphics g = null;

		try
		{
			// Get the graphics object from the imagebuffer
			g = bf.getDrawGraphics();

			// Clear screen (draw and fill black rectangle)
			g.setColor( getBackground() );
			g.fillRect( 0, 0, screenWidth, screenHeight );
			
			// Draw out little spaceship friend
			playerShip.drawSpaceShip( g );
			
			// Draw fps
			updateFPS();
			drawFPS( g, getLastFPS() );
		}
		finally
		{
			// Dispose of the graphics object when you are done with it
			g.dispose();
		}
		
		// Show contents of backbuffer onto the screen (This will flip buffers automatically)
		bf.show();
		
		//Tell the System to do the Drawing now, otherwise it can take a few extra ms until 
        //Drawing is done which looks very jerky
        //Toolkit.getDefaultToolkit().sync();	
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
	}
	
	public void initAliens()
	{
		/*
		alienCount = 2 + (int) Math.floor(gameLevel/2.0);
		if(alienCount >= 5)
			alienCount = 5;
			
		int x;
		int y;
		//double vel;
		int tmp;
		int angle;
		
		for(int i = 0; i < alienCount; i++)
		{
			tmp = randomGenerator.nextInt(2); // Random number between 0 and 1
			x = 2*tmp*WIDTH - WIDTH/2; // Initialize outside of the board boundaries 
			y = randomGenerator.nextInt(HEIGHT);
			//x = randomGenerator.nextInt(WIDTH/3) + WIDTH/3;
			//y = randomGenerator.nextInt(HEIGHT/3) + HEIGHT/3;

			angle = randomGenerator.nextInt(359)-179; // Random direction
			createAlienShip(spaceShipArr, x, y, SpaceShip.SHIP_MAX_VEL, 0, angle); // Create alien
		}
		*/
	}
	
	private void createAlienShip(ArrayList<SpaceShip> arr, int x, int y, double v_x, double v_y, double angle)
	{
		/*
		arr.add(new AlienShip(gameLevel, x, y, v_x, v_y, angle));
		*/
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
	
	public void drawScreenMessage( Graphics g, String str, int x, int y, Color c )
	{
		g.setColor(c);
		g.setFont(new Font("ARIAL", Font.BOLD, 30));
		g.drawString(str, x, y);
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
		g.setColor( Color.YELLOW );
		g.setFont(new Font("ARIAL", Font.PLAIN, 16));
		g.drawString( fps + " fps", 20, 40 );
	}
	
	public class TAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			int key = e.getKeyCode();
			
			//System.out.println( "Key pressed: " + key );
			
			// Accelerate
			if(key == KeyEvent.VK_W)
				playerShip.setSpaceShipThrust( playerShip.getMaxThrust() );
			else if(key == KeyEvent.VK_A)
				playerShip.setSpaceShipAngularThrust( -playerShip.getMaxAngularThrust());
			else if(key == KeyEvent.VK_D)
				playerShip.setSpaceShipAngularThrust( playerShip.getMaxAngularThrust() );
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
