package spaceGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

import panel.FrontPanel;
import panel.SkillSpace;

import com.space.Asteroid;
import com.space.StarField;
import com.weapons.PlasmaBomb;
import com.weapons.Missile;
import com.weapons.SeekMissile;
import com.weapons.Weapon;


public class Board extends JPanel implements ActionListener, MouseListener, Runnable
{
	// Grid
	private Grid grid;
	Thread T;
	
	// Game Objects
	private FrontPanel skillsPanel;
	
	// User craft
	private PlayerShip craft;
	SpaceObject objSelected = null; // Target for Seeker missiles
	
	//private ArrayList<Missile> missileArr;
	//private ArrayList<PlasmaBomb> areaBombArr;
	private ArrayList<Weapon> weaponArr;
	
	private ArrayList<BombSpaceObj> bombArr;
	private BombSpaceObj thisBombSpaceObj;
	
	private ArrayList<SpaceShip> spaceShipArr;
	private ArrayList<LifeSpaceObj> lifeObjArr;
	private ArrayList<Asteroid> asteroidArr;
	
	private static StarField stars;
	private static int alienCount;
	private static int gameLevel;
	
	public static boolean inGame;
	public static int WIDTH;
	public static int HEIGHT;
	
	private Timer timer;
	private static Image background;
	private static boolean gamePaused;
	
	// Helper variables  
	private Random randomGenerator;

	private double now;
	private double frameRate;
	private long frameRate_tmp;
	private long frameRate_last;
	private double helperObj_rate 			= 45;
	private double helperObj_lastTime 		= 0;
	private double boosterRegen_rate 		= 0.5; // In seconds
	private double boosterFuelGain_lastTime = 0;
	private double starCreator_rate 		= 0.5; //0.1;
	private double starCreator_lastTime 	= 0;
	private double levelTimer_rate 			= 10;
	private double levelTimer_lastTime 		= 0;
	private double areaBomb_rate 			= 40;
	private double areaBomb_lastTime 		= 0;
	private double asteroid_rate 			= 10; //10;
	private double asteroid_lastTime 		= 0;
	private double redraw_rate				= 0.05;
	private double redraw_rate_lastTime     = 0;
	private long lastTime;
	
	// Collision recyclable variables
	private SpaceShip spaceShipA;
	private SpaceShip spaceShipB;
	private Rectangle spaceShipA_bounds;
	private Rectangle spaceShipB_bounds;
	private double now_collision_sec;
	private double last_collision_sec;	
	
	private AlienShip aShip;
	private SpaceShip spaceShip;
	
	private Missile thisMissile;
	private Weapon thisWeapon;
	private PlasmaBomb thisAreaBomb;
	private Asteroid thisAsteroid;
	private Rectangle missileBounds;
	private int weaponCount;
	private LifeSpaceObj thisLifeObj;
	private Rectangle lifeObjBounds;
	
	private MsgScreen msgScreen;
	private Point mousePointer;

	// Asteroid recyclable variables
	private int asteroidX;
	private int asteroidY;
	private double asteroidAngle;
	private int asteroidVersion;
	private int asteroidLvl;
	private double asteroidRateDeg;
	private double asteroidVel;
	private int randInt;
	
	/* Double Buffering */
	private Image dbImg;
	private Graphics dbg; /* Double Buffer graphics */
	
	public Board(int w, int h)
	{
		addKeyListener(new TAdapter());
		addMouseListener(this);
		
		setBackground(Color.black);
		setDoubleBuffered(true);
		setFocusable(true);
		//background = new ImageIcon(this.getClass().getResource("/resources/msgScreen.png")).getImage();
		
		WIDTH = w;
		HEIGHT = h;
		
		// Grid initialize
		grid = new Grid(2, 2);
		
		// Dynamic Objects
		msgScreen = new MsgScreen(WIDTH, HEIGHT);

		initializeGameObjects();

		//timer = new Timer(1, this);
		//timer.start();
		
		T = new Thread(this);
		T.start();
	}
	
