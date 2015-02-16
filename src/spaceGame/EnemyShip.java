package spaceGame;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class EnemyShip extends SpaceShip
{
	public static final int    NO_STRATEGY                = 0;
	public static final int    CRUISE_STRATEGY            = 1;
	public static final int    FOLLOW_STRATEGY            = 2;
	public static final int    FOLLOW_AND_ATTACK_STRATEGY = 3;
	public static final int    EVADE_STRATEGY             = 4;
	public static final int    EVADE_AND_ESCAPE_STRATEGY  = 5;
	public static final int    GO_TO_POSITION_STRATEGY    = 6;
	
	public static final double AWARENESS_RADIUS           = 400.0;
	public static final double FOLLOW_RADIUS              = 400.0;
	public static final double ATTACK_RADIUS              = 400.0;
	public static final double PROXIMITY_RADIUS           = 300.0;	
	
	public static final int    ENEMY_MIN_RATE_OF_FIRE_MS  = 500;
	public static final int    ENEMY_MAX_RATE_OF_FIRE_MS  = 5000;
	
	// Attack Parameters
	private int rateOfFire; // in seconds (How many missiles it can fire per second)
	private int accuracy;   // Angle with respect to target. 0 < accuracy < 180
	
	// These variables dictate behavior of enemy
	private int strategy;
	private SpaceShip targetSpaceShip;
	
	// Automatic motion and AI fields
	private double targetPosX;
	private double targetPosY;
		
	// Variables used for motion
	private PID directionPID;
	private PID distancePID;
	
	public EnemyShip( double x, double y, double v_x, double v_y, double initialAngle )
	{
		super( SpaceShip.SPACESHIP_03, SpaceShip.ENEMYSHIP_OBJ_TYPE, x, y, v_x, v_y, initialAngle, SpaceShip.SPACESHIP_MASS );
		
		directionPID = new PID( 0.75, 0.0, 500.0 ); //( 0.01, 0.0, 5.0 ); 
		distancePID  = new PID( 0.05, 0.0, 2.5 ); 
		
		strategy = NO_STRATEGY;
		targetSpaceShip = null;
		accuracy = SpaceObject.randGenerator.nextInt(35) + 10; // 10 to 45 degrees

		// Set rate of fire
		setRateOfFire( ENEMY_MAX_RATE_OF_FIRE_MS );
		
		// Make the enemies a little slower ( 25% slower )
		super.setMaxSpeed( 0.80 * SpaceShip.SPACESHIP_MAX_SPEED );
	}
	
	public boolean isAttacking()
	{
		return (strategy == FOLLOW_AND_ATTACK_STRATEGY);
	}
	
	public void attack( SpaceShip target )
	{
		strategy = FOLLOW_AND_ATTACK_STRATEGY;
	}
	
	/*
	public void setDirectionAngle( double angle )
	{
		directionAngle = super.checkAngleBoundaries(angle);
	}
	*/
	
	// Enemy AI state machine
	public void AI( double dt )
	{
		double distErr;
		double distCorrection;
		double angleErr;
		double angleCorrection;
		
		switch( strategy )
		{
			case NO_STRATEGY:
			case CRUISE_STRATEGY:
				if( distanceWithRespectTo( Game.getPlayer()) <= AWARENESS_RADIUS )
					followAndDestroy( Game.getPlayer() );
				
				break;
			case GO_TO_POSITION_STRATEGY:
				// Angle error
				angleErr = getSupplementaryAngle( angleWithRespectTo(targetPosX, targetPosY) - getAngle() );
				angleCorrection = directionPID.update( angleErr, dt );

				// Set angular thrust to face playerShip
				if( Math.abs(angleErr) > 1.0 ) 
					setSpaceShipAngularThrust( SPACESHIP_MAX_TURNING_THRUST*angleCorrection/45.0 );
				else
					setSpaceShipAngularThrust( 0 );
				
				// Only start moving forward if pointing at the target spaceship
				if( Math.abs(angleErr) <= 5.0 )
				{
					// Distance error
					distErr = distanceWithRespectTo( targetPosX, targetPosY );
					distCorrection = distancePID.update( distErr, dt );
					
					// Move towards user (but avoid collision)
					if( distErr > PROXIMITY_RADIUS )
						setSpaceShipThrust( distCorrection );
					else
						setSpaceShipThrust( 0 );
				}
				else 
					setSpaceShipThrust( 0 );
				
				break;
			case FOLLOW_AND_ATTACK_STRATEGY:
			case FOLLOW_STRATEGY:
			{
				if( targetSpaceShip != null && distanceWithRespectTo(targetSpaceShip) <= AWARENESS_RADIUS )
				{
					// Angle error
					angleErr = getSupplementaryAngle( angleWithRespectTo( targetSpaceShip ) - getAngle() );
					angleCorrection = directionPID.update( angleErr, dt );
					
					// Set angular thrust to face playerShip
					if( Math.abs(angleErr) > 5 ) 
						setSpaceShipAngularThrust( SPACESHIP_MAX_TURNING_THRUST*angleCorrection/45.0 );
					else
						setSpaceShipAngularThrust( 0 );

					// Only start moving forward if pointing at the target spaceship
					if( Math.abs(angleErr) <= 45.0 )
					{
						// Distance error
						distErr = distanceWithRespectTo( Game.playerShip );
						distCorrection = distancePID.update( distErr, dt );

						// Move towards user (but avoid collision)
						if( distErr > PROXIMITY_RADIUS )
							setSpaceShipThrust( distCorrection );
						else
							setSpaceShipThrust( 0 );
						
						// Attack ???
						if( strategy == FOLLOW_AND_ATTACK_STRATEGY )
						{
							// Move towards user (but avoid collision)
							if( distErr <= ATTACK_RADIUS && Math.abs(angleErr) <= accuracy )
							{
								//System.out.println( "accuracy: " + accuracy  + ". rof: " + rateOfFire );
								if( super.fireMissile() )
									setRateOfFire( SpaceObject.randGenerator.nextInt(ENEMY_MAX_RATE_OF_FIRE_MS - ENEMY_MIN_RATE_OF_FIRE_MS) + ENEMY_MIN_RATE_OF_FIRE_MS );
							}
						}
					}
				}
				else
				{
					strategy = CRUISE_STRATEGY;
					targetSpaceShip = null;
					
					// Stop using thrusters
					super.setSpaceShipThrust( 0 );
					super.setSpaceShipAngularThrust( 0 );
				}
				
				break;
			}
		}
		
		// Update spaceship motion (in super class)
		super.updateSpaceShipMotion( dt );
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
	
	/*
	public boolean directionAngleReached()
	{
		if( Math.abs(getAngle() - getDirectionAngle()) < 10 )
			return true;
		else
			return false;
	}
	*/
	
	public void cruise( SpaceShip aShip, Random randGenerator, int WIDTH, int HEIGHT )
	{
		double posX = getPosX(); // current position and direction
		double posY = getPosY();
		double randAngle;
		
		//if( directionAngleReached() )
		{
			randAngle = 180*SpaceObject.randGenerator.nextDouble()-90;
			//setTargetAngle(getAngle() + randAngle);
		}

		// Check that the Alien ships are within the boundaries of the board
		if(posX - 2*getImgWidth() <= 0 || posX + 2*getImgWidth() >= WIDTH ||
		   posY <= 0 || posY + 2*getImgHeight() >= HEIGHT)
		{
			//setDirectionAngle( angleWithRespectTo(aShip) );
		}
	}
	
	public void avoidCollisionWith(SpaceShip other)
	{
		strategy = EVADE_STRATEGY;
		//setDirectionAngle(angleWithRespectTo(other) - 180);
	}
	
	// Follows another ship. Based on experience
	public void followSpaceShip( SpaceShip ship )
	{
		if( ship != null ) 
		{
			targetSpaceShip = ship;
			strategy = FOLLOW_STRATEGY;
		}
	}
	
	public void followAndDestroy( SpaceShip ship )
	{
		if( ship != null )
		{
			targetSpaceShip = ship;
			strategy = FOLLOW_AND_ATTACK_STRATEGY;
		}
	}

	public void escapeFrom(SpaceShip other)
	{
		double dist = distanceWithRespectTo(other);

		strategy = EVADE_AND_ESCAPE_STRATEGY;
		
		if(dist > 200 || other.isDestroyed()) // Stop escaping if the distance is long enough
			strategy = CRUISE_STRATEGY;
		//else // Otherwise move as far away as possible from other
		//	setDirectionAngle( angleWithRespectTo(other) - 180 );
	}
	
	public void goTo( double x, double y )
	{
		targetPosX = x;
		targetPosY = y;
		strategy = GO_TO_POSITION_STRATEGY;
	}
	
	public boolean isFollowing()
	{
		return (strategy == FOLLOW_STRATEGY);
	}
	
	/*
	public void rotate()
	{
		double err = directionAngle - getAngle();
		
		// Prevents sudden jerks due to angle-boundary transitions (i.e. from 180 to -179 deg or viseversa)
		if( err > 0 )
			super.rotate( -getMaxTurningRate() );
		else if( err < 0 )
			super.rotate( getMaxTurningRate() );
	}
	 */
	
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
	
	public void setRateOfFire( int newROF )
	{
		rateOfFire = newROF;
		super.setMissileRateOfFire( newROF );
	}
	
	public static EnemyShip createEnemyShip( double speedX, double speedY,
											 double initialAngle,
											 double offsetX, double offsetY,
			                                 double minDistanceFromPlayer, double maxDistanceFromPlayer )
	{
		// random point in space within given boundaries
		double r = SpaceObject.randGenerator.nextDouble()*(maxDistanceFromPlayer-minDistanceFromPlayer) + minDistanceFromPlayer;
		double theta = SpaceObject.randGenerator.nextDouble()*2.0*Math.PI;
		
		return new EnemyShip( r*Math.cos(theta) + offsetX, r*Math.sin(theta) + offsetY, speedX, speedY, initialAngle );
	}
	
	//public static void createEnemyShip( ArrayList<EnemyShip> arr,
	public static void createEnemyShip( HashMap<Integer, SpaceObject> map,
			                            double posX, double posY, 
			                            double speedX, double speedY, 
			                            double initialAngle )
	{
		EnemyShip ship = new EnemyShip( posX, posY, speedX, speedY, initialAngle );
		map.put( ship.getObjectID(), ship );
	}
	
	// Initial position is not given. Spawn at random location in map 
	// between {minX, minY} and {maxX, maxY}
	//public static void createEnemyShip( ArrayList<EnemyShip> arr,
	public static void createEnemyShip( HashMap<Integer, SpaceObject> map,
			                            double speedX, double speedY,
			                            double initialAngle,
			                            double minX, double minY, double maxX, double maxY )
	{
		// random point in space within given boundaries
		double posX = SpaceObject.randGenerator.nextDouble()*(maxX - minX) + minX;
		double posY = SpaceObject.randGenerator.nextDouble()*(maxY - minY) + minY;
		
		createEnemyShip( map, posX, posY, speedX, speedY, initialAngle );
	}
}
