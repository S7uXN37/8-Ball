package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class Ball {
	public static final int BALL_RADIUS = 10;
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
		
		if (vel.length() < 10f) {
			vel.scale(0f);
		}
		
		// apply velocity
		pos.add(vCopy.scale(delta/1000f).makeVector2f());
	}
	
	public void addForce(Vector2f f) {
		vel.add(f);
	}
	
	public void collide (ImmutableVector2f normal) {
		ImmutableVector2f n = normal.normalise();
		ImmutableVector2f v = new ImmutableVector2f(vel);
		ImmutableVector2f ref = v.sub( n.scale(2 * v.dot(n)) );
		vel = ref.makeVector2f();
	}
}
