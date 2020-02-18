package com.llstudios.entities;

import com.llstudios.graphics.Spritesheet;
import com.llstudios.main.Game;
import com.llstudios.world.AStar;
import com.llstudios.world.Camera;
import com.llstudios.world.Vector2i;
import com.llstudios.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends Entity {
    private double speed = 0.6;

    private Spritesheet globalSpriteSheet = Game.spritesheet;

    private int frames = 0, maxFrames = 8, animIndex = 0, animMaxIndex = 3;
    private boolean moved = false;
    private BufferedImage[] sprites;

    public boolean isDamaged;
    private int damageFrames;

    private int life = 10;

    public Enemy(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);

        sprites = new BufferedImage[4];

        for (int i = 0; i <= animMaxIndex; i++) {
            sprites[i] = globalSpriteSheet.getSprite(32 + (i * 16), 32, 16, 16);
        }
    }

    @Override
    public void tick() {
        moved = false;
        /*if(this.calculateDistance(this.getX(), this.getY(), Game.player.getX(), Game.player.getY()) < 80){
            if (!isCollidingWithPlayer()) {
                if ((int) x < Game.player.getX() && World.isFree((int) (x + speed), this.getY())
                        && !isCollidingBetween((int) (x + speed), this.getY())) {
                    moved = true;
                    x += speed;
                } else if ((int) x > Game.player.getX() && World.isFree((int) (x - speed), this.getY())
                        && !isCollidingBetween((int) (x - speed), this.getY())) {
                    moved = true;
                    x -= speed;
                }

                if ((int) y < Game.player.getY() && World.isFree(this.getX(), (int) (y + speed))
                        && !isCollidingBetween(this.getX(), (int) (y + speed))) {
                    moved = true;
                    y += speed;
                } else if ((int) y > Game.player.getY() && World.isFree(this.getX(), (int) (y - speed))
                        && !isCollidingBetween(this.getX(), (int) (y - speed))) {
                    moved = true;
                    y -= speed;
                }
            } else {
                if (Game.rand.nextInt(100) < 60) {
                    Game.player.life -= Game.rand.nextInt(3);
                    Game.player.isDamaged = true;
                }
            }
        }*/

        if (path == null || path.size() == 0) {
            Vector2i start = new Vector2i((int) (x / 16), (int) (y / 16));
            Vector2i end = new Vector2i((int) (Game.player.x / 16), (int) (Game.player.y));
            path = AStar.findPath(Game.world, start, end);
        }

        followPath(path);

        if (moved) {
            frames++;
            if (frames == maxFrames) {
                frames = 0;
                animIndex++;
                if (animIndex > animMaxIndex)
                    animIndex = 0;
            }
        } else {
            frames = 0;
            animIndex = 0;
        }

        checkForBulletCollision();

        if (isDamaged) {
            damageFrames++;
            if (this.damageFrames == 8) {
                this.damageFrames = 0;
                isDamaged = false;
            }
        }

        if (life <= 0) {
            destroy();
        }
    }

    private void destroy() {
        Game.entities.remove(this);
        Game.enemies.remove(this);
    }

    private void checkForBulletCollision() {
        for (int i = 0; i < Game.bullets.size(); i++) {
            Entity e = Game.bullets.get(i);
            if (Entity.isColliding(this, e)) {
                isDamaged = true;
                life -= 5;
                Game.bullets.remove(e);
            }
        }
    }

    public boolean isCollidingWithPlayer() {
        Rectangle current = new Rectangle(getX(), getY(), World.TILE_SIZE, World.TILE_SIZE);
        Rectangle player = new Rectangle(Game.player.getX(), Game.player.getY(), World.TILE_SIZE, World.TILE_SIZE);

        return current.intersects(player);
    }

    public boolean isCollidingBetween(int xNext, int yNext) {
        Rectangle enemyCurrent = new Rectangle(xNext, yNext, World.TILE_SIZE, World.TILE_SIZE);
        for (int i = 0; i < Game.enemies.size(); i++) {
            Enemy e = Game.enemies.get(i);
            if (e != this) {
                Rectangle targetEnemy = new Rectangle(e.getX(), e.getY(), World.TILE_SIZE, World.TILE_SIZE);
                if (enemyCurrent.intersects(targetEnemy)) {
                    return true;
                }
            }
        }

        return false;
    }

    @Override
    public void render(Graphics g) {
        if (!isDamaged) {
            g.drawImage(sprites[animIndex], getX() - Camera.x, getY() - Camera.y, null);
        } else {
            g.drawImage(Entity.ENEMY_FEEDBACK, getX() - Camera.x, getY() - Camera.y, null);
        }
    }
}
