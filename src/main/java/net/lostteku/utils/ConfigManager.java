package net.lostteku.utils;

import net.lostteku.Reingekekst;
import net.lostteku.enums.DefaultConf;
import net.lostteku.enums.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigManager {

    private static List<File> fileList = new ArrayList<>();
    private static String lang = "EN_en";


    public void loadFiles() {
        if (!Reingekekst.getPlugin().getDataFolder().exists()) {
            Reingekekst.getPlugin().getDataFolder().mkdir();
        }

        File lang = new File(Reingekekst.getPlugin().getDataFolder() + "\\lang\\");
        if(!lang.exists()){
            lang.mkdir();
        }

       for (DefaultConf confs : DefaultConf.values()) {
            setDefaultsToConfig(createNewFile("config"), confs.getPath(), confs.getValue());
        }

       /* for (Messages msgs : Messages.values()) {
            setDefaultsToConfig(createNewFile("lang\\EN_en"), msgs.getPath(), msgs.getStandardMessage());
        }*/

        for (File files : Reingekekst.getPlugin().getDataFolder().listFiles()) {
            if (files.getName().endsWith(".yml")) {
                fileList.add(files);
            }
        }

        switch (getConfigFile("config").getString("language")) {
            case "DE": case "DE_de":
                setLang("DE_de");
                break;
            case "EN": case "EN_en":
                setLang("EN_en");
                break;
            default:
                Bukkit.getConsoleSender().sendMessage("§cPlease ensure that the language string in the config.yml is right!");
                return;
        }

        File file = new File(Reingekekst.getPlugin().getDataFolder() + "/lang/" +  getLang() + ".yml");
        if(!file.exists()){
            Reingekekst.getPlugin().saveResource( "lang/" + getLang() + ".yml", true);
        }

    }

    private File createNewFile(String name) {
        File newfile = new File(Reingekekst.getPlugin().getDataFolder() + "\\" + name + ".yml");
        if (newfile.exists()) return newfile;
        try {
            newfile.createNewFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return newfile;
    }


    public YamlConfiguration getConfigFile(String name) {
        for (File files : fileList) {
            if (files.getName().equals(name + ".yml")) {
                return YamlConfiguration.loadConfiguration(files);
            }
        }
        return null;
    }

    private void setDefaultsToConfig(File newfile, String path, Object value) {

        YamlConfiguration conf = YamlConfiguration.loadConfiguration(newfile);
        if (conf.get(path) != null) return;

        conf.set(path, value);
        try {
            conf.save(newfile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void setConfigurationToFile(String name, String configpath, Object value) {

        if(getConfigFile(name) == null) {
            Bukkit.getConsoleSender().sendMessage("§cBitte starte den Server nochmal neu, damit ich die fehlenden Configdateien erstellen kann!");
            return;
        }
        YamlConfiguration cnf = getConfigFile(name);

        cnf.set(configpath, value);
        try {
            cnf.save(new File(Reingekekst.getPlugin().getDataFolder() + "\\" + name + ".yml"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void setLang(String lang) {
        ConfigManager.lang = lang;
    }

    public static String getLang() {
        return lang;
    }

    public static String getMessage(Messages path){
        return ChatColor.translateAlternateColorCodes('&', YamlConfiguration.loadConfiguration(new File(Reingekekst.getPlugin().getDataFolder() + "\\lang\\" + getLang() + ".yml")).getString(Messages.getPath(path)));
    }
}
