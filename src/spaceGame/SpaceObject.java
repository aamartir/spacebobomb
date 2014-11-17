package spaceGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.geom.AffineTransform;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.ImageIcon;
import com.ship.effects.Explosion;
import com.ship.effects.Target;

public class SpaceObject 
{
	private Quadrant quad;
	private int gridID;

	private double x;
	private double y;
	private double vel;
	private double angle; // Direction angle
	
	private double rotationAngle; // Rotation angle
	private double rotationRateDeg;
	private int width;
	private int height;
	private ImageIcon img;
	
	private boolean visible;
	private boolean destroyed;
	
	private double MAX_TURNING_RATE;
	public static double OBJ_MAX_VEL = 0.15;
	private int OBJ_DIRECTION = 0; //Up or down
	
	private AffineTransform transf;
	
	// Dynamic Objects
	private Explosion expl = new Explosion();
	private SpaceShip thisSpaceShip;
	private Target objectTarget = new Target();
	
	public static final Color TRANSPARENT_COLOR = new Color(255, 255, 255, 40);
	public static final BasicStroke bStroke = new BasicStroke(1f);
	public static final Font bFont = new Font("Arial", Font.PLAIN, 12);
	
	private boolean selected = false;
	private boolean mouseHover = false;
	
	// Moves objects based on their angle
	public SpaceObject(String PATH, String imgFilename, int x, int y, double vel, double angle)
	{
		setImg(PATH, imgFilename);
		setVelocity(vel);
		setAngle(angle);
		
		this.x = x;
		this.y = y;
		this.visible = true;
		
		//if(this instanceof SpaceShip)
			Grid.assignObjectToQuadrant(x, y, this);
	}
	
	public void setGridID(int val)
	{
		this.gridID = val;
	}
	
	public int getGridID()
	{
		return gridID;
	}
	
	public SpaceObject(int x, int y, double vel, double angle)
	{
		img = null;
		
		this.x = x;
		this.y = y;
		this.vel = vel;
		this.angle = angle;
		this.visible = true;
	}
	
	public void move()
	{
		x += vel*Math.cos(angle*Math.PI/180);
		y += vel*Math.sin(angle*Math.PI/180);
		
		rotationAngle += rotationRateDeg;
		
		// Update the Quadrant
		Grid.assignObjectToQuadrant((int) x, (int) y, this);
	}
	
	public Quadrant getQuadrant()
	{
		return quad;
	}
	
	public void assignQuadrant(Quadrant q)
	{
		quad = q;
	}
	
	public void accelerate(double acc)
	{
		vel += acc;
		
		if(vel > OBJ_MAX_VEL)
			vel = OBJ_MAX_VEL;
		else if(vel < - OBJ_MAX_VEL)
			vel = -OBJ_MAX_VEL;
	}
	
	public void setRotationRateDeg(double rateDeg)
	{
		this.rotationRateDeg = rateDeg;
	}
	
	public double getRotationRateDeg()
	{
		return rotationRateDeg;	
	}
	
	public void setRotationAngle(double val)
	{
		this.rotationAngle = val;
	}
	
	public double getRotationAngle()
	{
		return rotationAngle;
	}
	
	public void setVelocity(double vel)
	{
		this.vel = vel;
		OBJ_DIRECTION = (int) Math.signum(vel);
	}
	
	public double getVelocity()
	{
		return vel;
	}
	
	public void setMaxVelocity(double val)
	{
		OBJ_MAX_VEL = val;
		OBJ_DIRECTION = (int) Math.signum(OBJ_MAX_VEL);
	}
	
	public double getMaxVelocity()
	{
		return OBJ_MAX_VEL;
	}
	
	public void setDirection(int dir)
	{
		OBJ_DIRECTION = dir;
	}
	
	public int getDirection()
	{
		return OBJ_DIRECTION;
	}
	
	public void setMaxTurningRate(double val)
	{
		MAX_TURNING_RATE = val;
	}
	
	public double getMaxTurningRate()
	{
		return MAX_TURNING_RATE;
	}
	
	public void setDestroyed(boolean destroyed)
	{
		this.destroyed = destroyed;
		this.visible = !destroyed;
		
		// Stop the target and the thread controlling it to save memory
		getObjectTarget().rest();
		//objectTarget = null;
	}
	
	public boolean isCollidingWith(SpaceObject other)
	{
		if(getBounds().intersects(other.getBounds()))
			return true;
		
		return false;
	}
	
	public boolean containsPoint(Point p)
	{
		return (new Rectangle(p.x - 12, p.y - 33, 20, 20).intersects(getBounds()));
	}
	
	public void setMouseHover()
	{
		// Play sound if set on this object for the first time
		if(!mouseHover)
			SFX_Player.playSound(SFX_Player.OTHER_SOUND_PATH, SFX_Player.BEEP_29_SOUND);
			
		mouseHover = true;
		getObjectTarget().activate();
	}
		
	public void unsetMouseHover()
	{
		mouseHover = false;
		getObjectTarget().rest();
	}
	
	public boolean isMouseHovering()
	{
		return mouseHover;
	}
	
	public void destroy()
	{
		setDestroyed(true);
	}
	
	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	public void rotate(double dr)
	{
		if(Math.abs(dr) > MAX_TURNING_RATE)
			dr = Math.signum(dr)*MAX_TURNING_RATE;
		
		angle += dr;
		setAngle(angle);
	}
	
	/*public int getHeadingQuadrant()
	{
		if(angle >= 0 && angle < 90)
			return 1;
		else if(angle >= 90 && angle < 180)
			return 2;
		else if(angle >= 180 && angle < 270)
			return 3;
		else
			return 4;
	}*/
	
