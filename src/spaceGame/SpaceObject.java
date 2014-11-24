package spaceGame;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
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
import Mathematics.Vector2D;
import com.ship.effects.Explosion;
import com.ship.effects.Target;

public class SpaceObject 
{
	private double pos_x;
	private double pos_y;
	private double v_x;
	private double v_y;
	private double a_x;
	private double a_y;	
	private double angle;
	private double mass;
	private double rotationDegPerSecSquared; // Rotational acceleration
	private double rotationDegPerSec; // The rate of change of rotationAngle
	
	private ImageIcon img;
	private AffineTransform transf;
	private static AffineTransform savedTransform;
	private boolean visible;
	private boolean destroyed;

	public static final int NO_SPEED_LIMIT      = 0;
	public static final int FRICTIONLESS_OBJECT = 0;
	
	// Moves objects based on their angle
	public SpaceObject( String imgFilename, int x, int y, double v_x, double v_y, double initialAngle, double mass )
	{
		img = getImgResource( imgFilename );
		setVelocity( v_x, v_y );
		setAngle( initialAngle );
		setPosition( x, y );
		setVisible( true );
	}
	
	public SpaceObject( int x, int y, double v_x, double v_y, double initialAngle, double mass )
	{
		this( null, x, y, v_x, v_y, initialAngle, mass );
	}
	
	// This function is called automatically for every space object
	public void updateSpaceObjectMotion( double maxSpeed, double maxRotationRate, double thrustFriction, double angularFriction, double dt )
	{
		// Update speed( based on acceleration )
		if( maxSpeed == NO_SPEED_LIMIT )
		{
			v_x += a_x * dt;
			v_y += a_y * dt;
		}
		else
		{
			v_x = limit( v_x + a_x*dt, -maxSpeed, maxSpeed );
			v_y = limit( v_y + a_y*dt, -maxSpeed, maxSpeed );
		}

		// Friction affects linear motion (and is directly proportional to the speed)
		if( a_x == 0 )
			v_x -= thrustFriction*v_x;
		if( a_y == 0 )
			v_y -= thrustFriction*v_y;
		
		// Update position with linear velocities
		pos_x += v_x * dt;
		pos_y += v_y * dt;
		
		// Update angle (based on angular acceleration)
		if( maxRotationRate == NO_SPEED_LIMIT )
			rotationDegPerSec += rotationDegPerSecSquared * dt;
		else
			rotationDegPerSec = limit(rotationDegPerSec + rotationDegPerSecSquared * dt, -maxRotationRate, maxRotationRate );
		
		// Angular rotation is also affected by friction (proportional to angular speed)
		if( rotationDegPerSecSquared == 0 )
			rotationDegPerSec -= angularFriction*rotationDegPerSec;
		
		// update angle with angular rotation
		angle += rotationDegPerSec * dt;
	}
	
	public double limit( double val, double lowerLimit, double upperLimit )
	{
		if( val > upperLimit )
			val = upperLimit;
		else if( val < lowerLimit )
			val = lowerLimit;
		
		return val;
	}
	
	public void setAcceleration( double a_x, double a_y )
	{
		this.a_x = a_x;
		this.a_y = a_y;
	}
	
	public double getAccel_x()
	{
		return this.a_x;
	}
	
	public double getAccel_y()
	{
		return this.a_y;
	}
	
	public double getAccelMagnitude()
	{
		return Math.sqrt( a_x*a_y + a_y*a_y);
	}
	
	// Does not take into account angular Thrust
	public void setRotationRateDegPerSec(double rateDegPerSec)
	{
		this.rotationDegPerSec = rateDegPerSec;
	}
	
	public double getRotationRateDegPerSec()
	{
		return rotationDegPerSec;	
	}
	
	public void setRotationDegPerSecSquared( double rateDegPerSecSquared )
	{
		rotationDegPerSecSquared = rateDegPerSecSquared;
	}
	
	public double getRotationDegPerSecSquared()
	{
		return rotationDegPerSecSquared;
	}
	
	public void setAngle(double val)
	{
		this.angle = checkAngleBoundaries( val );
	}
	
	public double getAngle()
	{
		return this.angle;
	}
	
	public void setPosition( int x, int y )
	{
		this.pos_x = x;
		this.pos_y = y;
	}
	
	public void setVelocity(double v_x, double v_y)
	{
		this.v_x = v_x;
		this.v_y = v_y;
	}
	
	public double getVelocityX()
	{
		return v_x;
	}
	
	public double getVelocityY()
	{
		return v_y;
	}
	
	public void destroy( boolean val )
	{
		this.destroyed = val;
		this.visible = !val;
	}
	
