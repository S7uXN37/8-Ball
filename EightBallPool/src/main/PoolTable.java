package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;

import org.newdawn.slick.geom.Vector2f;

import main.Ball.BallType;

public class PoolTable {
	// public POCKETS constants
	public static final ImmutableVector2f[] POCKETS = new ImmutableVector2f[]{
			new ImmutableVector2f(Ball.RADIUS, Ball.RADIUS),
			new ImmutableVector2f(405, Ball.RADIUS),
			new ImmutableVector2f(810 - Ball.RADIUS, Ball.RADIUS),
			new ImmutableVector2f(Ball.RADIUS, 420 - Ball.RADIUS),
			new ImmutableVector2f(405, 420 - Ball.RADIUS),
			new ImmutableVector2f(810 - Ball.RADIUS, 420 - Ball.RADIUS)
	};
	public static final int POCKET_RADIUS = Ball.RADIUS * 2;
	
	// public BALL constants
	private static final Vector2f racket = new Vector2f(600, 210);
	private static final Ball[] BALL_PRESETS = new Ball[]{
			new Ball(new Vector2f(200, 210), BallType.WHITE),
			
			new Ball(new Vector2f(Ball.RADIUS * -4, Ball.RADIUS * 0).add(racket), BallType.SOLIDS),
			
			new Ball(new Vector2f(Ball.RADIUS * -2, Ball.RADIUS * 1).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * -2, Ball.RADIUS * -1).add(racket), BallType.SOLIDS),
			
