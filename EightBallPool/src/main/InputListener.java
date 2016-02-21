package main;

import org.newdawn.slick.Input;

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
	
	private int oldx = -1;
	private int oldy = -1;
	
	@Override
	public void mousePressed(int button, int x, int y) {
		if (!game.cueReady)
			return;
		
		oldx = x;
		oldy = y;
	}
	
	private static final float maxSpeed = 300;
	private static final float maxPullBackPx = 100;
	private static final float powPerPx = 5f;
	
	@Override
	public void mouseReleased(int button, int x, int y) {
		if (!game.cueReady)
			return;
		
		if (oldx == -1 && oldy == -1) {
			return;
		}
		
		ImmutableVector2f drag = new ImmutableVector2f(x - oldx, y - oldy);
		float dragLength = getDragPower(drag);
		
		if (dragLength > 0) {
			ImmutableVector2f proj = game.cueDragNorm.scale(game.cueDragNorm.dot(drag));
			game.shoot(proj.scale(-powPerPx));
		}
		
		oldx = -1;
		oldy = -1;
	}

	@Override
	public void mouseMoved(int oldx, int oldy, int newx, int newy) {
		game.updateCue(newx, newy);
	}

	@Override
	public void mouseDragged(int oldx, int oldy, int newx, int newy) {
		if (!game.cueReady)
			return;
		
		game.pullCue(getDragPower(newx, newy) / (powPerPx * maxSpeed / maxPullBackPx));
	}
	
	private float getDragPower(ImmutableVector2f drag) {
		float projLength = game.cueDragNorm.dot(drag);
		float dragPower = Math.min(Math.max(projLength, 0), maxSpeed);
		return dragPower * powPerPx;
	}
	private float getDragPower(int x, int y) {
		ImmutableVector2f drag = new ImmutableVector2f(x - this.oldx, y - this.oldy);
		return getDragPower(drag);
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
