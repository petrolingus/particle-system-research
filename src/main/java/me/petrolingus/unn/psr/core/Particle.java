package me.petrolingus.unn.psr.core;

public class Particle {

    public double x;
    public double y;
    public double vx;
    public double vy;
    public double ax;
    public double ay;

    public Particle(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = 0;
        this.ay = 0;
    }

    public void move() {
        x += vx;
        y += vy;
    }

    public void recalculateVelocity() {
        x = (x > 1) ? (x - 1) : x;
        x = (x < 0) ? (1 + x) : x;
        y = (y > 1) ? (y - 1) : y;
        y = (y < 0) ? (1 + y) : y;
    }

    @Override
    public String toString() {
        return "Particle{" +
                "x=" + x +
                ", y=" + y +
                ", vx=" + vx +
                ", vy=" + vy +
                ", ax=" + ax +
                ", ay=" + ay +
                '}';
    }
}
