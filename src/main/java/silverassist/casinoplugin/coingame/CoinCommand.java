package silverassist.casinoplugin.coingame;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class CoinCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MainSystem_Coin MAIN_SYSTEM;

    public CoinCommand(JavaPlugin plugin, MainSystem_Coin mainSystemCoin){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystemCoin;
        plugin.getCommand("coingame").setExecutor(this);
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            return null;
        }
    }
}
