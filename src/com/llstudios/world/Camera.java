package com.llstudios.world;

public class Camera {
    public static int x;
    public static int y;

    public static int clamp(int coordAtual, int coordMin, int coordMax) {
        if (coordAtual < coordMin) {
            coordAtual = coordMin;
        }

        if (coordAtual > coordMax) {
            coordAtual = coordMax;
        }

        return coordAtual;
    }
}
