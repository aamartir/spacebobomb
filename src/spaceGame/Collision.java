package spaceGame;

import com.space.Asteroid;
import com.weapons.Missile;

// TODO
public class Collision 
{
	private static Quadrant q;
	private static Object[] objInQ;
	private static SpaceObject obj1;
	private static SpaceObject obj2;
	
	public static int checkCollisions()
	{
		int collisions = 0;
		
		for( int g = 0; g < Game.grid.size(); g++ )
		{
			q = Game.grid.get(g);
			
			// Ignore quadrant if there are no objects within it
			if( q.getNumOfObjectsInQuadrant() < 2 )
				continue;
			
			// Get kth quadrant and the objects inside it
			objInQ = q.getObjectsInQuadrant();

			for( int i = 0; i < objInQ.length - 1; i++ )
			{
				obj1 = ((SpaceObject) objInQ[i]);
				if( obj1.isDestroyed() || !obj1.isVisible() )
					continue;
				
				for( int j = i + 1; j < objInQ.length; j++ )
				{
					obj2 = ((SpaceObject) objInQ[j]);
					
					if( obj2.isDestroyed() || !obj2.isVisible() )
						continue;
					
					if( obj1.isCollidingWith(obj2) )
					{
						//System.out.println( "Collision between " + obj1.getObjectID() + " and " + obj2.getObjectID() + "." );
						if( collision( obj1, obj2 ) )
							collisions++;
					}
				}
			}
		}
		
		return collisions;
	}
	
	// Both a and b are undetermined
	public static boolean collision( SpaceObject a, SpaceObject b )
	{
		boolean ret = false;
		
		switch( a.getObjectType() )
		{
			case SpaceObject.ENEMYSHIP_OBJ_TYPE:
			case SpaceObject.PLAYERSHIP_OBJ_TYPE:
				ret = spaceshipCollision( (SpaceShip) a, b );
				break;
			case SpaceObject.ASTEROID_OBJ_TYPE:
				ret = asteroidCollision( (Asteroid) a, b );
				break;
			case SpaceObject.MISSILE_OBJ_TYPE:
				ret = missileCollision( (Missile) a, b );
				break;
			default:
				ret = spaceObjectCollision( a, b );
		}
		
		return ret;
			
	}
	
	// Object a is SpaceObject, b is undetermined
	public static boolean spaceObjectCollision( SpaceObject a, SpaceObject b )
	{
		a.destroySpaceObject( false );
		b.destroySpaceObject( false );
		
		return true;
	}
	
	public static boolean spaceshipCollision( SpaceShip ship, SpaceObject obj )
	{
		boolean ret = false;
		
		// Determine what is obj
		switch( obj.getObjectType() )
		{
			case SpaceObject.ENEMYSHIP_OBJ_TYPE:
			case SpaceObject.PLAYERSHIP_OBJ_TYPE:
				ret = spaceshipCollision( ship, (SpaceShip) obj );
				break;
			case SpaceObject.ASTEROID_OBJ_TYPE:
				ret = spaceshipCollision( ship, (Asteroid) obj );
				break;
			case SpaceObject.MISSILE_OBJ_TYPE:
				ret = spaceshipCollision( ship, (Missile) obj ); 
				break;
			default:
				ret = spaceObjectCollision( obj, ship );
		}
		
		return ret;
	}
	
	public static boolean asteroidCollision( Asteroid ast, SpaceObject obj )
	{
		boolean ret = false;
		
		switch( obj.getObjectType() )
		{
			case SpaceObject.ENEMYSHIP_OBJ_TYPE:
			case SpaceObject.PLAYERSHIP_OBJ_TYPE:
				ret = spaceshipCollision( (SpaceShip) obj, ast );
				break;
			case SpaceObject.ASTEROID_OBJ_TYPE:
				ret = asteroidCollision( ast, (Asteroid) obj );
				break;
			case SpaceObject.MISSILE_OBJ_TYPE:
				ret = asteroidCollision( ast, (Missile) obj ); 
				break;
			default:
				ret = spaceObjectCollision( obj, ast );
		}
		
		return ret;
	}
	
	public static boolean missileCollision( Missile m, SpaceObject obj )
	{
		boolean ret = false;
		
		switch( obj.getObjectType() )
		{
			case SpaceObject.ENEMYSHIP_OBJ_TYPE:
			case SpaceObject.PLAYERSHIP_OBJ_TYPE:
				ret = spaceshipCollision( (SpaceShip) obj, m );
				break;
			case SpaceObject.ASTEROID_OBJ_TYPE:
				ret = asteroidCollision( (Asteroid) obj, m );
				break;
			case SpaceObject.MISSILE_OBJ_TYPE:
				ret = missileCollision( m, (Missile) obj ); 
				break;
			default:
				ret = spaceObjectCollision( obj, m );
		}
		
		return ret;
	}
	
	public static boolean spaceshipCollision( SpaceShip a, SpaceShip b )
	{
		// Collision not valid between enemy spaceships.
		if( a.getObjectType() != SpaceObject.ENEMYSHIP_OBJ_TYPE ||
			b.getObjectType() != SpaceObject.ENEMYSHIP_OBJ_TYPE )
		{
			double tmp = a.getCurrentLife();
			
			a.decreaseLife( b.getCurrentLife() );
			b.decreaseLife( tmp );
			
			return true;
		}
		
		// Calculate resultant vector
		// TODO
		
		return false;
	}
	
	public static boolean spaceshipCollision( SpaceShip ship, Asteroid asteroid )
	{
		asteroid.destroySpaceObject( true );
		ship.decreaseLife( Asteroid.ASTEROID_COLLISION_DAMAGE );
		
		return true;
	}
	
	public static boolean spaceshipCollision( SpaceShip ship, Missile missile )
	{
		// A missile cannot damage its source spaceship
		if( missile.getSourceSpaceShip().getObjectID() != ship.getObjectID() )
		{
			ship.decreaseLife( missile.getDmg() );
			missile.destroySpaceObject( false );
			
			return true;
		}
		
		return false;
	}

	public static boolean asteroidCollision( Asteroid a, Asteroid b )
	{
		// TODO (Destroy less massive asteroid)
		a.destroySpaceObject( true );
		b.destroySpaceObject( true );
		
		return true;
	}
	
	public static boolean asteroidCollision( Asteroid a, Missile m )
	{
		a.destroySpaceObject( true );
		m.destroySpaceObject( false );
		
		return true;
	}
}
