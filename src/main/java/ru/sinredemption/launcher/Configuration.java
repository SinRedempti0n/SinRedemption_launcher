package ru.sinredemption.launcher;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

public class Configuration {
    public static String username = "";
    public static String password = "";
    public static String ram = "2048";
    public static String modsSize = "0";
    public static String downloadPort = "1988";
    public static String minecraftDirectory = System.getenv("AppData") + "\\.sinredemption";
    private Properties properties = new Properties();
    private File propertiesFile = new File(minecraftDirectory + "\\launcher.properties");

    public Configuration() {
        new File(minecraftDirectory).mkdir();
        if (propertiesFile.exists())
            try (FileInputStream fis = new FileInputStream(propertiesFile)) {
                properties.load(fis);
                for (Field f : Configuration.class.getFields()) {
                    if (!properties.containsKey(f.getName()))
                        properties.setProperty(f.getName(), (String) this.getClass().getDeclaredField(f.getName()).get(this));
                    else
                        f.set(this, properties.getProperty(f.getName()));
                }
            } catch (Exception e) {
                System.out.println(e.toString());
            }
    }

    public boolean saveConfiguration(){
        try(OutputStream outputStream = Files.newOutputStream(propertiesFile.toPath(), StandardOpenOption.CREATE)){
            for (Field f : Configuration.class.getFields())
                try {
                    properties.setProperty(f.getName(), (String) this.getClass().getDeclaredField(f.getName()).get(this));
                }catch (Exception e) {
                    System.out.println(e.toString());
                }
            properties.store(outputStream, null);
        }catch (Exception e){
            System.out.println(e.toString());
            return false;
        }
        return true;
    }
}

