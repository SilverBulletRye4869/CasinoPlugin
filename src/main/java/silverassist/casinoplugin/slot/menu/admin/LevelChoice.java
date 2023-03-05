package silverassist.casinoplugin.slot.menu.admin;

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
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.slot.MainSystem_slot;

public class LevelChoice {
    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final Player P;
    private final String ID;

    public LevelChoice(JavaPlugin plugin, MainSystem_slot mainSystem_slot, Player p,String id){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem_slot;
        this.P = p;
        this.ID = id;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public void open(){
        Inventory inv = Bukkit.createInventory(P,54,"§d§lレベル選択");
        Util.invFill(inv);
        for(int i=0;i<7;i++)inv.setItem(10+i,Util.createItem(Material.DROPPER,"§6§lレベル: "+(i+1)));
        Util.delayInvOpen(P,inv);
    }

    private class listener implements Listener {
        @EventHandler
        public void onInvenotryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;
            e.setCancelled(true);
            int slot = e.getSlot();
            if(slot>9 && slot<17)new CategoryChoice(plugin,MAIN_SYSTEM,P,ID,slot-10);
        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;
            HandlerList.unregisterAll(this);
            new MainMenu(plugin,MAIN_SYSTEM,P,ID).open();
        }
    }
}
