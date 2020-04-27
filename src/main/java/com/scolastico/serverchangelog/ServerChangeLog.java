package com.scolastico.serverchangelog;

import com.scolastico.serverchangelog.commands.ChangeLog;
import com.scolastico.serverchangelog.config.ChangeLogDataStore;
import com.scolastico.serverchangelog.config.ConfigDataStore;
import com.scolastico.serverchangelog.config.ConfigHandler;
import com.scolastico.serverchangelog.events.OnJoin;
import com.scolastico.serverchangelog.internal.ErrorHandler;
import com.scolastico.serverchangelog.internal.Language;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.HashMap;

public class ServerChangeLog extends JavaPlugin {

    private static ConsoleCommandSender console;
    private static ConfigDataStore config = new ConfigDataStore();
    private static ChangeLogDataStore changeLog = new ChangeLogDataStore();
    private static ConfigHandler configConfigHandler;
    private static ConfigHandler changeLogConfigHandler;
    private static final String pluginName = "ServerChangeLog";
    private static Plugin plugin;
    private final Language language = Language.getInstance();

    @Override
    public void onEnable() {
        try {

            // Set Vars
            ErrorHandler.getInstance().setName(pluginName);
            console = Bukkit.getServer().getConsoleSender();
            plugin = Bukkit.getPluginManager().getPlugin(ServerChangeLog.pluginName);
            if (plugin == null) ErrorHandler.getInstance().handleFatal(new Exception("Can not get plugin while init."));

            // Load Config
            language.setPrefix(Language.DARK_CYAN + "[" + Language.CYAN + pluginName + Language.DARK_CYAN + "]");
            language.sendColorMessage(Language.DARK_GREEN + "Loading Config...", console);
            File pluginFolder = new File("plugins/" + pluginName + "/");
            pluginFolder.mkdirs();
            configConfigHandler = new ConfigHandler(config, "plugins/" + pluginName + "/config.json");
            config = (ConfigDataStore) configConfigHandler.getConfigObject();
            changeLogConfigHandler = new ConfigHandler(changeLog, "plugins/" + pluginName + "/data.json");
            changeLog = (ChangeLogDataStore) changeLogConfigHandler.getConfigObject();
            HashMap<String, String> languageData = config.getLanguage();
            if (languageData.containsKey("prefix")) language.setPrefix(languageData.get("prefix"));
            language.setStrings(languageData);
            language.sendColorMessage(Language.DARK_GREEN + "Config " + Language.GREEN + Language.BOLD + "[OK]", console);

            // Register Events
            PluginManager pluginManager = getServer().getPluginManager();
            pluginManager.registerEvents(new OnJoin(), this);

            // Register Commands
            PluginCommand command = this.getCommand("ChangeLog");
            if (command != null) {
                ChangeLog cmd = new ChangeLog();
                command.setExecutor(cmd);
                command.setTabCompleter(cmd);
            } else {
                ErrorHandler.getInstance().handleFatal(new Exception("Can not get command 'ChangeLog' while init."));
            }

        } catch (Exception e) {
            ErrorHandler.getInstance().handleFatal(e);
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public Language getLanguage() {
        return language;
    }

    public static String getPluginName() {
        return pluginName;
    }

    public static Plugin getPlugin() {
        return plugin;
    }

    public static ConfigDataStore getConfigDataStore() {
        return config;
    }

    public static void setConfigDataStore(ConfigDataStore config) {
        ServerChangeLog.config = config;
    }

    public static ChangeLogDataStore getChangeLogDataStore() {
        return changeLog;
    }

    public static void setChangeLogDataStore(ChangeLogDataStore changeLog) {
        ServerChangeLog.changeLog = changeLog;
    }

    public static ConfigHandler getConfigConfigHandler() {
        return configConfigHandler;
    }

    public static ConfigHandler getChangeLogConfigHandler() {
        return changeLogConfigHandler;
    }
}
