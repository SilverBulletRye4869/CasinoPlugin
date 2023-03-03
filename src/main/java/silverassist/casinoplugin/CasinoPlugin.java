package silverassist.casinoplugin;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class CasinoPlugin extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;
    private static Vault vault = null;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getLogger();

        vault = new Vault(this);
        if (!vault.setupEconomy() ) vault.log.severe(String.format("[%s] プラグイン「Vault」「Essentials」の認証に失敗しました。", getDescription().getName()));



        // Plugin startup logic

    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}
    public static Vault getVault(){return vault;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
