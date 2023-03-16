package silverassist.casinoplugin.slot;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CasinoPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MainSystem_slot {
    public final Function<String,ItemStack> FRAME_SET_STICK = id -> Util.createItem(Material.STICK,"§6§l額縁編集棒", List.of("§fid: "+id,"§e§l額縁に対して右クリック！"),Map.of(Enchantment.DAMAGE_ALL,1));
    private final JavaPlugin plugin;
    private final Map<String,Spin> SLOTS = new HashMap<>();
    private final SlotSign SIGN_SYSTEM;

    public MainSystem_slot(JavaPlugin plugin){
        this.plugin = plugin;
        this.SIGN_SYSTEM = new SlotSign(plugin,this);
        new ItemFrameRegister(plugin,this);
    }

    public boolean existSlot(String id){
        return SLOTS.containsKey(id) || Files.exists(Paths.get(plugin.getDataFolder()+"/slot/data/"+id));
    }

    public Spin getSlot(String id){
        if(!SLOTS.containsKey(id)){
            if(!CustomConfig.existYml(id))return null;
            SLOTS.put(id,new Spin(plugin,this,id));
        }
        return SLOTS.get(id);
    }

    public void deleteSlot(String id){
        SLOTS.remove(id);
        if(CustomConfig.getYmlByID(id).get("sign")!=null){
            CustomConfig.getYmlByID(id).getLocation("sign").getBlock().setType(Material.AIR);
        }
        CustomConfig.deleteYmlByID(id);
        Util.deleteDirectory(new File(CasinoPlugin.getInstance().getDataFolder()+"/slot/data/"+id));
    }

    public List<String> getSlotList(){return getSlotList("");}
    public List<String> getSlotList(String startRegex){
        Stream<Path> stream;
        try {
            stream = Files.list(Paths.get(plugin.getDataFolder().getPath()+"/slot/data"));
        } catch (IOException e) {
            System.err.println("dataフォルダの取得に失敗しました");
            e.printStackTrace();
            return null;
        }
        return stream.map(e->e.getFileName().toString()).filter(g->g.matches("^"+startRegex+".*")).collect(Collectors.toList());
    }

    public boolean reloadSlot(String id){
        if(!existSlot(id))return false;
        SLOTS.put(id,new Spin(plugin,this,id));
        return true;
    }


    public boolean spin(String id, Player p){
        if(!SLOTS.containsKey(id)){
            if(!reloadSlot(id))return false;
        }
        boolean result = SLOTS.get(id).run(p);
        return result;
    }

    public SlotSign getSignSystem(){return SIGN_SYSTEM;}
}
