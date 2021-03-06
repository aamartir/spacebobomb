package spaceGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.Rectangle2D.Double;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import Mathematics.MyMath;
import Mathematics.Vector2D;
import com.ship.effects.StatusMessage;
import com.weapons.Missile;
import com.weapons.Weapon;

public class SpaceShip extends SpaceObject
{
	// Motion dynamics
	public static final double SPACESHIP_TURNING_RATE            = 0.2;
	public static final double SPACESHIP_MAX_TURNING_THRUST      = 0.0003;
	public static final double SPACESHIP_MAX_SPEED               = 0.15;    // Natural velocity (without boosters)
	public static final double SPACESHIP_MAX_THRUST              = 0.0001;
	public static final double SPACESHIP_THRUST_FRICTION         = 0.0005;  // Affects linear motion
	public static final double SPACESHIP_ANGULAR_THRUST_FRICTION = 0.0025;  // Affects angular motion
	public static final double SPACESHIP_MASS                    = 1.0;
	public static final double SPACESHIP_MAX_HP                  = 100;
	public static final double SPACESHIP_MAX_FUEL                = 100;
	public static final double SPACESHIP_MAX_FUEL_CONSUMPTION    = 0.00075; // units of fuel per unit of thrust per second
	
	// Drawing constants
	private static final double LIFE_BAR_WIDTH     		 = 50.0;
	private static final double LIFE_BAR_HEIGHT    		 = 5.0;
	private static final double FUEL_BAR_WIDTH           = LIFE_BAR_WIDTH;
	private static final double FUEL_BAR_HEIGHT          = LIFE_BAR_HEIGHT;
	private static final double EXHAUST_MAX_LENGTH 	     = 10.0;
	private static final double EXHAUST_MAX_WIDTH  	     = 5.0;
	private static final double DUAL_EXHAUST_SEPARATION  = 6.0;
	
	// Weapons constants
	public  static final int    DT_TO_NEXT_MISSILE_MS    = 200;
	
	// Possible spaceship bodies
	public static final String SPACESHIP_01  = "shipBlue.png";
	public static final String SPACESHIP_02  = "shipStyle3_small.png";
	public static final String SPACESHIP_03  = "shipRed.png";
		
	private AffineTransform transf;
	private static AffineTransform savedTransform;
	
	// Possible spaceship booster images
	//public static final String SHIP_EXHAUST_01 = "exhaust_01.png";
	
	// Images associated with spaceShip
	// private ImageIcon exhaustImg;
	
	// SpaceShip's current parameters
	private double thrust;
	private double angularThrust;
	private double life;
	private double fuel;
	
	// SpaceShip's absolute parameters
	private double maxLife;
	private double maxSpeed;
	private double maxThrust;
	private double maxAngularThrust; // angular acceleration
	private double maxTurningRate; // angular speed
	private double maxFuelCapacity;
	
	// Weapons instance variables
	private int missileRateOfFireMillis;
	private long timeOfLastMissileMillis;
	//private ArrayList<Weapon> weapons;
	
	// Status messages to display on top of spaceship 
	private ArrayList<StatusMessage> statusMessages;

	public SpaceShip( String spaceshipType, int objType, double x, double y, double v_x, double v_y, double initialAngle, double mass )
	{
		super( spaceshipType, objType, x, y, v_x, v_y, initialAngle, 0, mass );

		// Set exhaust image
		//exhaustImg = SpaceObject.getImgResource( SHIP_EXHAUST_01 );
		
		// Spaceship parameters
		//weapons                 = new ArrayList<Weapon>();
		statusMessages			= new ArrayList<StatusMessage>();
		
		missileRateOfFireMillis = DT_TO_NEXT_MISSILE_MS;
		timeOfLastMissileMillis = System.currentTimeMillis();
		
		maxLife          = SPACESHIP_MAX_HP;
		maxFuelCapacity  = SPACESHIP_MAX_FUEL;
		maxSpeed         = SPACESHIP_MAX_SPEED;
		maxThrust 	     = SPACESHIP_MAX_THRUST;
		maxTurningRate   = SPACESHIP_TURNING_RATE; // degrees per second 
		maxAngularThrust = SPACESHIP_MAX_TURNING_THRUST;
		life             = maxLife;
		fuel             = maxFuelCapacity;
		thrust           = 0;
		angularThrust    = 0;		
	}
	
	public int getMissileRateOfFire()
	{
		return missileRateOfFireMillis;
	}
	
