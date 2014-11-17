package spaceGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import javax.swing.ImageIcon;

import panel.FrontPanel;
import panel.SkillSpace;
import panel.skills.Skill;

import com.ship.effects.Explosion;
import com.ship.effects.ShipStatusMessage;
import com.ship.effects.SpaceShipTrail;
import com.space.Asteroid;
import com.weapons.PlasmaBomb;
import com.weapons.Missile;
import com.weapons.SeekMissile;
import com.weapons.Weapon;

public class SpaceShip extends SpaceObject
{
	private static AffineTransform transform;
	
	// Target for follower missiles
	private SpaceObject target;
	private boolean targeted;
	
	// Weapons
	//private ArrayList<Missile> missileArr;
	//private ArrayList<PlasmaBomb> bombArr;
	//private ArrayList<SeekMissile> seekMissileArr;
	private ArrayList<Weapon> weaponArr;
	
	//private Explosion expl; // Explosion object if a ship if destroyed
	private SpaceShipTrail trail; // the trail ships make as they move through space (due to boosters)
	private int lastX;
	private int lastY;
	private int MAX_MISSILES;
	private int MAX_BOMBS;
	private int MAX_SEEK_MISSILES;
	private int bombCounter;
	private int missileCounter;
	private int seekMissileCounter;
	private int life;
	private int MAX_LIFE;
	
	// Booster variables
	private double boosterFuel; //
	private double MAX_BOOSTER_FUEL; //
	private boolean usingBoosters;
	private double BOOSTER_VEL_MULT = 2.0;
	
	private double rotation;
	
	// Message to display
	private ArrayList<ShipStatusMessage> msgArr;
	ShipStatusMessage thisMessage;
	ShipStatusMessage lastMessage;
	boolean next = false;
	
	public static final String PATH = "/resources/";
	public static final String SPACESHIP_BLUE = "shipBlue.png";
	public static final String SPACESHIP_GREEN = "shipStyle3_small.png";
	public static final String SPACESHIP_RED = "shipRed.png";
	public static final double TURNING_RATE = 0.3;
	public static final double SHIP_MAX_VEL = 0.12; // Natural velocity (without boosters)

	public SpaceShip(String PATH, String type, int x, int y, double vel, double angle)
	{
		super(PATH, type, x, y, vel, angle);
		
		super.setDestroyed(false);
		super.setMaxTurningRate(TURNING_RATE);
		
		//weapons
		//missileArr = new ArrayList<Missile>();
		//bombArr = new ArrayList<PlasmaBomb>();
		weaponArr = new ArrayList<Weapon>();
		
		msgArr = new ArrayList<ShipStatusMessage>();
		trail = new SpaceShipTrail(3);
		lastX = (int) getPosX();
		lastY = (int) getPosY();
		
		// Max number of each weapon type
		MAX_MISSILES = 5;
		MAX_SEEK_MISSILES = 5;
		MAX_BOMBS = 5;
		
		// Weapon Counter
		bombCounter = 0; //MAX_BOMBS;
		seekMissileCounter = 0;//MAX_SEEK_MISSILES;
		missileCounter = 0;
		
		MAX_LIFE = 100;
		life = MAX_LIFE;
		rotation = 0;
		targeted = false;
		
		/////
		MAX_BOOSTER_FUEL = 100;
		boosterFuel = MAX_BOOSTER_FUEL;
		usingBoosters = false;
	}

	// Only called when B is pressed or released. Does not decrease Booster Fuel continuously (as it should)
	public void affectMovement()
	{
		//System.out.println("hasBoosterFuel() = " + hasBoosterFuel() + ". isUsingBoosters() = " + isUsingBoosters());
		if(hasBoosterFuel() && isUsingBoosters())
		{
			super.setMaxVelocity(super.getDirection()*SHIP_MAX_VEL*BOOSTER_VEL_MULT); // Increase Max Velocity
			super.setVelocity(super.getMaxVelocity());
		}
		else
		{
			super.setMaxVelocity(super.getDirection()*SHIP_MAX_VEL); // Bring Max velocity to the normal
			super.setVelocity(super.getMaxVelocity());
		}
		
		if(!hasBoosterFuel())
			FrontPanel.getSkillSpace(Skill.BOOSTERS_SKILL).getSkill().setImage(Skill.BOOSTERS_IMG);
	}
	
