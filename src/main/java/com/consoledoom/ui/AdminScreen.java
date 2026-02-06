package com.consoledoom.ui;

import com.consoledoom.models.LeaderboardEntry;
import com.consoledoom.models.User;
import com.consoledoom.security.SecurityContext;
import com.consoledoom.service.AdminService;
import com.consoledoom.service.AdminService.OperationResult;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;

import java.util.List;

public class AdminScreen {
    private final AdminService adminService;

    private enum Tab {
        USERS, RECORDS
    }

    private Tab currentTab = Tab.USERS;

    private List<User> users;
    private List<LeaderboardEntry> records;
    private int selectedIndex = 0;
    private int scrollOffset = 0;
    private static final int VISIBLE_ROWS = 10;

    private String statusMessage = "";
    private boolean isError = false;
    private boolean shouldExit = false;

    public AdminScreen() {
        this.adminService = new AdminService();
        refreshData();
    }

    private void refreshData() {
        try {
            users = adminService.getAllUsers();
            records = adminService.getAllGameRecords();
        } catch (SecurityException e) {
            statusMessage = "Access denied: " + e.getMessage();
            isError = true;
        }
    }

    public void handleInput(KeyStroke key) {
        statusMessage = "";

        if (key.getKeyType() == KeyType.Escape) {
            shouldExit = true;
        } else if (key.getKeyType() == KeyType.Tab) {
            currentTab = (currentTab == Tab.USERS) ? Tab.RECORDS : Tab.USERS;
            selectedIndex = 0;
            scrollOffset = 0;
        } else if (key.getKeyType() == KeyType.ArrowUp) {
            if (selectedIndex > 0) {
                selectedIndex--;
                if (selectedIndex < scrollOffset) {
                    scrollOffset = selectedIndex;
                }
            }
        } else if (key.getKeyType() == KeyType.ArrowDown) {
            int maxIndex = (currentTab == Tab.USERS) ? users.size() - 1 : records.size() - 1;
            if (selectedIndex < maxIndex) {
                selectedIndex++;
                if (selectedIndex >= scrollOffset + VISIBLE_ROWS) {
                    scrollOffset = selectedIndex - VISIBLE_ROWS + 1;
                }
            }
        } else if (key.getKeyType() == KeyType.Delete ||
                (key.getKeyType() == KeyType.Character && key.getCharacter() == 'd')) {
            deleteSelected();
        } else if (key.getKeyType() == KeyType.Character && key.getCharacter() == 'r') {
            refreshData();
            statusMessage = "Data refreshed";
        }
    }

    private void deleteSelected() {
        OperationResult result;

        if (currentTab == Tab.USERS && selectedIndex < users.size()) {
            User user = users.get(selectedIndex);
            result = adminService.deleteUser(user.getId());
        } else if (currentTab == Tab.RECORDS && selectedIndex < records.size()) {
            LeaderboardEntry record = records.get(selectedIndex);
            result = adminService.deleteGameRecord(record.getSessionId());
        } else {
            return;
        }

        statusMessage = result.getMessage();
        isError = !result.isSuccess();

        if (result.isSuccess()) {
            refreshData();
            if (selectedIndex >= (currentTab == Tab.USERS ? users.size() : records.size())) {
                selectedIndex = Math.max(0, selectedIndex - 1);
            }
        }
    }

    public void render(Screen screen) throws Exception {
        screen.clear();
        TextGraphics g = screen.newTextGraphics();

        g.setForegroundColor(TextColor.ANSI.RED);
        g.putString(25, 1, "=== ADMIN PANEL ===");

        SecurityContext.getInstance().getCurrentUser().ifPresent(user -> {
            g.setForegroundColor(TextColor.ANSI.WHITE);
            g.putString(2, 1, "Logged in as: " + user.getNickname() +
                    " [" + user.getRole() + "]");
        });

        g.setForegroundColor(currentTab == Tab.USERS ? TextColor.ANSI.YELLOW : TextColor.ANSI.WHITE);
        g.putString(2, 3, "[USERS]");
        g.setForegroundColor(currentTab == Tab.RECORDS ? TextColor.ANSI.YELLOW : TextColor.ANSI.WHITE);
        g.putString(12, 3, "[RECORDS]");

        g.setForegroundColor(TextColor.ANSI.CYAN);
        g.putString(30, 3, "Tab:Switch | ↑↓:Select | D:Delete | R:Refresh | ESC:Back");

        if (currentTab == Tab.USERS) {
            renderUsers(g);
        } else {
            renderRecords(g);
        }

        if (!statusMessage.isEmpty()) {
            g.setForegroundColor(isError ? TextColor.ANSI.RED : TextColor.ANSI.GREEN);
            g.putString(2, 20, statusMessage);
        }

        screen.refresh();
    }

    private void renderUsers(TextGraphics g) {
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(2, 5, String.format("%-4s %-16s %-10s %-20s %-8s",
                "ID", "NICKNAME", "ROLE", "CREATED", "ACTIVE"));

        int y = 7;
        for (int i = scrollOffset; i < Math.min(scrollOffset + VISIBLE_ROWS, users.size()); i++) {
            User user = users.get(i);

            if (i == selectedIndex) {
                g.setForegroundColor(TextColor.ANSI.BLACK);
                g.setBackgroundColor(TextColor.ANSI.WHITE);
            } else {
                g.setForegroundColor(TextColor.ANSI.WHITE);
                g.setBackgroundColor(TextColor.ANSI.BLACK);
            }

            String created = user.getCreatedAt() != null
                    ? user.getCreatedAt().toString().substring(0, 16)
                    : "N/A";

            g.putString(2, y++, String.format("%-4d %-16s %-10s %-20s %-8s",
                    user.getId(),
                    user.getNickname(),
                    user.getRole(),
                    created,
                    user.isActive() ? "Yes" : "No"));
        }

        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.setBackgroundColor(TextColor.ANSI.BLACK);

        g.putString(2, 18, "Total users: " + users.size());
    }

    private void renderRecords(TextGraphics g) {
        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.putString(2, 5, String.format("%-6s %-16s %-8s %-6s %-6s %-6s %-10s",
                "ID", "PLAYER", "SCORE", "KILLS", "WAVE", "TIME", "DATE"));

        int y = 7;
        for (int i = scrollOffset; i < Math.min(scrollOffset + VISIBLE_ROWS, records.size()); i++) {
            LeaderboardEntry record = records.get(i);

            if (i == selectedIndex) {
                g.setForegroundColor(TextColor.ANSI.BLACK);
                g.setBackgroundColor(TextColor.ANSI.WHITE);
            } else {
                g.setForegroundColor(TextColor.ANSI.WHITE);
                g.setBackgroundColor(TextColor.ANSI.BLACK);
            }

            String time = String.format("%02d:%02d",
                    record.getTimeSurvivedSec() / 60,
                    record.getTimeSurvivedSec() % 60);

            String date = record.getPlayedAt() != null
                    ? record.getPlayedAt().toString().substring(0, 10)
                    : "N/A";

            g.putString(2, y++, String.format("%-6d %-16s %-8d %-6d %-6d %-6s %-10s",
                    record.getSessionId(),
                    record.getNickname(),
                    record.getScore(),
                    record.getKills(),
                    record.getWave(),
                    time,
                    date));
        }

        g.setForegroundColor(TextColor.ANSI.WHITE);
        g.setBackgroundColor(TextColor.ANSI.BLACK);

        g.putString(2, 18, "Total records: " + records.size());
    }

    public boolean shouldExit() {
        return shouldExit;
    }
}
