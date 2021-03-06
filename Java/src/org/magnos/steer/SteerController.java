package org.magnos.steer;

import org.magnos.steer.vec.Vec;


/**
 * A controller which applies the force from a steering behavior to a
 * subject and updates the acceleration, velocity, position, and direction
 * of the subject.
 */
public class SteerController<V extends Vec<V>>
{
	public SteerSubject<V> subject;
	public Steer<V> force;
	public Constraint<V> constraint;
	public boolean immediate;
	
	public SteerController(SteerSubject<V> subject, Steer<V> force )
	{
		this( subject, force, null );
	}
	
	public SteerController(SteerSubject<V> subject, Steer<V> force, Constraint<V> constraint )
	{
		this.subject = subject;
		this.force = force;
		this.constraint = constraint;
		this.immediate = false;
		this.updateDirection();
	}
	
	public void updateDirection()
	{
		V v = subject.getVelocity();
		V d = subject.getDirection();
		
		if (!v.isZero(SteerMath.EPSILON)) 
		{
			d.set( v ).normali();
		}
		else if (d.isZero(SteerMath.EPSILON))
		{
		    d.defaultUnit();
		}
	}
	
	public void update( float elapsed )
	{
		V a = subject.getAcceleration();
		V v = subject.getVelocity();
		V p = subject.getPosition();
		float vmax = subject.getMaximumVelocity();
		
		if ( immediate )
		{
			v.clear();	
		}
		
		a.clear();
		float magnitude = force.getForce( elapsed, subject, a );
		
		if (!Float.isNaN( magnitude ) && !Float.isInfinite( magnitude ))
		{
		    a.muli( magnitude );
		    a.subi( v );

            if ( constraint != null )
            {
                constraint.constrain( elapsed, subject );
            }
		    
	        v.addsi( a, immediate ? 1.0f : elapsed );
	        
	        if (vmax != Steer.INFINITE)
	        {
	            v.max( vmax );
	        }

	        p.addsi( v, elapsed );
	        
	        updateDirection();
		}
	}
	
}
