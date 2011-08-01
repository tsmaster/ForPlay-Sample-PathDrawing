package com.google.forplay.sample.pathdrawing.core;

import static forplay.core.ForPlay.assetManager;
import static forplay.core.ForPlay.graphics;
import static forplay.core.ForPlay.log;
import static forplay.core.ForPlay.mouse;
import static forplay.core.ForPlay.pointer;
import forplay.core.AssetWatcher;
import forplay.core.Game;
import forplay.core.Image;
import forplay.core.Mouse;
import forplay.core.Pointer;
import forplay.core.SurfaceLayer;

public class PathDrawingGame implements Game, Pointer.Listener, Mouse.Listener {
	private Tank tank;
	private Image groundTexture;
	private SurfaceLayer surfaceLayer;
    private final int groundSize = 600;
	private Image fontTexture;
	private boolean drawingWaypoints;
	private AssetWatcher watcher;
    private boolean assetsLoaded;

	@Override
	public void init() {
	    graphics().setSize(800, 600);
	    loadContent();
	    
	    // add a listener for pointer (mouse, touch) input
	    pointer().setListener(this);
	    mouse().setListener(this);
	}

	@Override
	public int updateRate() {
		return 30;
	}
	
    private void loadContent() {
	    surfaceLayer = graphics().createSurfaceLayer(graphics().width(), graphics().height());
		graphics().rootLayer().add(surfaceLayer);

		assetsLoaded = false;
        // Load our font and ground
		fontTexture = assetManager().getImage("images/font.png");
		groundTexture = assetManager().getImage("images/ground.png");

        // Create the tank
        tank = new Tank();
        tank.reset(new Vector2(100, 100));
        tank.setMoveSpeed(225f);
        
        watcher = new AssetWatcher(new AssetWatcher.Listener() {
		@Override
          public void error(Throwable e) {
            log().error(e.getMessage());
          }

          @Override
          public void done() {
            assetsLoaded = true;
            log().info("all assets loaded");
          }
        });
        watcher.add(fontTexture);
        watcher.add(groundTexture);
        tank.loadAssets(watcher);
        
        watcher.start();
    }
    
    /// Allows the game to run logic such as updating the world,
    /// checking for collisions, gathering input, and playing audio.
    /// <param name="gameTime">Provides a snapshot of timing values.</param>
	@Override
	public void update(float frameRate) {
		if (!assetsLoaded) {
            log().info("update before all assets loaded");
			return;
		}
        // Update the tank
        tank.update(frameRate);
    }

    /// This is called when the game should draw itself.
    /// <param name="gameTime">Provides a snapshot of timing values.</param>

	@Override
	public void paint(float alpha) {
		if (!assetsLoaded) {
            log().info("paint before all assets loaded");
			return;
		}
    	surfaceLayer.surface().clear();
        // First draw our ground
        drawGround();

        // Next draw the path
        drawPath();

        // Draw our instruction text
        drawInstructions();
        
        // Draw our tank
        tank.draw(surfaceLayer.surface());
    }
	
	/// Helper method to draw instructions.
	private void drawInstructions()
	{
		int color = 0xffffffff; // Color.White
        drawString("Drag a path from the tank to have him drive around.", new Vector2(5, 5), color);
	}
	
	private void drawString(String message, Vector2 position, int color) {
		int charWidth = 12;
		int charHeight = 24;
		
		for (int i = 0; i < message.length(); ++i) {
			char c = message.charAt(i);
			int ascii = (int)c;
			
			if (ascii >= 32 && ascii < 32+16*6) {
				int fontColumn = (ascii - 32) % 16;
				int fontRow = (ascii - 32) / 16;
				
				surfaceLayer.surface().drawImage(fontTexture, 
						position.x + charWidth*i, position.y, charWidth, charHeight,
						fontColumn * charHeight, fontRow * charHeight, charWidth, charHeight);
			}
		}
	}

    /// Helper method to draw our ground texture.
    private void drawGround()
    {
        // Compute the source rectangle based on our viewport size and ground scale
        float sourceWidth = (graphics().width() / groundSize) * groundTexture.width();
        float sourceHeight = (graphics().height() / groundSize) * groundTexture.height();

        // Draw the ground using our source rectangle which will cause it to wrap across the screen
    	surfaceLayer.surface().drawImage(groundTexture, 0.0f, 0.0f, graphics().width(), graphics().height(), 
    			0, 0, sourceWidth, sourceHeight);
    }

    /// Helper method to draw the path.
    private void drawPath()
    {
    	surfaceLayer.surface().setFillColor(0xffffffff);
    	Vector2 v1 = tank.getLocation();
    	
    	for (int i = 0; i < tank.getWaypoints().size(); ++i) {
    		Vector2 v2 = tank.getWaypoints().getWaypoint(i);
    		
        	surfaceLayer.surface().drawLine(v1.x, v1.y, v2.x, v2.y, 3.0f);
        	v1 = v2;
    	}
    }

	@Override
	public void onMouseDown(float x, float y, int button) {
        // If the primary touch is in the tank, we start drawing our path
		Vector2 mousePosition = new Vector2(x, y);
        if (tank.hitTest(mousePosition))
        {
            // Clear the waypoints to start a new path
            tank.getWaypoints().clear();

            // We're now drawing waypoints
            drawingWaypoints = true;

            // Use the touch location as the first waypoint
            tank.getWaypoints().enqueue(mousePosition);
        }
		
	}

	@Override
	public void onMouseUp(float x, float y, int button) {
        // if the primary touch is released, we stop drawing our path
        drawingWaypoints = false;
	}

	@Override
	public void onMouseMove(float x, float y) {
        // If we're drawing waypoints and the drag gesture has moved from the last location,
        // enqueue the position of the gesture as the next waypoint.
        if (drawingWaypoints)
        {
            tank.getWaypoints().enqueue(new Vector2(x, y));
        }
	}

	@Override
	public void onMouseWheelScroll(float velocity) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPointerStart(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPointerEnd(float x, float y) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onPointerDrag(float x, float y) {
		// TODO Auto-generated method stub
		
	}
}

