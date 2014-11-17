package spaceGame;
import java.util.Random;

public class AlienShip extends SpaceShip
{
	private static Random randGenerator;
	private double targetAngle;
	private double DELTA_ANGLE = 0.0025;
	private double experience; // 5 difficulty levels. The higher the level, the more accuracy and rate of fire the ship has
	private final int MAX_EXPERIENCE = 10;
	private boolean follow;
	private boolean attack;
	private boolean escape;
	private int rateOfFire; // in Milliseconds
	private int xpValue;
	
	public AlienShip(int xp, int x, int y, double vel, double angle)
	{
		super(SpaceShip.PATH, SpaceShip.SPACESHIP_RED, x, y, vel, angle);
		
		randGenerator = new Random();
		
		targetAngle = angle;
		experience = (MAX_EXPERIENCE/2-1)*randGenerator.nextDouble() + 1;
		xpValue = (int) Math.round(5*experience);
		
		follow = false;
		attack = false;
		escape = false;
		
		//System.out.println(experience);
		super.setMaxMissiles(1);
	}
	
	public int maxExperience()
	{
		return MAX_EXPERIENCE;
	}
	
	public int getXP()
	{
		return xpValue;
	}
	
	public void setXP(int val)
	{
		xpValue = val;
	}
	
	public void setDelta(double newDelta)
	{
		DELTA_ANGLE = newDelta;
	}
	
	public double getDelta()
	{
		return DELTA_ANGLE;
	}
	
	public double getTargetAngle()
	{
		return targetAngle;
	}
	
	public boolean isAttacking()
	{
		return this.attack;
	}
	
	public void setAttack(boolean attack)
	{
		this.attack = attack;
	}
	
	public void setTargetAngle(double angle)
	{
		targetAngle = super.checkAngleBoundaries(angle);
	}
	
	// AI for firing missiles (based on experience)
	public void thinkAndFire(SpaceShip other)
	{
		double dist = distanceWithRespectTo(other);
		// If ship is following user, then shoot (based on experience)
		if(isFollowing() && isAttacking() && !isEscaping())
		{
			// Only shoot if in sight
			if(Math.abs(getClosestAngle(getAngle(), angleWithRespectTo(other))) < 50/experience && dist < 200)		
			{
				super.fireMissile();
			}
		}
	}
	
	public boolean targetAngleReached()
	{
		if(Math.abs(getAngle() - getTargetAngle()) < 10)
			return true;
		else
			return false;
	}
	
	public void cruise(SpaceShip aShip, Random randGenerator, int WIDTH, int HEIGHT)
	{
		double posX = getPosX(); // current position and direction
		double posY = getPosY();
		double randAngle;
		
		if(targetAngleReached())
		{
			randAngle = 180*randGenerator.nextDouble()-90;
			setTargetAngle(getAngle() + randAngle);
		}

		// Check that the Alien ships are within the boundaries of the board
		if(posX - 2*getImgWidth() <= 0 || posX + 2*getImgWidth() >= WIDTH ||
		   posY <= 0 || posY + 2*getImgHeight() >= HEIGHT)
		{
			setTargetAngle(angleWithRespectTo(aShip));
		}
	}
	
	public void avoidCollisionWith(SpaceShip other)
	{
		niceBehavior();
		setTargetAngle(angleWithRespectTo(other) - 180);
	}
	
	// Follows another ship. Based on experience
	public void followOther(SpaceShip other)
	{
		double angle = super.angleWithRespectTo(other);
		double var = 100*randGenerator.nextDouble()-50/(experience);
		
		if(targetAngleReached())
			setTargetAngle(angle + var);
		
		follow = true;
	}
	
	public void followAndDestroy(SpaceShip other)
	{
		this.attack = true;
		this.escape = false; //just in case
		
		followOther(other);
		thinkAndFire(other);
	}
	
	public void escapeFrom(SpaceShip other)
	{
		double dist = distanceWithRespectTo(other);
		
		this.escape = true;
		niceBehavior();
		
		if(dist > 200 || other.isDestroyed()) // Stop escaping if the distance is long enough
		{
			this.escape = false;
		}
		else // Otherwise move as far away as possible from other
		{
			setTargetAngle(angleWithRespectTo(other) - 180);
		}
	}
	
	public void niceBehavior()
	{
		this.follow = false;
		this.attack = false;
	}
	
	public boolean isFollowing()
	{
		return this.follow;
	}
	
	public void rotate()
	{
		double err = targetAngle - getAngle();
		
		// Prevents sudden jerks due to angle-boundary transitions (i.e. from 180 to -179 deg or viseversa)
		if(err > 180)
			err -= 360;
		else if(err < -180)
			err += 360;

		super.rotate(DELTA_ANGLE*err);
	}

	public boolean isEscaping() 
	{
		return this.escape;
	}

	public int getIntent() 
	{
		if(isAttacking())
			return 0;
		else if(isEscaping())
			return 1;
		else
			return 2;
	}
}
