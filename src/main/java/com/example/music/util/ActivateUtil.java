package com.example.music.util;

import java.util.Random;

public class ActivateUtil {
    public static String generate(){
        Random random = new Random();
        int code = random.nextInt(900000) + 100000;
        return String.valueOf(code);
    }
}
