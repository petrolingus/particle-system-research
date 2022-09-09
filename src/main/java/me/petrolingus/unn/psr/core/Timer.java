package me.petrolingus.unn.psr.core;

import java.util.HashMap;
import java.util.Map;

public class Timer {

    private static Map<String, Long> map = new HashMap<>();

    public static void start(String name) {
        map.put(name, System.currentTimeMillis());
    }

    public static boolean isCome(String name, long ms) {
        long now = System.currentTimeMillis();
        if (now - map.get(name) > ms) {
            map.put(name, now);
            return true;
        }
        return false;
    }

    public static void measure(String name) {
        long now = System.currentTimeMillis();
        System.out.println(name + " takes " + (now - map.get(name)) + " ms");
        map.put(name, now);
    }

}
