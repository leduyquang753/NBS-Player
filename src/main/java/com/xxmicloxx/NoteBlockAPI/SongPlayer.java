package com.xxmicloxx.NoteBlockAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class SongPlayer {

    protected Song song;
    protected boolean playing = false;
    protected short tick = -1;
    protected ArrayList<String> playerList = new ArrayList<String>();
    protected boolean loop;
    protected boolean autoDestroy = false;
    protected boolean destroyed = false;
    protected Thread playerThread;
    protected byte fadeTarget = 100;
    protected byte volume = 100;
    protected byte fadeStart = volume;
    protected int fadeDuration = 60;
    protected int fadeDone = 0;
    protected FadeType fadeType = FadeType.FADE_LINEAR;
    protected EntityPlayerSP player = Minecraft.getMinecraft().player;

    public SongPlayer(Song song) {
        this.song = song;
//        createThread();
    }

    public FadeType getFadeType() {
        return fadeType;
    }

    public void setFadeType(FadeType fadeType) {
        this.fadeType = fadeType;
    }

    public byte getFadeTarget() {
        return fadeTarget;
    }

    public void setFadeTarget(byte fadeTarget) {
        this.fadeTarget = fadeTarget;
    }

    public byte getFadeStart() {
        return fadeStart;
    }

    public void setFadeStart(byte fadeStart) {
        this.fadeStart = fadeStart;
    }

    public int getFadeDuration() {
        return fadeDuration;
    }

    public void setFadeDuration(int fadeDuration) {
        this.fadeDuration = fadeDuration;
    }

    public int getFadeDone() {
        return fadeDone;
    }

    public void setFadeDone(int fadeDone) {
        this.fadeDone = fadeDone;
    }

    protected void calculateFade() {
        if (fadeDone == fadeDuration) {
            return; // no fade today
        }
        double targetVolume = Interpolator.interpLinear(new double[]{0, fadeStart, fadeDuration, fadeTarget}, fadeDone);
        setVolume((byte) targetVolume);
        fadeDone++;
    }

    public void createThread() {
    	playing = true;
        playerThread = new Thread(new Runnable() {
            @Override
            public void run() {
//            	Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Play."));
                while (!destroyed) {
                    long startTime = System.currentTimeMillis();
                    synchronized (SongPlayer.this) {
                        if (playing) {
//                        	Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Play."));
                            tick++;
                            
                            if (tick > song.getLength()) {
                                if(loop){
                                    tick = 0;
                                    continue;
                                }
                                playing = false;
                                tick = -1;
                                MinecraftForge.EVENT_BUS.post(new SongEndEvent());
                                if (autoDestroy) {
                                    destroy();
                                    return;
                                }
                            }
                            playTick(tick);
                        }
                    }
                    long duration = System.currentTimeMillis() - startTime;
                    float delayMillis = song.getDelay() * 50;
                    if (duration < delayMillis) {
                        try {
                            Thread.sleep((long) (delayMillis - duration));
                        } catch (InterruptedException e) {
                            // do nothing
                        }
                    }
                }
            }
        });
        playerThread.setPriority(Thread.MAX_PRIORITY);
        playerThread.start();
    }

    public List<String> getPlayerList() {
        return Collections.unmodifiableList(playerList);
    }
    public abstract void playTick(int tick);

    public void destroy() {
        synchronized (this) {
        	SongDestroyingEvent event = new SongDestroyingEvent();
            MinecraftForge.EVENT_BUS.post(event);
            if (event.isCanceled()) {
                return;
            }
            destroyed = true;
            playing = false;
            setTick((short) -1);
        }
    }

    public boolean isPlaying() {
        return playing;
    }
    public short getTick() {
        return tick;
    }

    public void setTick(short tick) {
        this.tick = tick;
    }

    public void stop() {
    	MinecraftForge.EVENT_BUS.post(new SongEndEvent());
        destroy();
    }

    public byte getVolume() {
        return volume;
    }

    public void setVolume(byte volume) {
        this.volume = volume;
    }

    public Song getSong() {
        return song;
    }
}
