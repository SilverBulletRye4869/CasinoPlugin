package silverassist.casinoplugin.slot;

import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.slot.menu.admin.MainMenu;

import java.util.List;

public class SlotCommand implements CommandExecutor {
    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;

    public SlotCommand(JavaPlugin plugin, MainSystem_slot mainSystem_slot){
        this.plugin =plugin;
        this.MAIN_SYSTEM = mainSystem_slot;
        PluginCommand command = plugin.getCommand("slot");
        command.setExecutor(this);
        command.setTabCompleter(new Tab());
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if(!(sender instanceof Player))return true;
        Player p = (Player) sender;
        if(args.length < 2){
            //ヘルプ
            return true;
        }

        String id = args[1];
        switch (args[0]){
            case "create":
                if(!MAIN_SYSTEM.existSlot(id)){
                    YamlConfiguration yml = CustomConfig.createYmlByID(id);
                    yml.set("name",id);
                    yml.set("nowmode","1");
                    yml.set("spintime.1",40);yml.set("spintime.2",20);yml.set("spintime.3",20);
                    yml.set("stock_per_spin",0);
                    yml.set("payment",100);
                    CustomConfig.saveYmlByID(id);
                    for(int i = 0;i<7;i++)CustomConfig.createYmlByID(id,String.valueOf(i));
                    Util.sendPrefixMessage(p,"§a§lスロット§d§l『"+id+"』を作成しました");
                }
            case "edit":
                if(!MAIN_SYSTEM.existSlot(id)){
                    Util.sendPrefixMessage(p,"§c§lそのスロットは存在しません");
                    return true;
                }
                new MainMenu(plugin,MAIN_SYSTEM,p,id).open();
        }
        return false;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            return null;
        }
    }
}
