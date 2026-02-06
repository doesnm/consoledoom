package com.consoledoom.ui;

import com.consoledoom.security.Permission;
import com.consoledoom.security.SecurityContext;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.ArrayList;
import java.util.List;

public class MainMenuScreen {

    public enum MenuAction {
        PLAY, LEADERBOARD, ADMIN, LOGOUT, QUIT
    }

    private final List<MenuItem> menuItems = new ArrayList<>();
    private int selectedIndex = 0;
    private MenuAction selectedAction = null;

    public MainMenuScreen() {
        buildMenu();
    }

    private void buildMenu() {
        menuItems.clear();
        menuItems.add(new MenuItem("Play Game", MenuAction.PLAY));
        menuItems.add(new MenuItem("Leaderboard", MenuAction.LEADERBOARD));

        if (SecurityContext.getInstance().hasPermission(Permission.VIEW_ADMIN_PANEL)) {
            menuItems.add(new MenuItem("Admin Panel", MenuAction.ADMIN));
        }

        menuItems.add(new MenuItem("Logout", MenuAction.LOGOUT));
        menuItems.add(new MenuItem("Quit", MenuAction.QUIT));
    }

    public void handleInput(KeyStroke key) {
        if (key.getKeyType() == KeyType.ArrowUp) {
            selectedIndex = (selectedIndex - 1 + menuItems.size()) % menuItems.size();
        } else if (key.getKeyType() == KeyType.ArrowDown) {
            selectedIndex = (selectedIndex + 1) % menuItems.size();
        } else if (key.getKeyType() == KeyType.Enter) {
            selectedAction = menuItems.get(selectedIndex).action;
        }
    }

    public void render(Screen screen) throws Exception {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();

        g.setForegroundColor(TextColor.ANSI.RED);
        g.putString(15, 2, "  ____                       _        ____                        ");
        g.putString(15, 3, " / ___|___  _ __  ___  ___  | | ___  |  _ \\  ___   ___  _ __ ___  ");
        g.putString(15, 4, "| |   / _ \\| '_ \\/ __|/ _ \\ | |/ _ \\ | | | |/ _ \\ / _ \\| '_ ` _ \\ ");
        g.putString(15, 5, "| |__| (_) | | | \\__ \\ (_) || |  __/ | |_| | (_) | (_) | | | | | |");
        g.putString(15, 6, " \\____\\___/|_| |_|___/\\___/ |_|\\___| |____/ \\___/ \\___/|_| |_| |_|");

        SecurityContext.getInstance().getCurrentUser().ifPresent(user -> {
            g.setForegroundColor(TextColor.ANSI.CYAN);
            g.putString(2, 8, "Welcome, " + user.getNickname() + " [" + user.getRole() + "]");
        });

        int y = 11;
        for (int i = 0; i < menuItems.size(); i++) {
            MenuItem item = menuItems.get(i);

            if (i == selectedIndex) {
                g.setForegroundColor(TextColor.ANSI.YELLOW);
                g.putString(30, y, "> " + item.label + " <");
            } else {
                g.setForegroundColor(TextColor.ANSI.WHITE);
                g.putString(32, y, item.label);
            }
            y += 2;
        }

        g.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        g.putString(25, y + 2, "Use ↑↓ to navigate, Enter to select");

        screen.refresh();
    }

    public MenuAction getSelectedAction() {
        return selectedAction;
    }

    public void clearAction() {
        selectedAction = null;
    }

    private record MenuItem(String label, MenuAction action) {
    }
}
