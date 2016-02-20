package main;

import org.newdawn.slick.Color;
import org.newdawn.slick.geom.Vector2f;

public class Ball {
	public Color color;
	public Vector2f pos;
	
	public Ball(Color c, Vector2f spawnPos) {
		color = c;
		pos = spawnPos;
	}
	public Ball(Color c, ImmutableVector2f spawnPos) {
		this(c, spawnPos.makeVector2f());
	}
}