	public void initializeGameObjects()
	{
		msgScreen.newMsg("GAME", "Initializing Game Data...");
		
		// Random number generator init
		randomGenerator = new Random();
		
		// SpaceShip array init
		spaceShipArr = new ArrayList<SpaceShip>();
		
		// User craft init
		craft = initUserShip();
		spaceShipArr.add(craft); // Add spaceShip to position 0
		skillsPanel = new FrontPanel(WIDTH - FrontPanel.MAX_SKILL_SPACES*SkillSpace.WIDTH - 20, HEIGHT - 80, craft);
		
		inGame = true;
		gameLevel = 0;
		gamePaused = false;
		
		// Aliens init
		initAliens();
		msgScreen.newMsg("GAME", "Aliens initialized...");
		
		// LifeObjects init
		initLifeObjects();
		initBombSpaceObjects();
		lastTime = (long) (System.currentTimeMillis()/1000.0);
		last_collision_sec = lastTime;
		frameRate_last = lastTime;
		
		// StarField init
		msgScreen.newMsg("GAME", "Initializing Star Field...");
		initStarField();
		
		// Asteroid init
		msgScreen.newMsg("GAME", "Initializing Asteroid Field...");
		initAsteroids();
		
		msgScreen.newMsg("GAME", "SUCCESS!!!");
	}

	/* Main loop */
	public void run()
	{
		while(true)
		{
			try
			{
				if(!gamePaused)
				{
					now = System.currentTimeMillis()/1000.0 - lastTime;
					
					// Create a new LifeObject
					if(now - helperObj_lastTime > helperObj_rate)
					{
						//lifeObjArr.add(newLifeObj());
						helperObj_lastTime = now;
						
						msgScreen.newMsg("CPU_life", "New Life Object at " + now);
					}
					
					// Create a new AreaBomb
					if(now - areaBomb_lastTime > areaBomb_rate)
					{
						//bombArr.add(newBombObj());
						areaBomb_lastTime = now;
						
						msgScreen.newMsg("CPU_weapons", "New Area Bomb at " + now);
					}
					
					// Regenerate fuel
					if(now - boosterFuelGain_lastTime > boosterRegen_rate && !craft.isUsingBoosters() && !craft.isDestroyed())
					{
						craft.addBoosterFuel(2);
						boosterFuelGain_lastTime = now;
						
						msgScreen.newMsg("CPU_fuel", "Booster Regen...");
					}
					
					// Create new stars
					if(now - starCreator_lastTime >= starCreator_rate)
					{
						// Init a new star randomly (y < 0, x = random from 0 to WIDTH)
						stars.createAndAddRandomStar(randomGenerator.nextInt(WIDTH), 10);
						starCreator_lastTime = now;
					}
					
					// Create new Asteroids
					if(now - asteroid_lastTime >= asteroid_rate)
					{
						// Init a new star randomly (y < 0, x = random from 0 to WIDTH)
						asteroidArr.add(newAsteroid());
						asteroid_lastTime = now;
						
						msgScreen.newMsg("CPU_asteroids", "New Asteroid at " + now);
					}
					
					// Check if alien ships are destroyed and exploded
					if(now - levelTimer_lastTime > levelTimer_rate && allAliensDeadAndExploded())
					{
						initAliens();
						levelTimer_lastTime = now;
						
						gameLevel++;
						msgScreen.newMsg("CPU_level", "Game Level increased to " + gameLevel);
						msgScreen.newMsg("CPU_asteroids", "Ateroids rate increased to " + asteroid_rate);
						
						
						asteroid_rate -= 0.1*asteroid_rate;
						
						if(asteroid_rate <= 2.5)
							asteroid_rate = 2.5; 
						
					}
					
					// Move the player's ship with its missiles
					moveAllSpaceShips();
					//moveLifeObjects(); 
					//moveBombObjects();
					moveAsteroids();
					
					checkCollisions(); // Check Collisions
				}
				
				T.sleep(1);
			}
			catch(Exception e) {}

			repaint();
			
		}
	}
	
	public void initStarField()
	{
		stars = new StarField(10, WIDTH, HEIGHT, randomGenerator);
	}
	
	public void initBombSpaceObjects() 
	{
		bombArr = new ArrayList<BombSpaceObj>();
	}
	
	public void initAsteroids()
	{
		asteroidArr = new ArrayList<Asteroid>();
	}
	
