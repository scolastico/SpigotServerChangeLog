package com.scolastico.serverchangelog.api;

import com.scolastico.serverchangelog.ServerChangeLog;
import com.scolastico.serverchangelog.config.ChangeLogDataStore;
import com.scolastico.serverchangelog.config.ConfigHandler;
import com.scolastico.serverchangelog.internal.ErrorHandler;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ServerChangeLogAPI {

    /**
     * Get All changes since last login from a player.
     * @param player The player to get all changes since last login.
     * @return All changes since last login from a player
     */
    public Map<Long, String> getChangesFromPlayer(Player player) {
        Map<Long, String> changes = new HashMap<>();
        ChangeLogDataStore dataStore = ServerChangeLog.getChangeLogDataStore();
        HashMap<String, Long> lastSeen = dataStore.getLastSeen();
        Long highestId = dataStore.getCurrentId();
        if (lastSeen.containsKey(player.getUniqueId().toString())) {
            Long id = lastSeen.get(player.getUniqueId().toString());
            highestId = id;
            HashMap<Long, String> changesComplete = dataStore.getUpdates();
            for (Long currentId : changesComplete.keySet()) {
                if (id < currentId) changes.put(currentId, changesComplete.get(currentId));
                if (currentId > highestId) highestId = currentId;
            }
        }
        lastSeen.remove(player.getUniqueId().toString());
        lastSeen.put(player.getUniqueId().toString(), highestId);
        dataStore.setLastSeen(lastSeen);
        ServerChangeLog.setChangeLogDataStore(dataStore);
        ConfigHandler configHandler = ServerChangeLog.getChangeLogConfigHandler();
        configHandler.setConfigObject(dataStore);
        try {
            configHandler.saveConfigObject();
        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
        }
        return changes;
    }

}
