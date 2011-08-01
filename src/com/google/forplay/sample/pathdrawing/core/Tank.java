package com.google.forplay.sample.pathdrawing.core;

import static forplay.core.ForPlay.assetManager;
import static forplay.core.ForPlay.log;
import forplay.core.AssetWatcher;
import forplay.core.Image;
import forplay.core.ResourceCallback;
import forplay.core.Surface;

/// A simple object that moves towards its set destination. This class is largely borrowed from
/// the Waypoint Sample with the exception that behaviors have been removed and instead we've hard
/// coded the LinearBehavior into the Tank because other steering behaviors usually cause issues
/// with path-based waypoints given that the points are generally quite close to each other.
public class Tank {
    private static final float ANGULAR_TOLERANCE = 0.15f;

	/// The "close enough" limit, if the tank is inside this many pixel 
    /// to it's destination it's considered at it's destination
    final float atDestinationLimit = 5f;
    
    /// This is how much the Tank can turn in one second in radians, since Pi 
    /// radians makes half a circle the tank can all the way around in one second
    private static float maxAngularVelocity = (float) Math.PI;
    
    // Graphics data
    Vector2 tankTextureCenter;
	private Image tankTexture;

    /// The list of points the tanks will move to in order from first to last
	private WaypointList waypoints;

	public WaypointList getWaypoints() {
		return waypoints;
	}

    // Rotation values
    float rotation;
    boolean recomputeTargetRotation = true;
    float targetRotation;
    float previousRotation;
    float rotationInterpolation;
    
    /// Tank constructor
    public Tank()
    {
        location = Vector2.ZERO;
        waypoints = new WaypointList();
    }
    
    /// Length 1 vector that represents the tank's movement and facing direction
    private Vector2 direction;
    
   /// Reset the Tank's location on the map
   /// <param name="newLocation">new location on the map</param>
   public void reset(Vector2 newLocation)
   {
       location = newLocation;
       waypoints.clear();
   }
	
    /// The tank's current movement speed
	private float moveSpeed;

	public void setMoveSpeed(float speed) {
		moveSpeed = speed;
	}
	
    /// The tank's location on the map
	private Vector2 location;
	public Vector2 getLocation() {
		return location;
	}
	
    /// Linear distance to the Tank's current destination
	public float getDistanceToDestination() {
		return Vector2.distance(location, waypoints.peek());
	}
	
    /// True when the tank is "close enough" to its destination
    public boolean getAtDestination() {
        return getDistanceToDestination() < atDestinationLimit;
    }
	
    /// Tests if a given point is considered to "hit" the tank.
    /// <param name="point">The point to test against.</param>
    /// <returns>True if the point is "hitting" the tank, false otherwise.</returns>
    public boolean hitTest(Vector2 point)
    {
        // We leverage a comparison of squared distances to avoid two square root operations,
        // which can be a slow operation if performed frequently.
        return Vector2.distanceSquared(point, location) < tankTextureCenter.lengthSquared() * 1.5f;
    }

    /// Update the Tank's position if it's not "close enough" to 
    /// it's destination
    /// <param name="gameTime">Provides a snapshot of timing values.</param>
    public void update(float frameRate)
    {
        float elapsedTime = 1.0f / frameRate;
        
        // If we have any waypoints, the first one on the list is where 
        // we want to go
        if (waypoints.size() > 0)
        {
            if (getAtDestination())
            {
                // If we're at the destination and there is at least one 
                // waypoint in the list, get rid of the first one since we're 
                // there now
                waypoints.dequeue();

                // Whenever we arrive at a destination, we are going to need to
                // figure out a new target rotation.
                recomputeTargetRotation = true;
            }
            else
            {
                // This gives us a vector that points directly from the tank's
                // current location to the waypoint.
                direction = Vector2.subtract(waypoints.peek(), getLocation());

                // This scales the vector to 1, we'll use move Speed and elapsed Time 
                // in the Tank's Update function to find the how far the tank moves
                direction.normalize();

                // If we need to recompute our target rotation...
                if (recomputeTargetRotation)
                {
                    // Calculate the new rotation based on the direction
                    targetRotation = (float)Math.atan2(direction.y, direction.x);

                    // Reset our interpolation value
                    rotationInterpolation = 0f;

                    // We want to make sure we always turn the shortest way, so we need
                    // to check our rotation values and correct the target value if the
                    // two are more than 180 degrees different.
                    while (targetRotation - rotation > Math.PI)
                        targetRotation -= 2*Math.PI;
                    while (targetRotation - rotation < -Math.PI)
                        targetRotation += 2*Math.PI;

                    // We don't need to recompute the rotation until we hit the next destination
                    recomputeTargetRotation = false;
                }
                
                float deltaRotation = targetRotation - rotation;
                float absDeltaRotation = Math.abs(deltaRotation);

                float velocityMultiplier = 1.0f;

                if (Math.abs(deltaRotation) > ANGULAR_TOLERANCE) {
                	float maxFrameRotation = maxAngularVelocity * elapsedTime; 
                	float frameRotation = clamp(deltaRotation, -maxFrameRotation, maxFrameRotation);
                	rotation += frameRotation;
                }
                
                if (absDeltaRotation > 2 * ANGULAR_TOLERANCE) {
                	velocityMultiplier = 0.0f;
                } else if (absDeltaRotation > ANGULAR_TOLERANCE) {
                	velocityMultiplier = (2.0f - absDeltaRotation / ANGULAR_TOLERANCE);
                } else {
                	velocityMultiplier = 1.0f;
                }
                
                // Move us along in our direction
                location = Vector2.add(location, Vector2.multiply(direction,
                		velocityMultiplier * moveSpeed * elapsedTime));
            }
        }
    }

    private float clamp(float v, float a, float b) {
    	if (v < a) {
    		return a;
    	} else if (v > b) {
    		return b;
    	} else {
    		return v;
    	}
	}

	/// Draw the Tank
    /// <param name="gameTime"></param>
    /// <param name="spriteBatch"></param>
    public void draw(Surface surface)
    {
    	if (tankTextureCenter == null) {
    		log().info("drawing tank with no center?");
    		return;
    	}
    	surface.save();
    	surface.translate(location.x, location.y);
    	surface.rotate(rotation);
    	surface.translate(-tankTextureCenter.x, -tankTextureCenter.y);
		surface.drawImage(tankTexture, 0, 0);
		surface.restore();
    }

	public void loadAssets(AssetWatcher watcher) {
		tankTexture = assetManager().getImage("images/tank.png");
		watcher.add(tankTexture);
		tankTexture.addCallback(new ResourceCallback<Image>() {
			@Override
			public void done(Image resource) {
		        tankTextureCenter = new Vector2(resource.width() / 2.0f, resource.height() / 2.0f);
			}

			@Override
			public void error(Throwable err) {
				// TODO Auto-generated method stub
			}});
	}
}
