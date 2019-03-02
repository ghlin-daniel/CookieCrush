package com.brickgit.cookiecrush;

import java.util.List;

public class Swap {
  public static final int SWAP_ORIENTATION_HORIZONTAL = 0;
  public static final int SWAP_ORIENTATION_VERTICAL = 1;

  public int orientation;
  public Cookie fromCookie;
  public Cookie toCookie;

  public Swap(int direction, Cookie fromCookie, Cookie toCookie) {
    this.orientation = direction;
    this.fromCookie = fromCookie;
    this.toCookie = toCookie;
  }

  public void swap() {
    int tmpCookieType = fromCookie.cookieType;
    fromCookie.cookieType = toCookie.cookieType;
    toCookie.cookieType = tmpCookieType;
  }

  @Override
  public String toString() {
    return "Swap "
        + orientation
        + " ("
        + fromCookie.coord.row
        + ", "
        + fromCookie.coord.column
        + ") -> ("
        + toCookie.coord.row
        + ", "
        + toCookie.coord.column
        + ")";
  }

  public interface SwapListener {
    void onSwapStart(List<Swap> swaps);

    void onSwapEnd(List<Swap> swaps);
  }
}
