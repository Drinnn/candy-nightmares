package com.llstudios.entities;

import com.llstudios.main.Game;
import com.llstudios.world.Camera;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Bullet extends Entity {

    private double dx;
    private double dy;
    private double speed = 4;

    private int duration = 50, currDuration = 0;


    public Bullet(double x, double y, int width, int height, BufferedImage sprite, double dx, double dy) {
        super(x, y, width, height, sprite);
        this.dx = dx;
        this.dy = dy;
    }

    @Override
    public void tick() {
        x += dx * speed;
        y += dy * speed;
        currDuration++;
        if (currDuration == duration) {
            Game.bullets.remove(this);
        }
    }

    @Override
    public void render(Graphics g) {
        g.setColor(new Color(217, 192, 241));
        g.fillOval(this.getX() - Camera.x, this.getY() - Camera.y, 3, 3);
    }
}
