package me.petrolingus.unn.psr.core;

public class Test {

    public static void main(String[] args) {

        Configuration.recalculate();
        Algorithm algorithm = new Algorithm();

        for (int i = 0; i < 1; i++) {
            System.out.println(i + ":");
            algorithm.particles.forEach(System.out::println);
            System.out.println("===================================");
        }

    }
}