	public int getImgWidth()
	{
		return width;
	}
	
	public int getImgHeight()
	{
		return height;
	}
	
	public double getPosX() 
	{
		return x;
	}
	
	public void setPosX(int x) 
	{
		this.x = x;
	}
	
	public double getPosY() 
	{
		return y;
	}
	
	public double getAngle()
	{
		return angle;
	}
	
	public double degToRad(double deg)
	{
		return deg*Math.PI/180;
	}
	
	public double getAngleRad() 
	{
		return getAngle()*Math.PI/180;
	}
	
	public void setAngle(double angle)
	{
		this.angle = checkAngleBoundaries(angle);
	}
	
	// Returns angle in the range -180 < angle <= 180
	public double checkAngleBoundaries(double angle)
	{
		if(angle >= 360 || angle <= -360)
			angle = angle%360;
		
		if(angle > 180)
			angle = angle%180 - 180;
		else if(angle <= -180)
			angle = 180 + angle%180;
		
		return angle;
	}
	
	public void setPosY(double posY) 
	{
		this.y = y;
	}
	
	public Rectangle getBounds()
	{
		return new Rectangle((int) x, (int) y, width, height);
	}
	
	public void setVisible(boolean visibility)
	{
		this.visible = visibility;
	}
	
	public boolean isVisible()
	{
		return this.visible;
	}
	
	public Image getImg() 
	{
		return img.getImage();
	}
	public void setImg(ImageIcon img) 
	{
		this.img = img;
		this.width = img.getImage().getWidth(null);
		this.height = img.getImage().getHeight(null);
	}
	
	public void setImg(String PATH, String imgFilename)
	{
		setImg(new ImageIcon(getClass().getResource(PATH + "" + imgFilename)));
	}
	
	public void drawObjectData(Graphics2D g2d)
	{
		g2d.setColor(TRANSPARENT_COLOR);
		g2d.fillRect((int) (getPosX() + 2*getImgWidth()), (int) getPosY(), 80, 50);
		
		g2d.setColor(Color.white);
		g2d.setFont(bFont);
		g2d.setStroke(bStroke);
		g2d.drawRect((int) (getPosX() + 2*getImgWidth()), (int) getPosY(), 80, 50);
		
		if(this instanceof SpaceShip)
		{
			thisSpaceShip = (SpaceShip) this;
			g2d.drawString("Life: " + thisSpaceShip.getLife(), (int) (getPosX() + 2*getImgWidth() + 5), (int) getPosY() + 15);
			g2d.drawString("Bombs: " + thisSpaceShip.getNumberOfBombsLeft(), (int) (getPosX() + 2*getImgWidth() + 5), (int) getPosY() + 30);
			g2d.drawString("Fuel: " + Math.round(thisSpaceShip.getFuel()), (int) (getPosX() + 2*getImgWidth() + 5), (int) getPosY() + 45);
		}	
	}
		
	public void select()
	{
		selected = true;
		objectTarget.activate();
	}
	
	public void unselect()
	{
		selected = false;
		objectTarget.rest();
	}
	
	public boolean isSelected()
	{
		return selected;
	}
	
	public void draw(Graphics2D g2d)
	{
		transf = new AffineTransform();
		transf.rotate(getAngleRad() + degToRad(rotationAngle), getPosX() + getImgWidth()/2, getPosY() + getImgHeight()/2);
		transf.translate(getPosX(), getPosY());
		
		g2d.drawImage(getImg(), transf, null);
		
		// Draw target
		if(selected)
			objectTarget.setImage(Target.RED_TARGET);
		else
			objectTarget.setImage(Target.BLUE_TARGET);
		
		// Attempt to draw target
		objectTarget.draw(g2d, (int) getPosX(), (int) getPosY(), getImgWidth(), getImgHeight());
	}
	
	public void explode()
	{
		expl.setVisible(true);
		setDestroyed(true);
	}
	
	public Explosion getExplObj()
	{
		return expl;
	}
	
	public boolean isExploding()
	{
		if(expl != null)
			return !isVisible() && expl.isVisible();
		
		return false;
	}
	
	public void drawExplosion(Graphics2D g2d)
	{
		if(expl != null)
		{
			expl.setPosX(getPosX());
			expl.setPosY(getPosY());
			expl.drawExplotion(g2d);
		}
	}
	
	public Target getObjectTarget()
	{
		return objectTarget;
	}
	
	public double distanceWithRespectTo(SpaceObject other)
	{
		double diffX = this.getPosX() - other.getPosX();
		double diffY = this.getPosY() - other.getPosY();
		
		return Math.sqrt(diffX*diffX + diffY*diffY);
	}
	
	public double angleWithRespectTo(SpaceObject other)
	{
		double diffX = this.getPosX() - other.getPosX();
		double diffY = this.getPosY() - other.getPosY();
		
		return checkAngleBoundaries((Math.atan2(diffY, diffX) + Math.PI)*180/Math.PI); // target angle
	}
	
	public static double angleWithRespectToPoint(SpaceObject obj, int x, int y)
	{
		return Math.atan2(y-obj.getPosY(), x-obj.getPosX());
	}
	
	public static double angleWithRespectToPoint(int x1, int y1, int x2, int y2)
	{
		return Math.atan2(y2-y1, x2-x1)*180/Math.PI;
	}
	
	public static double getClosestAngle(double angle1, double angle2)
	{
		double res = angle2 - angle1;
		
		if(res > 180)
			return 180 - res;
		else if(res < -180)
			return res + 180;
		else
			return res;
	}
}