	public void setMissileRateOfFire( int rof )
	{
		if( rof > 0 )
			missileRateOfFireMillis = rof;
	}
	
	public double getMaxTurningRate()
	{
		return maxTurningRate;
	}

	public double getMaxThrust()
	{
		return maxThrust;
	}
	
	public double getMaxSpeed()
	{
		return maxSpeed;
	}
	
	public void setMaxSpeed( double newMaxSpeed )
	{
		if( newMaxSpeed > 0 && newMaxSpeed <= SPACESHIP_MAX_SPEED )
			maxSpeed = newMaxSpeed;
	}
	
	public void setSpaceShipThrust( double newThrust )
	{
		if( newThrust > maxThrust )
			newThrust = maxThrust;
		else if( newThrust < 0 )
			newThrust = 0;
		
		thrust = newThrust;
	}

	public double getMaxAngularThrust()
	{
		return maxAngularThrust;
	}
	
	public void setSpaceShipAngularThrust( double newAngularThrust )
	{
		angularThrust = newAngularThrust;
		
		if( angularThrust > maxAngularThrust )
			angularThrust = maxAngularThrust;
		else if( angularThrust < -maxAngularThrust )
			angularThrust = -maxAngularThrust;
		
		super.setRotationDegPerSecSquared( angularThrust );
	}
		
	public void updateSpaceShipMotion( double dt )
	{
		// Make sure we have fuel
		if( fuel <= 0 )
		{
			thrust = 0;
			super.setRotationDegPerSecSquared( 0 );
		}
		
		// Set acceleration (based on angle). Need fuel for this.
		super.setAcceleration( thrust * Math.cos(Math.toRadians(getAngle())),
							   thrust * Math.sin(Math.toRadians(getAngle())) );
		
		// Call the method of the super class
		super.updateSpaceObjectMotion( maxSpeed, maxTurningRate, SPACESHIP_THRUST_FRICTION, SPACESHIP_ANGULAR_THRUST_FRICTION, dt );
		
		// Consume fuel if spaceship is using forward thrusters
		if( thrust > 0 )
			consumeFuel( SPACESHIP_MAX_FUEL_CONSUMPTION * MyMath.map( thrust, SPACESHIP_MAX_THRUST ) * dt );
	}
	
	public double getSpaceShipAcceleration()
	{
		return ( super.getAccelMagnitude() );
	}
	
	public void destroySpaceShip()
	{
		this.setCurrentLife(0);
		super.destroySpaceObject( true );
	}
	
	/*
	public double getFuelCapacity()
	{
		return MAX_BOOSTER_FUEL;
	}
	*/
	
	public void addFuel( double val ) // Function can also be used to subtract fuel
	{
		if( val + fuel > maxFuelCapacity )
			val = (maxFuelCapacity - fuel);
		
		fuel += val;
		newStatusMessage( getPosX() - getImgWidth()/2.0, getPosY(), "+" + val+ " Fuel", Color.YELLOW );
	}
	
	public double getCurrentFuel()
	{
		return fuel;
	}
	
	public double getFuelCapacity()
	{
		
		return maxFuelCapacity;
	}
	
	public void consumeFuel( double val )
	{
		fuel -= val;
		if( fuel < 0 )
			fuel = 0;
	}

	// Use/stop boosters affect the velocity of the craft
	public void useBoosters()
	{
		/*
		if(hasBoosterFuel())
		{
			FrontPanel.getSkillSpace(Skill.BOOSTERS_SKILL).getSkill().setAvailable(true);
			FrontPanel.getSkillSpace(Skill.BOOSTERS_SKILL).getSkill().setImage(Skill.BOOSTERS_ACT_IMG);
			usingBoosters = true;
		}
		else
		{
			FrontPanel.getSkillSpace(Skill.BOOSTERS_SKILL).getSkill().setAvailable(false);
			FrontPanel.getSkillSpace(Skill.BOOSTERS_SKILL).getSkill().setImage(Skill.BOOSTERS_IMG);
			usingBoosters = false;
		}
		*/
	}
	
	public void stopBoosters()
	{
		/*
		FrontPanel.getSkillSpace(Skill.BOOSTERS_SKILL).getSkill().setImage(Skill.BOOSTERS_IMG);
		usingBoosters = false;
		*/
	}
	
	/*
	public double getFuel()
	{
		return boosterFuel;
	}
	
	public void setFuel(int val)
	{
		if(val >= 0 && val <= MAX_BOOSTER_FUEL)
			boosterFuel = val;
	}
	*/
	
