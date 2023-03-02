package silverassist.casinoplugin.slot;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Spin {
    private static final int FRAME_COUNT = 3;

    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final String ID;
    private final List<ItemStack> DISPLAY_ITEMS = new ArrayList<>();
    private final LinkedHashMap<Integer,String> PROBABILITY = new LinkedHashMap<>();
    private final HashMap<String,ItemStack[]> CATEGORY_CONTAIN_ITEMS = new HashMap<>();
    private final List<UUID> ITEM_FRAMES;
    private final int[] SPIN_TIME = new int[FRAME_COUNT];

    private int categoryRandomMax = 0;

    private String mode = "1";
    public Spin(JavaPlugin plugin, MainSystem_slot mainSystem, String id){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem;
        this.ID = id;
        YamlConfiguration yml = CustomConfig.getYmlByID(id,"system");
        this.mode = yml.getString("nowMode");
        ITEM_FRAMES = yml.getStringList("itemframes").stream().map(UUID::fromString).collect(Collectors.toList());
        for(int i = 0;i<FRAME_COUNT;i++)SPIN_TIME[i] = (i==0 ? 0 : SPIN_TIME[i-1]) + yml.getInt("spintime."+i);
        setMode(mode);
    }

    public boolean run(Player p){
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{
            ItemStack memo = null, bingoItem = null;
            int bingoNum = (int) (Math.random() * categoryRandomMax);
            String category = null;
            for(int i : PROBABILITY.keySet()){
                if(bingoNum < i){
                    category=PROBABILITY.get(i);
                    if(category!="miss")bingoItem=(ItemStack) Util.getRandomIndex(CATEGORY_CONTAIN_ITEMS.get(category));
                    break;
                }
            }

            int fin=0;
            for(int i = 1;i<=SPIN_TIME[FRAME_COUNT-1];i++){
               for(int j=fin;j<FRAME_COUNT;i++) Util.setItemFrame(ITEM_FRAMES.get(j), DISPLAY_ITEMS.get((int)(Math.random() * DISPLAY_ITEMS.size())));
               if(i==SPIN_TIME[0]){
                   fin++;
                   if(category!="miss")Util.setItemFrame(ITEM_FRAMES.get(0),bingoItem);
               }
               else if(i==SPIN_TIME[1]){
                   fin++;
                   if(category!="miss")Util.setItemFrame(ITEM_FRAMES.get(1),bingoItem);
                   else memo = Util.getItemFrame(ITEM_FRAMES.get(1));
               }else if(i==SPIN_TIME[2]){
                   if(category!="miss")Util.setItemFrame(ITEM_FRAMES.get(1),bingoItem);
                   else{
                       List<ItemStack> dispItems = new ArrayList<>(DISPLAY_ITEMS);
                       dispItems.remove(memo);
                       Util.setItemFrame(ITEM_FRAMES.get(2),dispItems.get((int)(Math.random() * dispItems.size())));
                   }
               }
            }
        });
        return true;
    }

    public void setMode(String mode){
        DISPLAY_ITEMS.clear();
        PROBABILITY.clear();
        YamlConfiguration yml = CustomConfig.getYmlByID(ID,mode);
        AtomicInteger now = new AtomicInteger(0);
        yml.getKeys(false).forEach(e->{
            int size = yml.getConfigurationSection(e).getKeys(false).size();
            ItemStack[] itemStacks = new ItemStack[size];
            for(int i = 0;i<size;i++){
                ItemStack item = yml.getItemStack(ID+"."+i);
                if(item == null)break;
                DISPLAY_ITEMS.add(item);
                itemStacks[i] = item;
            }
            PROBABILITY.put(now.addAndGet(yml.getInt(e+".weight")),e);
            CATEGORY_CONTAIN_ITEMS.put(e,itemStacks);
        });
        PROBABILITY.put(now.addAndGet(yml.getInt("miss.weight")),"miss");
        categoryRandomMax = now.get();
    }

}
