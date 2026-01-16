package com.consoledoom;

import java.io.IOException;

import com.consoledoom.core.Game;

public class Main {
    public static void main(String[] args) {
        System.out.println("Start the game...");

        Game game = new Game();
        try {
            game.start();
        } catch (Exception e) {
            System.out.println("IOException");
        }
    }
}
