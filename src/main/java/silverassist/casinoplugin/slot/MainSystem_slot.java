package silverassist.casinoplugin.slot;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class MainSystem_slot {
    public final Function<String,ItemStack> FRAME_SET_STICK = id -> Util.createItem(Material.STICK,"§6§l額縁編集棒", List.of("§fid: "+id,"§e§l額縁に対して右クリック！"),Map.of(Enchantment.DAMAGE_ALL,1));
    private final JavaPlugin plugin;
    private final Map<String,Spin> SLOTS = new HashMap<>();

    public MainSystem_slot(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public boolean existSlot(String id){
        return SLOTS.containsKey(id) || CustomConfig.existYml(id);
    }

    public Spin getSlot(String id){
        if(!SLOTS.containsKey(id)){
            if(!CustomConfig.existYml(id))return null;
            SLOTS.put(id,new Spin(plugin,this,id));
        }
        return SLOTS.get(id);
    }

    public boolean spin(String id, Player p){
        if(!SLOTS.containsKey(id))return false;
        boolean result = SLOTS.get(id).run(p);
        return result;
    }
}
