package main;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeSet;
import java.util.Map.Entry;
import java.util.SortedSet;

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
			
			new Ball(new Vector2f(Ball.RADIUS * -4, Ball.RADIUS * 0).add(racket), BallType.STRIPES),
			
			new Ball(new Vector2f(Ball.RADIUS * -2, Ball.RADIUS * 1).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * -2, Ball.RADIUS * -1).add(racket), BallType.STRIPES),

			new Ball(new Vector2f(Ball.RADIUS * 0, Ball.RADIUS * 2).add(racket), BallType.STRIPES),
			new Ball(new Vector2f(Ball.RADIUS * 0, Ball.RADIUS * 0).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 0, Ball.RADIUS * -2).add(racket), BallType.SOLIDS),

			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * 3).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * 1).add(racket), BallType.STRIPES),
			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * -1).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 2, Ball.RADIUS * -3).add(racket), BallType.STRIPES),

			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * 4).add(racket), BallType.STRIPES),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * 2).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * 0).add(racket), BallType.STRIPES),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * -2).add(racket), BallType.SOLIDS),
			new Ball(new Vector2f(Ball.RADIUS * 4, Ball.RADIUS * -4).add(racket), BallType.SOLIDS),
	};
	
	// private BALL variables
	protected ArrayList<Ball> balls;
	protected Player[] players;
	protected int playerTurnId = 0;
	protected boolean keepPlayer = false;
	protected boolean hasHitBall = false;
	protected boolean hasHitOwnBallFirst = true;
	
	// protected CUE variables
	protected ImmutableVector2f cueDragNorm = new ImmutableVector2f(1, 0);
	protected float cueDragLength = 0f;
	protected boolean cueReady = true;
	protected float distToCollision = -1f;
	protected boolean ballCollisionEstimated = false;
	protected ArrayList<ImmutableVector2f> estCollisions = new ArrayList<ImmutableVector2f>();
	
	protected void updateDistToCollision() {
		RaycastHit ret = raycast(getWhitePos(), cueDragNorm.scale(-1f), getWhite().id);
		distToCollision = ret.distance;
		
		ArrayList<ImmutableVector2f> colls = new ArrayList<ImmutableVector2f>();
		colls.add(ret.actorVelocity);
		colls.add(ret.objectVelocity);
		estCollisions = colls;
		ballCollisionEstimated = ret.actorVelocity != null;
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
	
	private RaycastHit raycast(ImmutableVector2f pos, ImmutableVector2f dir, int idIgnore) {
		ImmutableVector2f dirNorm = dir.normalise();
		
		SortedSet<RaycastHit> ballData =
				new TreeSet<RaycastHit> (
						new Comparator<RaycastHit>() {
							
							@Override
							public int compare(RaycastHit arg0,
									RaycastHit arg1) {
								return arg0.getDistance().compareTo(arg1.distance);
							}
						}
				);
		
		for (Ball b : balls) {
			if (b.type == BallType.WHITE || b.pocketed || b.id == idIgnore)
				continue;
			
			ImmutableVector2f pMinusA = b.getPos().sub(pos);
			float projLength = pMinusA.dot(dirNorm);
			if (projLength < 0)
				continue;
			
			ImmutableVector2f proj = dirNorm.scale(projLength);
			ImmutableVector2f distPos = pos.add(proj);
			
			ImmutableVector2f toBall = b.getPos().sub(distPos);
			
			if (toBall.length() <= 2 * Ball.RADIUS) {
				float factor = (float) Math.sqrt((Ball.RADIUS * 2) * (Ball.RADIUS * 2) - distPos.sub(b.getPos()).lengthSquared());
				ImmutableVector2f displacement = proj.normalise().scale(factor);
				ImmutableVector2f collPos = distPos.sub(displacement);
				ballData.add( getBallRaycastData(collPos.sub(pos).length(), dirNorm, collPos, b) );
			}
		}
		
		if (ballData.size() > 0) {
			return ballData.first();
		}
		
		// minimum distance to border on axis
		float dx = dirNorm.x > 0 ? PoolGame.WIDTH - pos.x : pos.x;
		float dy = dirNorm.y > 0 ? PoolGame.HEIGHT - pos.y : pos.y;
		
		// factor in the radius of the ball
		dx -= Ball.RADIUS;
		dy -= Ball.RADIUS;
		
		// project dx and dy on dirNorm (modified form of scalar projection)
		float distRightLeft = dirNorm.x == 0 ? Float.POSITIVE_INFINITY : Math.abs(dx / dirNorm.x);
		float distUpDown = dirNorm.y == 0 ? Float.POSITIVE_INFINITY : Math.abs(dy / dirNorm.y);
		
		float retDist = Math.min(distRightLeft, distUpDown);
		return new RaycastHit(null, null, null, retDist);
	}
	
	private RaycastHit getBallRaycastData(float dist, ImmutableVector2f velNorm, ImmutableVector2f collPos, Ball b) {
		Ball testBall = new Ball(collPos, BallType.WHITE);
		testBall.addForce(velNorm.scale(20f));
		
		RaycastHit coll = Ball.ballCollision(testBall, b, false);
		coll.distance = dist;
		return coll;
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
		BallType filter = players[playerTurnId].color;
		for (Ball b : balls) {
			if (b.type == BallType.WHITE || b.pocketed)
				continue;
			
			if (filter != BallType.WHITE) { // should we use filter
				if (b.type != filter)
					continue;
			}
			
			// find best shot
			ImmutableVector2f bestShot = null;
			float bestScore = -Float.MAX_VALUE;
			
			for (ImmutableVector2f pocket : POCKETS) {
				ImmutableVector2f optVel = pocket.sub(b.getPos()).normalise();
				ImmutableVector2f optCollPoint = b.getPos().add(optVel.scale(-2f * Ball.RADIUS)); // position of white on collision event
				ImmutableVector2f optShot = optCollPoint.sub(getWhitePos());
				
				// if vectors point roughly in the same direction (angle between < 90�), add to map
				if (optShot.dot(optVel) > 0) {
					RaycastHit raycastWhite = raycast(getWhitePos(), optShot, getWhite().id);
					
					float score = 0f;
					boolean improved = false;
					
					// recalculate if a collision happens before we reach the ball
					if (raycastWhite.distance < optShot.length() - 3 * Ball.RADIUS) {
						Ball other = raycastWhite.other;
						if (other.type == players[playerTurnId].color) {
							ImmutableVector2f optPushShot;
							
							// push original ball
							ImmutableVector2f optPushVel = optCollPoint.sub(other.getPos()).normalise();
							ImmutableVector2f optPushCollPoint = other.getPos().add(optPushVel.scale(-2f * Ball.RADIUS));
							optPushShot = optPushCollPoint.sub(getWhitePos());
							
							if (optPushShot.dot(optPushVel) > 0) {
								RaycastHit pushRaycastWhite = raycast(getWhitePos(), optPushShot, getWhite().id);
								RaycastHit pushRaycastOther = raycast(other.getPos(), optPushVel, other.id);
								
								// consider if no collisions in between
								if (pushRaycastWhite.distance >= optPushShot.length() - 3 * Ball.RADIUS
										&& pushRaycastOther.distance >= optPushVel.length() - 3 * Ball.RADIUS) {
									raycastWhite = pushRaycastWhite;
									optShot = optPushShot;
									improved = true;
									System.out.println("Shot improved");
								}
							}
						}
						
						if (!improved) {
							score -= 200;
						}
					}
					
					
					// don't add if collision doesn't trigger (angle too flat?)
					if (raycastWhite.actorVelocity == null)
						continue;
					
					// score = efficiency of shot
					score += raycastWhite.objectVelocity.length();
					
					if (!improved) {
						RaycastHit raycastBall = raycast(b.getPos(), optVel, b.id);
						// decrease score if ball collides
						if (raycastBall.actorVelocity != null) {
							score -= 50f;
						}
					}
					
					System.out.println("Score: " + score);
					
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
	
	protected void reset() {
		balls = new ArrayList<Ball>();
		for (Ball b : BALL_PRESETS) {
			ImmutableVector2f spawn = new ImmutableVector2f(b.getPos());
			BallType t = b.type;
			
			balls.add(new Ball(spawn, t));
		}
		
		for (Player p : players) {
			p.color = BallType.WHITE;
		}
	}
	
	public void setKeepPlayer(boolean keep) {
		keepPlayer = keep;
	}
	public void nextTurn() {
		if (!keepPlayer || getWhite().pocketed || !hasHitOwnBallFirst || !hasHitBall)
			playerTurnId = (playerTurnId + 1) % 2;
		
		keepPlayer = false;
		hasHitBall = false;
		hasHitOwnBallFirst = false;
	}
	
	public void regBallCollision(Ball b) {
		if (!hasHitBall) {
			hasHitOwnBallFirst = b.type == players[playerTurnId].color;
		}

		hasHitBall = true;
	}
	
	public void overrideOwnBallHit(boolean b) {
		hasHitBall = b;
		hasHitOwnBallFirst = b;
	}
}

class RaycastHit {
	final Ball other;
	final ImmutableVector2f actorVelocity;
	final ImmutableVector2f objectVelocity;
	
	float distance;
	
	public RaycastHit(Ball obj, ImmutableVector2f actorVel, ImmutableVector2f objectVel, float dist) {
		other = obj;
		actorVelocity = actorVel;
		objectVelocity = objectVel;
		distance = dist;
	}

	public Float getDistance() {
		return new Float(distance);
	}
}
