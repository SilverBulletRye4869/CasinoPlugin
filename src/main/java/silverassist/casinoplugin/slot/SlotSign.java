package silverassist.casinoplugin.slot;

import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CustomConfig;
import silverassist.casinoplugin.Util;

public class SlotSign {
    private final JavaPlugin plugin;
    private final MainSystem_slot MAIN_SYSTEM;

    public SlotSign(JavaPlugin plugin,MainSystem_slot mainSystem_slot){
        this.plugin = plugin;
        this.MAIN_SYSTEM = mainSystem_slot;
        plugin.getServer().getPluginManager().registerEvents(new listener(),plugin);
    }

    public boolean update(Location loc,int stockAmount){
        if(loc.getBlock()==null || !(loc.getBlock().getState() instanceof Sign))return false;
        Sign sign = (Sign)loc.getBlock().getState();
        sign.setLine(2,"§a§lSTOCK: §d§l"+stockAmount);
        return true;
    }

    private class listener implements Listener{
        @EventHandler
        public void onSignChange(SignChangeEvent e){
            String[] lines = e.getLines();
            if(!lines[0].equals("slot") || MAIN_SYSTEM.existSlot(lines[3]))return;
            YamlConfiguration yml = CustomConfig.getYmlByID(lines[3]);
            e.setLine(0,"§6§l"+yml.getString("name"));
            e.setLine(1,"§a§l料金: §e§l"+yml.getInt("payment")+"§a§l/回");
            e.setLine(2,"§a§lSTOCK: §d§l"+yml.getInt("stock"));
            yml.set("sign",e.getBlock().getLocation());
            CustomConfig.saveYmlByID(lines[3]);
        }

        @EventHandler
        public void onSignClick(PlayerInteractEvent e){
            if(e.getAction()!= Action.RIGHT_CLICK_BLOCK)return;
            if(e.getClickedBlock()==null || !(e.getClickedBlock().getState() instanceof Sign))return;
            String[] lines = ((Sign) e.getClickedBlock().getState()).getLines();
            if(!MAIN_SYSTEM.existSlot(lines[3]))return;


            if(MAIN_SYSTEM.getSlot(lines[3]).canSpin(e.getPlayer())) {
                boolean result = MAIN_SYSTEM.spin(lines[3],e.getPlayer());
                if(!result)Util.sendPrefixMessage(e.getPlayer(),"§c§lスロットデータに誤りがあります。お近くの運営にお知らせください。");
            }
            else{
                Util.sendPrefixMessage(e.getPlayer(),"§c§l所持金が足りません！");
            }

        }
    }
}
