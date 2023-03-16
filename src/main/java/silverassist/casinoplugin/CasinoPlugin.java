package silverassist.casinoplugin;

import org.bukkit.plugin.java.JavaPlugin;
import silverassist.casinoplugin.slot.MainSystem_slot;
import silverassist.casinoplugin.slot.SlotCommand;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Logger;

public final class CasinoPlugin extends JavaPlugin {
    private static JavaPlugin plugin = null;
    private static Logger log = null;
    private static Vault vault = null;
    private static MainSystem_slot SLOT_MAIN_SYSTM = null;

    @Override
    public void onEnable() {
        plugin = this;
        log = this.getLogger();

        if(!folderSetup()){
            Util.sendConsole("プラグインフォルダの作成に失敗しました", Util.MessageType.ERROR);
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        vault = new Vault(this);
        if (!vault.setupEconomy() ) vault.log.severe(String.format("[%s] プラグイン「Vault」「Essentials」の認証に失敗しました。", getDescription().getName()));

        SLOT_MAIN_SYSTM = new MainSystem_slot(this);
        new SlotCommand(this,SLOT_MAIN_SYSTM);
        // Plugin startup logic

    }

    public static JavaPlugin getInstance(){return plugin;}
    public static Logger getLog(){return log;}
    public static Vault getVault(){return vault;}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private boolean folderSetup(){
        try{
            Files.createDirectories(Paths.get(this.getDataFolder()+"/slot/data"));
            Files.createDirectories(Paths.get(this.getDataFolder()+"/slot/log"));
        }catch (IOException e){
            Util.sendConsole("dataフォルダ又はlogフォルダの作成に失敗しました", Util.MessageType.ERROR);
            e.printStackTrace();
            return false;
        }
        File file = new File(this.getDataFolder()+"/README.txt");

        try {
            file.createNewFile();
        }catch (IOException e){
            if(Files.exists(Paths.get(this.getDataFolder()+"README.txt")))return true;
            else{
                Util.sendConsole("README.txtファイルの作成に失敗しました", Util.MessageType.ERROR);
                e.printStackTrace();
                return false;
            }
        }
        if(!file.isFile()){
            Util.sendConsole("README.txtはファイルではありません",Util.MessageType.ERROR);
            return false;
        }
        if(!file.canWrite()){
            Util.sendConsole("README.txtは書き込み不可のファイルです",Util.MessageType.ERROR);
            return false;
        }

        try {
            FileWriter fw = new FileWriter(file);
            fw.write(
                    "[注意] config.yml以外のファイルを手動で編集した場合の動作は保証しません\n" +
                            "[Note] We do not guarantee the operation if files other than \"config.yml\" are edited manually."
            );
            fw.close();
        }catch (IOException e){
            Util.sendConsole("README.txtファイルの書き込みに失敗しました",Util.MessageType.ERROR);
            return false;
        }

        return true;
    }
}
