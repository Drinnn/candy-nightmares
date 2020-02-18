package com.llstudios.main;

import javafx.scene.media.AudioClip;

import java.nio.file.Paths;

public class Sound {

    private AudioClip clip;

    private double volume;

    public static final Sound musicBg = new Sound(Paths.get("res/bg.mp3").toUri().toString(), 0.1);
    public static final Sound shotEffet = new Sound(Paths.get("res/shot.wav").toUri().toString(), 0.08);

    private Sound(String name) {
        try {
            clip = new AudioClip(name);
            this.volume = 100;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    private Sound(String name, double volume){
        try {
            clip = new AudioClip(name);
            this.volume = volume;
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void play() {
        try {
            new Thread(() -> {
                clip.setVolume(volume);
                clip.play();
            }).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public void loop() {
        try {
            new Thread(() -> {
                clip.setCycleCount(AudioClip.INDEFINITE);
                clip.setVolume(volume);
                clip.play();
            }).start();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
