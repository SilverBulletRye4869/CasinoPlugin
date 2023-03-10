package silverassist.casinoplugin.slot;

import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;

import java.util.List;

public class ItemFrameRegister implements Listener {
    public static final int ITEM_FRAME_COUNT = 3;

    private final JavaPlugin plugin;

    public ItemFrameRegister(JavaPlugin plugin){
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this,plugin);
    }

    @EventHandler
    public void onItemFrameClick(PlayerInteractEvent e){
        if(!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))return;
        if(!e.getClickedBlock().getType().equals(Material.ITEM_FRAME))return;
        ItemFrame itemFrame = (ItemFrame) e.getClickedBlock().getState();
        String uuidStr = itemFrame.getUniqueId().toString();
        ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
        ItemMeta meta;
        if(!item.hasItemMeta() || !(meta = item.getItemMeta()).hasLore())return;
        String id = meta.getLore().get(0).substring(6);
        List<String> itemframes = CustomConfig.getYmlByID(id).getStringList("itemframes");
        itemframes.add(uuidStr);
        if(itemframes.size()>ITEM_FRAME_COUNT)itemframes.subList(itemframes.size()-ITEM_FRAME_COUNT,itemframes.size());
        CustomConfig.getYmlByID(id).set("itemframes",itemframes);
        CustomConfig.saveYmlByID(id);
    }

}
