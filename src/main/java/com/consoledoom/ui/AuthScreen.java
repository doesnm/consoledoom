// src/main/java/com/consoledoom/ui/AuthScreen.java
package com.consoledoom.ui;

import com.consoledoom.service.AuthService;
import com.consoledoom.service.AuthService.AuthResult;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

/**
 * Authentication screen for login/registration.
 */
public class AuthScreen {
    private final AuthService authService;
    private final StringBuilder nicknameBuffer = new StringBuilder();
    private final StringBuilder passwordBuffer = new StringBuilder();

    private enum AuthMode {
        LOGIN, REGISTER
    }

    private enum InputField {
        NICKNAME, PASSWORD
    }

    private AuthMode mode = AuthMode.LOGIN;
    private InputField activeField = InputField.NICKNAME;
    private String statusMessage = "";
    private boolean isError = false;
    private boolean authComplete = false;

    public AuthScreen() {
        this.authService = new AuthService();
    }

    public void handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.Tab) {
            // Switch between fields
            activeField = (activeField == InputField.NICKNAME)
                    ? InputField.PASSWORD
                    : InputField.NICKNAME;
        } else if (key.getKeyType() == KeyType.F1) {
            // Switch mode
            mode = (mode == AuthMode.LOGIN) ? AuthMode.REGISTER : AuthMode.LOGIN;
            statusMessage = "";
        } else if (key.getKeyType() == KeyType.Enter) {
            processAuth();
        } else if (key.getKeyType() == KeyType.Backspace) {
            StringBuilder buffer = (activeField == InputField.NICKNAME)
                    ? nicknameBuffer
                    : passwordBuffer;
            if (buffer.length() > 0) {
                buffer.deleteCharAt(buffer.length() - 1);
            }
        } else if (key.getKeyType() == KeyType.Character) {
            char ch = key.getCharacter();
            StringBuilder buffer = (activeField == InputField.NICKNAME)
                    ? nicknameBuffer
                    : passwordBuffer;
            if (buffer.length() < 16) {
                buffer.append(ch);
            }
        }
    }

    private void processAuth() {
        String nickname = nicknameBuffer.toString().trim();
        String password = passwordBuffer.toString();

        AuthResult result;
        if (mode == AuthMode.LOGIN) {
            result = authService.login(nickname, password);
        } else {
            result = authService.register(nickname, password);
        }

        statusMessage = result.getMessage();
        isError = !result.isSuccess();
        authComplete = result.isSuccess();
    }

    public void render(Screen screen) throws Exception {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();

        // Title
        g.setForegroundColor(TextColor.ANSI.CYAN);
        String title = (mode == AuthMode.LOGIN) ? "=== LOGIN ===" : "=== REGISTER ===";
        g.putString(25, 2, title);

        // Instructions
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(2, 4, "F1: Switch to " +
                (mode == AuthMode.LOGIN ? "Register" : "Login"));
        g.putString(2, 5, "Tab: Switch field | Enter: Submit | ESC: Quit");

        // Nickname field
        g.setForegroundColor(activeField == InputField.NICKNAME
                ? TextColor.ANSI.YELLOW
                : TextColor.ANSI.WHITE);
        g.putString(2, 8, "Nickname: ");
        g.putString(12, 8, nicknameBuffer.toString() +
                (activeField == InputField.NICKNAME ? "_" : ""));

        // Password field (masked)
        g.setForegroundColor(activeField == InputField.PASSWORD
                ? TextColor.ANSI.YELLOW
                : TextColor.ANSI.WHITE);
        g.putString(2, 10, "Password: ");
        String maskedPassword = "*".repeat(passwordBuffer.length());
        g.putString(12, 10, maskedPassword +
                (activeField == InputField.PASSWORD ? "_" : ""));

        // Status message
        if (!statusMessage.isEmpty()) {
            g.setForegroundColor(isError ? TextColor.ANSI.RED : TextColor.ANSI.GREEN);
            g.putString(2, 13, statusMessage);
        }

        // Validation hints
        g.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        g.putString(2, 16, "Nickname: 3-16 chars, letters/numbers/_/-");
        g.putString(2, 17, "Password: 4-32 chars");

        screen.refresh();
    }

    public boolean isComplete() {
        return authComplete;
    }

    public boolean isAuthenticated() {
        return authService.isAuthenticated();
    }
}
