package gui;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.geom.Vector2f;

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
	
	public static final int WIDTH = 810;
	public static final int HEIGHT = 420;
	
	private static final Color BCKG_COLOR = Color.lightGray;
	private static final Color BORDER_COLOR = Color.black;
	private static final Color POCKET_COLOR = Color.red;
	
	private static final Vector2f[] POCKETS = new Vector2f[]{
			new Vector2f(0, 0),
			new Vector2f(405, 0),
			new Vector2f(810, 0),
			new Vector2f(0, 420),
			new Vector2f(405, 420),
			new Vector2f(810, 420)
	};
	private static final int POCKET_RADIUS = 30;
	
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
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		gc.getInput().addKeyListener(new InputListener());
		gc.getInput().addMouseListener(new InputListener());
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		// TODO Auto-generated method stub

	}
}