	public void move()
	{
		//if(this instanceof PlayerShip)
			affectMovement(); // Affects the movement due to boosters
		
		super.move();
		rotate(); // local function
		
		// Decrease booster fluid if being used
		//if(this instanceof PlayerShip)
		//{
			// Subtract booster fuel. Fuel is added over time.
			if(isUsingBoosters())
				addBoosterFuel(-0.05);
		//}
		
		// Everytime a Ship moves, it leaves a trail
		if(Math.abs(super.getVelocity()) > 0 && distanceBetweenPoints((int) getPosX(), (int) getPosY(), lastX, lastY) > 4)
		{
			//if(isUsingBoosters())
			//	trail.addTrailComponent(new Color(220, 0, 10), (int) getPosX(), (int) getPosY()); // Grayish color
			//else
				double theta = getAngleRad();
				//trail.addTrailComponent(Color.gray, (int) (getPosX()-getImgWidth()*Math.cos(theta)/1.5+getImgWidth()/3), (int) (getPosY()-getImgHeight()*Math.sin(theta)/3+getImgHeight()/2.5)); // Grayish color
				trail.addTrailComponent(Color.gray, 
								       (int) (getPosX() + getImgWidth()/2 - 5 - getImgHeight()/3*Math.cos(theta)),
								       (int) (getPosY() + getImgHeight()/2 - 5 - getImgHeight()/3*Math.sin(theta)));
				
				//g2d.fillOval((int) (craft.getPosX()-craft.getImgWidth()*Math.cos(craft.getAngleRad())+craft.getImgWidth()/3), (int) (craft.getPosY()-craft.getImgHeight()*Math.sin(craft.getAngleRad())/2+craft.getImgHeight()/2.5), 10, 10);

			// Update last location
			lastX = (int) getPosX();
			lastY = (int) getPosY();
		}
	}
	
	public void addBomb()
	{
		bombCounter--;
		
		if(bombCounter < 0)
			bombCounter = 0;
	}
	
	public void addBombs(int num)
	{
		bombCounter -= num;

		if(bombCounter < 0)
			bombCounter = 0;
		
		// Make AreaBomb Panel Icon available for player
		if(this instanceof PlayerShip)
		{
			SkillSpace bombSpace = FrontPanel.getSkillSpace(Skill.AREA_BOMB_SKILL);
			if(bombSpace != null) // if it's not null, it must be binded. So we don't need to check
			{
				bombSpace.getSkill().setAvailable(true);
			}
		}
	}
	
	public void setTarget(SpaceObject obj)
	{
		//assert(obj != null);
		target = obj;
	}
	
	public SpaceObject getTarget()
	{
		return target;
	}
	
	public boolean isTargeted()
	{
		return targeted;
	}
	
	public void setTargeted(boolean val)
	{
		this.targeted = val;
	}
	
	public void destroy()
	{
		setLife(0);
		super.explode();
		
		SFX_Player.playSound(SFX_Player.SPACE_SOUND_PATH, SFX_Player.IMPLOSION_01);
		
		if(this instanceof PlayerShip)
		{
			FrontPanel.deactivatePanel();	
			Board.inGame = false;
		}
	}
	
	private static double distanceBetweenPoints(int x1, int y1, int x2, int y2)
	{
		return Math.sqrt((x2-x1)*(x2-x1) + (y2-y1)*(y2-y1));
	}
	
	public void drawTrail(Graphics2D g2d)
	{
		if(trail != null && trail.hasTrailComponents())
			trail.drawTrail(g2d);
	}
	
	public double getFuelCapacity()
	{
		return MAX_BOOSTER_FUEL;
	}
	