	public boolean hasStatusMessages()
	{
		return ( statusMessages.size() > 0 );
	}
	
	public void flushAllStatusMessages()
	{
		if( hasStatusMessages() )
			statusMessages.clear();
	}
	
	public void newStatusMessage( double x, double y, String msg, Color c )
	{
		statusMessages.add( new StatusMessage( x, y, msg, c) );
	}
	
	/*public Explosion getExplotionObj()
	{
		return expl;
	}*/
	
	public void updateSpaceShipStatusMessages( double dt )
	{
		// Update status messages
		for( int i = 0; i < statusMessages.size(); i++ )
		{
			if( !statusMessages.get(i).isCompleted() )
				statusMessages.get(i).updateStatusMessage( dt );
		}
	}
	
	public void rotate()
	{
		//super.rotate(rotation);
	}
	
	//public ArrayList<Weapon> getWeaponsFired()
	{
	//	return weapons;
	}
	
	public void fireMissile()
	{
		Weapon w;
		
		if( System.currentTimeMillis() - timeOfLastMissileMillis >= missileRateOfFireMillis )
		{
			double r = Math.sqrt( super.getImgWidth()*super.getImgWidth()/4.0 + super.getImgHeight()*super.getImgHeight()/4.0 );
			w = new Missile(this,
			                super.getPosX() + super.getImgWidth()/2.0 + (r+1)*Math.cos(Math.toRadians(super.getAngle())), 
			  	 			super.getPosY() + super.getImgHeight()/2.0 + (r+1)*Math.sin(Math.toRadians(super.getAngle())),
				 			super.getVelocityX(), 
				 			super.getVelocityY(),
				 			super.getAngle() );
			
			// Put missile into spaceObject data structure	 		
			Game.spaceObjects.put( w.getObjectID(), w );

			timeOfLastMissileMillis = System.currentTimeMillis();
		}
	}
	
	public void fireBomb()
	{
		/*
		if(bombCounter < MAX_BOMBS && !isDestroyed())
		{
			weaponArr.add(new PlasmaBomb((int) (super.getPosX() + super.getImgWidth()/2), 
									 	 (int) (super.getPosY() + super.getImgHeight()/2), 
									 	  super.getVelocity(), 
									 	  super.getAngle()));
			
			bombCounter++;
		}
		
		// If there's no more area bombs left, deactivate the skill panel icon
		if(!hasBombsLeft() || isDestroyed())
			FrontPanel.getSkillSpace(Skill.AREA_BOMB_SKILL).getSkill().setAvailable(false);
		*/
	}
	
	public void fireSeekingMissile()
	{
		/*
		if(seekMissileCounter < MAX_SEEK_MISSILES && !isDestroyed())
		{
			if(target != null)
			{
				weaponArr.add(new SeekMissile((int) (super.getPosX() + super.getImgWidth()/2), 
										 	 (int) (super.getPosY() + super.getImgHeight()/2), 
										 	  super.getVelocity(), 
										 	  super.getAngle(),
										 	  target));
				
				seekMissileCounter++;
			}
			else
				System.out.println("No Target Selected!");
		}
		
		if(!hasSeekMissilesLeft() || isDestroyed())
			FrontPanel.getSkillSpace(Skill.SEEK_MISSILE_SKILL).getSkill().setAvailable(false);
		*/
	}
	
	/*public void drawExplotion(Graphics2D g2d)
	{
		expl.setPosX(getwPosX());
		expl.setPosY(getPosY());
		expl.drawExplotion(g2d);
	}*/
	
