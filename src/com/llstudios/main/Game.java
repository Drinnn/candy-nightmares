package com.llstudios.main;

import com.llstudios.entities.Bullet;
import com.llstudios.entities.Enemy;
import com.llstudios.entities.Entity;
import com.llstudios.entities.Player;
import com.llstudios.graphics.Spritesheet;
import com.llstudios.graphics.UI;
import com.llstudios.world.World;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class Game extends Canvas implements Runnable, KeyListener {
    public static JFrame frame;
    private Thread thread;
    private boolean isRunning;
    public static final int WIDTH = 240;
    public static final int HEIGHT = 160;
    public static final int SCALE = 3;

    public static int CURR_LEVEL = 1, MAX_LEVEL = 2;

    private BufferedImage layer;

    public static ArrayList<Entity> entities;
    public static ArrayList<Enemy> enemies;
    public static ArrayList<Bullet> bullets;
    public static Spritesheet spritesheet;

    public static World world;

    public static Player player;

    public static Random rand;

    public UI ui;

    public InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream("Candyshop.ttf");
    public static Font newFont;

    public static Menu menu;

    public static Saver saver;

    public static String gameState;

    public static String GAME_DEFAULT = "DEFAULT";
    public static String GAME_MENU = "MENU";
    public static String GAME_OVER = "OVER";

    private boolean showMessageGameOver = true;
    private int framesGameOver;
    private boolean restartGame;

    public boolean saveGame = false;

    public Game() {
        Sound.musicBg.loop();
        addKeyListener(this);
        setPreferredSize(new Dimension(WIDTH * SCALE, HEIGHT * SCALE));
        setFocusable(true);
        gameState = GAME_MENU;
        initFrame();
        // Inicializando objetos
        saver = new Saver();
        rand = new Random();
        ui = new UI();
        layer = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_ARGB);
        entities = new ArrayList<>();
        enemies = new ArrayList<>();
        bullets = new ArrayList<>();
        spritesheet = new Spritesheet("/spritesheet.png");
        player = new Player(0, 0, 16, 16, spritesheet.getSprite(32, 0, 16, 16));
        entities.add(player);
        world = new World("/level1.png");

        try {
            newFont = Font.createFont(Font.TRUETYPE_FONT, stream).deriveFont(36f);
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
        }

        menu = new Menu();
    }

    public void initFrame() {
        frame = new JFrame("Candy Nightmares");
        frame.add(this);
        frame.setResizable(false);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public synchronized void start() {
        thread = new Thread(this);
        isRunning = true;
        thread.start();
    }

    public synchronized void stop() {
        isRunning = false;
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void tick() {
        if (gameState.equals(GAME_DEFAULT)) {
            if (saveGame) {
                saveGame = false;
                String[] opt1 = {"level"};
                int[] opt2 = {CURR_LEVEL};
                saver.saveGame(opt1, opt2, 10);
            }
            restartGame = false;
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);
                e.tick();
            }

            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).tick();
            }

            if (enemies.size() == 0) {
                CURR_LEVEL++;
                if (CURR_LEVEL > MAX_LEVEL)
                    CURR_LEVEL = 1;
                String newWorld = "Level" + CURR_LEVEL + ".png";
                World.loadLevel(newWorld);
            }
        } else if (gameState.equals(GAME_OVER)) {
            framesGameOver++;
            if (framesGameOver == 50) {
                framesGameOver = 0;
                if (showMessageGameOver) {
                    showMessageGameOver = false;
                } else {
                    showMessageGameOver = true;
                }
            }
        } else if (gameState.equals(GAME_MENU)) {
            menu.tick();
        }

        if (restartGame) {
            restartGame = false;
            gameState = GAME_DEFAULT;
            World.loadLevel("Level1.png");
            CURR_LEVEL = 1;
        }
    }

    private void render() {
        BufferStrategy bs = getBufferStrategy();
        if (bs == null) {
            createBufferStrategy(3);
        } else {
            Graphics g = layer.createGraphics(); // Contexto gráfico da nossa layer (onde usamos para desenhar as coisas)

            g.setColor(new Color(0, 0, 0));
            g.fillRect(0, 0, WIDTH, HEIGHT);

            world.render(g);
            for (int i = 0; i < entities.size(); i++) {
                Entity e = entities.get(i);
                e.render(g);
            }

            for (int i = 0; i < bullets.size(); i++) {
                bullets.get(i).render(g);
            }

            g.dispose();
            g = bs.getDrawGraphics(); // Contexto gráfico dos nossos buffers (onde usamos para renderizar)
            g.drawImage(layer, 0, 0, WIDTH * SCALE, HEIGHT * SCALE, null);

            ui.render(g);

            if (gameState.equals(GAME_OVER)) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 0, 0, 100));
                g2.fillRect(0, 0, WIDTH * SCALE, HEIGHT * SCALE);
                g.setFont(new Font("arial", Font.BOLD, 28));
                g.setColor(Color.white);
                g.drawString("Game Over", 310, 220);
                if (showMessageGameOver) {
                    g.setFont(new Font("arial", Font.BOLD, 20));
                    g.drawString("Press SPACE to continue.", 260, 250);
                }
            } else if (gameState.equals(GAME_MENU)) {
                menu.render(g);
            }

            bs.show();
        }
    }


    @Override
    public void run() {
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;

        int frames = 0;
        double timer = System.currentTimeMillis();

        requestFocus();

        while (isRunning) {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            if (delta >= 1) {
                tick();
                render();
                frames++;
                delta--;
            }

            if (System.currentTimeMillis() - timer >= 1000) {
                System.out.println("FPS: " + frames);
                frames = 0;
                timer += 1000;
            }
        }
        stop();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            player.right = true;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            player.left = true;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            if (gameState.equals(GAME_MENU)) {
                menu.up = true;
            }
            player.up = true;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            player.down = true;
            if (gameState.equals(GAME_MENU)) {
                menu.down = true;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.isShooting = true;
            if (gameState.equals(GAME_OVER))
                restartGame = true;
            if (gameState.equals(GAME_MENU)) {
                menu.enter = true;
            }
        }

        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (gameState.equals(GAME_DEFAULT))
                menu.isPaused = true;
            gameState = GAME_MENU;
        }

        if (e.getKeyCode() == KeyEvent.VK_F) {
            if (gameState.equals(GAME_DEFAULT))
                saveGame = true;
        }

    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT || e.getKeyCode() == KeyEvent.VK_D) {
            player.right = false;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT || e.getKeyCode() == KeyEvent.VK_A) {
            player.left = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_UP || e.getKeyCode() == KeyEvent.VK_W) {
            player.up = false;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN || e.getKeyCode() == KeyEvent.VK_S) {
            player.down = false;
        }

        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            player.isShooting = false;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static void main(String[] args) {
        Game game = new Game();
        game.start();
    }
}
