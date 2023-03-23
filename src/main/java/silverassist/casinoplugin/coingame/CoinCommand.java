package silverassist.casinoplugin.coingame;

import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.Vault;

import java.util.List;

public class CoinCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final int MIN_BET = 100;

    public CoinCommand(JavaPlugin plugin){
        this.plugin = plugin;
        PluginCommand command = plugin.getCommand("coingame");
        command.setExecutor(this);
        command.setTabCompleter(new Tab());
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player)sender;
        if(args.length<2){
            //ヘルプ
            return true;
        }
        switch (args[0]){
            case "start":
                if(args[1].matches("\\d+")){
                    int betAmount = Integer.parseInt(args[1]);
                    if(betAmount<MIN_BET){
                        Util.sendPrefixMessage(p,"§c§l最低ベット額は§6§l"+MIN_BET+"円§c§lです");
                        return true;
                    }
                    if(Vault.getEconomy().getBalance(p) < betAmount){
                        Util.sendPrefixMessage(p,"§c§l所持金が足りません");
                        return true;
                    }
                    if(!Game.start(Integer.parseInt(args[1]),p))return true;
                    if(args.length>2 && (args[2].equals("back") || args[2].equals("b"))){Game.betToBack(p);}
                    else Game.betToFace(p);
                }
                return true;

            case "join":
            case "bet":
                if(!Game.canJoin(p)){
                    Util.sendPrefixMessage(p,"§c§l所持金が足りません");
                    return true;
                }
                switch (args[1]){
                    case "face":
                    case "f":
                        Game.betToFace(p);
                        break;
                    case "back":
                    case "b":
                        Game.betToBack(p);
                        break;
                    default:
                        Util.sendPrefixMessage(p,"§c§lベット先が判定できませんでした");
                }
        }

        return true;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
            switch (args.length){
                case 1:
                    return List.of("start","join");
                case 2:
                    switch (args[0]){
                        case "start":
                            return List.of("金額");
                        case "join":
                            return List.of("face","back");
                    }
                    return List.of("");
                case 3:
                    switch (args[0]){
                        case "start":
                            return List.of("face","back");
                    }
                    return List.of("");
            }

            return List.of("");
        }
    }
}