	// This needs optimizing
	public void drawThrustExhaust( Graphics g )
	{
		double rotThrust;
		double leftExhaust;
		double rightExhaust;
		
		Graphics2D g2d = ( Graphics2D )g;
		Shape exhaustShape;
		savedTransform = g2d.getTransform();

		// Left exhaust
		g.setColor( new Color(0, 191, 255) );
		
		// Scale rotation thrust to 0.5 of LINEAR THRUST
		rotThrust = getRotationDegPerSecSquared();
		if( Math.abs(rotThrust) > 0 )
			rotThrust = (SpaceShip.SPACESHIP_MAX_THRUST/2.0)*(rotThrust/SpaceShip.SPACESHIP_MAX_TURNING_THRUST);
		
		// Apply rotation matrix
		g2d.rotate(Math.toRadians( super.getAngle() ), 
		           super.getPosX() + super.getImgWidth()/2,
		           super.getPosY() + super.getImgHeight()/2);
		
		leftExhaust  = EXHAUST_MAX_LENGTH*(thrust + rotThrust)/SpaceShip.SPACESHIP_MAX_THRUST;
		rightExhaust = EXHAUST_MAX_LENGTH*(thrust - rotThrust)/SpaceShip.SPACESHIP_MAX_THRUST;
		
		// Left exhaust
		exhaustShape = new Ellipse2D.Double( super.getPosX() - leftExhaust, 
			                                 super.getPosY() + super.getImgHeight()/2.0 - EXHAUST_MAX_WIDTH/2.0 - DUAL_EXHAUST_SEPARATION, 
			                                 leftExhaust,   // Width 
			                                 EXHAUST_MAX_WIDTH ); // height
		
	    g2d.fill( exhaustShape );
	    g2d.draw( exhaustShape ); //g2d.draw( transf.createTransformedShape(r1) );
	
		// Right exhaust
		exhaustShape = new Ellipse2D.Double( super.getPosX() - rightExhaust, 
                                             super.getPosY() + super.getImgHeight()/2.0 - EXHAUST_MAX_WIDTH/2.0 + DUAL_EXHAUST_SEPARATION, 
                                             rightExhaust,   // Width 
                                             EXHAUST_MAX_WIDTH ); // height
		
		g2d.fill( exhaustShape );
		g2d.draw( exhaustShape ); //g2d.draw( transf.createTransformedShape(r1) ); 
		
		// Restore transformation matrix
		g2d.setTransform( savedTransform );
	}
	
	public void drawSpaceShip( Graphics g )
	{
		if( super.isVisible() )
		{
			// Draw Object
			super.drawSpaceObject( g );
			
			// Draw its life bar atop it (This method is efficient)
			drawLifeBar( g );
	
			// Draw fuel bar
			drawFuelBar( g );
			
			// Draw exhausts (This method is NOT very efficient)
			drawThrustExhaust( g );
			
			// Draw status messages
			if( hasStatusMessages() )
				drawStatusMessages( g );
			
			/*if(this instanceof AlienShip)
			{
				AlienShip a = (AlienShip)this;
				g2d.setColor(Color.gray);
				g2d.drawString("Heading( " + (int)getAngle() + ")", (int)getPosX(), (int)getPosY()-getImgHeight()/2);
				g2d.drawString("TargetHeading( " + (int)a.getTargetAngle() + ")", (int)a.getPosX(), (int)a.getPosY()-a.getImgHeight());	
			}*/
		}
	}
	
	/*private void drawBoosterFuel(Graphics2D g2d)
	{
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(new Color(255, 0, 0, 255)); //red base
		g2d.fillRect((int)(getPosX()-getImgWidth()/2), (int)(getPosY()+getImgHeight())+5, 50, 5);
		g2d.setColor(new Color(0, 255, 0, 255));
		g2d.fillRect((int)(getPosX()-getImgWidth()/2), (int)(getPosY()+getImgHeight())+5, (int) (50*getFuel()/getFuelCapacity()), 5);
	}*/
	
	private void drawBombsLeft(Graphics2D g2d) 
	{
		/*
		int xIncr = 0;
		
		for(int i = 0; i < getNumberOfBombsLeft(); i++)
		{
			g2d.setColor(Color.yellow);
			g2d.fillOval((int) (getPosX()-getImgWidth()/2 + xIncr), (int) (getPosY()-getImgHeight()/4-10), 5, 5);
			xIncr += 8;
		}
		*/
	}

	// Needs optimizing
	private void drawLifeBar( Graphics g )
	{
		Shape base, lifeBar;
		Graphics2D g2d = (Graphics2D) g;
		
		base    = new Rectangle2D.Double( getPosX()-getImgWidth(), getPosY()-16, LIFE_BAR_WIDTH, LIFE_BAR_HEIGHT );
		lifeBar = new Rectangle2D.Double( getPosX()-getImgWidth(), getPosY()-16, LIFE_BAR_WIDTH * getCurrentLife()/getMaxLife(), LIFE_BAR_HEIGHT );
		
		g2d.setColor( Color.RED );
		g2d.fill( base );
		
		g2d.setColor( Color.GREEN );
		g2d.fill( lifeBar );
	}
	
