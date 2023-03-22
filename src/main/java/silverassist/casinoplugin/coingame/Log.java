package silverassist.casinoplugin.coingame;

import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.CasinoPlugin;
import silverassist.casinoplugin.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Log {

    public static final JavaPlugin plugin = CasinoPlugin.getInstance();
    //time, playerName, uuid, isBingo, nowLevel, category,stock, constantMoney, multiplier, wonMoney, nextLevel, wonItemName, ItemStack
    public static void write(Player p, List<Integer> values_int){
        List<String> values = values_int.stream().map(e->String.valueOf(e)).collect(Collectors.toList());

        String date = LocalDate.now().toString();
        Date nowTime = new Date();
        String timeStr = nowTime.getHours()+":"+nowTime.getMinutes()+":"+nowTime.getSeconds();

        File logFile = new File(plugin.getDataFolder(),"coingame/log/"+date+".csv");
        boolean isFirstWrite = false;
        if(!logFile.exists()){
            try {
                Path path = Paths.get(plugin.getDataFolder().getPath()+"/coingame/log");
                if(!Files.exists(path))Files.createDirectories(path);
                logFile.createNewFile();
                isFirstWrite = true;
            }catch (IOException e){
            Util.sendConsole( "coingame:"+timeStr + "のlogファイルの作成に失敗しました");
                e.printStackTrace();
                playerLogErrorMessage(p,nowTime.toString());
            }
        }
        if(!logFile.isFile()){
            Util.sendConsole("logファイル『"+logFile.getPath()+"』はファイルではありません");
            playerLogErrorMessage(p,timeStr);
            return;
        }else if(!logFile.canWrite()){
            Util.sendConsole("logファイル『"+logFile.getPath()+"』は書き込み不可のファイルです");
            playerLogErrorMessage(p,timeStr);
            return;
        }

        try {
            FileWriter fileWriter = new FileWriter(logFile, true);
            String writeData = timeStr+","+p.getName()+","+p.getUniqueId()+","+String.join(",",values)+", \n";
            if(isFirstWrite)fileWriter.write("Time,Player,UUID,before,after,delta\n");
            fileWriter.write(writeData);
            fileWriter.close();
        }catch (IOException e){
            Util.sendConsole("logファイル『"+logFile.getPath()+"』への書き込みに失敗しました");
            e.printStackTrace();
            playerLogErrorMessage(p,timeStr);
            return;
        }

    }

    private static void playerLogErrorMessage(Player p, String date){
        p.sendMessage("§4§lこの文章が表示された場合は、以下の内容を運営までお伝えください。(スクリーンショット推奨)");
        p.sendMessage("§c§l"+date+" -> CasinoPluginでエラーが発生");
        p.sendMessage("§c§lコンソールを至急確認してください");
    }
}

