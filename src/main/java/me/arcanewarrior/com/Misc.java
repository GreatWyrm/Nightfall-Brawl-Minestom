package me.arcanewarrior.com;

public class Misc {

    public static int randomInt(int min, int max) {
        return min + (int) (Math.random() * (max + 1 - min));
    }
}
