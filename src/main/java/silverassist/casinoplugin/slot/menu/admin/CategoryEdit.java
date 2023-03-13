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
import java.util.function.BiFunction;

public class CategoryEdit {
    private static final BiFunction<String,Boolean, ItemStack> boolItem = (name, isEnable)-> Util.createItem(isEnable ? Material.LIME_CONCRETE : Material.RED_CONCRETE,"§b§l"+name+": "+(isEnable ? "§a§l有効" : "§c§l無効"));

    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final Player P;
    private final String ID;
    private final int LEVEL;
    private final String CATEGORY;
    private final YamlConfiguration YML;

    private boolean isGotoNext = false;
    private boolean isDelete = false;

    public CategoryEdit(JavaPlugin plugin, MainSystem_slot mainSystem_slot, Player p, String id, int level,String category){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem_slot;
        this.P = p;
        this.ID = id;
        this.LEVEL = level;
        this.CATEGORY = category;
        this.YML = CustomConfig.getYmlByID(id,String.valueOf(level));
        P.closeInventory();
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,45, Util.PREFIX+"§d§l"+ID+"-"+LEVEL+"の編集");

        for(int i = 0;i<18;i++){
            if(YML.get(CATEGORY+".display."+i)==null)break;
            inv.setItem(i,YML.getItemStack(CATEGORY+"."+i));
        }
        inv.setItem(18,Util.createItem(Material.GOLD_INGOT,"§6§l固定報酬を設定", List.of("§e§l現在: "+YML.getInt(CATEGORY+".constant_money",0),"§c0で無効化")));
        inv.setItem(19,Util.createItem(Material.EMERALD,"§a§l倍率を設定",List.of("§e§l現在: "+YML.getDouble(CATEGORY+".multiplier",1.0),"§c§l固定報酬有効時無効")));
        inv.setItem(20,boolItem.apply("当選時ブロードキャスト",YML.getBoolean(CATEGORY+".broadcast",false)));
        inv.setItem(21,boolItem.apply("当選時タイトル",YML.getBoolean(CATEGORY+".title",false)));
        inv.setItem(22,Util.createItem(Material.NAME_TAG,"§f§lカテゴリ名変更"));
        inv.setItem(23,Util.createItem(Material.END_PORTAL_FRAME,"§a§lモード遷移先を変更",List.of("§f§l現在: "+(YML.getInt(CATEGORY+".nextmode")+1))));
        inv.setItem(24,Util.createItem(Material.DROPPER,"§7§l比重を設定",List.of("§f§l現在: "+YML.getInt(CATEGORY+".weight",1))));
        inv.setItem(25,Util.createItem(Material.LIGHT_BLUE_STAINED_GLASS_PANE,"§b§l当選時アイテム⇒",List.of("§fこのカテゴリの目が出たときに","§f貰えるアイテム")));
        inv.setItem(26,YML.getItemStack(CATEGORY+".item"));
        for(int i = 27;i<44;i++)inv.setItem(i,Util.createItem(Material.GRAY_STAINED_GLASS_PANE,"§r"));
        inv.setItem(44,Util.createItem(Material.LAVA_BUCKET,"§c§lこのカテゴリを削除"));


        Util.delayInvOpen(P,inv);


    }

    private class listener implements Listener {
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            int slot = e.getSlot();
            if(slot>17 && slot<45 && slot !=26){
                e.setCancelled(true);
                switch (slot){
                    case 18:
                        isGotoNext = true;
                        P.closeInventory();
                        Util.sendPrefixMessage(P,"§e§l固定報酬を変更するには以下のコマンドを実行してください");
                        Util.sendPrefixMessage(P,"§a/slot edit <id> setconstantmoney <level> <category-id> <固定報酬の額(整数)>");
                        Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setconstantmoney "+LEVEL+" "+CATEGORY+" ");
                        break;

                    case 19:
                        isGotoNext = true;
                        P.closeInventory();
                        Util.sendPrefixMessage(P,"§e§l倍率を設定するには以下のコマンドを実行してください");
                        Util.sendPrefixMessage(P,"§a/slot edit <id> setmultiplier <level> <category-id> <倍率(正の実数)>");
                        Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setmultiplier "+LEVEL+" "+CATEGORY+" ");
                        break;

                    case 20:
                    case 21:
                        boolean toEnable = e.getCurrentItem().getType().equals(Material.RED_CONCRETE);
                        YML.set(CATEGORY+(slot == 20 ? ".broadcast" : ".title"),toEnable);
                        e.getInventory().setItem(slot,boolItem.apply(slot ==20 ? "当選時ブロードキャスト" : "当選時タイトル",toEnable));
                        CustomConfig.saveYmlByID(ID,String.valueOf(LEVEL));
                        break;

                    case 22:
                        isGotoNext = true;
                        P.closeInventory();
                        Util.sendPrefixMessage(P,"§e§lカテゴリ名を変更するには以下のコマンドを実行してください");
                        Util.sendPrefixMessage(P,"§a/slot edit <id> setcategoryname <level> <category-id> <名前>");
                        Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setcategoryname "+LEVEL+" "+CATEGORY+" ");
                        break;

                    case 23:
                        isGotoNext = true;
                        P.closeInventory();
                        Util.sendPrefixMessage(P,"§e§l遷移先を変更するには以下のコマンドを実行してください");
                        Util.sendPrefixMessage(P,"§a/slot edit <id> setnextmode <level> <category-id> <次のレベル>");
                        Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setnextmode "+LEVEL+" "+CATEGORY+" ");
                        break;

                    case 24:
                        isGotoNext = true;
                        P.closeInventory();
                        Util.sendPrefixMessage(P,"§e§l比重を設定するには以下のコマンドを実行してください");
                        Util.sendPrefixMessage(P,"§a/slot edit <id> setweight <level> <category-id> <比重(整数)>");
                        Util.sendSuggestMessage(P,"§d§l[ここをクリックして自動入力]","/slot edit "+ID+" setweight "+LEVEL+" "+CATEGORY+" ");
                        break;
                    case 44:
                        YML.set(CATEGORY,null);
                        isDelete = true;
                        CustomConfig.saveYmlByID(ID,String.valueOf(LEVEL));
                        P.closeInventory();
                        break;
                }
            }

        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);

            if(!isDelete) {
                Inventory inv = e.getInventory();
                int cnt = 0;
                for (int i = 0; i < 18; i++) {
                    ItemStack item = inv.getItem(i);
                    if (item != null) {
                        YML.set(CATEGORY + ".display." + cnt, item);
                        cnt++;
                    }

                }
                YML.set(CATEGORY + ".item", inv.getItem(26));
                CustomConfig.saveYmlByID(ID, String.valueOf(LEVEL));
            }

            if(!isGotoNext)new CategoryChoice(plugin,MAIN_SYSTEM,P,ID,LEVEL).open();

        }
    }
}
