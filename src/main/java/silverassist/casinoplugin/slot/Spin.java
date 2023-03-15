package silverassist.casinoplugin.slot;

import org.bukkit.Bukkit;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.Vault;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Spin {
    private static final int ITEM_FRAME_COUNT = ItemFrameRegister.ITEM_FRAME_COUNT;
    private static final int SPIN_TICK = 5;

    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;
    private final String ID;
    private final String NAME;
    private final YamlConfiguration SYSTEM_YML;
    private final List<ItemStack> DISPLAY_ITEMS = new ArrayList<>();
    private final LinkedHashMap<Integer,String> PROBABILITY = new LinkedHashMap<>();
    private final HashMap<String,ItemStack[]> CATEGORY_CONTAIN_ITEMS = new HashMap<>();
    private final List<ItemFrame> ITEM_FRAMES;
    private final int[] SPIN_TIME = new int[ITEM_FRAME_COUNT];
    private final int DEFAULT_STOCK;
    private final int STOCK_PER_SPIN;
    private final int PAYMENT;
    private final HashMap<String,Integer> CONSTANT_MONEY_BY_CATEGORY = new HashMap<>();
    private final HashMap<String,Double> MULTIPLIER_BY_CATEGORY = new HashMap<>();
    private final HashMap<String,ItemStack> GAVE_ITEM_BY_CATEGORY = new HashMap<>();
    private final Set<String> BROADCAST = new HashSet<>();
    private final Set<String> TITLE = new HashSet<>();
    private final HashMap<String, String> NEXT_MODE=new HashMap<>();
    private final Sign SIGN;

    private boolean isSpinning = false;
    private int categoryRandomMax = 0;

    private String mode = "-1";
    public Spin(JavaPlugin plugin, MainSystem_slot mainSystem, String id){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem;
        this.ID = id;
        SYSTEM_YML = CustomConfig.getYmlByID(id);
        this.NAME = SYSTEM_YML.getString("name",ID);
        String nowmode = SYSTEM_YML.getString("nowmode","0");
        setMode(nowmode);
        this.mode = nowmode;
        ITEM_FRAMES = SYSTEM_YML.getStringList("itemframes").stream().map(UUID::fromString).map(g->(ItemFrame)Bukkit.getEntity(g)).collect(Collectors.toList());
        for(int i = 0; i< ITEM_FRAME_COUNT; i++)SPIN_TIME[i] = (i==0 ? 0 : SPIN_TIME[i-1]) + SYSTEM_YML.getInt("spintime."+(i+1));
        DEFAULT_STOCK = SYSTEM_YML.getInt("def_stock",0);
        STOCK_PER_SPIN = SYSTEM_YML.getInt("stock_per_spin",0);
        PAYMENT = SYSTEM_YML.getInt("payment",100);
        SIGN = (Sign)SYSTEM_YML.getLocation("sign").getBlock().getState();
    }

    public boolean run(Player p){
        if(PROBABILITY.keySet().size() == 0 || ITEM_FRAMES.size() < ITEM_FRAME_COUNT)return false;
        if(isSpinning){
            Util.sendPrefixMessage(p,"§c§lこのスロットは現在まわっています");
            return true;
        }
        isSpinning = true;

        Vault.getEconomy().withdrawPlayer(p,PAYMENT);

        ItemStack bingoItem=null;
        int bingoNum = (int) (Math.random() * categoryRandomMax);
        String category=null;
        for(int i : PROBABILITY.keySet()){
            if(bingoNum < i){
                category=PROBABILITY.get(i);
                if(!category.equals("miss"))bingoItem=(ItemStack) Util.getRandomIndex(CATEGORY_CONTAIN_ITEMS.get(category));
                break;
            }
        }
        int nowStock = getAndUpdateStock(true, !category.equals("miss"));

        final String category_f = category;
        final ItemStack bingoItem_f = bingoItem;
        Bukkit.getScheduler().runTaskAsynchronously(plugin,()->{;
        ItemStack memo=null;

            int fin=0;
            for(int i = 1; i<=SPIN_TIME[ITEM_FRAME_COUNT -1]; i++){
               for(int j = fin; j< ITEM_FRAME_COUNT; j++){
                   ITEM_FRAMES.get(j).setItem(DISPLAY_ITEMS.get((int)(Math.random() * DISPLAY_ITEMS.size())));}

               if(i==SPIN_TIME[0]){
                   fin++;
                   if(!category_f.equals("miss"))ITEM_FRAMES.get(0).setItem(bingoItem_f);
               }
               else if(i==SPIN_TIME[1]){
                   fin++;
                   if(!category_f.equals("miss"))ITEM_FRAMES.get(1).setItem(bingoItem_f);
                   else memo = ITEM_FRAMES.get(1).getItem();
               }else if(i==SPIN_TIME[2]){
                   if(!category_f.equals("miss"))ITEM_FRAMES.get(2).setItem(bingoItem_f);
                   else{
                       List<ItemStack> dispItems = new ArrayList<>(DISPLAY_ITEMS);
                       dispItems.remove(memo);
                       ITEM_FRAMES.get(2).setItem(dispItems.get((int)(Math.random() * dispItems.size())));
                   }
               }
                try {
                    Thread.sleep(50*SPIN_TICK);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            if(category_f.equals("miss"))Util.sendPrefixMessage(p,"§c§lはずれました...");
            else{
                int wonMoney = CONSTANT_MONEY_BY_CATEGORY.get(category_f);
                if(wonMoney==0)wonMoney=(int)(nowStock * MULTIPLIER_BY_CATEGORY.get(category_f));
                String bingoType = (bingoItem_f.getItemMeta().hasDisplayName() ? bingoItem_f.getItemMeta().getDisplayName() : bingoItem_f.getType().toString());
                Util.sendPrefixMessage(p,"§a§lおめでとうございます！！§d§l『"+bingoType+"』§a§l揃いです！");
                Util.sendPrefixMessage(p,"§6"+wonMoney+"円§eを獲得しました。");
                Vault.getEconomy().depositPlayer(p,wonMoney);
                if(GAVE_ITEM_BY_CATEGORY.containsKey(category_f))p.getInventory().addItem(GAVE_ITEM_BY_CATEGORY.get(category_f));
                if(BROADCAST.contains(category_f))Util.broadcast("§b§l"+p.getName()+"§a§lが§d§l『"+NAME+"』§a§lで、§6§l"+bingoType+"揃い§a§lにより§e§l"+wonMoney+"円§a§l獲得！！");
                if(TITLE.contains(category_f))Util.title("§c§l"+NAME+"§6§lで当選！","§b§l"+p.getName()+"§a§lが§e§l"+wonMoney+"円§a§l獲得！！");
                SYSTEM_YML.set("stock",DEFAULT_STOCK);
                CustomConfig.saveYmlByID(ID);
                setMode(NEXT_MODE.get(category_f));
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
        yml.getKeys(false).stream().filter(g->!g.equals("miss")).forEach(e->{
            int weight = yml.getInt(e+".weight");
            if(weight == 0)return;
            Slot.PROBABILITY.put(now.addAndGet(weight),e);
            int size = yml.getConfigurationSection(e+".display").getKeys(false).size();
            ItemStack[] itemStacks = new ItemStack[size];
            for(int i = 0;i<size;i++){
                ItemStack item = yml.getItemStack(e+".display."+i);
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
            Slot.CATEGORY_CONTAIN_ITEMS.put(e,itemStacks);
            Slot.NEXT_MODE.put(e,yml.getString(e+".nextmode"));
        });
        if(yml.getInt("miss.weight")>0)Slot.PROBABILITY.put(now.addAndGet(yml.getInt("miss.weight",1)),"miss");
        Slot.categoryRandomMax = now.get();
    }


    public int getAndUpdateStock(boolean toAdd,boolean isBingo){
        int nowStock = SYSTEM_YML.getInt("stock", DEFAULT_STOCK)+ (toAdd ? STOCK_PER_SPIN : 0);
        SYSTEM_YML.set("stock",nowStock);
        CustomConfig.saveYmlByID(ID);

        MAIN_SYSTEM.getSignSystem().update(SIGN,nowStock);
        if(isBingo){
            Bukkit.getScheduler().runTaskLater(plugin,()->{
                MAIN_SYSTEM.getSignSystem().update(SIGN,DEFAULT_STOCK);
            },SPIN_TIME[ITEM_FRAME_COUNT -1] * SPIN_TICK);
        }

        return nowStock;
    }

    public boolean canSpin(Player p){
        return Vault.getEconomy().getBalance(p) >= PAYMENT;
    }


}
