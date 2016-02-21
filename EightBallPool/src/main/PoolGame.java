package main;

import java.util.ArrayList;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

import main.Ball.BallType;

public class PoolGame extends BasicGame {
	public static void main(String[] args) {
		try {
			PoolGame game = new PoolGame();
			AppGameContainer appgc = new AppGameContainer(game, PoolGame.WIDTH, PoolGame.HEIGHT, false);
			
			appgc.setShowFPS(false);
			appgc.setAlwaysRender(false);
			appgc.setForceExit(true);
			
			appgc.start();
		} catch (SlickException e) {
			e.printStackTrace();
		}
	}
	
	// public GUI constants
	public static final int WIDTH = 810;
	public static final int HEIGHT = 420;
	
	// private GUI constants
	private static final Color BCKG_COLOR = Color.lightGray;
	private static final Color BORDER_COLOR = Color.black;
	private static final Color POCKET_COLOR = Color.red;
	
	// private POCKETS constants
	private static final Vector2f[] POCKETS = new Vector2f[]{
			new Vector2f(0, 0),
			new Vector2f(405, 0),
			new Vector2f(810, 0),
			new Vector2f(0, 420),
			new Vector2f(405, 420),
			new Vector2f(810, 420)
	};
	private static final int POCKET_RADIUS = 30;
	
	// public BALL constants
	private static final Vector2f racket = new Vector2f(600, 210);
	private static final Ball[] BALL_PRESETS = new Ball[]{
			new Ball(Color.white, new Vector2f(200, 210), BallType.WHITE),
			
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * -4, Ball.BALL_RADIUS * 0).add(racket), BallType.SOLIDS),
			
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * -2, Ball.BALL_RADIUS * 1).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * -2, Ball.BALL_RADIUS * -1).add(racket), BallType.SOLIDS),
			
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 0, Ball.BALL_RADIUS * 0).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 0, Ball.BALL_RADIUS * 2).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 0, Ball.BALL_RADIUS * -2).add(racket), BallType.SOLIDS),

			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 2, Ball.BALL_RADIUS * -3).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 2, Ball.BALL_RADIUS * -1).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 2, Ball.BALL_RADIUS * 1).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 2, Ball.BALL_RADIUS * 3).add(racket), BallType.SOLIDS),
			
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 4, Ball.BALL_RADIUS * 0).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 4, Ball.BALL_RADIUS * 2).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 4, Ball.BALL_RADIUS * 4).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 4, Ball.BALL_RADIUS * -2).add(racket), BallType.SOLIDS),
			new Ball(Color.red, new Vector2f(Ball.BALL_RADIUS * 4, Ball.BALL_RADIUS * -4).add(racket), BallType.SOLIDS),
	};
	
	// private BALL variables
	private ArrayList<Ball> balls;
	
	
	public PoolGame(String title) {
		super(title);
	}
	public PoolGame() {
		this("Eight-ball Pool Game");
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setColor(BORDER_COLOR);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		g.setColor(POCKET_COLOR);
		for (Vector2f p : POCKETS) {
			g.fillRect(p.x - POCKET_RADIUS, p.y - POCKET_RADIUS, POCKET_RADIUS * 2, POCKET_RADIUS * 2);
		}
		
		g.setColor(BCKG_COLOR);
		g.fillRect(2, 2, WIDTH - 4, HEIGHT - 4);
		
		for (Ball b : balls) {
			g.setColor(b.color);
			g.fillOval(b.getPos().x - Ball.BALL_RADIUS, b.getPos().y - Ball.BALL_RADIUS, Ball.BALL_RADIUS * 2, Ball.BALL_RADIUS * 2);
		}
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		// add listeners
		gc.getInput().addKeyListener(new InputListener(this));
		gc.getInput().addMouseListener(new InputListener(this));
		
		// create balls
		balls = new ArrayList<Ball>();
		for (Ball b : BALL_PRESETS) {
			ImmutableVector2f spawn = new ImmutableVector2f(b.getPos());
			Color c = new Color(b.color);
			BallType t = b.type;
			
			balls.add(new Ball(c, spawn, t));
		}
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		for (Ball b : balls) {
			b.tick(delta);
		}
		for (Ball b : balls) {
			for (Ball b2 : balls) {
				if (b.id >= b2.id)
					continue;
				
				ImmutableVector2f dist = b2.getPos().sub(b.getPos());
				float l = dist.length();
				
				if (l < Ball.BALL_RADIUS * 2) {
					System.out.println("collision ball");
					b.collideBall(b2);
				}
			}
			
			if (b.getPos().x - Ball.BALL_RADIUS < 0) {
				// left
				b.collideBorder(new ImmutableVector2f(1, 0));
			}
			if (b.getPos().x + Ball.BALL_RADIUS > WIDTH) {
				// right
				b.collideBorder(new ImmutableVector2f(-1, 0));
			}
			if (b.getPos().y - Ball.BALL_RADIUS < 0) {
				// top
				b.collideBorder(new ImmutableVector2f(0, 1));
			}
			if (b.getPos().y + Ball.BALL_RADIUS > HEIGHT) {
				// bottom
				b.collideBorder(new ImmutableVector2f(0, -1));
			}
		}
	}
	
	public void shoot(Vector2f shot) {
		balls.get(0).addForce(shot);
		System.out.println("shot: " + shot.length());
	}
}
