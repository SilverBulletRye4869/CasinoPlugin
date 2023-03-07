package silverassist.casinoplugin.slot;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CasinoPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.Vault;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Spin {
    private static final int FRAME_COUNT = 3;

    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final String ID;
    private final String NAME;
    private final YamlConfiguration SYSTEM_YML;
    private final List<ItemStack> DISPLAY_ITEMS = new ArrayList<>();
    private final LinkedHashMap<Integer,String> PROBABILITY = new LinkedHashMap<>();
    private final HashMap<String,ItemStack[]> CATEGORY_CONTAIN_ITEMS = new HashMap<>();
    private final List<UUID> ITEM_FRAMES;
    private final int[] SPIN_TIME = new int[FRAME_COUNT];
    private final int STOCK_PER_SPIN;
    private final int PAYMENT;
    private final HashMap<String,Integer> CONSTANT_MONEY_BY_CATEGORY = new HashMap<>();
    private final HashMap<String,Double> MULTIPLIER_BY_CATEGORY = new HashMap<>();
    private final HashMap<String,ItemStack> GAVE_ITEM_BY_CATEGORY = new HashMap<>();
    private final Set<String> BROADCAST = new HashSet<>();
    private final Set<String> TITLE = new HashSet<>();
    private final HashMap<String, String> NEXT_MODE=new HashMap<>();
    private final Location SIGN_POS;

    private boolean isSpinning = false;
    private int categoryRandomMax = 0;

    private String mode = "1";
    public Spin(JavaPlugin plugin, MainSystem_slot mainSystem, String id){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem;
        this.ID = id;
        SYSTEM_YML = CustomConfig.getYmlByID(id);
        this.NAME = SYSTEM_YML.getString("name",ID);
        this.mode = SYSTEM_YML.getString("nowMode","1");
        ITEM_FRAMES = SYSTEM_YML.getStringList("itemframes").stream().map(UUID::fromString).collect(Collectors.toList());
        for(int i = 0;i<FRAME_COUNT;i++)SPIN_TIME[i] = (i==0 ? 0 : SPIN_TIME[i-1]) + SYSTEM_YML.getInt("spintime."+i);
        STOCK_PER_SPIN = SYSTEM_YML.getInt("stock_per_spin",0);
        PAYMENT = SYSTEM_YML.getInt("payment",100);
        SIGN_POS = SYSTEM_YML.getLocation("sign");
        setMode(mode);
    }

    public boolean run(Player p){
        if(isSpinning || PROBABILITY.keySet().size() == 0)return false;
        isSpinning = true;
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{
            Vault.getEconomy().withdrawPlayer(p,PAYMENT);
            int nowStock = getAndUpdateStock(true);

            ItemStack memo = null, bingoItem = null;
            int bingoNum = (int) (Math.random() * categoryRandomMax);
            String category = null;
            for(int i : PROBABILITY.keySet()){
                if(bingoNum < i){
                    category=PROBABILITY.get(i);
                    if(!category.equals("miss"))bingoItem=(ItemStack) Util.getRandomIndex(CATEGORY_CONTAIN_ITEMS.get(category));
                    break;
                }
            }

            int fin=0;
            for(int i = 1;i<=SPIN_TIME[FRAME_COUNT-1];i++){
               for(int j=fin;j<FRAME_COUNT;i++) Util.setItemFrame(ITEM_FRAMES.get(j), DISPLAY_ITEMS.get((int)(Math.random() * DISPLAY_ITEMS.size())));
               if(i==SPIN_TIME[0]){
                   fin++;
                   if(!category.equals("miss"))Util.setItemFrame(ITEM_FRAMES.get(0),bingoItem);
               }
               else if(i==SPIN_TIME[1]){
                   fin++;
                   if(!category.equals("miss"))Util.setItemFrame(ITEM_FRAMES.get(1),bingoItem);
                   else memo = Util.getItemFrame(ITEM_FRAMES.get(1));
               }else if(i==SPIN_TIME[2]){
                   if(!category.equals("miss"))Util.setItemFrame(ITEM_FRAMES.get(1),bingoItem);
                   else{
                       List<ItemStack> dispItems = new ArrayList<>(DISPLAY_ITEMS);
                       dispItems.remove(memo);
                       Util.setItemFrame(ITEM_FRAMES.get(2),dispItems.get((int)(Math.random() * dispItems.size())));
                   }
               }
                try {
                    Thread.sleep(50*5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(category.equals("miss"))Util.sendPrefixMessage(p,"§c§lはずれました...");
            else{
                int wonMoney = CONSTANT_MONEY_BY_CATEGORY.get(category);
                if(wonMoney==0)wonMoney=(int)(nowStock * MULTIPLIER_BY_CATEGORY.get(category));
                String bingoType = (bingoItem.getItemMeta().hasDisplayName() ? bingoItem.getItemMeta().getDisplayName() : bingoItem.getType().toString());
                Util.sendPrefixMessage(p,"§a§lおめでとうございます！！§d§l『"+bingoType+"』§a§l揃いです！");
                Vault.getEconomy().depositPlayer(p,wonMoney);
                if(GAVE_ITEM_BY_CATEGORY.containsKey(category))p.getInventory().addItem(GAVE_ITEM_BY_CATEGORY.get(category));
                if(BROADCAST.contains(category))Util.broadcast("§b§l"+p.getName()+"§a§lが§d§l『"+NAME+"』§a§lで、§6§l"+bingoType+"揃い§a§lにより§e§l"+wonMoney+"円§a§l獲得！！");
                if(TITLE.contains(category))Util.title("§c§l"+NAME+"§6§lで当選！","§b§l"+p.getName()+"§a§lが§e§l"+wonMoney+"円§a§l獲得！！");
                SYSTEM_YML.set("stock",SYSTEM_YML.getInt("def_stock",0));
                getAndUpdateStock(false);
                CustomConfig.saveYmlByID(ID);
                setMode(NEXT_MODE.get(category));
            }
            isSpinning = false;
        });
        return true;
    }

    public void setMode(String mode){setMode(this,mode);}
    public static void setMode(Spin Slot,String mode){
        if(Slot.mode.equals(mode))return;
        Slot.DISPLAY_ITEMS.clear();
        Slot.PROBABILITY.clear();
        Slot.MULTIPLIER_BY_CATEGORY.clear();
        Slot.GAVE_ITEM_BY_CATEGORY.clear();
        Slot.CATEGORY_CONTAIN_ITEMS.clear();
        Slot.BROADCAST.clear();Slot.TITLE.clear();

        YamlConfiguration yml = CustomConfig.getYmlByID(Slot.ID,mode);
        AtomicInteger now = new AtomicInteger(0);
        yml.getKeys(false).forEach(e->{
            int size = yml.getConfigurationSection(e).getKeys(false).size();
            ItemStack[] itemStacks = new ItemStack[size];
            for(int i = 0;i<size;i++){
                ItemStack item = yml.getItemStack(e+"."+i);
                if(item == null){
                    if(i==0)return;
                    else break;
                }
                Slot.DISPLAY_ITEMS.add(item);
                itemStacks[i] = item;
            }
            Slot.CONSTANT_MONEY_BY_CATEGORY.put(e,yml.getInt(e+".constant_money",0));
            Slot.MULTIPLIER_BY_CATEGORY.put(e,yml.getDouble(e+".multiplier",1.0));
            if(yml.get(e+".item")!=null)Slot.GAVE_ITEM_BY_CATEGORY.put(e,yml.getItemStack(e+".item"));
            if(yml.getBoolean(e+".broadcast",false))Slot.BROADCAST.add(e);
            if(yml.getBoolean(e+".title",false))Slot.TITLE.add(e);
            Slot.PROBABILITY.put(now.addAndGet(yml.getInt(e+".weight")),e);
            Slot.CATEGORY_CONTAIN_ITEMS.put(e,itemStacks);
            Slot.NEXT_MODE.put(e,yml.getString(e+".nextmode"));
        });
        Slot.PROBABILITY.put(now.addAndGet(yml.getInt("miss.weight")),"miss");
        Slot.categoryRandomMax = now.get();
    }


    public int getAndUpdateStock(boolean toAdd){
        int nowStock = SYSTEM_YML.getInt("stock",SYSTEM_YML.getInt("def_stock",0)) + (toAdd ? STOCK_PER_SPIN : 0);
        SYSTEM_YML.set("stock",nowStock);
        CustomConfig.saveYmlByID(ID);

        MAIN_SYSTEM.getSignSystem().update(SIGN_POS,nowStock);


        return nowStock;
    }

    public boolean canSpin(Player p){
        return Vault.getEconomy().getBalance(p) >= PAYMENT;
    }


}