	public Asteroid newAsteroid()
	{
		randInt = randomGenerator.nextInt(2);
		
		asteroidX = WIDTH*randInt - randInt;
		asteroidY = randomGenerator.nextInt(HEIGHT);
		asteroidAngle = SpaceObject.angleWithRespectToPoint(asteroidX, asteroidY, WIDTH/2, HEIGHT/2);
		asteroidVersion = randomGenerator.nextInt(3) + 1; // 1 to 3
		asteroidLvl = 2; // 2 to 3
		asteroidRateDeg = 2*Asteroid.ASTEROID_MAX_ROTATION*randomGenerator.nextDouble() - Asteroid.ASTEROID_MAX_ROTATION;
		asteroidVel = Asteroid.ASTEROID_MAX_VEL/4*(3*randomGenerator.nextDouble() + 1);
		
		//System.out.println("Asteroid: vel = " + vel + ". rateDeg = " + rateDeg);
		
		return new Asteroid("asteroid" + asteroidVersion + "Lvl" + asteroidLvl + ".png", asteroidX, asteroidY, asteroidVel, asteroidAngle, asteroidRateDeg, 100, asteroidLvl);
	}
	
	public void asteroidCollides(Asteroid asteroid)
	{
		asteroid.destroy();
		asteroid.explode();
		
		Asteroid ast;
		double rateDeg;
		double angle;
		int lvl = asteroid.getAsteroidLvl();
		
		int innerAsteroids = asteroid.getInnerAsteroids();
		
		for(int i = 0; i < innerAsteroids; i++)
		{
			rateDeg = randomGenerator.nextDouble();
			angle = randomGenerator.nextInt(359) - 179;
			
			ast = new Asteroid("asteroid1Lvl" + (lvl-1) + ".png", (int) asteroid.getPosX(), (int) asteroid.getPosY(), asteroid.getVelocity(), angle, rateDeg, asteroid.getAsteroidDmg()/2, lvl-1);
			asteroidArr.add(ast);
		}
	}
	
	public LifeSpaceObj newLifeObj()
	{
		int x = randomGenerator.nextInt(WIDTH);
		int y = 0;
		
		return new LifeSpaceObj(x, y, SpaceObject.OBJ_MAX_VEL, 90);
	}
	
	public BombSpaceObj newBombObj()
	{
		int x = randomGenerator.nextInt(WIDTH);
		int y = 0;
		
		return new BombSpaceObj(x, y, SpaceObject.OBJ_MAX_VEL, 90);
	}
	
	public PlayerShip initUserShip()
	{
		PlayerShip player = new PlayerShip(WIDTH/2, HEIGHT/2);
		return player;
	}
	
