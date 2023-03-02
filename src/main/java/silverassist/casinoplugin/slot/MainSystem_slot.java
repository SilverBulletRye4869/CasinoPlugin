package silverassist.casinoplugin.slot;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;

import java.util.HashMap;
import java.util.Map;

public class MainSystem_slot {
    private final JavaPlugin plugin;
    private final Map<String,Spin> SLOTS = new HashMap<>();

    public MainSystem_slot(JavaPlugin plugin){
        this.plugin = plugin;
    }

    public boolean existSlot(String id){
        return SLOTS.containsKey(id) || CustomConfig.existYml(id,"system");
    }

    public Spin getSlot(String id){
        if(!SLOTS.containsKey(id)){
            if(!CustomConfig.existYml(id,"system"))return null;
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
