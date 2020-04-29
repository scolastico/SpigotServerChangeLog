package com.scolastico.serverchangelog.config;

import java.util.HashMap;

public class ConfigDataStore {

    private int waitSecondsAfterJoin = 5;
    private boolean onlyNotifyOnJoin = false;

    private HashMap<String, String> language = new HashMap<String, String>() {
        {
            put("prefix", "&3[&bChangeLog&3]");
            put("permission", "&cSorry, you dont have the permission to do that!");
            put("done", "&aDone!");
            put("update", "&2New Update!");
            put("update_line", "&e[&l#%id%&r&e] %update%");
            put("no_update", "&cSorry, no update available.");
            put("nothing_new", "&cYou have already seen all new updates. But you can see the last 10 updates with '&l/changelog history&r&c'.");
            put("config_error", "&cOops.. The config cant be loaded. Check console for more information!");
            put("save_error", "&cOops.. The config cant be saved. Check console for more information!");
            put("cant_find_id", "&cOops.. This id does not exist!");
            put("cant_find_page", "&cOops.. This page does not exist!");
            put("history_title", "&fHistory Page %page% from %max%");
            put("history_footer", "&fFor Other Pages: '/changelog history <page>'");
            put("help_main", "&f/changelog - Shows new updates.");
            put("help_help", "&f/changelog help - Shows new updates.");
            put("help_history1", "&f/changelog history - Shows the history.");
            put("help_history2", "&f/changelog history <page> - Shows an history page.");
            put("help_create", "&f/changelog create <message> - Create an changelog entry.");
            put("help_delete", "&f/changelog delete <id> - Delete an changelog entry.");
            put("help_reload", "&f/changelog reload - Reload Config.");
            put("notify_on_join", "&cNew Updates available! Use /changelog to see it!");
        }
    };

    public boolean isOnlyNotifyOnJoin() {
        return onlyNotifyOnJoin;
    }

    public void setOnlyNotifyOnJoin(boolean onlyNotifyOnJoin) {
        this.onlyNotifyOnJoin = onlyNotifyOnJoin;
    }

    public HashMap<String, String> getLanguage() {
        return language;
    }

    public void setLanguage(HashMap<String, String> language) {
        this.language = language;
    }

    public int getWaitSecondsAfterJoin() {
        return waitSecondsAfterJoin;
    }

    public void setWaitSecondsAfterJoin(int waitSecondsAfterJoin) {
        this.waitSecondsAfterJoin = waitSecondsAfterJoin;
    }
}