package com.brickgit.cookiecrush;

public class Coord {
	public int row;
	public int column;

	public Coord(int row, int column) {
		this.row = row;
		this.column = column;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && (obj instanceof Coord)) {
			Coord coord = (Coord) obj;
			if (this.row == coord.row && this.column == coord.column) {
				return true;
			}
		}
		return false;
	}
}
