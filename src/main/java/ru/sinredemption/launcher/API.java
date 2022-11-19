package ru.sinredemption.launcher;

import javafx.scene.text.Text;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class API extends Throwable {

    private Text status;
    public API(Text status){
        this.status = status;
    }
    static int downloadMinecraft() throws IOException {
        Configuration conf = new Configuration();
        System.out.println("Downloading");
        DataInputStream dis = new DataInputStream(
                new Socket("sinredemption.ru",
                        Integer.parseInt(Configuration.downloadPort)).getInputStream());
        FileOutputStream fos = new FileOutputStream("client.zip", false);
        byte[] buffer = new byte[4096];

        int filesize = dis.readInt(); // Send file size in separate msg
        int modSize = dis.readInt();
        int read = 0;
        int totalRead = 0;
        int remaining = filesize;
        while ((read = dis.read(buffer, 0, Math.min(buffer.length, remaining))) > 0) {
            totalRead += read;
            remaining -= read;
            fos.write(buffer, 0, read);
        }
        fos.close();
        dis.close();
        return modSize;
    }

    static void install() throws IOException {
        int size = downloadMinecraft();
        Util.deleteFolder(new File(Configuration.minecraftDirectory + "\\mods"));
        Util.unzip(Files.newInputStream(Paths.get("client.zip")), Paths.get(Configuration.minecraftDirectory));
        new File("client.zip").delete();
        Configuration.modsSize = String.valueOf(size);
    }

    static void autologinConfig() {
        Path configFolder = Paths.get(Configuration.minecraftDirectory + "\\config");
        if (!new File(Configuration.minecraftDirectory + "\\config").exists())
            new File(Configuration.minecraftDirectory + "\\config").mkdir();
        try (FileOutputStream fileOutputStream = new FileOutputStream(Configuration.minecraftDirectory + "\\config\\autologin.cfg")) {
            String autologin = "general {\n    B:\"Auto Login enabled?\"=true\n    S:password=%password%\n}".replace("%password", Configuration.password);
            fileOutputStream.write(autologin.getBytes());
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    }

    public static void runMinecraft() throws IOException {
        long length = 0;
        Path modFolder = Paths.get(Configuration.minecraftDirectory + "\\mods");
        if (!new File(modFolder.toString()).exists()) {
            install();
        }
        long size = Files.walk(modFolder)
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();

        if (size != Integer.parseInt(Configuration.modsSize))
            install();
        size = Files.walk(modFolder)
                .filter(p -> p.toFile().isFile())
                .mapToLong(p -> p.toFile().length())
                .sum();
        if (size != Integer.parseInt(Configuration.modsSize))
            System.exit(-80);

        autologinConfig();

        String[] command = "javaw -Djava.library.path=%minecraft%\\versions\\1.7.10\\natives -cp %minecraft%\\libraries\\optifine\\OptiFine\\1.7.10_HD_U_E7\\OptiFine-1.7.10_HD_U_E7.jar;%minecraft%\\libraries\\com\\mumfrey\\liteloader\\1.7.10\\liteloader-1.7.10.jar;%minecraft%\\libraries\\net\\minecraft\\launchwrapper\\1.11\\launchwrapper-1.11.jar;%minecraft%\\libraries\\org\\ow2\\asm\\asm-all\\5.0.3\\asm-all-5.0.3.jar;%minecraft%\\libraries\\net\\minecraftforge\\forge\\1.7.10-10.13.4.1614-1.7.10\\forge-1.7.10-10.13.4.1614-1.7.10.jar;%minecraft%\\libraries\\net\\minecraft\\launchwrapper\\1.12\\launchwrapper-1.12.jar;%minecraft%\\libraries\\org\\ow2\\asm\\asm-all\\5.0.3\\asm-all-5.0.3.jar;%minecraft%\\libraries\\com\\typesafe\\akka\\akka-actor_2.11\\2.3.3\\akka-actor_2.11-2.3.3.jar;%minecraft%\\libraries\\com\\typesafe\\config\\1.2.1\\config-1.2.1.jar;%minecraft%\\libraries\\org\\scala-lang\\scala-actors-migration_2.11\\1.1.0\\scala-actors-migration_2.11-1.1.0.jar;%minecraft%\\libraries\\org\\scala-lang\\scala-compiler\\2.11.1\\scala-compiler-2.11.1.jar;%minecraft%\\libraries\\org\\scala-lang\\plugins\\scala-continuations-library_2.11\\1.0.2\\scala-continuations-library_2.11-1.0.2.jar;%minecraft%\\libraries\\org\\scala-lang\\plugins\\scala-continuations-plugin_2.11.1\\1.0.2\\scala-continuations-plugin_2.11.1-1.0.2.jar;%minecraft%\\libraries\\org\\scala-lang\\scala-library\\2.11.1\\scala-library-2.11.1.jar;%minecraft%\\libraries\\org\\scala-lang\\scala-parser-combinators_2.11\\1.0.1\\scala-parser-combinators_2.11-1.0.1.jar;%minecraft%\\libraries\\org\\scala-lang\\scala-reflect\\2.11.1\\scala-reflect-2.11.1.jar;%minecraft%\\libraries\\org\\scala-lang\\scala-swing_2.11\\1.0.1\\scala-swing_2.11-1.0.1.jar;%minecraft%\\libraries\\org\\scala-lang\\scala-xml_2.11\\1.0.2\\scala-xml_2.11-1.0.2.jar;%minecraft%\\libraries\\lzma\\lzma\\0.0.1\\lzma-0.0.1.jar;%minecraft%\\libraries\\com\\google\\guava\\guava\\17.0\\guava-17.0.jar;%minecraft%\\libraries\\com\\mojang\\realms\\1.3.5\\realms-1.3.5.jar;%minecraft%\\libraries\\org\\apache\\commons\\commons-compress\\1.8.1\\commons-compress-1.8.1.jar;%minecraft%\\libraries\\org\\apache\\httpcomponents\\httpclient\\4.3.3\\httpclient-4.3.3.jar;%minecraft%\\libraries\\commons-logging\\commons-logging\\1.1.3\\commons-logging-1.1.3.jar;%minecraft%\\libraries\\org\\apache\\httpcomponents\\httpcore\\4.3.2\\httpcore-4.3.2.jar;%minecraft%\\libraries\\java3d\\vecmath\\1.3.1\\vecmath-1.3.1.jar;%minecraft%\\libraries\\net\\sf\\trove4j\\trove4j\\3.0.3\\trove4j-3.0.3.jar;%minecraft%\\libraries\\com\\ibm\\icu\\icu4j-core-mojang\\51.2\\icu4j-core-mojang-51.2.jar;%minecraft%\\libraries\\net\\sf\\jopt-simple\\jopt-simple\\4.5\\jopt-simple-4.5.jar;%minecraft%\\libraries\\com\\paulscode\\codecjorbis\\20101023\\codecjorbis-20101023.jar;%minecraft%\\libraries\\com\\paulscode\\codecwav\\20101023\\codecwav-20101023.jar;%minecraft%\\libraries\\com\\paulscode\\libraryjavasound\\20101123\\libraryjavasound-20101123.jar;%minecraft%\\libraries\\com\\paulscode\\librarylwjglopenal\\20100824\\librarylwjglopenal-20100824.jar;%minecraft%\\libraries\\com\\paulscode\\soundsystem\\20120107\\soundsystem-20120107.jar;%minecraft%\\libraries\\io\\netty\\netty-all\\4.0.10.Final\\netty-all-4.0.10.Final.jar;%minecraft%\\libraries\\org\\apache\\commons\\commons-lang3\\3.3.2\\commons-lang3-3.3.2.jar;%minecraft%\\libraries\\commons-io\\commons-io\\2.4\\commons-io-2.4.jar;%minecraft%\\libraries\\commons-codec\\commons-codec\\1.9\\commons-codec-1.9.jar;%minecraft%\\libraries\\net\\java\\jinput\\jinput\\2.0.5\\jinput-2.0.5.jar;%minecraft%\\libraries\\net\\java\\jutils\\jutils\\1.0.0\\jutils-1.0.0.jar;%minecraft%\\libraries\\com\\google\\code\\gson\\gson\\2.2.4\\gson-2.2.4.jar;%minecraft%\\libraries\\org\\tlauncher\\authlib\\1.7.211\\authlib-1.7.211.jar;%minecraft%\\libraries\\org\\apache\\logging\\log4j\\log4j-api\\2.0-beta9\\log4j-api-2.0-beta9.jar;%minecraft%\\libraries\\org\\apache\\logging\\log4j\\log4j-core\\2.0-beta9\\log4j-core-2.0-beta9.jar;%minecraft%\\libraries\\org\\lwjgl\\lwjgl\\lwjgl\\2.9.1\\lwjgl-2.9.1.jar;%minecraft%\\libraries\\org\\lwjgl\\lwjgl\\lwjgl_util\\2.9.1\\lwjgl_util-2.9.1.jar;%minecraft%\\libraries\\tv\\twitch\\twitch\\5.16\\twitch-5.16.jar;%minecraft%\\versions\\1.7.10\\1.7.10.jar -Xmx6950M -XX:+UnlockExperimentalVMOptions -XX:+UseG1GC -XX:G1NewSizePercent=20 -XX:G1ReservePercent=20 -XX:MaxGCPauseMillis=50 -XX:G1HeapRegionSize=32M -Dfml.ignoreInvalidMinecraftCertificates=true -Dfml.ignorePatchDiscrepancies=true -Djava.net.preferIPv4Stack=true -Dminecraft.applet.TargetDirectory=%minecraft% -Dlog4j.configurationFile=%minecraft%\\assets\\log_configs\\client-1.7.xml net.minecraft.launchwrapper.Launch --tweakClass com.mumfrey.liteloader.launch.LiteLoaderTweaker --username SinRedemption --version 1.7.10 --gameDir %minecraft% --assetsDir %minecraft%\\assets --assetIndex 1.7.10 --uuid d982b9050c824fd5ad846b8133356198 --accessToken null --userProperties {} --userType mojang --tweakClass cpw.mods.fml.common.launcher.FMLTweaker --width 925 --height 530 --server sinredemption.ru".replace("%minecraft%", Configuration.minecraftDirectory).replace("\"", "").split(" (?=-)");
        command[3] = "-Xmx" + Configuration.ram + "M";
        command[16] = "--username " + Configuration.username;
        command[21] = "--uuid " + UUID.nameUUIDFromBytes(Configuration.username.getBytes());
        StringBuilder strBuilder = new StringBuilder();
        strBuilder.append(command[0]);
        for (int i = 1; i < command.length; i++) {
            strBuilder.append(" " + command[i]);
        }
        System.out.println(strBuilder.toString());
        Process process = Runtime.getRuntime().exec(strBuilder.toString(),null, new File(Configuration.minecraftDirectory));

    }
}
