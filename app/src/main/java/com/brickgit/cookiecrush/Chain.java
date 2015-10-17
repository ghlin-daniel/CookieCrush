package com.brickgit.cookiecrush;

public class Chain {
	public final static int SHORTEST_LENGTH = 3;
	public final static int DIRECTION_RIGHT = 0;
	public final static int DIRECTION_DOWN  = 1;

	public int direction;
	public Cookie head;
	public Cookie tail;

	public Chain(int direction, Cookie head, Cookie tail) {
		this.direction = direction;
		this.head = head;
		this.tail = tail;
	}

	@Override
	public String toString() {
		return "Chain " + direction + " (" + head.coord.row + ", " + head.coord.column + ") -> (" + tail.coord.row + ", " + tail.coord.column + ")";
	}
}
