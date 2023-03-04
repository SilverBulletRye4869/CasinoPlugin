package silverassist.casinoplugin;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class CustomConfig {
    private static Map<String, YamlConfiguration> config = new HashMap<>();
    private static JavaPlugin plugin = CasinoPlugin.getInstance();
    private static HashSet<String> existSet = new HashSet<>();

    public static YamlConfiguration getYmlByID(String id){return getYmlByID(id,"system");}
    public static YamlConfiguration getYmlByID(String id, String type) {
        if(!config.containsKey(id)){
            if(!reloadYmlByID(id,type))return null;
        }
        return config.get(id);
    }

    public static boolean existYml(String id){return existYml(id,"system");}
    public static boolean existYml(String id, String type){
        if(existSet.contains(id+"_"+type))return true;
        if(new File(plugin.getDataFolder(),"data/"+id+"/"+type+".yml").exists()){existSet.add(id+"_"+type);return true;}
        return false;
    }

    public static YamlConfiguration createYmlByID(String id){return createYmlByID(id,"system");}
    public static YamlConfiguration createYmlByID(String id,String type){
        File file = new File(plugin.getDataFolder(),"data/"+id+"/"+type+".yml");
        try {
            file.createNewFile();
        }catch (IOException e){
            System.err.println("id: "+id+"のymlファイルの作成に失敗しました");
            e.printStackTrace();
            return null;
        }
        return getYmlByID(id,type);
    }

    public static boolean deleteYmlByID(String id){return deleteYmlByID(id,"all");}
    public static boolean deleteYmlByID(String id, String type){
        File file = new File(plugin.getDataFolder(),type.equals("all") ? "data/"+id : "data/"+id+"/"+type+".yml");
        boolean result = file.delete();
        if(result){
            config.remove(id+"_"+type);
            existSet.remove(id+"_"+type);
        }
        return result;
    }

    public static boolean reloadYmlByID(String id){return reloadYmlByID(id,"system");}
    public static boolean reloadYmlByID(String id, String type){
        File file = new File(plugin.getDataFolder(),"data/"+id+"/"+type+".yml");
        if(!file.exists())return false;
        YamlConfiguration y = YamlConfiguration.loadConfiguration(file);
        config.put(id+"_"+type,y);
        return true;
    }

    public static void saveYmlByID(String id){saveYmlByID(id,"system");}
    public static void saveYmlByID(String id, String type){
        try{
            config.get(id+"_"+type).save(new File(plugin.getDataFolder(),"data/" + id +"/"+type +".yml"));
        }catch (IOException e){
            System.err.println("スロット『"+id+"-"+type+"+』の保存に失敗しました。:"+e);
        }
    }
}