	public void addBoosterFuel(double val) // Function can also be used to subtract fuel
	{
		boosterFuel += val;
		//newMessage("+" + val+ " Fuel", new Color(0, 0, 255));
		
		if(boosterFuel < 0)
			boosterFuel = 0;
		else if(boosterFuel > MAX_BOOSTER_FUEL)
			boosterFuel = MAX_BOOSTER_FUEL;
	}
	
	public void setRotation(double r)
	{
		rotation = r;
	}
	
	public double getRotation()
	{
		return rotation;
	}
	
	public boolean isUsingBoosters()
	{
		return usingBoosters;
	}
	
	public boolean hasBoosterFuel()
	{
		if(boosterFuel > 0)
			return true;
		else
		{
			usingBoosters = false;
			return false;
		}
	}
	
	// Use/stop boosters affect the velocity of the craft
	public void useBoosters()
	{
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
	}
	
	public void stopBoosters()
	{
		FrontPanel.getSkillSpace(Skill.BOOSTERS_SKILL).getSkill().setImage(Skill.BOOSTERS_IMG);
		usingBoosters = false;
	}
	
	public double getFuel()
	{
		return boosterFuel;
	}
	
	public void setFuel(int val)
	{
		if(val >= 0 && val <= MAX_BOOSTER_FUEL)
			boosterFuel = val;
	}
	
	public int hasMessages()
	{
		return msgArr.size();
	}
	
	public void flushAllMessages()
	{
		msgArr = new ArrayList<ShipStatusMessage>();
	}
	
	public void newMessage(String msg, Color c)
	{
		msgArr.add(new ShipStatusMessage(msg, c));
	}
	
	/*public Explosion getExplotionObj()
	{
		return expl;
	}*/
	
	public void rotate()
	{
		super.rotate(rotation);
	}
	
	public void fireMissile()
	{
		if(missileCounter < MAX_MISSILES && !isDestroyed())
		{
			weaponArr.add(new Missile((int) (super.getPosX() + super.getImgWidth()/2), 
						 			  (int) (super.getPosY() + super.getImgHeight()/2),
						 			   super.getVelocity(),
						 			   super.getAngle()));
			missileCounter++;
		}
	}
	
	public void fireBomb()
	{
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
	}
	
	public void fireSeekingMissile()
	{
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
	}
	
	/*public void drawExplotion(Graphics2D g2d)
	{
		expl.setPosX(getPosX());
		expl.setPosY(getPosY());
		expl.drawExplotion(g2d);
	}*/
	
	public void drawShipData(Graphics2D g2d, int intent)
	{
		// Draw Life and Missiles Left on the upper left corner
		//drawIntention(g2d, intent);
		drawLifeBar(g2d);
		drawBombsLeft(g2d);
		
		if(hasMessages() > 0)
			drawStatusMessages(g2d);
		
		/*if(this instanceof AlienShip)
		{
			AlienShip a = (AlienShip)this;
			g2d.setColor(Color.gray);
			g2d.drawString("Heading( " + (int)getAngle() + ")", (int)getPosX(), (int)getPosY()-getImgHeight()/2);
			g2d.drawString("TargetHeading( " + (int)a.getTargetAngle() + ")", (int)a.getPosX(), (int)a.getPosY()-a.getImgHeight());	
		}*/
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
		int xIncr = 0;
		
		for(int i = 0; i < getNumberOfBombsLeft(); i++)
		{
			g2d.setColor(Color.yellow);
			g2d.fillOval((int) (getPosX()-getImgWidth()/2 + xIncr), (int) (getPosY()-getImgHeight()/4-10), 5, 5);
			xIncr += 8;
		}
	}

