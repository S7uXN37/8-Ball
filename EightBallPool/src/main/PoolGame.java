package main;

import java.awt.Font;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.UnicodeFont;
import org.newdawn.slick.font.effects.ColorEffect;

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
	private static final Color BCKG_COLOR = new Color(0.2f, 0.6f, 0.2f);
	private static final Color BORDER_COLOR = Color.black;
	private static final Color POCKET_COLOR = new Color(0f, 0f, 0f, 0.1f);
	private static final Color WIN_COLOR = Color.orange;
	
	// private CUE constants
	private static final float CUE_LENGTH = 200f;
	private static final Color CUE_COLOR = new Color(0, 0, 0, 0.5f);
	private static final Color PATH_COLOR = new Color(1f, 1f, 0, 0.5f);
	
	// private GUI variables
	private UnicodeFont winFont;
	
	// private GAME variables
	private boolean allPocketed = false;
	private float respawnTimer = -1f;
	private PoolTable table;
	
	public PoolGame(String title) {
		super(title);
		
		table = new PoolTable();
		table.players = new Player[]{new Player(0), new Player(1)};
		Ball.table = table;
	}
	public PoolGame() {
		this("Eight-ball Pool Game");
	}
	
	@Override
	public void render(GameContainer container, Graphics g) throws SlickException {
		g.setLineWidth(2f);
		
		// draw background
		g.setAntiAlias(false);
		g.setColor(BORDER_COLOR);
		g.fillRect(0, 0, WIDTH, HEIGHT);
		
		// draw play area
		g.setColor(BCKG_COLOR);
		g.fillRect(2, 2, WIDTH - 4, HEIGHT - 4);
		
		// draw pockets
		g.setColor(POCKET_COLOR);
		for (ImmutableVector2f p : PoolTable.POCKETS) {
			g.fillOval(p.x - PoolTable.POCKET_RADIUS, p.y - PoolTable.POCKET_RADIUS, PoolTable.POCKET_RADIUS * 2, PoolTable.POCKET_RADIUS * 2);
		}
		
		// draw balls
		g.setAntiAlias(true);
		for (Ball b : table.balls) {
			if (b.pocketed)
				continue;
			
			g.setColor(b.color);
			g.fillOval(b.getPos().x - Ball.RADIUS, b.getPos().y - Ball.RADIUS, Ball.RADIUS * 2, Ball.RADIUS * 2);
			
			BallType target = table.players[table.playerTurnId].color;
			if (b.type == BallType.WHITE) {
				g.setColor(Ball.getColor(target));
				g.fillOval(b.getPos().x - Ball.RADIUS / 2f, b.getPos().y - Ball.RADIUS / 2f, Ball.RADIUS, Ball.RADIUS);
			}
		}
		
		if (table.cueReady) {
			// display cue
			ImmutableVector2f cueStart = table.getWhitePos().add(table.cueDragNorm.scale(Ball.RADIUS)).add(table.cueDragNorm.scale(table.cueDragLength));
			ImmutableVector2f cueEnd = cueStart.add(table.cueDragNorm.scale(CUE_LENGTH));
			g.setColor(CUE_COLOR);
			g.drawLine(cueStart.x, cueStart.y, cueEnd.x, cueEnd.y);
			
			if (table.distToCollision != -1) {
				// display estimated path
				ImmutableVector2f pathStart = table.getWhitePos().add(table.cueDragNorm.scale(-Ball.RADIUS));
				ImmutableVector2f pathEnd = pathStart.add(table.cueDragNorm.scale(-(table.distToCollision - Ball.RADIUS)));
				
				g.setColor(PATH_COLOR);
				g.drawLine(pathStart.x, pathStart.y, pathEnd.x, pathEnd.y);
				g.drawOval(pathEnd.x - Ball.RADIUS, pathEnd.y - Ball.RADIUS, Ball.RADIUS * 2, Ball.RADIUS * 2);
				
				if (table.ballCollisionEstimated) {
					// display estimated collision
					ImmutableVector2f whiteCollEnd = pathEnd.add(table.estCollisions.get(0));
					g.drawLine(pathEnd.x, pathEnd.y, whiteCollEnd.x, whiteCollEnd.y);
					
					ImmutableVector2f otherCollStart = pathEnd.add(table.estCollisions.get(1).normalise().scale(2 * Ball.RADIUS));
					ImmutableVector2f otherCollEnd = otherCollStart.add(table.estCollisions.get(1));
					g.drawLine(otherCollStart.x, otherCollStart.y, otherCollEnd.x, otherCollEnd.y);
				}
			}
		}
		
		if (allPocketed) {
			g.setFont(winFont);
			g.setColor(WIN_COLOR);
			g.drawString("YOU WIN!", 200, 150);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void init(GameContainer gc) throws SlickException {
		winFont = new UnicodeFont(new Font("Courier New", Font.BOLD, 100));
		winFont.addGlyphs("YOU WIN!");
		winFont.getEffects().add(new ColorEffect());
		winFont.loadGlyphs();
		
		// add listeners
		InputListener listener = new InputListener(table);
		gc.getInput().addKeyListener(listener);
		gc.getInput().addMouseListener(listener);
		
		// create balls
		table.reset();
	}

	@Override
	public void update(GameContainer container, int delta) throws SlickException {
		// time dilation
		delta *= 2;
		
		boolean allStopped = true;
		for (Ball b : table.balls) {
			b.tick(delta);
			allStopped &= b.isStopped();
		}
		
		if (!table.cueReady && allStopped) {
			table.nextTurn();
		}
		table.cueReady = allStopped;
		
		if (table.cueReady && table.getWhite().pocketed) {
			table.getWhite().respawn();
			System.out.println("White pocketed, respawned");
		}
		
		for (Ball b : table.balls) {
			if (b.pocketed)
				continue;
			
			// check ball collisions
			for (Ball b2 : table.balls) {
				if (b.id >= b2.id || b2.pocketed)
					continue;
				
				ImmutableVector2f dist = b2.getPos().sub(b.getPos());
				float l = dist.length();
				
				if (l < Ball.RADIUS * 2) {
					Ball.ballCollision(b, b2, true);
					
					if (b.type == BallType.WHITE) {
						table.regBallCollision(b2);
					} else if (b2.type == BallType.WHITE) {
						table.regBallCollision(b);
					}
				}
			}
			
			// check wall collisions
			if (b.getPos().x - Ball.RADIUS < 0) {
				// left
				b.collideBorder(new ImmutableVector2f(1, 0));
			}
			if (b.getPos().x + Ball.RADIUS > WIDTH) {
				// right
				b.collideBorder(new ImmutableVector2f(-1, 0));
			}
			if (b.getPos().y - Ball.RADIUS < 0) {
				// top
				b.collideBorder(new ImmutableVector2f(0, 1));
			}
			if (b.getPos().y + Ball.RADIUS > HEIGHT) {
				// bottom
				b.collideBorder(new ImmutableVector2f(0, -1));
			}
			
			// check pocketed
			for (ImmutableVector2f p : PoolTable.POCKETS) {
				if (b.getPos().sub(p).length() < PoolTable.POCKET_RADIUS) {
					b.pocket();
					
					if (b.type != BallType.WHITE) {
						// keep player if at least one own ball was pocketed
						if (b.type == table.players[table.playerTurnId].color) {
							table.setKeepPlayer(true);
						}
					}
				}
			}
		}
		
		table.updateDistToCollision();
		
		int ballsAliveSol = 0;
		int ballsAliveStr = 0;
		for (Ball b : table.balls) {
			if (!b.pocketed && b.type != BallType.WHITE) {
				switch (b.type) {
				case SOLIDS:
					ballsAliveSol++;
					break;
				case STRIPES:
					ballsAliveStr++;
					break;
				default:
					break;
				}
			}
		}
		
		if (ballsAliveStr <= 0 || ballsAliveSol <= 0) {
			if (allStopped && !allPocketed) {
				allPocketed = true;
				respawnTimer = 5f;
			}
		} else {
			allPocketed = false;
		}
		
		if (respawnTimer < 0 && allPocketed) {
			table.reset();
		}
		respawnTimer -= delta / 1000f;
	}
}
