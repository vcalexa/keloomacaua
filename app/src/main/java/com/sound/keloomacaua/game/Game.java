package com.sound.keloomacaua.game;

public class Game {
    private boolean player1Joined;
    private boolean player2Joined;

    public boolean isPlayer1Joined() {
        return player1Joined;
    }

    public void setPlayer1Joined(boolean player1Joined) {
        this.player1Joined = player1Joined;
    }

    public boolean isPlayer2Joined() {
        return player2Joined;
    }

    public void setPlayer2Joined(boolean player2Joined) {
        this.player2Joined = player2Joined;
    }
}
