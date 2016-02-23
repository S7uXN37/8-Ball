package main;

import java.util.ArrayList;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class Ball {
	public static final int RADIUS = 10;
	private static final Color SOLID_COLOR = Color.red;
	private static final Color STRIPE_COLOR = Color.blue;
	private static final Color WHITE_COLOR = Color.white;
	private static final float minSpeed = 10f;
	public enum BallType {STRIPES, SOLIDS, WHITE};
	private static int nextId = 0;
	
	public Color color;
	public BallType type;
	public int id;
	
	private Vector2f vel;
	private Vector2f pos;
	private ImmutableVector2f spawnPos;
	public boolean pocketed = false;
	
	public Ball(Vector2f spawnPos, BallType t) {
		switch (t) {
		case WHITE:
			color = WHITE_COLOR;
			break;
		case SOLIDS:
			color = SOLID_COLOR;
			break;
		case STRIPES:
			color = STRIPE_COLOR;
			break;
		}
		
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
		float friction = 1 * delta/1000f;
		vel.add(vCopy.scale(-friction).makeVector2f());
		
		if (isStopped()) {
			vel.scale(0f);
		}
		
		// apply velocity
		pos.add(vCopy.scale(delta/1000f).makeVector2f());
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
		vel = new Vector2f(0, 0);
		pocketed = true;
	}
	public void respawn() {
		pos = spawnPos.makeVector2f();
		vel = new Vector2f(0, 0);
		pocketed = false;
	}
}
