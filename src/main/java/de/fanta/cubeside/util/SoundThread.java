package de.fanta.cubeside.util;

import net.minecraft.client.sound.Sound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class SoundThread extends Thread {

    private long seconds;
    private SoundEvent sound;
    private PlayerEntity player;
    private boolean running;
    private boolean soundPlaying;
    private boolean force;

    public SoundThread(long seconds, SoundEvent sound, PlayerEntity player) {
        super();
        this.seconds = seconds;
        this.sound = sound;
        this.player = player;
        this.soundPlaying = true;
    }

    @Override
    public void run() {
        while (running) {
            if (soundPlaying && (force || System.currentTimeMillis() % (seconds * 1000) < 500)) {
                player.playSound(sound, SoundCategory.PLAYERS, 100.0f, 1.0f);
                force = false;
            }
        }
    }

    @Override
    public synchronized void start() {
        this.running = true;
        super.start();
    }

    public synchronized void pauseSounds() {
        this.soundPlaying = false;
    }

    public synchronized void resumeSounds() {
        this.soundPlaying = true;
        this.force = true;
    }

    public synchronized void stopThread() {
        this.running = false;
    }
}
