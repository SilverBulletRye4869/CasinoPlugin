package silverassist.casinoplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class CasinoPlugin extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getLogger();
        // Plugin startup logic

    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
