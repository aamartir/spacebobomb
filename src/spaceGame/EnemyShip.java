package spaceGame;

import java.util.ArrayList;
import java.util.Random;

public class EnemyShip extends SpaceShip
{
	// Motion parameters
	private double directionAngle; // <- What is this for?

	// Attack Parameters
	private int strategy;
	private SpaceShip attackTarget;
	private int rateOfFire; // in Milliseconds
	
	public static final int CRUISE_STRATEGY            = 0;
	public static final int FOLLOW_STRATEGY            = 1;
	public static final int FOLLOW_AND_ATTACK_STRATEGY = 2;
	public static final int EVADE_STRATEGY             = 3;
	public static final int EVADE_AND_ESCAPE_STRATEGY  = 4;
	
	public EnemyShip( double x, double y, double v_x, double v_y, double initialAngle )
	{
		super( SpaceShip.SPACESHIP_03, x, y, v_x, v_y, initialAngle, SpaceShip.SPACESHIP_MASS );
		directionAngle = initialAngle;
	}
	
	public double getDirectionAngle()
	{
		return directionAngle;
	}
	
	public boolean isAttacking()
	{
		return (strategy == FOLLOW_AND_ATTACK_STRATEGY);
	}
	
	public void attack( SpaceShip target )
	{
		strategy = FOLLOW_AND_ATTACK_STRATEGY;
	}
	
	public void setDirectionAngle( double angle )
	{
		directionAngle = super.checkAngleBoundaries(angle);
	}
	
	// AI for firing missiles (based on experience)
	public void thinkAndFire( SpaceShip other )
	{
		double dist = distanceWithRespectTo( other );
		
		// If ship is following player, shoot (based on experience)
		if(isFollowing() && isAttacking() && !isEscaping())
		{
			// Only shoot if in sight
			//if(Math.abs(getClosestAngle(getAngle(), angleWithRespectTo(other))) < 50/experience && dist < 200)		
			{
			//	super.fireMissile();
			}
		}
	}
	
	public boolean directionAngleReached()
	{
		if( Math.abs(getAngle() - getDirectionAngle()) < 10 )
			return true;
		else
			return false;
	}
	
	public void cruise( SpaceShip aShip, Random randGenerator, int WIDTH, int HEIGHT )
	{
		double posX = getPosX(); // current position and direction
		double posY = getPosY();
		double randAngle;
		
		if( directionAngleReached() )
		{
			randAngle = 180*SpaceObject.randGenerator.nextDouble()-90;
			//setTargetAngle(getAngle() + randAngle);
		}

		// Check that the Alien ships are within the boundaries of the board
		if(posX - 2*getImgWidth() <= 0 || posX + 2*getImgWidth() >= WIDTH ||
		   posY <= 0 || posY + 2*getImgHeight() >= HEIGHT)
		{
			setDirectionAngle( angleWithRespectTo(aShip) );
		}
	}
	
	public void avoidCollisionWith(SpaceShip other)
	{
		strategy = EVADE_STRATEGY;
		setDirectionAngle(angleWithRespectTo(other) - 180);
	}
	
	// Follows another ship. Based on experience
	public void followOther(SpaceShip other)
	{
		double angle = super.angleWithRespectTo(other);
		double var = 100*randGenerator.nextDouble()-50;
		
		if( directionAngleReached() )
			setDirectionAngle(angle + var);
		
		strategy = FOLLOW_STRATEGY;
	}
	
	public void followAndDestroy(SpaceShip other)
	{
		strategy = FOLLOW_AND_ATTACK_STRATEGY;
		followOther(other);
		thinkAndFire(other);
	}
	
	public void escapeFrom(SpaceShip other)
	{
		double dist = distanceWithRespectTo(other);

		strategy = EVADE_AND_ESCAPE_STRATEGY;
		
		if(dist > 200 || other.isDestroyed()) // Stop escaping if the distance is long enough
			strategy = CRUISE_STRATEGY;
		else // Otherwise move as far away as possible from other
			setDirectionAngle( angleWithRespectTo(other) - 180 );
	}
	
	public boolean isFollowing()
	{
		return (strategy == FOLLOW_STRATEGY);
	}
	
	public void rotate()
	{
		double err = directionAngle - getAngle();
		
		// Prevents sudden jerks due to angle-boundary transitions (i.e. from 180 to -179 deg or viseversa)
		if( err > 0 )
			super.rotate( -getMaxTurningRate() );
		else if( err < 0 )
			super.rotate( getMaxTurningRate() );
	}

	public boolean isEscaping() 
	{
		return ( strategy == EVADE_AND_ESCAPE_STRATEGY );
	}

	public void setStrategy( int newStrategy )
	{
		strategy = newStrategy;
	}
	
	public int getStrategy() 
	{
		return strategy;
	}
	
	public static void createEnemyShip( ArrayList<SpaceShip> arr, 
			                            double posX, double posY, 
			                            double speedX, double speedY, 
			                            double initialAngle )
	{
		arr.add( new EnemyShip(posX, posY, speedX, speedY, initialAngle) );
	}
	
	// Initial position is not given. Spawn at random location in map 
	// between {minX, minY} and {maxX, maxY}
	public static void createEnemyShip( ArrayList<SpaceShip> arr,
			                            double speedX, double speedY,
			                            double initialAngle,
			                            double minX, double minY, double maxX, double maxY )
	{
		// random point in space within given boundaries
		double posX = SpaceObject.randGenerator.nextDouble()*(maxX - minX) + minX;
		double posY = SpaceObject.randGenerator.nextDouble()*(maxY - minY) + minY;
		
		createEnemyShip( arr, posX, posY, speedX, speedY, initialAngle );
	}
}
