package me.petrolingus.unn.psr.core.model;

public class Particle {

    private double oldX;
    private double oldY;

    private double path;

    public double x;
    public double y;
    public double vx;
    public double vy;
    public double ax;
    public double ay;

    public double mv2;

    public Particle() {
        this(0, 0, 0, 0);
    }

    public Particle(double x, double y, double vx, double vy) {
        this.x = x;
        this.y = y;
        this.vx = vx;
        this.vy = vy;
        this.ax = 0;
        this.ay = 0;
        this.mv2 = 0;
    }

    public void periodic(double width, double height) {
        x = (x < 0) ? (x + width) : x;
        x = (x > width) ? (x - width) : x;
        y = (y < 0) ? (y + height) : y;
        y = (y > height) ? (y - height) : y;
    }

    public void saveCurrentPosition() {
        oldX = x;
        oldY = y;
        path = 0;
    }

    public void move(double dx, double dy) {
        x += dx;
        y += dy;
        path += Math.sqrt(dx * dx + dy * dy);
    }

    public double getDistanceDifference(double w, double h) {
//        double dx = oldX - x;
//        double dy = oldY - y;
//        dx = (Math.abs(dx) > 0.5 * w) ? dx - w * Math.signum(dx) : dx;
//        dy = (Math.abs(dy) > 0.5 * h) ? dy - h * Math.signum(dy) : dy;
        return path;
    }
}
