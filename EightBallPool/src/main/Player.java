package main;

import main.Ball.BallType;

public class Player {
	public BallType color = BallType.WHITE;
	public int id;
	
	public Player(int index) {
		id = index;
	}
}
