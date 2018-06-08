package de.dhbw.mosbach.lehre;

public class WayPoint {

    public WayPoint(Integer _x, Integer _y) {
        this._x = _x;
        this._y = _y;
    }

    private Integer _x;
    private Integer _y;

    public Integer getX() {
        return _x;
    }

    public Integer getY() {
        return _y;
    }

    @Override
    public String toString() {
        return "WayPoint{" +
                "_x=" + _x +
                ", _y=" + _y +
                '}';
    }
}