	public boolean isCollidingWith( SpaceObject other )
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
		/*
		// Play sound if set on this object for the first time
		if(!mouseHover)
			SFX_Player.playSound(SFX_Player.OTHER_SOUND_PATH, SFX_Player.BEEP_29_SOUND);
			
		mouseHover = true;
		getObjectTarget().activate();
		*/
	}
		
	public void unsetMouseHover()
	{
		/*
		mouseHover = false;
		getObjectTarget().rest();
		*/
	}
	
	/*
	public boolean isMouseHovering()
	{
		return mouseHover;
	}
	*/
	
	public boolean isDestroyed()
	{
		return destroyed;
	}
	
	public void rotate( double dr )
	{
		/*
		if(Math.abs(dr) > MAX_TURNING_RATE)
			dr = Math.signum(dr)*MAX_TURNING_RATE;
		
		angle += dr;
		setAngle(angle);
		*/
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
		return img.getImage().getWidth(null);
	}
	
	public int getImgHeight()
	{
		return img.getImage().getHeight(null);
	}
	
	/*
	public void setImg( ImageIcon img ) 
	{
		this.img = img;
		img_width = img.getImage().getWidth(null);
		img_height = img.getImage().getHeight(null);
	}
	
	public void setImg( String imgFilename )
	{
		setImg( new ImageIcon(getClass().getResource("/resources/" + imgFilename)) );
	}
	*/
	
	public static ImageIcon getImgResource( String imgFilename )
	{
		return new ImageIcon(SpaceObject.class.getResource("/resources/" + imgFilename));
	}
	
	public double getPosX() 
	{
		return this.pos_x;
	}
	
	public void setPosX(int x) 
	{
		this.pos_x = x;
	}
	
	public double getPosY() 
	{
		return this.pos_y;
	}
	
	public void setPosY(int y) 
	{
		this.pos_y = y;
	}
	
	/*
	public double getAngle()
	{
		return angle;
	}
	*/
	
	public double degToRad(double deg)
	{
		return deg*Math.PI/180;
	}
	
	/*
	public double getAngleRad() 
	{
		return getAngle()*Math.PI/180;
	}
	*/
	
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
	
	public Rectangle getBounds()
	{
		return new Rectangle( (int) pos_x, (int) pos_y, getImgWidth(), getImgHeight() );
	}
	
	public void setVisible( boolean visibility )
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
	
	/*
	public void drawObjectData( Graphics g )
	{
		g2d.setColor(TRANSPARENT_COLOR);
		g2d.fillRect((int) (getPosX() + 2*getImgWidth()), (int) getPosY(), 80, 50);
		
		g2d.setColor(Color.white);
		g2d.setFont(bFont);
		g2d.setStroke(bStroke);
		g2d.drawRect((int) (getPosX() + 2*getImgWidth()), (int) getPosY(), 80, 50);
		
		if(this instanceof SpaceShip)
		{
			//thisSpaceShip = (SpaceShip) this;
			//g2d.drawString("Life: " + thisSpaceShip.getLife(), (int) (getPosX() + 2*getImgWidth() + 5), (int) getPosY() + 15);
			//g2d.drawString("Bombs: " + thisSpaceShip.getNumberOfBombsLeft(), (int) (getPosX() + 2*getImgWidth() + 5), (int) getPosY() + 30);
			//g2d.drawString("Fuel: " + Math.round(thisSpaceShip.getFuel()), (int) (getPosX() + 2*getImgWidth() + 5), (int) getPosY() + 45);
		}	
	}
	*/
	
	public void drawSpaceObject( Graphics g )
	{
		Graphics2D g2d = (Graphics2D) g;
		
		// Save original transform
		//AffineTransform saveTransform = g2d.getTransform();
		savedTransform = g2d.getTransform();
		
		// This will transform our sprite
		transf = new AffineTransform();

		g2d.rotate( Math.toRadians( this.angle ),
					getPosX() + getImgWidth()/2, 
					getPosY() + getImgHeight()/2 );

		transf.translate( getPosX(), getPosY() );
		g2d.drawImage( getImg(), transf, null );
		
		// Only for debugging purposes
		//g2d.setColor( Color.WHITE );
		//g2d.drawRect( (int)getPosX(), (int)getPosY(), getImgWidth(), getImgHeight() );
		
		// Restore original transform matrix
		g2d.setTransform( savedTransform );
	}
	
	public void explode()
	{
		//expl.setVisible(true);
		//setDestroyed(true);
	}
	
	/*
	public Explosion getExplObj()
	{
		return expl;
	}
	*/
	
	/*
	public boolean isExploding()
	{
		if(expl != null)
			return !isVisible() && expl.isVisible();
		
		return false;
	}
	*/
	
	/*
	public void drawExplosion(Graphics2D g2d)
	{
		if(expl != null)
		{
			expl.setPosX(getPosX());
			expl.setPosY(getPosY());
			expl.drawExplotion(g2d);
		}
	}
	*/
	
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