	private void drawFuelBar( Graphics g )
	{
		Shape base, fuelBar;
		Graphics2D g2d = (Graphics2D) g;
		
		base    = new Rectangle2D.Double( getPosX()-getImgWidth(), getPosY()-10, FUEL_BAR_WIDTH, FUEL_BAR_HEIGHT );
		fuelBar = new Rectangle2D.Double( getPosX()-getImgWidth(), getPosY()-10, FUEL_BAR_WIDTH * getCurrentFuel()/getFuelCapacity(), FUEL_BAR_HEIGHT );
		
		g2d.setColor( Color.RED );
		g2d.fill( base );
		
		g2d.setColor( Color.YELLOW );
		g2d.fill( fuelBar );
	}
	
	private void drawIntention(Graphics2D g2d, int intent)
	{
		// Red seekAndDestroy (0)
		// Green Randomly moving (1)
		// Blue escaping (2)
		
		switch(intent)
		{
			case 0: // Attacking
				g2d.setColor(new Color(255, 0, 0, 255)); //red base
				break;
			case 1: // Moving randomly
				g2d.setColor(new Color(0, 255, 0, 255)); //red base
				break;
			case 2: // Escaping
				g2d.setColor(new Color(0, 0, 255, 255)); //red base
				break;
		}
		
		g2d.setStroke(new BasicStroke(3));
		g2d.fillArc((int)(getPosX()-getImgWidth()/2), (int)(getPosY()-getImgHeight()/4), 20, 20, 0, 360);
	}
	
	public void drawStatusMessages( Graphics g )
	{
		for( int i = 0; i < statusMessages.size(); i++ )
		{
			if( !statusMessages.get(i).isCompleted() )
				statusMessages.get(i).drawMessage( g );
			else
			{
				statusMessages.remove( i );
				continue;
			}
		}
	}		
	
	public void drawShipStatusMessage(Graphics g, String str, Color c)
	{
		g.setColor(c);
		g.drawString(str, (int) getPosX(), (int) getPosY() + getImgHeight());
	}
	
	public void drawShipStatusMessage(Graphics g, String str, Color c, int x, int y)
	{
		g.setColor(c);
		g.drawString(str, x, y);
	}
	
	public void decreaseLife( double val )
	{
		if( this.life > 0 )
		{
			this.life -= val;
			newStatusMessage( (getPosX() - getImgWidth()/2.0), getPosY(), "-" + val + " HP", StatusMessage.STATUS_MSG_RED_COLOR );
			
			if( this.life <= 0 && !isDestroyed() )
				destroySpaceShip();
		}
	}
	
	public double getMaxLife()
	{
		return maxLife;
	}
	
	public void addLife( double val )
	{
		if( (life + val) > maxLife )
			val = maxLife - life;
		
		life += val;
		
		// Display status message
		newStatusMessage( getPosX() - getImgWidth()/2.0, getPosY(), "+" + val + " HP", StatusMessage.STATUS_MSG_GREEN_COLOR );
	}
	
	public void increaseMaxLife( double val )
	{
		this.maxLife += val;
		this.life = this.maxLife;
		
		//newMessage("+" + val + " Life Increase!", new Color(0, 0, 255));
	}
	
	/*
	public int getNumOfMissilesLeft()
	{
		return max_missiles - missileCounter;
	}
	*/
	
	/*
	public int getNumOfMissilesFired()
	{
		return max_missiles - missileCounter;
	}
	*/
	
	public void resetMissileCounter()
	{
		//missileCounter = 0;
	}
	
	public void decreaseMissileCounter()
	{
		/*
		missileCounter--;
		
		if(missileCounter < 0)
			missileCounter = 0;
		*/
	}
	
	public void incrementNumOfMissiles()
	{
		//max_missiles++;
		//newMessage("+1 Missile", new Color(0, 0, 255));
	}
	
	public void setMaxMissiles(int val)
	{
		//max_missiles = val;
	}
	
	public int getMaxMissiles()
	{
		return 0;
		//return max_missiles;
	}
	
	/*public ArrayList<Missile> getMissiles()
	{
		return missileArr;
	}*/
	
	/*
	public ArrayList<Weapon> getWeaponArr()
	{
		return weaponArr;
	}
	*/
	
	public double getCurrentLife() 
	{
		return this.life;
	}
	
	public void setCurrentLife(int newLife)
	{
		this.life = newLife;
	}
	
	/*public boolean isInLineOfSight(SpaceShip other)
	{
		
	}*/
}
