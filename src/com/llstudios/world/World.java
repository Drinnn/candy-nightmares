package com.llstudios.world;

import com.llstudios.entities.*;
import com.llstudios.graphics.Spritesheet;
import com.llstudios.main.Game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class World {

    public static Tile[] tiles;
    public static int WIDTH, HEIGTH;
    public static final int TILE_SIZE = 16;

    public World(String path) {
        try {
            BufferedImage map = ImageIO.read(getClass().getResource(path));
            WIDTH = map.getWidth();
            HEIGTH = map.getHeight();
            int[] pixels = new int[WIDTH * HEIGTH];
            tiles = new Tile[WIDTH * HEIGTH];
            map.getRGB(0, 0, WIDTH, HEIGTH, pixels, 0, WIDTH); // Primeiro passamos a posição inicial X, depois Y, a largura, a altura, o array que iremos armazenar, offset e por fim, a extensão do
            for (int xx = 0; xx < map.getWidth(); xx++) {
                for (int yy = 0; yy < map.getHeight(); yy++) {
                    int currPixel = pixels[xx + (yy * WIDTH)];
                    tiles[xx + (yy * WIDTH)] = new FloorTile(xx * TILE_SIZE, yy * TILE_SIZE, Tile.TILE_FLOOR); // Valor é multiplicado por TILE_SIZE para atender ao tamanho da imagem no sprite
                    switch (currPixel) {
                        case 0xFFFFFFFF: // Parede
                            tiles[xx + (yy * WIDTH)] = new WallTile(xx * TILE_SIZE, yy * TILE_SIZE, Tile.TILE_WALL);
                            break;
                        case 0xFF99D3EB: // Player
                            Game.player.setX(xx * TILE_SIZE);
                            Game.player.setY(yy * TILE_SIZE);
                            break;
                        case 0xFF15FD4F: // Arma
                            Game.entities.add(new Weapon(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.WEAPON_EN));
                            break;
                        case 0xFFFDBB15: // Munição
                            Game.entities.add(new AmmoBox(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.AMMOBOX_EN));
                            break;
                        case 0xFFEA15FD: // Vida
                            Game.entities.add(new Lifepack(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.LIFEPACK_EN));
                            break;
                        case 0xFFF33F5D: // Inimigo
                            Enemy enemy = new Enemy(xx * TILE_SIZE, yy * TILE_SIZE, TILE_SIZE, TILE_SIZE, Entity.ENEMY_EN);
                            Game.entities.add(enemy);
                            Game.enemies.add(enemy);
                            break;
                        default: // CHÃO
                            tiles[xx + (yy * WIDTH)] = new FloorTile(xx * TILE_SIZE, yy * TILE_SIZE, Tile.TILE_FLOOR);
                            break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean isFree(int xNext, int yNext) {
        int x1 = xNext / TILE_SIZE;
        int y1 = yNext / TILE_SIZE;

        int x2 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
        int y2 = yNext / TILE_SIZE;

        int x3 = xNext / TILE_SIZE;
        int y3 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

        int x4 = (xNext + TILE_SIZE - 1) / TILE_SIZE;
        int y4 = (yNext + TILE_SIZE - 1) / TILE_SIZE;

        return !(tiles[x1 + (y1 * World.WIDTH)] instanceof WallTile ||
                tiles[x2 + (y2 * World.WIDTH)] instanceof WallTile ||
                tiles[x3 + (y3 * World.WIDTH)] instanceof WallTile ||
                tiles[x4 + (y4 * World.WIDTH)] instanceof WallTile);
    }

    public static void loadLevel(String level) {
        Game.entities = new ArrayList<>();
        Game.enemies = new ArrayList<>();
        Game.bullets = new ArrayList<>();
        Game.spritesheet = new Spritesheet("/spritesheet.png");
        Game.player = new Player(0, 0, 16, 16, Game.spritesheet.getSprite(32, 0, 16, 16));
        Game.entities.add(Game.player);
        Game.world = new World("/" + level);
    }


    public void render(Graphics g) {
        int xStart = Camera.x >> 4; // Posição inicial da câmera
        int yStart = Camera.y >> 4; // Posição inicial da câmera

        int xFinal = xStart + (Game.WIDTH >> 4); // Quantos tiles cabem na tela
        int yFinal = yStart + (Game.HEIGHT >> 4); // Quantos tiles cabem na tela

        for (int xx = xStart; xx <= xFinal; xx++) {
            for (int yy = yStart; yy <= yFinal; yy++) {
                if (xx < 0 || yy < 0 || xx >= World.WIDTH || yy >= World.HEIGTH)
                    continue;
                Tile tile = tiles[xx + (yy * WIDTH)];
                tile.render(g);
            }
        }
    }

}
