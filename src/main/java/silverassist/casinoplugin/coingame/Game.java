package silverassist.casinoplugin.coingame;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CasinoPlugin;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.Vault;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {
    private final static JavaPlugin plugin = CasinoPlugin.getInstance();

    private static int bet = -1;
    private static Set<Player> faceBeters = new HashSet<>();
    private static Set<Player> backBeters = new HashSet<>();
    private static boolean isCanbet = false;

    public static boolean start(int bet, Player owner){
        if(Game.bet>0){
            Util.sendPrefixMessage(owner,"§c現在、別のコインゲームが開催中です");
            return false;
        }
        isCanbet = true;
        Game.bet = bet;

        Util.broadcast("§6§k§laaa §6§lコインゲームが始められました！ §6§k§laaa");
        Util.broadcast("§e掛け金: §6§l"+bet+"円§f, §d残り30秒");
        TextComponent msg;
        msg= new TextComponent("§b§l[表にベット！]");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/coingame bet face"));
        plugin.getServer().spigot().broadcast(msg);
        msg = new TextComponent("§c§l[裏にベット！]");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/coingame bet back"));
        plugin.getServer().spigot().broadcast(msg);
        Bukkit.getScheduler().runTaskAsynchronously(plugin, ()->{
            try {
                Thread.sleep(10000);
                announce(20);
                Thread.sleep(10000);
                announce(10);
                Thread.sleep(5000);
                announce(5);
                Thread.sleep(2000);
                for(int i = 3;i>0;i--){
                    announce(i);
                    Thread.sleep(1000);
                }
                isCanbet = false;

                if( (faceBeters.size() * backBeters.size() ==0) || (faceBeters.size() + backBeters.size() == 3)) {
                    Set.of(faceBeters,backBeters).forEach(beters -> beters.forEach(p->{
                        Util.sendPrefixMessage(p,"§c人数が集まらなかったためキャンセルされました");
                        Util.addMoney(p,bet);
                    }));
                    return;
                }
                Util.broadcast("§c§l抽選中･･･");
                Thread.sleep((int)(Math.random() *3000 + 1000));
                String result = (Math.random() * 2 < 1) ? "表" : "裏";
                Util.broadcast("§6§l結果: §b§l"+result+"！");
                Thread.sleep(500);
                int totalMoney = bet * (faceBeters.size() + backBeters.size());
                if(result.equals("表")){
                    faceBeters.forEach(p->{
                        Util.sendPrefixMessage(p,"§a§l勝ち！");
                        int wonMoney = totalMoney / faceBeters.size();
                        Util.addMoney(p,wonMoney);
                        Log.write(p, List.of(bet,wonMoney,wonMoney-bet));
                    });
                    backBeters.forEach(p->{Util.sendPrefixMessage(p,"§c§l負け...");Log.write(p,List.of(bet,0,-bet));});
                }else{
                    backBeters.forEach(p->{
                        Util.sendPrefixMessage(p,"§a§l勝ち！");
                        int wonMoney = totalMoney / backBeters.size();
                        Util.addMoney(p,wonMoney);
                        Log.write(p, List.of(bet,wonMoney,wonMoney-bet));
                    });
                    faceBeters.forEach(p->{Util.sendPrefixMessage(p,"§c§l負け...");Log.write(p,List.of(bet,0,-bet));});
                }

            } catch (InterruptedException e) {
                Util.broadcast("§cエラーが発生したため、コインゲームを終了します。");
                Util.broadcast("§c掛け金は全て返還されました。");
                faceBeters.forEach(p -> Util.addMoney(p,bet));
                backBeters.forEach(p -> Util.addMoney(p,bet));
                return;
            } finally {
                isCanbet = false;
                Game.bet = 0;
            }
        });
        return true;
    }

    public static void betToFace(Player p){
        if(faceBeters.contains(p) || !isCanbet)return;
        if(backBeters.contains(p))backBeters.remove(p);
        else Util.removeMoney(p,bet);;
        faceBeters.add(p);
        Util.broadcast("§e§l"+p.getName()+"§a§lが§b§l表§a§lにベットしました");
        Util.broadcast("§b§l表§c§l->§6§l"+faceBeters.size()+"§c§l人");
        Util.broadcast("§b§l裏§c§l->§6§l"+backBeters.size()+"§c§l人");

    }

    public static void betToBack(Player p){
        if(backBeters.contains(p) || !isCanbet)return;
        if(faceBeters.contains(p))faceBeters.remove(p);
        else Util.removeMoney(p,bet);
        backBeters.add(p);
        Util.broadcast("§e§l"+p.getName()+"§a§lが§d§l裏§a§lにベットしました");
        Util.broadcast("§b§l表§c§l->§6§l"+faceBeters.size()+"§c§l人");
        Util.broadcast("§b§l裏§c§l->§6§l"+backBeters.size()+"§c§l人");
    }

    private static void announce(int time) {
        Util.broadcast("現在、コインゲームの賭け受付中です。§e掛け金: §6" + bet + "円§f, §d残り" + time + "秒");
    }

    public static boolean canJoin(Player p){
        return (Vault.getEconomy().getBalance(p) >= Game.bet || faceBeters.contains(p) || backBeters.contains(p));
    }
}
