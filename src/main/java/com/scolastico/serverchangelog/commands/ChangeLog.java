package com.scolastico.serverchangelog.commands;

import com.scolastico.serverchangelog.ServerChangeLog;
import com.scolastico.serverchangelog.api.ServerChangeLogAPI;
import com.scolastico.serverchangelog.config.ChangeLogDataStore;
import com.scolastico.serverchangelog.config.ConfigDataStore;
import com.scolastico.serverchangelog.config.ConfigHandler;
import com.scolastico.serverchangelog.internal.ErrorHandler;
import com.scolastico.serverchangelog.internal.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChangeLog implements CommandExecutor, TabCompleter {

    private final ServerChangeLogAPI api = new ServerChangeLogAPI();

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        try {
            Player player = null;
            if (commandSender instanceof Player) player = (Player) commandSender;
            if (args.length == 0) {
                if (player != null) {
                    if (player.hasPermission("changelog.display")) {
                        Map<Long, String> changes = api.getChangesFromPlayer(player);
                        if (changes.size() != 0) {
                            Language language = Language.getInstance();
                            language.sendConfigMessage("update", player);
                            for (Long key:changes.keySet()) {
                                HashMap<String, String> replace = new HashMap<>();
                                replace.put("%id%", key.toString());
                                replace.put("%update%", changes.get(key));
                                language.sendConfigMessage("update_line", player, false, replace);
                            }
                        } else Language.getInstance().sendConfigMessage("nothing_new", player);
                    } else Language.getInstance().sendConfigMessage("permission", player);
                    return true;
                }
            } else if (args.length == 1) {
                if (args[0].equalsIgnoreCase("history")) {
                    if (commandSender.hasPermission("changelog.history")) {
                        showCommandSenderHistory(0, commandSender);
                        return true;
                    }
                    return true;
                } else if (args[0].equalsIgnoreCase("reload")) {
                    if (commandSender.hasPermission("changelog.admin.reload")) {
                        try {
                            ConfigHandler handler = ServerChangeLog.getConfigConfigHandler();
                            handler.reloadConfigFile();
                            ServerChangeLog.setConfigDataStore((ConfigDataStore) handler.getConfigObject());
                            handler = ServerChangeLog.getChangeLogConfigHandler();
                            handler.reloadConfigFile();
                            ServerChangeLog.setChangeLogDataStore((ChangeLogDataStore) handler.getConfigObject());
                            Language.getInstance().sendConfigMessage("done", commandSender);
                        } catch (Exception e) {
                            ErrorHandler.getInstance().handle(e);
                            Language.getInstance().sendConfigMessage("config_error", commandSender);
                        }
                    } else Language.getInstance().sendConfigMessage("permission", commandSender);
                    return true;
                }
            } else {
                if (args[0].equalsIgnoreCase("history")) {
                    if (args.length == 2) {
                        if (commandSender.hasPermission("changelog.history")) {
                            try {
                                int page = Integer.parseInt(args[1]);
                                showCommandSenderHistory(page, commandSender);
                                return true;
                            } catch (NumberFormatException ignored) {}
                        }
                    }
                } else if (args[0].equalsIgnoreCase("create")) {
                    if (commandSender.hasPermission("changelog.admin.create")) {
                        String message = "";
                        for (int count = 1; count != args.length; count++) {
                            message += " " + args[count];
                        }
                        message = message.substring(1);
                        ChangeLogDataStore dataStore = ServerChangeLog.getChangeLogDataStore();
                        Long id = dataStore.getCurrentId() + 1;
                        HashMap<Long, String> updates = dataStore.getUpdates();
                        updates.put(id, message);
                        HashMap<String, String> replace = new HashMap<>();
                        replace.put("%id%", id.toString());
                        replace.put("%update%", message);
                        Language.getInstance().sendConfigMessage("update_line", Bukkit.getConsoleSender(), true, replace);
                        HashMap<String, Long> lastSeen = dataStore.getLastSeen();
                        for (Player p:Bukkit.getServer().getOnlinePlayers()) {
                            if (p.hasPermission("changelog.notify")) {
                                Language.getInstance().sendConfigMessage("update_line", p, true, replace);
                                lastSeen.remove(p.getUniqueId().toString());
                                lastSeen.put(p.getUniqueId().toString(), id);
                            }
                        }
                        dataStore.setLastSeen(lastSeen);
                        dataStore.setUpdates(updates);
                        dataStore.setCurrentId(id);
                        ServerChangeLog.setChangeLogDataStore(dataStore);
                        ConfigHandler handler = ServerChangeLog.getChangeLogConfigHandler();
                        handler.setConfigObject(dataStore);
                        try {
                            handler.saveConfigObject();
                        } catch (Exception e) {
                            Language.getInstance().sendConfigMessage("save_error", commandSender);
                            ErrorHandler.getInstance().handle(e);
                        }
                        Language.getInstance().sendConfigMessage("done", commandSender);
                    } else Language.getInstance().sendConfigMessage("permission", commandSender);
                    return true;
                } else if (args[0].equalsIgnoreCase("delete")) {
                    if (args.length == 2) {
                        try {
                            if (commandSender.hasPermission("changelog.admin.delete")) {
                                Long id = Long.parseLong(args[1]);
                                ChangeLogDataStore dataStore = ServerChangeLog.getChangeLogDataStore();
                                HashMap<Long, String> updates = dataStore.getUpdates();
                                if (updates.containsKey(id)) {
                                    updates.remove(id);
                                    dataStore.setUpdates(updates);
                                    ServerChangeLog.setChangeLogDataStore(dataStore);
                                    ConfigHandler handler = ServerChangeLog.getChangeLogConfigHandler();
                                    handler.setConfigObject(dataStore);
                                    handler.saveConfigObject();
                                    Language.getInstance().sendConfigMessage("done", commandSender);
                                } else Language.getInstance().sendConfigMessage("cant_find_id", commandSender);
                            } else Language.getInstance().sendConfigMessage("permission", commandSender);
                            return true;
                        } catch (NumberFormatException ignored) {}
                    }
                }
            }
            if (commandSender.hasPermission("changelog.display")) Language.getInstance().sendConfigMessage("help_main", commandSender);
            Language.getInstance().sendConfigMessage("help_help", commandSender);
            if (commandSender.hasPermission("changelog.history")) Language.getInstance().sendConfigMessage("help_history1", commandSender);
            if (commandSender.hasPermission("changelog.history")) Language.getInstance().sendConfigMessage("help_history2", commandSender);
            if (commandSender.hasPermission("changelog.admin.create")) Language.getInstance().sendConfigMessage("help_create", commandSender);
            if (commandSender.hasPermission("changelog.admin.delete")) Language.getInstance().sendConfigMessage("help_delete", commandSender);
            if (commandSender.hasPermission("changelog.admin.reload")) Language.getInstance().sendConfigMessage("help_reload", commandSender);
            return true;
        } catch (Exception e) {
            ErrorHandler.getInstance().handle(e);
        }
        return true;
    }

    private void showCommandSenderHistory(int page, CommandSender sender) {
        ChangeLogDataStore dataStore = ServerChangeLog.getChangeLogDataStore();
        HashMap<Long, String> updates = dataStore.getUpdates();
        ArrayList<HashMap<Long, String>> pages = new ArrayList<>();
        HashMap<Long, String> tmp = new HashMap<>();
        for (Long key:updates.keySet()) {
            if (tmp.size() == 10) {
                pages.add(tmp);
                tmp = new HashMap<>();
            }
            tmp.put(key, updates.get(key));
        }
        pages.add(tmp);
        if (page == 0) page = pages.size();
        if (0<page && page-1<pages.size()) {
            HashMap<Long, String> p = pages.get(page-1);
            HashMap<String, String> replace = new HashMap<>();
            replace.put("%page%", Integer.toString(page));
            replace.put("%max%", Integer.toString(pages.size()));
            Language.getInstance().sendConfigMessage("history_title", sender, true, replace);
            for (Long id:p.keySet()) {
                replace = new HashMap<>();
                replace.put("%id%", id.toString());
                replace.put("%update%", p.get(id));
                Language.getInstance().sendConfigMessage("update_line", sender, true, replace);
            }
            Language.getInstance().sendConfigMessage("history_footer", sender);
        } else Language.getInstance().sendConfigMessage("cant_find_page", sender);
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] args) {
        List<String> tabComplete = new ArrayList<>();
        if (args.length == 1) {
            tabComplete.add("help");
            if (commandSender.hasPermission("changelog.history")) tabComplete.add("history");
            if (commandSender.hasPermission("changelog.admin.create")) tabComplete.add("create");
            if (commandSender.hasPermission("changelog.admin.delete")) tabComplete.add("delete");
            if (commandSender.hasPermission("changelog.admin.reload")) tabComplete.add("reload");
        }
        return tabComplete;
    }
}
