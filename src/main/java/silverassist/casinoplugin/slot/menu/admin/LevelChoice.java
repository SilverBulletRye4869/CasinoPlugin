package silverassist.casinoplugin.slot.menu.admin;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.plugin.java.JavaPlugin;
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

    }

    private class listener implements Listener {
        @EventHandler
        public void onInvenotryClick(InventoryClickEvent e){
            if(!P.equals(e.getWhoClicked()) || e.getCurrentItem() == null || !e.getClickedInventory().getType().equals(InventoryType.CHEST))return;

        }

        @EventHandler
        public void onInventoryClose(InventoryCloseEvent e){
            if(!P.equals(e.getPlayer()))return;

            HandlerList.unregisterAll(this);
        }
    }
}
