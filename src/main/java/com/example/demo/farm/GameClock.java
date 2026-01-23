package com.example.demo.farm;

public class GameClock {
    private long tick = 0;

    public long now() {
        return tick;
    }

    public long nextTick() {
        tick++;
        return tick;
    }
}
