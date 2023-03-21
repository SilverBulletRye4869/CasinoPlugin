package silverassist.casinoplugin.coingame;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CasinoPlugin;
import silverassist.casinoplugin.Util;
import silverassist.casinoplugin.Vault;

import java.awt.*;
import java.util.HashSet;
import java.util.Set;

public class Game {
    private final static JavaPlugin plugin = CasinoPlugin.getInstance();

    private static int bet = -1;
    private static Set<Player> faceBeters = new HashSet<>();
    private static Set<Player> backBeters = new HashSet<>();
    private static boolean isCanbet = false;

    public static void start(int bet, Player owner){
        if(bet>0){
            Util.sendPrefixMessage(owner,"§c現在、別のコインゲームが開催中です");
            return;
        }
        isCanbet = true;
        Game.bet = bet;

        Util.broadcast("&6&k&laaa §6&lコインゲームが始められました！ &6&k&laaa");
        announce(30);
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
                        Util.addMoney(p,totalMoney / faceBeters.size());
                    });
                    backBeters.forEach(p->{Util.sendPrefixMessage(p,"§c§l負け...");});
                }else{
                    backBeters.forEach(p->{
                        Util.sendPrefixMessage(p,"§a§l勝ち！");
                        Util.addMoney(p,totalMoney / backBeters.size());
                    });
                    faceBeters.forEach(p->{Util.sendPrefixMessage(p,"§c§l負け...");});
                }

            } catch (InterruptedException e) {
                Util.broadcast("§cエラーが発生したため、コインゲームを終了します。");
                Util.broadcast("§c掛け金は全て返還されました。");
                faceBeters.forEach(p -> Util.addMoney(p,bet));
                backBeters.forEach(p -> Util.addMoney(p,bet));
                return;
            }
        });

    }

    public static void betToFace(Player p){
        if(faceBeters.contains(p) || !isCanbet)return;
        if(backBeters.contains(p))backBeters.remove(p);
        else Util.removeMoney(p,bet);;
        faceBeters.add(p);

    }

    public static void betToBack(Player p){
        if(backBeters.contains(p) || isCanbet)return;
        if(faceBeters.contains(p))faceBeters.remove(p);
        else Util.removeMoney(p,bet);
        backBeters.add(p);
    }

    private static void announce(int time){
        Util.broadcast("現在、コインゲームの賭け受付中です。");
        Util.broadcast("§e掛け金: §6"+bet+"円§f, §d残り"+time+"秒");
        TextComponent msg;
        msg= new TextComponent("§b§l[表にベット！]");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/coingame bet face"));
        plugin.getServer().spigot().broadcast(msg);
        msg = new TextComponent("§c§l[裏にベット！]");
        msg.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/coingame bet back"));
        plugin.getServer().spigot().broadcast(msg);
    }

}
