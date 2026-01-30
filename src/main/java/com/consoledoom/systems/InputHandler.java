package com.consoledoom.systems;

import com.consoledoom.entities.Player;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class InputHandler {

    public enum Action {
        NONE, MOVE, SHOOT, QUIT
    }

    public static class InputResult {
        public final Action action;
        public final int dx, dy;

        private InputResult(Action action, int dx, int dy) {
            this.action = action;
            this.dx = dx;
            this.dy = dy;
        }

        public static InputResult move(int dx, int dy) {
            return new InputResult(Action.MOVE, dx, dy);
        }

        public static InputResult shoot() {
            return new InputResult(Action.SHOOT, 0, 0);
        }

        public static InputResult quit() {
            return new InputResult(Action.QUIT, 0, 0);
        }

        public static InputResult none() {
            return new InputResult(Action.NONE, 0, 0);
        }
    }

    public static InputResult handleKey(KeyStroke key, Player player) {
        if (key == null)
            return InputResult.none();

        // quit
        if (key.getKeyType() == KeyType.Escape
                || (key.getKeyType() == KeyType.Character && Character.toLowerCase(key.getCharacter()) == 'q')) {
            return InputResult.quit();
        }

        if (key.getKeyType() == KeyType.ArrowUp) {
            player.setFacing(new com.consoledoom.utils.Vec2(0, -1));
            return InputResult.shoot();
        }
        if (key.getKeyType() == KeyType.ArrowDown) {
            player.setFacing(new com.consoledoom.utils.Vec2(0, 1));
            return InputResult.shoot();
        }
        if (key.getKeyType() == KeyType.ArrowLeft) {
            player.setFacing(new com.consoledoom.utils.Vec2(-1, 0));
            return InputResult.shoot();
        }
        if (key.getKeyType() == KeyType.ArrowRight) {
            player.setFacing(new com.consoledoom.utils.Vec2(1, 0));
            return InputResult.shoot();
        }

        if (key.getKeyType() != KeyType.Character)
            return InputResult.none();

        char c = Character.toLowerCase(key.getCharacter());

        switch (c) {
            case 'w' -> {
                player.setFacing(new com.consoledoom.utils.Vec2(0, -1));
                return InputResult.move(0, -1);
            }
            case 's' -> {
                player.setFacing(new com.consoledoom.utils.Vec2(0, 1));
                return InputResult.move(0, 1);
            }
            case 'a' -> {
                player.setFacing(new com.consoledoom.utils.Vec2(-1, 0));
                return InputResult.move(-1, 0);
            }
            case 'd' -> {
                player.setFacing(new com.consoledoom.utils.Vec2(1, 0));
                return InputResult.move(1, 0);
            }

            default -> {
                return InputResult.none();
            }
        }
    }
}
