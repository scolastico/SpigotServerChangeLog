package com.scolastico.serverchangelog.events;

import com.scolastico.serverchangelog.ServerChangeLog;
import com.scolastico.serverchangelog.api.ServerChangeLogAPI;
import com.scolastico.serverchangelog.internal.Language;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

public class OnJoin implements Listener {

    ServerChangeLogAPI api = new ServerChangeLogAPI();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        if (event.getPlayer().hasPermission("changelog.onjoin")) {
            BukkitScheduler scheduler = Bukkit.getScheduler();
            scheduler.runTaskLater(ServerChangeLog.getPlugin(), new Runnable() {
                @Override
                public void run() {
                    if (event.getPlayer().isOnline()) {
                        Map<Long, String> changes = api.getChangesFromPlayer(event.getPlayer());
                        if (changes.size() != 0) {
                            Language language = Language.getInstance();
                            language.sendConfigMessage("update", event.getPlayer());
                            for (Long key:changes.keySet()) {
                                HashMap<String, String> replace = new HashMap<>();
                                replace.put("%id%", key.toString());
                                replace.put("%update%", changes.get(key));
                                language.sendConfigMessage("update_line", event.getPlayer(), true, replace);
                            }
                        }
                    }
                }
            }, ServerChangeLog.getConfigDataStore().getWaitSecondsAfterJoin()*20);
        }
    }

}
