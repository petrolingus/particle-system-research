package me.petrolingus.unn.test;

public class Particle {

    double x;

    double y;

    double vx;

    double vy;

    double ax;

    double ay;

    public Particle(double x, double y) {
        this(x, y, 0, 0, 0, 0);
    }

    public Particle(double x, double y, double vx, double vy, double ax, double ay) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = ax;
        this.ay = ay;
    }
}
