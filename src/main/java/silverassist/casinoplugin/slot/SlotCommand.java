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
import silverassist.casinoplugin.slot.menu.admin.CategoryChoice;
import silverassist.casinoplugin.slot.menu.admin.CategoryEdit;
import silverassist.casinoplugin.slot.menu.admin.MainMenu;
import silverassist.casinoplugin.slot.menu.admin.SlotList;

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
            new SlotList(plugin,MAIN_SYSTEM,p).open();
            return true;
        }

        String id = args[1];
        YamlConfiguration yml;

        switch (args[0]){
            case "create":
                if(!MAIN_SYSTEM.existSlot(id)){
                    yml = CustomConfig.createYmlByID(id);
                    yml.set("name",id);
                    yml.set("nowmode","0");
                    yml.set("spintime.1",40);yml.set("spintime.2",20);yml.set("spintime.3",20);
                    yml.set("stock_per_spin",0);
                    yml.set("payment",100);
                    yml.set("stock",0);yml.set("def_stock",100);
                    CustomConfig.saveYmlByID(id);
                    for(int i = 0;i<7;i++){CustomConfig.createYmlByID(id,String.valueOf(i)).set("miss.weight",1);CustomConfig.saveYmlByID(id,String.valueOf(i));}
                    Util.sendPrefixMessage(p,"§a§lスロット§d§l『"+id+"』を作成しました");
                }
            case "edit":
                if(!MAIN_SYSTEM.existSlot(id)){
                    Util.sendPrefixMessage(p,"§c§lそのスロットは存在しません");
                    return true;
                }

                if(args.length<4) new MainMenu(plugin,MAIN_SYSTEM,p,id).open();
                else{
                    switch (args[2]){
                        case "setname":
                            CustomConfig.getYmlByID(id).set("name",args[3]);
                            CustomConfig.saveYmlByID(id);
                            new MainMenu(plugin,MAIN_SYSTEM,p,id).open();
                            break;

                        case "setpayment":
                            if(!args[3].matches("\\d+"))return true;
                            CustomConfig.getYmlByID(id).set("payment",Integer.parseInt(args[3]));
                            CustomConfig.saveYmlByID(id);
                            new MainMenu(plugin,MAIN_SYSTEM,p,id).open();
                            break;

                        case "setspintime":
                            if(args.length<6)return true;
                            for(int i = 3;i<6;i++){if(!args[i].matches("\\d+"))return true;}
                            yml = CustomConfig.getYmlByID(id);
                            if(yml == null)return true;
                            for(int i = 3;i<6;i++)yml.set("spintime."+(i-2),Integer.parseInt(args[i]));
                            CustomConfig.saveYmlByID(id);
                            new MainMenu(plugin,MAIN_SYSTEM,p,id).open();
                            break;

                        case "setmissweight":
                            if(args.length<5 || !args[3].matches("\\d+") || !args[4].matches("\\d+"))return true;
                            CustomConfig.getYmlByID(id,args[3]).set("miss.weight",Integer.parseInt(args[4]));
                            CustomConfig.saveYmlByID(id,args[3]);
                            new CategoryChoice(plugin,MAIN_SYSTEM,p,id,Integer.parseInt(args[3])).open();
                            break;

                        case "setdefaultstock":
                            if(!args[3].matches("\\d+"))return true;
                            yml = CustomConfig.getYmlByID(id);
                            yml.set("def_stock",Integer.parseInt(args[3]));
                            yml.set("stock",yml.getInt("def_stock",100));
                            CustomConfig.saveYmlByID(id);
                            new MainMenu(plugin,MAIN_SYSTEM,p,id).open();
                            break;

                        case "setstockperspin":
                            if(!args[3].matches("\\d+"))return true;
                            yml = CustomConfig.getYmlByID(id);
                            yml.set("stock_per_spin",Integer.parseInt(args[3]));
                            CustomConfig.saveYmlByID(id);
                            new MainMenu(plugin,MAIN_SYSTEM,p,id).open();
                            break;

                        case "setconstantmoney":
                        case "setmultiplier":
                        case "setcategoryname":
                        case "setnextmode":
                        case "setweight":
                            if(args.length<6)return true;
                            if(!args[3].matches("\\d+")||!args[4].matches("\\d+"))return true;
                            yml = CustomConfig.getYmlByID(id,args[3]);
                            if(yml == null)return true;
                            if(args[2].equals("setconstantmoney") && args[5].matches("\\d+"))yml.set(args[4]+".constant_money",Integer.parseInt(args[5]));
                            else if(args[2].equals("setmultiplier") && args[5].matches("\\d+\\.?\\d*"))yml.set(args[4]+".multiplier",Double.parseDouble(args[5]));
                            else if(args[2].equals("setcategoryname"))yml.set(args[4]+".name",args[5]);
                            else if(args[2].equals("setnextmode") && args[5].matches("\\d+"))yml.set(args[4]+".nextmode",Integer.parseInt(args[5])-1);
                            else if(args[2].equals("setweight") && args[5].matches("\\d+"))yml.set(args[4]+".weight",Integer.parseInt(args[5]));
                            CustomConfig.saveYmlByID(id,args[3]);
                            new CategoryEdit(plugin,MAIN_SYSTEM,p,id,Integer.parseInt(args[3]),args[4]).open();
                            break;




                    }
                }
                break;

            case "delete":
                if(!MAIN_SYSTEM.existSlot(id)){
                    Util.sendPrefixMessage(p,"§c§lそのスロットは存在しません");
                    return true;
                }
                MAIN_SYSTEM.deleteSlot(id);
                Util.sendPrefixMessage(p,"§c§lスロットを正常に削除しました");

                break;

            case "reloadslot":
                if (!MAIN_SYSTEM.existSlot(id)) {
                    Util.sendPrefixMessage(p,"§c§lそのスロットは存在しません");
                    return true;
                }
                MAIN_SYSTEM.reloadSlot(id);
                Util.sendPrefixMessage(p,"§a§lそのスロットをreloadしました");
                break;

        }
        return false;
    }

    private class Tab implements TabCompleter{

        @Override
        public List<String> onTabComplete(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
            switch (args.length){
                case 1:
                    return List.of("create","edit","delete","reloadslot");
                case 2:
                    switch (args[0]){
                        case "edit":
                        case "reloadslot":
                            return MAIN_SYSTEM.getSlotList(args[1]);
                    }
            }
            return null;
        }
    }
}
