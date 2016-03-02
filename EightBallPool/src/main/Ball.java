package main;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class Ball {
	public static final int RADIUS = 10;
	public static final Color SOLID_COLOR = new Color(0.905f, 0.213f, 0.129f);
	public static final Color STRIPE_COLOR = Color.blue;
	public static final Color WHITE_COLOR = Color.white;
	private static final float minSpeed = 10f;
	private static final float fricAcc = -1f;
	public enum BallType {STRIPES, SOLIDS, WHITE};
	private static int nextId = 0;
	public static PoolTable table;
	
	public Color color;
	public BallType type;
	public int id;
	
	private Vector2f vel;
	private Vector2f pos;
	private ImmutableVector2f spawnPos;
	public boolean pocketed = false;
	
	public Ball(Vector2f spawnPos, BallType t) {
		color = getColor(t);
		
		pos = spawnPos;
		this.spawnPos = new ImmutableVector2f(spawnPos);
		
		type = t;
		id = nextId;
		nextId++;
		
		vel = new Vector2f(0, 0);
	}
	public Ball(ImmutableVector2f spawnPos, BallType t) {
		this(spawnPos.makeVector2f(), t);
	}
	
	public ImmutableVector2f getPos() {
		return new ImmutableVector2f(pos);
	}
	
	public void tick(int delta) {
		// account for friction
		ImmutableVector2f vCopy = new ImmutableVector2f(vel);
		float friction = Math.min(0, fricAcc * delta/1000f);
		addForce(vCopy.scale(friction).makeVector2f());
		
		if (isStopped()) {
			vel.scale(0f);
		}
		
		// apply velocity
		pos.add(vCopy.scale(delta/1000f).makeVector2f());
		
		if (pos.x < 0 || pos.x >= PoolGame.WIDTH || pos.y < 0 || pos.y >= PoolGame.HEIGHT) {
			pocket();
		}
	}
	
	public boolean isStopped() {
		return vel.length() < minSpeed;
	}
	
	public void addForce(Vector2f f) {
		vel.add(f);
	}
	public void addForce(ImmutableVector2f f) {
		addForce(f.makeVector2f());
	}
	
	public void collideBorder (ImmutableVector2f normal) {
		ImmutableVector2f n = normal.normalise();
		ImmutableVector2f v = new ImmutableVector2f(vel);
		ImmutableVector2f ref = v.sub( n.scale(2 * v.dot(n)) );
		vel = ref.makeVector2f();
		
		float x = Math.max(RADIUS, Math.min(pos.x, PoolGame.WIDTH - RADIUS));
		float y = Math.max(RADIUS, Math.min(pos.y, PoolGame.HEIGHT - RADIUS));
		pos = new Vector2f(x, y);
	}
	
	public static ArrayList<ImmutableVector2f> ballCollision(Ball b1, Ball b2, boolean apply) {
		ArrayList<ImmutableVector2f> vels = new ArrayList<ImmutableVector2f>();
		
		ImmutableVector2f toTarg = b2.getPos().sub(b1.getPos());
		ImmutableVector2f targNorm = toTarg.normalise();
		ImmutableVector2f vRel = new ImmutableVector2f(b1.vel).sub(b2.vel);
		ImmutableVector2f velProj = targNorm.scale(vRel.dot(targNorm));
		
		vels.add(velProj.scale(-1f).add(b1.vel));
		vels.add(velProj.add(b2.vel));
		
		if (apply) {
			b1.vel = vels.get(0).makeVector2f();
			b2.vel = vels.get(1).makeVector2f();
			
			ImmutableVector2f displace = toTarg.sub(toTarg.normalise().scale(RADIUS * 2));
			
			b1.pos.add(displace.makeVector2f());
			b2.pos.add(displace.scale(-1f).makeVector2f());
		}
		
		return vels;
	}
	public void pocket() {
		Player curr = table.players[table.playerTurnId];
		if (curr.color == BallType.WHITE && type != BallType.WHITE) {
			for (Player p : table.players) {
				if (p.id == table.playerTurnId) {
					p.color = type;
				} else {
					p.color = (type == BallType.SOLIDS) ? BallType.STRIPES : BallType.SOLIDS;
				}
			}
		}
		
		if (type != curr.color && type != BallType.WHITE) {
			respawn();
		} else {
			vel = new Vector2f(0, 0);
			pocketed = true;
		}
	}
	public void respawn() {
		pos = spawnPos.makeVector2f();
		vel = new Vector2f(0, 0);
		pocketed = false;
	}
	
	public static Color getColor(BallType t) {
		switch(t) {
		case WHITE:
			return WHITE_COLOR;
		case SOLIDS:
			return SOLID_COLOR;
		case STRIPES:
			return STRIPE_COLOR;
		default:
			return null;
		}
	}
}
