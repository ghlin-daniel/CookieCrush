package com.brickgit.cookiecrush;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.List;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

	private static final int SWAP_SPEED = 200;
	private static final int FALL_SPEED = 80;

	private SurfaceHolder holder = getHolder();

	private Level level;

	private int canvasWidth, canvasHeight;
	private Point[][] grid;
	private Bitmap[] bps;
	private int bpWidth, bpHeight, gap = 0;
	private int left, top, right, down;
	private Cookie fromCookie;

	private boolean touchable = true;

	private ScoreListener scoreListener;

	public GameView(Context context) {
		super(context, null);
	}

	public GameView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public GameView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		holder.addCallback(this);
	}

	public void setScoreListener(ScoreListener scoreListener) {
		this.scoreListener = scoreListener;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		init();
	}

	private void init() {
		Canvas canvas = holder.lockCanvas();
		canvasWidth = canvas.getWidth();
		canvasHeight = canvas.getHeight();
		holder.unlockCanvasAndPost(canvas);

		level = new Level();
		level.setScoreListener(scoreListener);
		initBitmaps();
		initGrid();
		drawCookies();
	}

	public void restart() {
		level.restart();
		drawCookies();
	}

	private void initGrid() {
		int numRow = Level.NUM_ROW;
		int numColumn = Level.NUM_COLUMN;

		int initX = (canvasWidth - (bpWidth * numRow) - (gap * (numRow - 1))) / 2;
		int initY = (canvasHeight - (bpHeight * numColumn) - (gap * (numColumn - 1))) / 2;

		grid = new Point[numRow][numColumn];

		for (int row = 0; row < numRow; row++) {
			for (int column = 0; column < numColumn; column++) {
				int x = initX + (column * (bpWidth + gap));
				int y = initY + (row * (bpHeight + gap));
				grid[row][column] = new Point(x, y);
			}
		}

		left = initX;
		top = initY;
		right = left + (bpWidth * numRow) + (gap * (numRow - 1));
		down = top + (bpHeight * numColumn) + (gap * (numColumn - 1));
	}

	private void initBitmaps() {
		bps = new Bitmap[Cookie.NUM_COOKIE_TYPE];
		bps[0] = BitmapFactory.decodeResource(getResources(), R.drawable.croissant);
		bps[1] = BitmapFactory.decodeResource(getResources(), R.drawable.cupcake);
		bps[2] = BitmapFactory.decodeResource(getResources(), R.drawable.danish);
		bps[3] = BitmapFactory.decodeResource(getResources(), R.drawable.donut);
		bps[4] = BitmapFactory.decodeResource(getResources(), R.drawable.macaroon);
		bps[5] = BitmapFactory.decodeResource(getResources(), R.drawable.sugar_cookie);

		Bitmap bitmap = bps[0];
		bpWidth = canvasWidth / 10;
		float ratio = ((float) bpWidth) / (float) bitmap.getWidth();
		bpHeight = (int) (bitmap.getHeight() * ratio);

		for (int i = 0; i < Cookie.NUM_COOKIE_TYPE; i++) {
			bps[i] = Bitmap.createScaledBitmap(bps[i], bpWidth, bpHeight, true);
		}
	}

	private void drawCookies() {
		Canvas canvas = holder.lockCanvas();
		if (canvas == null) return;

		canvas.drawColor(0, PorterDuff.Mode.CLEAR);

		List<Cookie> cookies = level.getListCookies();
		for (Cookie cookie : cookies) {
			Bitmap bitmap = getBitmap(cookie);
			Point point = getPoint(cookie);
			if (null != bitmap) {
				canvas.drawBitmap(bitmap, point.x, point.y, null);
			}
		}
		holder.unlockCanvasAndPost(canvas);
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				if (!touchable) break;
				fromCookie = getCookie((int) event.getX(), (int) event.getY());
				break;
			case MotionEvent.ACTION_MOVE:
				handleSwipe((int) event.getX(), (int) event.getY());
				break;
			case MotionEvent.ACTION_UP:
				break;
		}
		return true;
	}

	private void handleSwipe(int x, int y) {
		if (fromCookie == null) {
			return;
		}

		Cookie toCookie = getCookie(x, y);
		if (toCookie == null) {
			fromCookie = null;
			return;
		}
		if (toCookie == fromCookie) {
			return;
		}

		Coord fromCoord = fromCookie.coord;
		Coord toCoord = toCookie.coord;
		Swap swap;
		if (toCoord.column != fromCoord.column) {
			swap = new Swap(Swap.SWAP_ORIENTATION_HORIZONTAL, fromCookie, toCookie);
		}
		else if (toCoord.row != fromCoord.row) {
			swap = new Swap(Swap.SWAP_ORIENTATION_VERTICAL, fromCookie, toCookie);
		}
		else {
			fromCookie = null;
			return;
		}

		touchable = false;

		List<Swap> swaps = new ArrayList<>();
		swaps.add(swap);

		swap(SWAP_SPEED, swaps, new Swap.SwapListener() {
			@Override
			public void onSwapStart(List<Swap> swaps) {}
			@Override
			public void onSwapEnd(List<Swap> swaps) {
				List<Chain> chains = level.checkChains();
				if (chains.size() == 0) {
					swap(SWAP_SPEED, swaps, null);
					touchable = true;
				} else {
					level.removeChains(chains);
					drawCookies();
					arrangeCookies();
				}
			}
		});
		fromCookie = null;
	}

	private void arrangeCookies() {
		List<Swap> swaps = new ArrayList<>();
		Cookie[][] cookies = level.getCookies();
		for (int column = 0; column < Level.NUM_COLUMN; column++) {
			for (int row = Level.NUM_ROW - 1; row > 0; row--) {
				Cookie cookie = cookies[row][column];
				Cookie uponCookie = cookies[row - 1][column];
				if (cookie.cookieType == Cookie.NO_COOKIE &&
						uponCookie.cookieType != Cookie.NO_COOKIE) {
					Swap swap = new Swap(Swap.SWAP_ORIENTATION_VERTICAL, cookie, uponCookie);
					swaps.add(swap);
				}
			}
		}
		if (swaps.size() != 0) {
			swap(FALL_SPEED, swaps, new Swap.SwapListener() {
				@Override
				public void onSwapStart(List<Swap> swaps) {}
				@Override
				public void onSwapEnd(List<Swap> swaps) {
					arrangeCookies();
				}
			});
		}
		else {
			level.fillCookies();
			drawCookies();
			List<Chain> chains = level.checkChains();
			if (chains.size() != 0) {
				level.removeChains(chains);
				drawCookies();
				arrangeCookies();
			}
			else {
				touchable = true;
			}
		}
	}

	private void swap(int speed, final List<Swap> swaps, final Swap.SwapListener swapListener) {
		ValueAnimator animator;
		animator = ValueAnimator.ofFloat(0.0f, 1.0f);
		animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator valueAnimator) {
				Float percent = (Float) valueAnimator.getAnimatedValue();
				drawSwapes(percent, swaps);
			}
		});
		animator.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animation) {
				if (null != swapListener) {
					swapListener.onSwapStart(swaps);
				}
			}
			@Override
			public void onAnimationEnd(Animator animation) {
				for (Swap swap : swaps) {
					swap.swap();
				}

				if (null != swapListener) {
					swapListener.onSwapEnd(swaps);
				}
			}
			@Override
			public void onAnimationCancel(Animator animation) {}
			@Override
			public void onAnimationRepeat(Animator animation) {}
		});
		animator.setDuration(speed);
		animator.start();
	}

	private void drawSwapes(float percent, List<Swap> swaps) {
		Canvas canvas = holder.lockCanvas();
		if (canvas == null) return;

		canvas.drawColor(0, PorterDuff.Mode.CLEAR);
		List<Cookie> cookies = level.getListCookies();
		for (Cookie cookie : cookies) {
			Swap swap = isInSwaps(cookie, swaps);
			if (null == swap) {
				Point point = getPoint(cookie);
				Bitmap bitmap = getBitmap(cookie);
				if (null != bitmap) canvas.drawBitmap(bitmap, point.x, point.y, null);
				continue;
			}
			else if (cookie == swap.toCookie) continue;

			Cookie fromCookie = swap.fromCookie;
			Point fromCookiePoint = getPoint(fromCookie);
			Cookie toCookie = swap.toCookie;
			Point toCookiePoint = getPoint(toCookie);
			int offset;
			if (swap.orientation == Swap.SWAP_ORIENTATION_HORIZONTAL) {
				offset = (int) ((toCookiePoint.x - fromCookiePoint.x) * percent);
			}
			else {
				offset = (int) ((toCookiePoint.y - fromCookiePoint.y) * percent);
			}

			int fromCookieNewX = fromCookiePoint.x;
			int fromCookieNewY = fromCookiePoint.y;
			if (swap.orientation == Swap.SWAP_ORIENTATION_HORIZONTAL) {
				fromCookieNewX += offset;
			}
			else {
				fromCookieNewY += offset;
			}
			Bitmap fromBitmap = getBitmap(fromCookie);
			if (null != fromBitmap) {
				canvas.drawBitmap(fromBitmap, fromCookieNewX, fromCookieNewY, null);
			}

			int toCookieNewX = toCookiePoint.x;
			int toCookieNewY = toCookiePoint.y;
			if (swap.orientation == Swap.SWAP_ORIENTATION_HORIZONTAL) {
				toCookieNewX -= offset;
			}
			else {
				toCookieNewY -= offset;
			}
			Bitmap bitmap = getBitmap(toCookie);
			if (null != bitmap) {
				canvas.drawBitmap(bitmap, toCookieNewX, toCookieNewY, null);
			}
		}
		holder.unlockCanvasAndPost(canvas);
	}

	private Swap isInSwaps(Cookie cookie, List<Swap> swaps) {
		for (Swap swap : swaps) {
			if (cookie == swap.fromCookie || cookie == swap.toCookie) {
				return swap;
			}
		}
		return null;
	}

	private Bitmap getBitmap(Cookie cookie) {
		int cookieType = cookie.cookieType;
		if (cookieType < 0 || cookieType >= Cookie.NUM_COOKIE_TYPE) {
			return null;
		}
		return bps[cookie.cookieType];
	}

	private Point getPoint(Cookie cookie) {
		Coord coord = cookie.coord;
		return grid[coord.row][coord.column];
	}

	private Cookie getCookie(int x, int y) {
		if (x >= left && x < right && y >= top && y < down) {
			int row = (y - top) / bpHeight;
			int column = (x - left) / bpWidth;
			return level.getCookie(new Coord(row, column));
		}
		return null;
	}
}
