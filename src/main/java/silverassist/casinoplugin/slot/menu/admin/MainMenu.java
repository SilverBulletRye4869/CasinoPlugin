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
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,45, Util.PREFIX+"§d§l"+ID+"§a§lの編集");
        Util.invFill(inv);
        inv.setItem(10,Util.createItem(Material.NAME_TAG,"§f§lスロット名を編集"));
        inv.setItem(12,Util.createItem(Material.GOLD_INGOT,"§e§lスロット料金を編集"));
        inv.setItem(14,Util.createItem(Material.CHEST,"§6§lスロットの目を編集"));
        inv.setItem(16,Util.createItem(Material.STICK,"§c§l額縁を設定する棒を取得",null, Map.of(Enchantment.DAMAGE_ALL,1)));
        inv.setItem(18,Util.createItem(Material.CLOCK,"§e§l回転時間を調整"));
        inv.setItem(30,Util.createItem(Material.DIAMOND_BLOCK,"§b§l初期ストックを変更"));
        inv.setItem(32,Util.createItem(Material.EMERALD_BLOCK,"§b§l回転毎のストックを変更"));
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

                case 12:
                    P.closeInventory();
                    Util.sendPrefixMessage(P,"§a§lスロット料金を変更するには次のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/slot edit <id> setpayment <料金(整数)>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setpayment ");
                    break;

                case 14:
                    //たぶんこの先一番大変になる所
                    new LevelChoice(plugin,MAIN_SYSTEM,P,ID).open();
                    break;

                case 16:
                    P.closeInventory();
                    P.getInventory().addItem(MAIN_SYSTEM.FRAME_SET_STICK.apply(ID));
                    Util.sendPrefixMessage(P,"§6§l額縁設定棒§a§lを付与しました");
                    break;

                case 28:

                    P.closeInventory();
                    Util.sendPrefixMessage(P,"§a§lスロット回転時間を変更するには次のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/slot edit <id> setspintime <1つめまでの時間(整数)> <1~2の時間(整数)> <2~3の時間(整数)>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setspintime ");
                    break;

                case 30:
                    P.closeInventory();
                    Util.sendPrefixMessage(P,"§a§lスロットの初期ストックを変更するには次のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/slot edit <id> setdefaultstock <初期ストック(整数)>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setdefaultstock ");
                    break;

                case 32:
                    P.closeInventory();
                    Util.sendPrefixMessage(P,"§a§l回転毎のストック貯蔵量を変更するには次のコマンドを実行してください");
                    Util.sendPrefixMessage(P,"§e/slot edit <id> setstock <ストック量(整数)>");
                    Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setstock ");
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
