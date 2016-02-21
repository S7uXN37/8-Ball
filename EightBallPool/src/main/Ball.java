package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class Ball {
	public static final int BALL_RADIUS = 10;
	private static final float minSpeed = 10f;
	public enum BallType {STRIPES, SOLIDS, WHITE};
	private static int nextId;
	
	public Color color;
	public BallType type;
	public int id;
	
	private Vector2f vel;
	private Vector2f pos;
	
	public Ball(Color c, Vector2f spawnPos, BallType t) {
		color = c;
		pos = spawnPos;
		type = t;
		id = nextId;
		nextId++;
		
		vel = new Vector2f(0, 0);
	}
	public Ball(Color c, ImmutableVector2f spawnPos, BallType t) {
		this(c, spawnPos.makeVector2f(), t);
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
	
	public void collideBall(Ball b2) {
		ImmutableVector2f toTarg = b2.getPos().sub(getPos());
		ImmutableVector2f targNorm = toTarg.normalise();
		ImmutableVector2f vRel = new ImmutableVector2f(vel).sub(b2.vel);
		ImmutableVector2f velProj = targNorm.scale(vRel.dot(targNorm));
		
		addForce(velProj.scale(-1f));
		b2.addForce(velProj);
		
		ImmutableVector2f displace = toTarg.sub(toTarg.normalise().scale(BALL_RADIUS * 2));
		pos.add(displace.makeVector2f());
		b2.pos.add(displace.scale(-1f).makeVector2f());
	}
}
