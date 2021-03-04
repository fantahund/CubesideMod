package de.fanta.cubeside.util;

import net.minecraft.client.sound.Sound;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;

public class SoundThread extends Thread {

    private final long seconds;
    private final SoundEvent sound;
    private final PlayerEntity player;
    private boolean running;
    private final boolean soundPlaying;
    private boolean force;

    public SoundThread(long seconds, SoundEvent sound, PlayerEntity player) {
        super();
        this.seconds = seconds;
        this.sound = sound;
        this.player = player;
        this.soundPlaying = true;
    }

    public static synchronized SoundThread of(long seconds, SoundEvent sound, PlayerEntity player){
        return new SoundThread(seconds, sound, player);
    }

    @Override
    public void run() {
        while (running) {
            if (soundPlaying) {
                player.playSound(sound, SoundCategory.PLAYERS, 100.0f, 1.0f);
                force = false;
            }
            try {
                if (!running) break;
                if (!force) {
                    Thread.sleep(seconds * 1000);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public synchronized void start() {
        this.running = true;
        super.start();
    }

    public synchronized void stopThread() {
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }
}