			new Ball(new Vector2f(Ball.RADIUS * 0, Ball.RADIUS * 0).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 0, Ball.RADIUS * 2).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 0, Ball.RADIUS * -2).add(racket), BallType.SOLIDS),

			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * -3).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * -1).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * 1).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * 3).add(racket), BallType.SOLIDS),
			
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * 0).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * 2).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * 4).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * -2).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * -4).add(racket), BallType.SOLIDS),
	};
	
	// private BALL variables
	protected ArrayList<Ball> balls;
	
	// protected CUE variables
	protected ImmutableVector2f cueDragNorm = new ImmutableVector2f(1, 0);
	protected float cueDragLength = 0f;
	protected boolean cueReady = true;
	protected float distToCollision = -1f;
	protected boolean ballCollisionEstimated = false;
	protected ArrayList<ImmutableVector2f> estCollisions = new ArrayList<ImmutableVector2f>();
	
	protected void updateDistToCollision() {
		Map.Entry<Float, ArrayList<ImmutableVector2f>> ret = raycast(getWhitePos(), cueDragNorm.scale(-1f), getWhite().id);
		distToCollision = ret.getKey();

		estCollisions = ret.getValue();
		ballCollisionEstimated = ret.getValue() != null;
	}
	public void shoot(Vector2f shot) {
		getWhite().addForce(shot);
		System.out.println("shot: " + shot.length());
		
		pullCue(0f);
	}
	public void shoot(ImmutableVector2f shot) {
		shoot(shot.makeVector2f());
	}
	
	public void pullCue(float dragDist) {
		cueDragLength = dragDist;
	}
	
	public void updateCue(int x, int y) {
		ImmutableVector2f toCursor = getWhitePos().sub(new Vector2f(x, y));
		cueDragNorm = toCursor.normalise();
	}
	
	public ImmutableVector2f getWhitePos() {
		return balls.get(0).getPos();
	}
	public Ball getWhite() {
		return balls.get(0);
	}
	
	private Map.Entry<Float, ArrayList<ImmutableVector2f>> raycast(ImmutableVector2f pos, ImmutableVector2f dir, int idIgnore) {
		ImmutableVector2f dirNorm = dir.normalise();
		
		for (float dist = 1; dist < Math.sqrt(PoolGame.WIDTH * PoolGame.WIDTH + PoolGame.HEIGHT * PoolGame.HEIGHT) + 500; dist += 1f) {
			ImmutableVector2f distPos = pos.add(dirNorm.scale(dist));
			
			for (Ball b : balls) {
				if (b.type == BallType.WHITE || b.pocketed || b.id == idIgnore)
					continue;
				
				ImmutableVector2f toBall = b.getPos().sub(distPos);
				
				if (toBall.length() <= 2 * Ball.RADIUS) {
					Ball testBall = new Ball(distPos, BallType.WHITE);
					testBall.addForce(dirNorm.scale(20f));
					
					final float retDist = dist;
					final ArrayList<ImmutableVector2f> retColl = Ball.ballCollision(testBall, b, false);
					return new Map.Entry<Float, ArrayList<ImmutableVector2f>>() {

						@Override
						public Float getKey() {
							return retDist;
						}

						@Override
						public ArrayList<ImmutableVector2f> getValue() {
							return retColl;
						}

						@Override
						public ArrayList<ImmutableVector2f> setValue(ArrayList<ImmutableVector2f> value) {
							return null;
						}
					};
				}
			}
			
			if (
					distPos.x - Ball.RADIUS < 0 ||
					distPos.x - Ball.RADIUS >= PoolGame.WIDTH ||
					distPos.x + Ball.RADIUS < 0 ||
					distPos.x + Ball.RADIUS >= PoolGame.WIDTH ||
					distPos.y - Ball.RADIUS < 0 ||
					distPos.y - Ball.RADIUS >= PoolGame.HEIGHT ||
					distPos.y + Ball.RADIUS < 0 ||
					distPos.y + Ball.RADIUS >= PoolGame.HEIGHT
			) {
				final float retDist = dist;
				return new Map.Entry<Float, ArrayList<ImmutableVector2f>>() {

					@Override
					public Float getKey() {
						return retDist;
					}

					@Override
					public ArrayList<ImmutableVector2f> getValue() {
						return null;
					}

					@Override
					public ArrayList<ImmutableVector2f> setValue(ArrayList<ImmutableVector2f> value) {
						return null;
					}
				};
			}
		}
		
		return new Map.Entry<Float, ArrayList<ImmutableVector2f>>() {

			@Override
			public Float getKey() {
				return -1f;
			}

			@Override
			public ArrayList<ImmutableVector2f> getValue() {
				return null;
			}

			@Override
			public ArrayList<ImmutableVector2f> setValue(ArrayList<ImmutableVector2f> value) {
				return null;
			}
		};
	}
	
	public void aiShoot() {
		// set of entries, sorted by values
		TreeSet<Map.Entry<ImmutableVector2f, Float>> sortedShots = new TreeSet<Map.Entry<ImmutableVector2f, Float>> (
				new Comparator<Map.Entry<ImmutableVector2f, Float>>() {
					@Override
					public int compare(Entry<ImmutableVector2f, Float> o1, Entry<ImmutableVector2f, Float> o2) {
						return o1.getValue().compareTo(o2.getValue());
					}
				});
		
		// must be assigned from other map
		HashMap<ImmutableVector2f, Float> possibleShots = new HashMap<ImmutableVector2f, Float>();
		
		// look at each target ball
		for (Ball b : balls) {
			if (b.type == BallType.WHITE || b.pocketed)
				continue;
			
			// find best shot
			ImmutableVector2f bestShot = null;
			float bestScore = -Float.MAX_VALUE;
			
			for (ImmutableVector2f pocket : POCKETS) {
				ImmutableVector2f optVel = pocket.sub(b.getPos()).normalise();
				ImmutableVector2f optCollPoint = b.getPos().add(optVel.scale(-2f * Ball.RADIUS)); // position of white on collision event
				ImmutableVector2f optShot = optCollPoint.sub(getWhitePos());
				
				// if vectors point roughly in the same direction (angle between < 90°), add to map
				if (optShot.dot(optVel) > 0) {
					Map.Entry<Float, ArrayList<ImmutableVector2f>> raycastWhite = raycast(getWhitePos(), optShot, getWhite().id);
					
					// don't add if a collision happens before we reach the ball
					if (raycastWhite.getKey() < optShot.length() - 3 * Ball.RADIUS)
						continue;
					
					Map.Entry<Float, ArrayList<ImmutableVector2f>> raycastBall = raycast(b.getPos(), optVel, b.id);
					
					// don't add if collision doesn't trigger (angle too flat?)
					if (raycastWhite.getValue() == null)
						continue;
					
					// score = efficiency of shot
					float score = raycastWhite.getValue().get(1).length();
					
					// decrease score if ball rebounds
					if (raycastBall.getValue() != null) {
						score -= 10f;
					}
					
					if (score > bestScore) {
						bestShot = optShot;
						bestScore = score;
					}
				}
			}
			
			// add the best shot
			if (bestShot != null) {
				possibleShots.put(bestShot.normalise().scale(InputListener.getMaxShotStrength()), bestScore);
			}
		}
		
		// add all shots, so they can be sorted
		sortedShots.addAll(possibleShots.entrySet());
		
		// if there are any possible shots
		if (sortedShots.size() > 0) {
			// choose the one with the largest score and shoot
			final Map.Entry<ImmutableVector2f, Float> chosen = sortedShots.last();
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					shoot(chosen.getKey());
				}
			}).start();
		}
	}
	
	protected void spawnBalls () {
		balls = new ArrayList<Ball>();
		for (Ball b : BALL_PRESETS) {
			ImmutableVector2f spawn = new ImmutableVector2f(b.getPos());
			BallType t = b.type;
			
			balls.add(new Ball(spawn, t));
		}
	}
}
