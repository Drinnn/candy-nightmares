package com.llstudios.entities;

import com.llstudios.graphics.Spritesheet;
import com.llstudios.main.Game;
import com.llstudios.main.Sound;
import com.llstudios.world.Camera;
import com.llstudios.world.World;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Player extends Entity {

    public boolean right, up, left, down;
    public final int RIGHT_DIR = 0, LEFT_DIR = 1;
    public int dir = RIGHT_DIR;
    public double speed = 0.7;

    private Spritesheet globalSpritesheet = Game.spritesheet;

    private int frames = 0, maxFrames = 8, animIndex = 0, animMaxIndex = 3;
    private boolean moved = false;
    private BufferedImage[] rightPlayer;
    private BufferedImage[] leftPlayer;

    private BufferedImage damagedSprite;

    public double life = 100, maxLife = 100;

    private boolean hasWeapon;

    public int ammo;

    public boolean isDamaged;
    private int damageFrames;

    public boolean isShooting;

    public int mouseX, mouseY;

    public Player(double x, double y, int width, int height, BufferedImage sprite) {
        super(x, y, width, height, sprite);

        rightPlayer = new BufferedImage[4];
        leftPlayer = new BufferedImage[4];

        for (int i = 0; i < rightPlayer.length; i++) {
            rightPlayer[i] = globalSpritesheet.getSprite(32 + (i * 16), 0, 16, 16);
        }

        for (int i = 0; i < leftPlayer.length; i++) {
            leftPlayer[i] = globalSpritesheet.getSprite(32 + (i * 16), 16, 16, 16);
        }

        damagedSprite = globalSpritesheet.getSprite(16, 16, 16, 16);
    }

    @Override
    public void tick() {
        moved = false;
        if (right && World.isFree((int) (x + speed), this.getY())) {
            moved = true;
            dir = RIGHT_DIR;
            x += speed;
        } else if (left && World.isFree((int) (x - speed), this.getY())) {
            moved = true;
            dir = LEFT_DIR;
            x -= speed;
        }

        if (up && World.isFree(this.getX(), (int) (y - speed))) {
            moved = true;
            y -= speed;
        } else if (down && World.isFree(this.getX(), (int) (y + speed))) {
            moved = true;
            y += speed;
        }

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

        checkForItens();

        if (isDamaged) {
            damageFrames++;
            if (this.damageFrames == 8) {
                this.damageFrames = 0;
                isDamaged = false;
            }
        }

        if (hasWeapon && isShooting && ammo > 0) {
            Sound.shotEffet.play();
            ammo--;
            isShooting = false;
            int dx;
            int ox = 3;
            int oy = 9;
            if (dir == RIGHT_DIR) {
                dx = 1;
            } else {
                dx = -1;
            }

            Bullet bullet = new Bullet(this.getX() + ox, this.getY() + oy, 3, 3, null, dx, 0);
            Game.bullets.add(bullet);
        }

        if (this.life <= 0) {
            this.life = 0;
            Game.gameState = Game.GAME_OVER;
        }

        // O algoritmo utilizado usa de base a distância do player do centro do mapa, o resultado disso é usado para calcular como a câmera deve estar
        Camera.x = Camera.clamp(this.getX() - (Game.WIDTH / 2), 0, World.WIDTH * 16 - Game.WIDTH);
        Camera.y = Camera.clamp(this.getY() - (Game.HEIGHT / 2), 0, World.HEIGTH * 16 - Game.HEIGHT);
    }

    public void checkForItens() {
        for (int i = 0; i < Game.entities.size(); i++) {
            Entity e = Game.entities.get(i);
            if (e instanceof Lifepack) {
                if (Entity.isColliding(this, e)) {
                    life += 10;
                    if (life >= 100)
                        life = 100;
                    Game.entities.remove(e);
                }
            } else if (e instanceof AmmoBox) {
                if (Entity.isColliding(this, e)) {
                    ammo += 8;
                    Game.entities.remove(e);
                }
            } else if (e instanceof Weapon) {
                if (Entity.isColliding(this, e)) {
                    hasWeapon = true;
                    Game.entities.remove(e);
                }
            }
        }
    }

    @Override
    public void render(Graphics g) {
        if (!isDamaged) {
            if (dir == RIGHT_DIR) {
                g.drawImage(rightPlayer[animIndex], getX() - Camera.x, getY() - Camera.y, null);
                if (hasWeapon) {
                    g.drawImage(Entity.WEAPON_EN, this.getX() - Camera.x, this.getY() - Camera.y + 3, null);
                }
            } else if (dir == LEFT_DIR) {
                g.drawImage(leftPlayer[animIndex], getX() - Camera.x, getY() - Camera.y, null);
                if (hasWeapon) {
                    g.drawImage(Entity.WEAPON_EN_INVERTED, this.getX() - Camera.x, this.getY() - Camera.y + 3, null);
                }
            }
        } else {
            g.drawImage(damagedSprite, getX() - Camera.x, getY() - Camera.y, null);
        }
    }
}
