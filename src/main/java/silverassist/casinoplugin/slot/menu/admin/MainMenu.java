package silverassist.casinoplugin.slot.menu.admin;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.slot.MainSystem_slot;

import java.util.Map;

public class MainMenu {
    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final Player P;
    private final String ID;

    public MainMenu(JavaPlugin plugin,MainSystem_slot mainSystem_slot, Player p,String id){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem_slot;
        this.P = p;
        this.ID = id;
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,54, Util.PREFIX+"§d§l"+ID+"§a§lの編集");
        Util.invFill(inv);
        inv.setItem(10,Util.createItem(Material.NAME_TAG,"§f§lスロット名を編集"));
        inv.setItem(13,Util.createItem(Material.GOLD_INGOT,"§e§lスロット料金を編集"));
        inv.setItem(16,Util.createItem(Material.CHEST,"§6§lスロットの目を編集"));
        inv.setItem(19,Util.createItem(Material.STICK,"§c§l額縁を設定する棒を取得",null, Map.of(Enchantment.DAMAGE_ALL,1)));
        inv.setItem(22,Util.createItem(Material.CLOCK,"§e§l回転時間を調整"));
        inv.setItem(25,Util.createItem(Material.BARRIER,"§r"));
        Util.delayInvOpen(P,inv);
    }

    private class listener implements Listener {
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem()==null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            switch (e.getSlot()){
                case 10:
                    P.closeInventory();
                    Util.sendPrefixMessage(P,"§a§lスロット名を変更するには以下のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/slot edit <id> setname <スロット名>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setname ");
                    break;

                case 13:
                    P.closeInventory();
                    Util.sendPrefixMessage(P,"§a§lスロット料金を変更するには次のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/slot edit <id> setpayment <料金(整数)>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setpayment ");
                    break;

                case 16:
                    //たぶんこの先一番大変になる所
                    break;

                case 19:
                    P.closeInventory();
                    P.getInventory().addItem(MAIN_SYSTEM.FRAME_SET_STICK.apply(ID));
                    Util.sendPrefixMessage(P,"§6§l額縁設定棒§a§lを付与しました");
                    break;

                case 22:

                    P.closeInventory();
                    Util.sendPrefixMessage(P,"§a§lスロット回転時間を変更するには次のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/slot edit <id> setspintime <1つめまでの時間(整数)> <1~2の時間(整数)> <2~3の時間(整数)>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setspintime ");
                    break;
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
        }

    }
}
