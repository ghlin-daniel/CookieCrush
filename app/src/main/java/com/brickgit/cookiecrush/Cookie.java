package com.brickgit.cookiecrush;

public class Cookie {
  public static final int NO_COOKIE = -1;
  public static final int NUM_COOKIE_TYPE = 6;

  public Coord coord;
  public int cookieType;

  public Cookie(int cookieType, int row, int column) {
    this.cookieType = cookieType;
    this.coord = new Coord(row, column);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj != null && (obj instanceof Cookie)) {
      Cookie cookie = (Cookie) obj;
      if (this.coord == cookie.coord && this.cookieType == cookie.cookieType) {
        return true;
      }
    }
    return false;
  }
}
