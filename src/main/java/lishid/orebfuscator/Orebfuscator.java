package lishid.orebfuscator;

import lishid.orebfuscator.commands.OrebfuscatorCommandExecutor;
import lishid.orebfuscator.utils.OrebfuscatorConfig;
import org.bukkit.event.Event;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Orebfuscator
        extends JavaPlugin {
    public static boolean usingSpout = false;
    private final OrebfuscatorBlockListener blockListener = new OrebfuscatorBlockListener(this);
    private final OrebfuscatorEntityListener entityListener = new OrebfuscatorEntityListener(this);
    private final OrebfuscatorPlayerListener playerListener = new OrebfuscatorPlayerListener(this);

    public void onEnable() {
        PluginManager pm = this.getServer().getPluginManager();
        OrebfuscatorConfig.Load(this.getConfiguration());
        pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, this.playerListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_BREAK, this.blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_DAMAGE, this.blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.BLOCK_PHYSICS, this.blockListener, Event.Priority.Monitor, this);
        pm.registerEvent(Event.Type.ENTITY_EXPLODE, this.entityListener, Event.Priority.Monitor, this);
        if (pm.getPlugin("Spout") != null && pm.getPlugin("OrebfuscatorSpoutBridge") == null) {
            System.out.println("[Orebfuscator] Error loading, Spout is found but OrebfuscatorSpoutBridge is not found.");
            pm.disablePlugin(this);
            return;
        }
        if (pm.getPlugin("Spout") != null) {
            System.out.println("[Orebfuscator] OrebfuscatorSpoutBridge found, using Spout.");
            usingSpout = true;
        } else {
            pm.registerEvent(Event.Type.PLAYER_JOIN, this.playerListener, Event.Priority.Low, this);
            System.out.println("[Orebfuscator] Spout not found, using Non-Spout mode.");
        }
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[Orebfuscator] version " + pdfFile.getVersion() + " initialization complete!");
        this.getCommand("ofc").setExecutor(new OrebfuscatorCommandExecutor(this));
    }

    public void onDisable() {
        OrebfuscatorConfig.Save();
        PluginDescriptionFile pdfFile = this.getDescription();
        System.out.println("[Orebfuscator] version " + pdfFile.getVersion() + " disabled!");
    }
}

