package com.scolastico.serverchangelog.config;

import java.util.HashMap;

public class ChangeLogDataStore {

    Long currentId = 0L;
    HashMap<Long, String> updates = new HashMap<>();
    HashMap<String, Long> lastSeen = new HashMap<>();

    public Long getCurrentId() {
        return currentId;
    }

    public void setCurrentId(Long currentId) {
        this.currentId = currentId;
    }

    public HashMap<Long, String> getUpdates() {
        return updates;
    }

    public void setUpdates(HashMap<Long, String> updates) {
        this.updates = updates;
    }

    public HashMap<String, Long> getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(HashMap<String, Long> lastSeen) {
        this.lastSeen = lastSeen;
    }

}
