package com.llstudios.main;

import com.llstudios.world.World;

import java.io.*;

public class Saver {

    public static final String SAVE_FILE = "save.txt";

    public void saveGame(String[] val1, int[] val2, int encode){
        BufferedWriter writer = null;

        try {
            writer = new BufferedWriter(new FileWriter(SAVE_FILE));
        } catch (IOException e){
            e.printStackTrace();
        }

        for(int i = 0; i < val1.length; i++){
            String curr = val1[i];
            curr += ":";
            char[] value = Integer.toString(val2[i]).toCharArray();
            for(int j = 0; j < value.length; j++){
                value[j]+= encode;
                curr += value[j];
            }
            try {
                writer.write(curr);
                if(i < val1.length - 1)
                    writer.newLine();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        try {
            writer.flush();
            writer.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static String loadGame(int encode){
        String line = "";
        File file = new File(SAVE_FILE);
        if(file.exists()){
            try {
                String singleLine = null;
                BufferedReader reader = new BufferedReader(new FileReader(SAVE_FILE));
                try {
                    while((singleLine = reader.readLine()) != null){
                        String[] transition = singleLine.split(":");
                        char[] val = transition[1].toCharArray();
                        transition[1] = "";
                        for(int i = 0; i < val.length; i++){
                            val[i] -= encode;
                            transition[1] += val[i];
                        }
                        line += transition[0];
                        line += ":";
                        line += transition[1];
                        line += "/";
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            } catch(FileNotFoundException e){
                e.printStackTrace();
            }
        }

        return line;
    }

    public static void applySave(String str){
        String[] split = str.split("/");
        for(int i = 0; i < split.length; i++){
            String[] split2 = split[i].split(":");
            switch(split2[0]){
                case "level":
                    World.loadLevel("Level" + split2[1] + ".png");
                    Game.gameState = Game.GAME_DEFAULT;
                    break;
            }
        }
    }
}
