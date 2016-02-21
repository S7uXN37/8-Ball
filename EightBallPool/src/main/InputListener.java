package main;

import org.newdawn.slick.Input;
import org.newdawn.slick.geom.Vector2f;

public class InputListener implements org.newdawn.slick.InputListener {
	private PoolGame game;
	
	public InputListener(PoolGame parent) {
		game = parent;
	}
	
	@Override
	public void mouseWheelMoved(int change) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(int button, int x, int y, int clickCount) {
		// TODO Auto-generated method stub
		
	}
	
	private int oldx, oldy;
	
	@Override
	public void mousePressed(int button, int x, int y) {
		oldx = x;
		oldy = y;
	}
	
	private static final float maxSpeed = 1500;
	private static final float minSpeed = 100;
	@Override
	public void mouseReleased(int button, int x, int y) {
		Vector2f drag = new Vector2f(x - oldx, y - oldy);
		drag.scale(-1f); // invert (pull back to push forward)
		drag.scale(5f);
		float dragLength = Math.min(Math.max(drag.length(), minSpeed), maxSpeed); // clamp to [100, 800]
		dragLength -= minSpeed;
		dragLength *= maxSpeed / (maxSpeed - minSpeed);
		if (dragLength > 0) {
			drag.scale(dragLength / drag.length());
			game.shoot(drag);
		}
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setInput(Input input) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isAcceptingInput() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void inputEnded() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputStarted() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(int key, char c) {
		switch (key) {
		case Input.KEY_ESCAPE:
			System.exit(0);
			break;
		case Input.KEY_SPACE:
			// TODO do best shot
			break;
		}
	}

	@Override
	public void keyReleased(int key, char c) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerLeftPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerLeftReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerRightPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerRightReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerUpPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerUpReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerDownPressed(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerDownReleased(int controller) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerButtonPressed(int controller, int button) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void controllerButtonReleased(int controller, int button) {
		// TODO Auto-generated method stub
		
	}

}
