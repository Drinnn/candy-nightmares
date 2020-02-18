package com.llstudios.entities;

import com.llstudios.main.Game;
import com.llstudios.world.Camera;
import com.llstudios.world.Node;
import com.llstudios.world.Vector2i;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Entity {
    public final static BufferedImage LIFEPACK_EN = Game.spritesheet.getSprite(96, 0, 16, 16);
    public final static BufferedImage WEAPON_EN = Game.spritesheet.getSprite(112, 0, 16, 16);
    public final static BufferedImage WEAPON_EN_INVERTED = Game.spritesheet.getSprite(128, 0, 16, 16);
    public final static BufferedImage AMMOBOX_EN = Game.spritesheet.getSprite(96, 16, 16, 16);
    public final static BufferedImage ENEMY_EN = Game.spritesheet.getSprite(32, 32, 16, 16);
    public final static BufferedImage ENEMY_FEEDBACK = Game.spritesheet.getSprite(16, 32, 16, 16);


    protected double x;
    protected double y;
    protected int width;
    protected int height;

    protected BufferedImage sprite;

    private int maskX, maskY, mWidth, mHeight;

    protected List<Node> path;

    public Entity(double x, double y, int width, int height, BufferedImage sprite) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.sprite = sprite;

        this.maskX = 0;
        this.maskY = 0;
        this.mWidth = width;
        this.mHeight = height;
    }

    public void setMask(int maskX, int maskY, int mWidth, int mHeight) {
        this.maskX = maskX;
        this.maskY = maskY;
        this.mWidth = mWidth;
        this.mHeight = mHeight;
    }

    public int getX() {
        return (int) this.x;
    }

    public int getY() {
        return (int) this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public static boolean isColliding(Entity en1, Entity en2) {
        Rectangle en1Mask = new Rectangle(en1.getX() + en1.maskX, en1.getY() + en1.maskY, en1.mWidth, en1.mHeight);
        Rectangle en2Mask = new Rectangle(en2.getX() + en2.maskX, en2.getY() + en2.maskY, en2.mWidth, en2.mHeight);

        return en1Mask.intersects(en2Mask);
    }

    public void followPath(List<Node> path) {
        if (path != null) {
            if (path.size() > 0) {
                Vector2i target = path.get(path.size() - 1).tile;
                /*xPrev = x;
                yPrev = y;*/
                if (x < target.x * 16) {
                    x++;
                } else if (x > target.x * 16) {
                    x--;
                }

                if (y < target.y * 16) {
                    y++;
                } else if (y > target.y * 16) {
                    y--;
                }

                if(x == target.x * 16 || y == target.y * 16){
                    path.remove(path.size() - 1);
                }
            }
        }
    }

    public void tick() {

    }

    public double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    public void render(Graphics g) {
        g.drawImage(sprite, getX() - Camera.x, getY() - Camera.y, null);

        // Debug da mask de colisão
        /*g.setColor(Color.RED);
        g.fillRect(this.getX() - Camera.x, this.getY() - Camera.y, mWidth, mHeight);*/
    }
}
