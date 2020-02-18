package com.llstudios.main;

import java.awt.*;
import java.io.File;

public class Menu {
    private static final String NEW_GAME = "NEW_GAME";
    private static final String LOAD_GAME = "LOAD_GAME";
    private static final String CONTINUE = "CONTINUE";
    private static final String QUIT = "QUIT";

    public String[] options = {NEW_GAME, LOAD_GAME, QUIT};

    public int currOption = 0;
    public int maxOption = options.length - 1;

    public boolean up, down, enter;

    public boolean isPaused;

    public static boolean saveExists;

    public void tick() {
        File file = new File(Saver.SAVE_FILE);
        if(file.exists()){
            saveExists = true;
        } else {
            saveExists = false;
        }

        if (up) {
            up = false;
            currOption--;
            if (currOption < 0)
                currOption = maxOption;
        } else if (down) {
            down = false;
            currOption++;
            if (currOption > maxOption)
                currOption = 0;
        }

        if (enter) {
            enter = false;
            if (options[currOption].equals(NEW_GAME) || options[currOption].equals(CONTINUE)) {
                Game.gameState = Game.GAME_DEFAULT;
                isPaused = false;
                file = new File(Saver.SAVE_FILE);
                file.delete();
                saveExists = false;
            } else if (options[currOption].equals(LOAD_GAME) && saveExists) {
                String saver = Saver.loadGame(10);
                Saver.applySave(saver);
            } else if (options[currOption].equals(QUIT)) {
                System.exit(1);
            }
        }
    }

    public void render(Graphics g) {
        g.setColor(Color.black);
        g.fillRect(0, 0, Game.WIDTH * Game.SCALE, Game.HEIGHT * Game.SCALE);
        g.setColor(Color.white);
        g.setFont(Game.newFont);
        g.drawString("Candy Nightmares", 220, 40);

        if (!isPaused) {
            g.setFont(new Font("arial", Font.BOLD, 24));
            g.drawString("New game", 310, 330);
        } else {
            g.setFont(new Font("arial", Font.BOLD, 24));
            g.drawString("Continue", 315, 330);
        }


        g.setFont(new Font("arial", Font.BOLD, 24));
        g.drawString("Load game", 305, 370);

        g.setFont(new Font("arial", Font.BOLD, 24));
        g.drawString("Quit", 340, 410);

        if (options[currOption].equals(NEW_GAME)) {
            g.drawString(">", 280, 330);
        } else if (options[currOption].equals(LOAD_GAME)) {
            g.drawString(">", 275, 370);
        } else if (options[currOption].equals(QUIT)) {
            g.drawString(">", 310, 410);
        }
    }
}