	private void drawLifeBar(Graphics2D g2d)
	{
		g2d.setStroke(new BasicStroke(1));
		g2d.setColor(new Color(255, 0, 0, 255)); //red base
		g2d.fillRect((int)(getPosX()-getImgWidth()/2), (int)(getPosY()-getImgHeight()/4), 50, 5);
		g2d.setColor(new Color(0, 255, 0, 255));
		g2d.fillRect((int)(getPosX()-getImgWidth()/2), (int)(getPosY()-getImgHeight()/4), 50*getLife()/getMaxLife(), 5);
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
	
	public void drawStatusMessages(Graphics2D g2d)
	{
		int i = 1;
		lastMessage = msgArr.get(0);
		
		if(!lastMessage.isFinished())
			lastMessage.drawMessage(g2d, (int) getPosX(), (int) getPosY());
		else
		{
			msgArr.remove(0);
			i = 0;
		}
			
		while(i < msgArr.size())
		{
			thisMessage = msgArr.get(i);
			
			if(lastMessage.getYincr() < -12 && !thisMessage.isFinished())
				thisMessage.drawMessage(g2d, (int) getPosX(), (int) getPosY());
			
			lastMessage = thisMessage;
			i++;
		}
	}	
	
	public void drawShipStatusMessage(Graphics2D g2d, String str, Color c)
	{
		g2d.setColor(c);
		g2d.drawString(str, (int) getPosX(), (int) getPosY() + getImgHeight());
	}
	
	public void drawShipStatusMessage(Graphics2D g2d, String str, Color c, int x, int y)
	{
		g2d.setColor(c);
		g2d.drawString(str, x, y);
	}
	
	public void decreaseLife(int val)
	{
		this.life -= val;
		this.newMessage("-" + val + " HP", new Color(255, 0, 0));
		
		if(life <= 0)
			destroy();
	}
	
	public int getMaxLife()
	{
		return MAX_LIFE;
	}
	
	public void addLife(int val)
	{
		life += val;
		this.newMessage("+" + val + " HP", new Color(0, 255, 0));
		
		if(life > MAX_LIFE)
		{
			life = MAX_LIFE;
		}
	}
	
	public void increaseMaxLife(double val)
	{
		MAX_LIFE += val;
		life = MAX_LIFE;
		
		newMessage("+" + val + " Life Increase!", new Color(0, 0, 255));
	}
	
	public int getNumOfMissilesLeft()
	{
		return MAX_MISSILES - missileCounter;
	}
	
	public int getNumberOfBombsLeft()
	{
		return MAX_BOMBS - bombCounter;
	}
	
	/*public int getBombsDeployed()
	{
		return bombArr.size();
	}*/
	
	public boolean hasBombsLeft()
	{
		if(bombCounter < MAX_BOMBS)
			return true;
		
		return false;
	}
	
	public boolean hasSeekMissilesLeft()
	{
		if(seekMissileCounter < MAX_SEEK_MISSILES)
			return true;
		
		return false;
	}
	
	/*public ArrayList<PlasmaBomb> getBombArr()
	{
		return bombArr;
	}*/
	
	public int getNumOfMissilesFired()
	{
		return MAX_MISSILES - missileCounter;
	}
	
	public void resetMissileCounter()
	{
		missileCounter = 0;
	}
	
	public void decreaseMissileCounter()
	{
		missileCounter--;
		
		if(missileCounter < 0)
			missileCounter = 0;
	}
	
	public void incrementNumOfMissiles()
	{
		MAX_MISSILES++;
		newMessage("+1 Missile", new Color(0, 0, 255));
	}
	
	public void setMaxMissiles(int val)
	{
		MAX_MISSILES = val;
	}
	
	public int getMaxMissiles()
	{
		return MAX_MISSILES;
	}
	
	/*public ArrayList<Missile> getMissiles()
	{
		return missileArr;
	}*/
	
	public ArrayList<Weapon> getWeaponArr()
	{
		return weaponArr;
	}
	
	public int getLife() 
	{
		return life;
	}
	
	public void setLife(int life)
	{
		this.life = life;
	}
	
	/*public boolean isInLineOfSight(SpaceShip other)
	{
		
	}*/
	
	public static double deg2rad(double angleInDeg)
	{
		return angleInDeg*Math.PI/180;
	}
}
