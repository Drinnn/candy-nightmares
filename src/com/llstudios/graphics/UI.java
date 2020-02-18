package com.llstudios.graphics;

import com.llstudios.entities.Player;
import com.llstudios.main.Game;

import java.awt.*;

public class UI {

    public void render(Graphics g) {
        g.setColor(new Color(211, 97, 97));
        g.fillRect(8, 10, 210, 24);
        g.setColor(new Color(97, 211, 110));
        g.fillRect(8, 10, (int) (Game.player.life / Game.player.maxLife * 210), 24);
        g.setColor(Color.WHITE);
        g.setFont(new Font("arial", Font.BOLD, 20));
        g.drawString((int) Game.player.life + "/" + (int) Game.player.maxLife, 80, 30);

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("arial", Font.BOLD, 20));
        g.drawString("Cupcakes: " + Game.player.ammo, 575, 30);


    }
}
