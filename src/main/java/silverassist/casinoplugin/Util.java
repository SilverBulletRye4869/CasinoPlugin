package silverassist.casinoplugin;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

public class Util {
    public static final String PREFIX = "§b§l[§e§lCasino§b§l]";

    public static final ItemStack GUI_BG = createItem(Material.BLUE_STAINED_GLASS_PANE,"§r");
    public static final ItemStack NULL_BG = createItem(Material.LIGHT_GRAY_STAINED_GLASS_PANE,"§r");
    private static final Logger log = CasinoPlugin.getLog();
    private static final JavaPlugin plugin = CasinoPlugin.getInstance();

    public static ItemStack createItem(Material m,String name){return createItem(m,name,null,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore){return createItem(m,name,lore,0,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, Map<Enchantment,Integer> ench){return createItem(m,name,lore,0,ench);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model){return createItem(m,name,lore,model,null);}
    public static ItemStack createItem(Material m, String name, List<String> lore, int model, Map<Enchantment,Integer> ench){
        ItemStack item = new ItemStack(m);
        ItemMeta itemMeta = item.getItemMeta();
        if(itemMeta!=null){
            itemMeta.setDisplayName(name);
            if(lore!=null)itemMeta.setLore(lore);
            itemMeta.setCustomModelData(model);
            item.setItemMeta(itemMeta);
        }
        if(ench!=null)item.addUnsafeEnchantments(ench);
        return item;
    }

    public static void invFill(Inventory inv){invFill(inv,GUI_BG,false);}
    public static void invFill(Inventory inv,ItemStack item){invFill(inv,item,false);}
    public static void invFill(Inventory inv,ItemStack item,boolean isAppend){
        int size = inv.getSize();
        for(int i = 0;i<size;i++){
            if(isAppend && inv.getItem(i).getType() != Material.AIR)continue;
            inv.setItem(i,item);
        }
    }

    public static int[] getRectSlotPlaces(int start,int w,int h){
        int[] slotPlaces = new int[w*h];
        for(int i = 0;i<slotPlaces.length;i++)slotPlaces[i] = start + i % w + 9 * (i/w);
        return slotPlaces;
    }

    public static void sendPrefixMessage(Player p, String msg) {
        p.sendMessage(PREFIX + "§r" + msg);
    }

    public enum MessageType{INFO,WARNING,ERROR}
    public static void sendConsole(String msg){sendConsole(msg,MessageType.ERROR);}
    public static void sendConsole(String msg, MessageType type){
        switch (type) {
            case INFO:
                log.info(String.format("[%s] " + msg, plugin.getDescription().getName()));
                break;
            case WARNING:
                log.warning(String.format("[%s] " + msg, plugin.getDescription().getName()));
                break;
            case ERROR:
                log.severe(String.format("[%s] " + msg, plugin.getDescription().getName()));
        }
    }

    //サジェストメッセージ送信
    public static void sendSuggestMessage(Player p, String text, String command){
        TextComponent msg = new TextComponent(PREFIX + text);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,command));
        p.spigot().sendMessage(msg);
    }

    //ﾗﾝコマンドメッセージを送信
    public static void sendRunCommandMessage(Player p, String text, String command){
        TextComponent msg = new TextComponent(PREFIX + text);
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, command));
        p.spigot().sendMessage(msg);
    }

    //アクションバーに表示
    public static void sendActionBar(Player p,String text){
        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(text));
    }

    public static void broadcast(String s){
        plugin.getServer().broadcastMessage(PREFIX+s);
    }

    public static void title(String mainTitle){title(mainTitle,"");}
    public static void title(String mainTitle,String subTitle){
        Bukkit.getOnlinePlayers().forEach(p-> p.sendTitle(mainTitle,subTitle,20,60,20));
    }


    public static boolean ChanceOf(double chance){
        double r = Math.random() * 100;
        return r<chance;
    }

    public static Location LocationCPY(Location loc){
        return new Location(loc.getWorld(),loc.getX(),loc.getY(),loc.getZ(),loc.getYaw(),loc.getPitch());
    }

    //e1から見たe2の相対位置角度（極座標θ部分）を取得
    public static double getRelativeAngle(Entity e1, Entity e2){
        Location e1loc = e1.getLocation();
        Location e2loc = e2.getLocation();
        double theta = Math.abs(-Math.atan2(e2loc.getX() - e1loc.getX(),e2loc.getZ() - e1loc.getZ()) - e1loc.getYaw() / 180*Math.PI);
        return Math.min(2*Math.PI-theta,theta);
    }

    public static void delayInvOpen(Player p,Inventory inv){
        Bukkit.getScheduler().runTaskLater(plugin,()->p.openInventory(inv),1);
    }

    public static ItemStack getHead(Player p){return getHead((OfflinePlayer) p);}
    public static ItemStack getHead(OfflinePlayer p){
        return new ItemStack(Material.PLAYER_HEAD){{
            SkullMeta head = (SkullMeta) this.getItemMeta();
            head.setOwningPlayer(p);
            this.setItemMeta(head);
        }};
    }

    public static boolean setItemFrame(String uuidStr, ItemStack item){return setItemFrame(UUID.fromString(uuidStr),item);}
    public static boolean setItemFrame(UUID uuid, ItemStack item){
        Entity entity = Bukkit.getEntity(uuid);
        if(entity==null || !(entity instanceof ItemFrame))return false;
        ItemFrame itemFrame = (ItemFrame) entity;
        itemFrame.setItem(item);
        return true;
    }

    public static ItemStack getItemFrame(String uuidStr){return getItemFrame(UUID.fromString(uuidStr));}
    public static ItemStack getItemFrame(UUID uuid){
        Entity entity = Bukkit.getEntity(uuid);
        if(entity==null || !(entity instanceof ItemFrame))return null;
        ItemFrame itemFrame = (ItemFrame) entity;
        return itemFrame.getItem();
    }

    public static Object getRandomIndex(Object[] array){
        return array[(int)(Math.random()*array.length)];
    }

    public static Object getRandomIndex(List<Object> list){
        return list.get((int)(Math.random())*list.size());
    }

    public static ItemStack getPlusBanner(){
        ItemStack item = new ItemStack(Material.YELLOW_BANNER);
        BannerMeta bannerMeta = (BannerMeta) item.getItemMeta();
        List<Pattern> patterns = new ArrayList<>();
        //ちかいうちにつくる
        patterns.add(new Pattern(DyeColor.LIGHT_BLUE, PatternType.STRAIGHT_CROSS));
        patterns.add(new Pattern(DyeColor.YELLOW, PatternType.BORDER));
        patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_TOP));
        patterns.add(new Pattern(DyeColor.YELLOW, PatternType.STRIPE_BOTTOM));

        bannerMeta.setPatterns(patterns);
        item.setItemMeta(bannerMeta);
        return  item;
    }

    public static void deleteDirectory(File targetFile){
        if(targetFile.isFile())targetFile.delete();
        else if(targetFile.isDirectory()){
            Arrays.stream(targetFile.listFiles()).forEach(Util::deleteDirectory);
            targetFile.delete();
        }
    }
}
