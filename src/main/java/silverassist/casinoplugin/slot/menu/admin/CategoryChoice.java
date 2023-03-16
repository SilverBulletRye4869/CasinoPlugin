package silverassist.casinoplugin.slot.menu.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.slot.MainSystem_slot;

import java.util.List;

public class CategoryChoice {
    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final Player P;
    private final String ID;
    private final int LEVEL;
    private final YamlConfiguration YML;

    private boolean isGotoNext = false;

    public CategoryChoice(JavaPlugin plugin, MainSystem_slot mainSystem_slot, Player p, String id, int level){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem_slot;
        this.P = p;
        this.ID = id;
        this.LEVEL = level;
        this.YML = CustomConfig.getYmlByID(ID, String.valueOf(LEVEL));
        P.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,18,"§d§lカテゴリー選択");

        int i;
        for(i = 0;i<17;i++){
            String name = YML.getString(i+".name");
            if(name == null)break;
            inv.setItem(i, Util.createItem(Material.PAPER,"§e§lカテゴリー: "+name, List.of("§f比重: "+YML.getString(i+".weight"))));
        }
        inv.setItem(17,Util.createItem(Material.STRUCTURE_VOID,"§b§lはずれ",List.of("§f比重: "+YML.getString("miss.weight"))));
        if(i<17)inv.setItem(i,Util.getPlusBanner());
        Util.delayInvOpen(P,inv);
    }


    private class listener implements Listener {
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            int slot = e.getSlot();
            if(e.getCurrentItem().getType().equals(Material.YELLOW_BANNER)){
                YML.set(slot+".name",String.valueOf(slot+1));
                YML.set(slot+".constant_moeny",0);
                YML.set(slot+".multiplier",1.00);
                YML.set(slot+".broadcast",false);
                YML.set(slot+".title",false);
                YML.set(slot+".weight",1);
                YML.set(slot+".nextmode",LEVEL);
                CustomConfig.saveYmlByID(ID, String.valueOf(LEVEL));
                e.getInventory().setItem(slot,Util.createItem(Material.PAPER,"§e§lカテゴリー: "+(slot+1),List.of("§f比重: 1")));
                if(slot<16)e.getInventory().setItem(slot+1,Util.getPlusBanner());

            }else if(slot<17){
                isGotoNext = true;
                new CategoryEdit(plugin,MAIN_SYSTEM,P,ID,LEVEL,String.valueOf(slot)).open();
            }else if(slot == 17){
                isGotoNext =true;
                P.closeInventory();
                Util.sendPrefixMessage(P,"§e§lハズレの比重を設定するには以下のコマンドを実行してください");
                Util.sendPrefixMessage(P,"§a/slot edit <id>  setmissweight <level> <比重(整数)>");
                Util.sendSuggestMessage(P,"§d§l[ここをクリックで自動入力]","/slot edit "+ID+" setmissweight "+LEVEL+" ");
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;

            HandlerList.unregisterAll(this);
            if(!isGotoNext)new LevelChoice(plugin,MAIN_SYSTEM,P,ID).open();
        }

    }
}
