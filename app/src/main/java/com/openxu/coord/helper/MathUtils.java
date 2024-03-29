package com.openxu.coord.helper;

/**
 * Copy from Android design library
 * Created by chensuilun on 16/7/24.
 */
public class MathUtils {

    public static int constrain(int amount, int low, int high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

    public static float constrain(float amount, float low, float high) {
        return amount < low ? low : (amount > high ? high : amount);
    }

}

