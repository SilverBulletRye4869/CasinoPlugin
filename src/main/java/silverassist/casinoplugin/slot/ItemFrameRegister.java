package silverassist.casinoplugin.slot;

import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;
import java.util.List;

public class ItemFrameRegister implements Listener {
    public static final int ITEM_FRAME_COUNT = 3;

    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;

    public ItemFrameRegister(JavaPlugin plugin,MainSystem_slot mainSystem){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onItemFrameClick(PlayerInteractEntityEvent e){
        Entity entity = e.getRightClicked();
        if(!e.getPlayer().isOp() || entity == null || !(entity instanceof ItemFrame))return;
        String uuidStr = entity.getUniqueId().toString();
        ItemFrame itemFrame = (ItemFrame) entity;
        itemFrame.setFixed(true);
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta;
        if(!item.hasItemMeta() || !(meta = item.getItemMeta()).hasLore())return;
        String id = meta.getLore().get(0).substring(6);
        if(!MAIN_SYSTEM.existSlot(id))return;
        e.setCancelled(true);
        List<String> itemframes = CustomConfig.getYmlByID(id).getStringList("itemframes");
        if(itemframes.contains(uuidStr))return;
        itemframes.add(uuidStr);
        if(itemframes.size()>ITEM_FRAME_COUNT)itemframes = itemframes.subList(itemframes.size()-ITEM_FRAME_COUNT,itemframes.size());

        CustomConfig.getYmlByID(id).set("itemframes",itemframes);
        CustomConfig.saveYmlByID(id);
    }

}