	public void initAliens()
	{
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
			createAlienShip(spaceShipArr, x, y, SpaceShip.SHIP_MAX_VEL, angle); // Create alien
		}
	}
	
	public void initLifeObjects()
	{
		lifeObjArr = new ArrayList<LifeSpaceObj>();
		//helperObj_rate = 30;
	}
	
	private void createAlienShip(ArrayList<SpaceShip> arr, int x, int y, double vel, double angle)
	{
		arr.add(new AlienShip(gameLevel, x, y, vel, angle));
	}
	
	public void moveSpaceShipWeapons(SpaceShip aShip)
	{
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
	}
	
	public void moveLifeObjects()
	{
		int i = 0;
		while(i < lifeObjArr.size() && i >= 0)
		{
			thisLifeObj = lifeObjArr.get(i);
			thisLifeObj.move();
			
			if(!isWithinBounds(thisLifeObj))
			{
				lifeObjArr.remove(i);
				i--;
			}
			
			i++;
		}
	}
	
	public void moveBombObjects()
	{
		int i = 0;
		while(i < bombArr.size() && i >= 0)
		{
			thisBombSpaceObj = bombArr.get(i);
			thisBombSpaceObj.move();
			
			if(!isWithinBounds(thisBombSpaceObj))
			{
				bombArr.remove(i);
				i--;
			}
			
			i++;
		}
	}
	
	public void moveAsteroids()
	{
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
	}
	
	public boolean allAliensDeadAndExploded() // Makes sure explosions finish before the arrayList is recycled for the next batch of aliens
	{
		for(int i = 0; i < spaceShipArr.size(); i++)
		{
			spaceShip = spaceShipArr.get(i);
			
			if(spaceShip instanceof AlienShip)
			{
				aShip = (AlienShip) spaceShip;
				
				if(!aShip.isDestroyed())
					return false;
				else if(spaceShip.getExplObj() != null)
					if(spaceShip.getExplObj().isVisible())
						return false;
			}
		}
		
		return true;
	}
	
	public void moveAllSpaceShips() // Including user's craft
	{
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
		}
	}
	
	public void update(Graphics g)
	{
		g.setColor(Color.black);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		paint(g);
	}
	
	public void paint(Graphics g)
	{
		frameRate_tmp = new Date().getTime();
		frameRate = 1000.0/(frameRate_tmp - frameRate_last);
		frameRate_last = frameRate_tmp;
		
		Graphics2D g2d = (Graphics2D) g;
		super.paint(g2d);
		mousePointer = MouseInfo.getPointerInfo().getLocation();
		
		int i;
		
		// Draw Grid
		grid.draw(g2d);
		
		// Draw and move stars
		stars.moveAndDrawStarField(g2d);
		
		// Draw asteroids
		drawAsteroids(g2d);
		
		// Draw ships with their respective missiles
		for(i = 0; i < spaceShipArr.size(); i++)
		{
			spaceShip = spaceShipArr.get(i);
			
			if(!spaceShip.isDestroyed())
			{
				spaceShip.draw(g2d);
				spaceShip.drawShipData(g2d, 1);
				spaceShip.drawTrail(g2d);
				
				/*if(spaceShip.getQuadrant() != null)
					spaceShip.drawShipStatusMessage(g2d, spaceShip.getQuadrant().getQuadrantHash() + "", Color.green);
				else
					spaceShip.drawShipStatusMessage(g2d, "NULL Quadrant", Color.red);*/
				
				// Draw ship info if mouse is above it
				if(spaceShip.containsPoint(mousePointer) && !spaceShip.isSelected())
				{
					//spaceShip.getObjectTarget().activate();
					spaceShip.setMouseHover();
				}
				else if(!spaceShip.isSelected())
				{
					//spaceShip.getObjectTarget().rest();
					spaceShip.unsetMouseHover();
				}
					
			}
			else if(spaceShip.isDestroyed() && spaceShip.getExplObj() != null)
				if(spaceShip.getExplObj().isVisible())
					spaceShip.drawExplosion(g2d);
			
			// Draw missiles, bombs, seeker missiles, etc
			drawShipWeapons(g2d, spaceShip.getWeaponArr());
		}
		
		// Draw Space objects such as life or bomb objects
		drawSpaceObjects(g2d);
			
		// Draw Game Data (on Top of everything)
		drawInfo(g2d);
	
		// Draw Message screen
		msgScreen.draw(g2d);
		
		// Draw Front Panel
		skillsPanel.draw(g2d); // 1ms delay
		
		// Draw skill information if mouse is on top
		FrontPanel.checkSkillSpaceMouseHover(mousePointer);
		
		if(gamePaused)
			drawScreenMessage(g2d, "Game Paused", WIDTH/2 - 100, HEIGHT/2, Color.white);
	}
	
	private void drawShipWeapons(Graphics2D g2d, ArrayList<Weapon> arr)
	{
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
	}
	
	private void drawSpaceObjects(Graphics2D g2d) 
	{
		int i;
		
		// Draw the lifeObjects
		for(i = 0; i < lifeObjArr.size(); i++)
		{
			thisLifeObj = lifeObjArr.get(i);
			
			if(thisLifeObj.isVisible())
				thisLifeObj.draw(g2d);
		}
		
		// Draw the bombSpaceObjects
		for(i = 0; i < bombArr.size(); i++)
		{
			thisBombSpaceObj = bombArr.get(i);
			
			if(thisBombSpaceObj.isVisible())
				thisBombSpaceObj.draw(g2d);
		}
	}

	public void drawAsteroids(Graphics2D g2d)
	{
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
	}
	
	public static boolean isWithinBounds(SpaceObject obj)
	{
		int x = (int) obj.getPosX();
		int y = (int) obj.getPosY();
		
		if(x + obj.getImgWidth() >= 0 && x <= WIDTH &&
		   y + obj.getImgHeight() >= 0 && y <= HEIGHT)
				return true;
		else
				return false;
	}
	
	public void drawInfo(Graphics2D g2d)
	{
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
		
		/* Frame per second */
		//g2d.setColor(Color.white);
		//g2d.setFont(new Font("ARIAL", Font.PLAIN, 14));
		//g2d.drawString("Frame Rate: " + Math.round(frameRate) + " fps.", 20, 130);
	}
	
	public void drawScreenMessage(Graphics2D g2d, String str, int x, int y, Color c)
	{
		g2d.setColor(c);
		g2d.setFont(new Font("ARIAL", Font.BOLD, 30));
		
		g2d.drawString(str, x, y);
	}
	
	public void drawBar(Graphics2D g2d, int x, int y, int width, int height, double val, Color baseColor, Color topColor)
	{
		// Base color
		g2d.setColor(baseColor);
		g2d.fillRect(x, y, width, height);
		
		// Top Color
		g2d.setColor(topColor);
		g2d.fillRect(x, y, (int) (width*val), height);
	}
	
	public class TAdapter extends KeyAdapter
	{
		public void keyPressed(KeyEvent e)
		{
			if(e.getKeyCode() == KeyEvent.VK_P) // Pause
				gamePaused = !gamePaused;
			else if(e.getKeyCode() == KeyEvent.VK_ENTER)
				msgScreen.call();
			else
				craft.keyPressed(e);
		}
		
		public void keyReleased(KeyEvent e)
		{
			craft.keyReleased(e);
		}
	}
	
	public void checkCollisions()
	{
		int m;
		int l;
		int counter;
		int ast;
		int ast2;
		
		
		// For each SpaceShip
		for(int i = 0; i < spaceShipArr.size(); i++)
		{
			spaceShipA = spaceShipArr.get(i); // spaceShip object
			weaponArr = spaceShipA.getWeaponArr(); // Missiles

			// For all other spaceships
			for(int j = 0; j < spaceShipArr.size(); j++)
			{
				spaceShipB = spaceShipArr.get(j);
				
				// Check collision between ships A and B (criteria, shipA and B are different, and they are not both alienShips, and they are not destroyed)
				if(!spaceShipA.isDestroyed() && !spaceShipB.isDestroyed() && 
					spaceShipA != spaceShipB && (spaceShipA instanceof PlayerShip))
				{
					if(spaceShipA.isCollidingWith(spaceShipB))
					{
						spaceShipA.destroy();
						spaceShipB.destroy();
						
						if(spaceShipA instanceof PlayerShip || spaceShipB instanceof PlayerShip)
							inGame = false;
						
						if(spaceShipA instanceof AlienShip)
							alienCount--;
						if(spaceShipB instanceof AlienShip)
							alienCount--;
					}
				}
				
				// SpaceShipA Missiles. Always executes even if Spaceships are destroyed
				if(spaceShipA != spaceShipB)
				{
					counter = 0; // restart weapon Counter
					while(counter < weaponArr.size() && counter >= 0)
					{
						if(spaceShipB.isDestroyed())
							break;
						else
						{
							thisWeapon = weaponArr.get(counter);
		
							if(spaceShipB.isCollidingWith(thisWeapon) && !thisWeapon.isDestroyed())
							{
								spaceShipB.decreaseLife(thisWeapon.getDmg());
								thisWeapon.explode();
								
								if(thisWeapon instanceof Missile)
									spaceShipA.decreaseMissileCounter();
								
								// Alien missile collision logic
								if(spaceShipB instanceof AlienShip)
								{
									aShip = (AlienShip) spaceShipB;
									aShip.escapeFrom(spaceShipA);
									
									if(aShip.isDestroyed())
									{
										alienCount--;
										
										if(spaceShipA instanceof PlayerShip)
										{
											craft = (PlayerShip) spaceShipA;
											craft.addXP(aShip.getXP());
											
											int fuelGained = randomGenerator.nextInt(aShip.getXP()) + aShip.getXP()/2;
											craft.addBoosterFuel(fuelGained); // Replenish fuel based on alien xp content (50 units max)
											craft.newMessage("+" + fuelGained + " Fuel", Color.blue);
											
											// Play Combo Sound
											craft.updateLastKillTime(System.currentTimeMillis()/1000.0);
										}
									}
								}
								
								counter--;
							}
							
							counter++;
						}
					}	
				}
			}
			
			// Collision between asteroid and missiles, bombs, spaceships, and other asteroids
			for(ast = 0; ast < asteroidArr.size(); ast++) // For every asteroid
			{
				// Asteroids
				thisAsteroid = asteroidArr.get(ast);
				for(counter = 0; counter < weaponArr.size(); counter++) // for every missile
				{
					thisWeapon = weaponArr.get(counter);
					
					if(thisWeapon.isCollidingWith(thisAsteroid) && !thisWeapon.isDestroyed() && !thisAsteroid.isDestroyed())
					{
						thisWeapon.destroy();
						thisWeapon.explode();
						
						if(thisWeapon instanceof Missile)
							spaceShipA.decreaseMissileCounter();
						
						asteroidCollides(thisAsteroid);
						
						break;
					}
				}
				
				// Check collisions between spaceships and asteroids
				if(!thisAsteroid.isDestroyed())
				{
					if(spaceShipA.isCollidingWith(thisAsteroid) && !spaceShipA.isDestroyed())
					{
						spaceShipA.decreaseLife(thisAsteroid.getAsteroidDmg());		
						asteroidCollides(thisAsteroid);
						
						if(spaceShipA.isDestroyed() && spaceShipA instanceof AlienShip)
							alienCount--;
					}
				}
				
				// Collision with other asteroids
				if(!thisAsteroid.isDestroyed() && thisAsteroid.getInnerAsteroids() > 1)
				{
					Asteroid otherAsteroid;
					for(ast2 = 0; ast2 < asteroidArr.size(); ast2++)
					{
						otherAsteroid = asteroidArr.get(ast2);
						if(thisAsteroid.isCollidingWith(otherAsteroid) && !otherAsteroid.isDestroyed() && otherAsteroid != thisAsteroid)
						{
							asteroidCollides(thisAsteroid);
							asteroidCollides(otherAsteroid);
							
							break;
						}
					}
				}
				
			} // End of asteroid collision check
			
			// Check collision of spaceShipA with helper objects like life points or weapon enhancements
			counter = 0;
			while(counter < lifeObjArr.size() && counter >= 0)
			{
				thisLifeObj = lifeObjArr.get(counter);
				
				if(spaceShipA.isCollidingWith(thisLifeObj) && !spaceShipA.isDestroyed())
				{
					spaceShipA.addLife(thisLifeObj.getLifeContent());
					lifeObjArr.remove(counter);
					counter--;
				}
				
				counter++;
			}
			
			// Check collision with bomb helper object
			counter = 0;
			while(counter < bombArr.size() && counter >= 0)
			{
				thisBombSpaceObj = bombArr.get(counter);
				
				if(spaceShipA.isCollidingWith(thisBombSpaceObj) && !spaceShipA.isDestroyed())
				{
					spaceShipA.addBombs(thisBombSpaceObj.getBombContent());
					spaceShipA.newMessage("+" + thisBombSpaceObj.getBombContent() + " Bombs", Color.yellow);
					
					bombArr.remove(counter);
					counter--;
				}
				
				counter++;
			}	
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) 
	{
		
	}

	private SpaceObject checkMouseObjectSelection(Point mousePointer)
	{
		boolean selection = false;
		
		// Check all spaceShips
		for(SpaceShip ship : spaceShipArr)
		{
			if(ship.containsPoint(mousePointer) && !selection && ship != craft)
			{
				ship.select();
				objSelected = ship;
				selection = true;

				//System.out.println("Ship Selected!");
			}
			else
				ship.unselect();
		}
		
		// Check Asteroids
		for(Asteroid ast : asteroidArr)
		{
			if(ast.containsPoint(mousePointer) && !selection)
			{
				ast.select();
				objSelected = ast;
				selection = true;
			}
			else
				ast.unselect();
		}

		return objSelected;
	}
	
	@Override
	public void mouseEntered(MouseEvent e) 
	{
		// TODO Auto-generated method stub
	}

	@Override
	public void mouseExited(MouseEvent e) 
	{
		// TODO Auto-generated method stub
		
	}

	// Much better event handler (in contrast to mouseClicked...)
	public void mousePressed(MouseEvent e) 
	{
		boolean skillSelected = skillsPanel.setMouseClickLocation(mousePointer.getLocation());	
		
		// Check if mouse selected any ship, and assign as user target
		if(!skillSelected)
			craft.setTarget(checkMouseObjectSelection(mousePointer));	
	}

	@Override
	public void mouseReleased(MouseEvent arg0) 
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		
	}


}
