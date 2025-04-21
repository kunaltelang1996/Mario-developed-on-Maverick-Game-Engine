package Util;

public class Time {
    private static float timeStarted = System.nanoTime();

    public static float getTime(){
        return ((System.nanoTime() - timeStarted) * 1E-9f);
    }
}
