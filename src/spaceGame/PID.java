package spaceGame;

public class PID 
{
	// Control parameters
	private double Kp;
	private double Ki;
	private double Kd;
	
	private double errInt;
	private double lastErr;
	private double errDer;

	public PID()
	{
		this( 0, 0, 0 );
	}
	
	public PID( double p, double i, double d )
	{
		Kp = p;
		Ki = i;
		Kd = d;
	}
	
	public void setPID( double newP, double newI, double newD )
	{
		Kp = newP;
		Ki = newI;
		Kd = newD;
	}
	
	public double update( double newErr, double dt )
	{
		errInt += newErr * dt;
		errDer = ( newErr - lastErr ) / dt;
		lastErr = newErr;
		
		return ( Kp*newErr + Ki*errInt + Kd*errDer );
	}
}
