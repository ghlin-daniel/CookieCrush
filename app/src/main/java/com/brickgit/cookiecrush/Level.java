package com.brickgit.cookiecrush;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Level {
	public final static int NUM_ROW     = 8;
	public final static int NUM_COLUMN  = 8;

	private final static int SHORTEST_CHAIN_SCORE = 60;

	private Cookie[][] cookies = new Cookie[NUM_ROW][NUM_COLUMN];
	private List<Cookie> listCookies = new ArrayList<>();

	private ScoreListener scoreListener;

	public Level() {
		shuffle();
	}

	public void restart() {
		shuffle();
	}

	public void setScoreListener(ScoreListener scoreListener) {
		this.scoreListener = scoreListener;
	}

	public Cookie[][] getCookies() {
		return cookies;
	}
	public List<Cookie> getListCookies() {
		return listCookies;
	}

	public Cookie getCookie(Coord coord) {
		int row = coord.row;
		int column = coord.column;

		if (row < 0 || NUM_ROW <= row) {
			return null;
		}
		if (column < 0 || NUM_COLUMN <= column) {
			return null;
		}

		return cookies[row][column];
	}

	public List<Chain> checkChains() {
		List<Chain> chains = new ArrayList<>();
		for (int row = 0; row < NUM_ROW; row++) {
			for (int column = 0; column < NUM_COLUMN; column++) {
				Cookie head = cookies[row][column];
				if (head.cookieType == Cookie.NO_COOKIE) continue;
				checkRightChain(chains, head);
				checkDownChain(chains, head);
			}
		}
		return chains;
	}

	private boolean isInChains(List<Chain> chains, int direction, Cookie cookie) {
		for (Chain chain : chains) {
			if (chain.direction == direction) {
				Coord cookieCoord = cookie.coord;
				Coord headCoord = chain.head.coord;
				Coord tailCoord = chain.tail.coord;
				if (direction == Chain.DIRECTION_RIGHT) {
					if (cookieCoord.row >= headCoord.row && cookieCoord.row <= tailCoord.row) {
						return true;
					}
				}
				else if (direction == Chain.DIRECTION_DOWN) {
					if (cookieCoord.column >= headCoord.column && cookieCoord.column <= tailCoord.column) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private void checkRightChain(List<Chain> chains, Cookie head) {
		if (isInChains(chains, Chain.DIRECTION_RIGHT, head)) {
			return;
		}

		Coord headCoord = head.coord;
		int nextColumn = headCoord.column + 1;
		if (nextColumn >= NUM_COLUMN) {
			return;
		}

		Cookie tail = head;
		Cookie next = cookies[headCoord.row][nextColumn];
		while (next.cookieType == tail.cookieType) {
			tail = next;
			Coord nextCoord = next.coord;
			nextColumn = nextCoord.column + 1;
			if (nextColumn >= NUM_COLUMN) break;
			next = cookies[nextCoord.row][nextColumn];
		}
		Coord tailCoord = tail.coord;
		if (tailCoord.column - headCoord.column + 1 >= Chain.SHORTEST_LENGTH) {
			Chain chain = new Chain(Chain.DIRECTION_RIGHT, head, tail);
			chains.add(chain);
		}
	}

	private void checkDownChain(List<Chain> chains, Cookie head) {
		if (isInChains(chains, Chain.DIRECTION_DOWN, head)) {
			return;
		}

		Coord headCoord = head.coord;
		int nextRow = headCoord.row + 1;
		if (nextRow >= NUM_ROW) {
			return;
		}

		Cookie tail = head;
		Cookie next = cookies[nextRow][headCoord.column];
		while (next.cookieType == tail.cookieType) {
			tail = next;
			Coord nextCoord = next.coord;
			nextRow = nextCoord.row + 1;
			if (nextRow >= NUM_ROW) break;
			next = cookies[nextRow][nextCoord.column];
		}
		Coord tailCoord = tail.coord;
		if (tailCoord.row - headCoord.row + 1 >= Chain.SHORTEST_LENGTH) {
			Chain chain = new Chain(Chain.DIRECTION_DOWN, head, tail);
			chains.add(chain);
		}
	}

	public void removeChains(List<Chain> chains) {
		for (Chain chain : chains) {
			Cookie head = chain.head;
			Coord headCoord = head.coord;
			Cookie tail = chain.tail;
			Coord tailCoord = tail.coord;
			if (chain.direction == Chain.DIRECTION_RIGHT) {
				int row = headCoord.row;
				for (int column = headCoord.column; column <= tailCoord.column; column++) {
					Cookie cookie = cookies[row][column];
					cookie.cookieType = Cookie.NO_COOKIE;
				}
				addScore(tailCoord.column - headCoord.column + 1);
			}
			else {
				int column = headCoord.column;
				for (int row = headCoord.row; row <= tailCoord.row; row++) {
					Cookie cookie = cookies[row][column];
					cookie.cookieType = Cookie.NO_COOKIE;
				}
				addScore(tailCoord.row - headCoord.row + 1);
			}
		}
	}

	private void addScore(int chainLength) {
		int score = (chainLength - 2) * SHORTEST_CHAIN_SCORE;
		if (null != scoreListener) scoreListener.onScore(score);
	}

	public void fillCookies() {
		Random random = new Random();
		for (Cookie cookie : listCookies) {
			if (cookie.cookieType == Cookie.NO_COOKIE) {
				int cookieType = random.nextInt(Cookie.NUM_COOKIE_TYPE);
				cookie.cookieType = cookieType;
			}
		}
	}

	private List<Cookie> shuffle() {
		Random random = new Random();
		listCookies.clear();
		for (int row = 0; row < NUM_ROW; row++) {
			for (int column = 0; column < NUM_COLUMN; column++) {
				int cookieType = random.nextInt(Cookie.NUM_COOKIE_TYPE);
				Cookie cookie = new Cookie(cookieType, row, column);
				listCookies.add(cookie);
				cookies[row][column] = cookie;
			}
		}
		return listCookies;
	}
}
