package silverassist.casinoplugin.slot.menu.admin;

import com.sun.tools.javac.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CasinoPlugin;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.slot.MainSystem_slot;

import java.io.File;
import java.util.List;

public class SlotList {
    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final Player P;
    private final List<String> fileNames;

    private int page;

    public SlotList(JavaPlugin plugin, MainSystem_slot mainSystem_slot, Player p){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem_slot;
        this.P = p;
        fileNames = mainSystem_slot.getSlotList();
        System.out.println( mainSystem_slot.getSlotList());
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){open(0);}
    public void open(int page){
        this.page = page;
        Inventory inv = Bukkit.createInventory(P,54,"§d§lスロットリスト");
        for(int i = 45*page; i<Math.min(45*(page+1),fileNames.size());i++)inv.setItem(i%45, Util.createItem(Material.PAPER,"§6§l"+fileNames.get(i)));
        for(int i=45;i<54;i++)inv.setItem(i,Util.GUI_BG);
        if(page>0)inv.setItem(45,Util.createItem(Material.RED_STAINED_GLASS_PANE,"§c§l前へ"));
        if(page < (fileNames.size()-1)/45)inv.setItem(53,Util.createItem(Material.LIME_STAINED_GLASS_PANE,"§a§l次へ"));
        unregisterCancel =false;
        Util.delayInvOpen(P,inv);
    }


    private boolean unregisterCancel = false;
    private class listener implements Listener {
        @EventHandler
        public void onInventoryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem()==null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            int slot = e.getSlot();
            if(slot < 45){
                String id = e.getCurrentItem().getItemMeta().getDisplayName().replace("§6§l","");
                new MainMenu(plugin,MAIN_SYSTEM,P,id).open();
            }else if(e.getCurrentItem().getType().equals(Material.RED_STAINED_GLASS_PANE)){
                unregisterCancel = true;
                open(--page);
            }else if(e.getCurrentItem().getType().equals(Material.LIME_STAINED_GLASS_PANE)){
                unregisterCancel = true;
                open(++page);
            }
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()) || unregisterCancel)return;
            HandlerList.unregisterAll(this);
        }
    }
}
