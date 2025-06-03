package lishid.orebfuscator.utils;

import gnu.trove.set.hash.TByteHashSet;
import org.bukkit.util.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OrebfuscatorConfig {
    private static final Random randomGenerator;
    private static Configuration config;
    private static TByteHashSet TransparentBlocks;
    private static TByteHashSet ObfuscateBlocks;
    private static TByteHashSet DarknessObfuscateBlocks;
    private static TByteHashSet LightEmissionBlocks;
    private static byte[] RandomBlocks;
    private static List<String> DisabledWorlds;
    private static int EngineMode;
    private static int UpdateRadius;
    private static int InitialRadius;
    private static int ProcessingThreads;
    private static boolean UpdateOnBreak;
    private static boolean UpdateOnDamage;
    private static boolean UpdateOnPhysics;
    private static boolean UpdateOnExplosion;
    private static boolean DarknessHideBlocks;
    private static boolean NoObfuscationForOps;
    private static boolean NoObfuscationForPermission;
    private static boolean Enabled;

    static {
        TransparentBlocks = new TByteHashSet();
        ObfuscateBlocks = new TByteHashSet();
        DarknessObfuscateBlocks = new TByteHashSet();
        LightEmissionBlocks = new TByteHashSet();
        RandomBlocks = new byte[0];
        DisabledWorlds = new ArrayList<String>();
        randomGenerator = new Random();
    }

    public static int EngineMode() {
        return EngineMode;
    }

    public static int UpdateRadius() {
        return UpdateRadius;
    }

    public static int InitialRadius() {
        if (InitialRadius < 0) {
            return 0;
        }
        return InitialRadius;
    }

    public static int ProcessingThreads() {
        if (ProcessingThreads < 0) {
            return 1;
        }
        return ProcessingThreads;
    }

    public static boolean UpdateOnBreak() {
        return UpdateOnBreak;
    }

    public static boolean UpdateOnDamage() {
        return UpdateOnDamage;
    }

    public static boolean UpdateOnPhysics() {
        return UpdateOnPhysics;
    }

    public static boolean UpdateOnExplosion() {
        return UpdateOnExplosion;
    }

    public static boolean DarknessHideBlocks() {
        return DarknessHideBlocks;
    }

    public static boolean NoObfuscationForOps() {
        return NoObfuscationForOps;
    }

    public static boolean NoObfuscationForPermission() {
        return NoObfuscationForPermission;
    }

    public static boolean Enabled() {
        return Enabled;
    }

    public static boolean isTransparent(byte id) {
        if (id == 0) {
            return true;
        }
        return TransparentBlocks.contains(id);
    }

    public static boolean isObfuscated(byte id) {
        if (id == 1) {
            return true;
        }
        return ObfuscateBlocks.contains(id);
    }

    public static boolean isDarknessObfuscated(byte id) {
        return DarknessObfuscateBlocks.contains(id);
    }

    public static boolean emitsLight(byte id) {
        return LightEmissionBlocks.contains(id);
    }

    public static boolean worldDisabled(String name) {
        return DisabledWorlds.contains(name.toLowerCase());
    }

    public static String disabledWorlds() {
        String retval = "";
        for (String world : DisabledWorlds) {
            retval = retval + world + ", ";
        }
        return retval.substring(0, retval.length() - 2);
    }

    public static byte GenerateRandomBlock() {
        return RandomBlocks[randomGenerator.nextInt(RandomBlocks.length)];
    }

    public static void SetEngineMode(int data) {
        OrebfuscatorConfig.SetData("Integers.EngineMode", data);
        EngineMode = data;
    }

    public static void SetUpdateRadius(int data) {
        OrebfuscatorConfig.SetData("Integers.UpdateRadius", data);
        UpdateRadius = data;
    }

    public static void SetInitialRadius(int data) {
        OrebfuscatorConfig.SetData("Integers.InitialRadius", data);
        InitialRadius = data;
    }

    public static void SetProcessingThreads(int data) {
        OrebfuscatorConfig.SetData("Integers.ProcessingThreads", data);
        ProcessingThreads = data;
    }

    public static void SetUpdateOnBreak(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.UpdateOnBreak", data);
        UpdateOnBreak = data;
    }

    public static void SetUpdateOnDamage(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.UpdateOnDamage", data);
        UpdateOnDamage = data;
    }

    public static void SetUpdateOnPhysics(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.UpdateOnPhysics", data);
        UpdateOnPhysics = data;
    }

    public static void SetUpdateOnExplosion(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.UpdateOnExplosion", data);
        UpdateOnExplosion = data;
    }

    public static void SetDarknessHideBlocks(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.DarknessHideBlocks", data);
        DarknessHideBlocks = data;
    }

    public static void SetNoObfuscationForOps(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.NoObfuscationForOps", data);
        NoObfuscationForOps = data;
    }

    public static void SetNoObfuscationForPermission(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.NoObfuscationForPermission", data);
        NoObfuscationForPermission = data;
    }

    public static void SetEnabled(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.Enabled", data);
        Enabled = data;
    }

    public static void SetDisabledWorlds(String name, boolean data) {
        if (!data) {
            DisabledWorlds.remove(name);
        } else {
            DisabledWorlds.add(name);
        }
        OrebfuscatorConfig.SetData("Lists.DisabledWorlds", DisabledWorlds);
    }

    private static boolean GetBoolean(String path, boolean defaultData) {
        if (config.getProperty(path) == null) {
            OrebfuscatorConfig.SetData(path, defaultData);
        }
        return config.getBoolean(path, defaultData);
    }

    private static int GetInt(String path, int defaultData) {
        if (config.getProperty(path) == null) {
            OrebfuscatorConfig.SetData(path, defaultData);
        }
        return config.getInt(path, defaultData);
    }

    private static List<Integer> GetIntList(String path, List<Integer> defaultData) {
        if (config.getProperty(path) == null) {
            OrebfuscatorConfig.SetData(path, defaultData);
        }
        return config.getIntList(path, defaultData);
    }

    private static List<String> GetStringList(String path, List<String> defaultData) {
        if (config.getProperty(path) == null) {
            OrebfuscatorConfig.SetData(path, defaultData);
        }
        return config.getStringList(path, defaultData);
    }

    private static void SetData(String path, Object data) {
        config.setProperty(path, data);
        config.save();
    }

    private static byte[] IntListToByteArray(List<Integer> list) {
        byte[] byteArray = new byte[list.size()];
        int i = 0;
        while (i < byteArray.length) {
            byteArray[i] = (byte) list.get(i).intValue();
            ++i;
        }
        return byteArray;
    }

    private static TByteHashSet IntListToTByteHashSet(List<Integer> list) {
        TByteHashSet bytes = new TByteHashSet();
        int i = 0;
        while (i < list.size()) {
            bytes.add((byte) list.get(i).intValue());
            ++i;
        }
        return bytes;
    }

    public static void Load(Configuration config) {
        OrebfuscatorConfig.config = config;
        EngineMode = OrebfuscatorConfig.GetInt("Integers.EngineMode", 2);
        if (EngineMode != 1 && EngineMode != 2) {
            EngineMode = 1;
            System.out.println("[Orebfuscator] EngineMode must be 1 or 2.");
        }
        UpdateRadius = OrebfuscatorConfig.GetInt("Integers.UpdateRadius", 2);
        InitialRadius = OrebfuscatorConfig.GetInt("Integers.InitialRadius", 1);
        if (InitialRadius > 4) {
            InitialRadius = 4;
            System.out.println("[Orebfuscator] InitialRadius must be less than 5.");
        }
        ProcessingThreads = OrebfuscatorConfig.GetInt("Integers.ProcessingThreads", 1);
        UpdateOnBreak = OrebfuscatorConfig.GetBoolean("Booleans.UpdateOnBreak", true);
        UpdateOnDamage = OrebfuscatorConfig.GetBoolean("Booleans.UpdateOnDamage", true);
        UpdateOnPhysics = OrebfuscatorConfig.GetBoolean("Booleans.UpdateOnPhysics", true);
        UpdateOnExplosion = OrebfuscatorConfig.GetBoolean("Booleans.UpdateOnExplosion", true);
        DarknessHideBlocks = OrebfuscatorConfig.GetBoolean("Booleans.DarknessHideBlocks", true);
        NoObfuscationForOps = OrebfuscatorConfig.GetBoolean("Booleans.NoObfuscationForOps", true);
        NoObfuscationForPermission = OrebfuscatorConfig.GetBoolean("Booleans.NoObfuscationForPermission", true);
        Enabled = OrebfuscatorConfig.GetBoolean("Booleans.Enabled", true);
        TransparentBlocks = OrebfuscatorConfig.IntListToTByteHashSet(OrebfuscatorConfig.GetIntList("Lists.TransparentBlocks", Arrays.asList(6, 8, 9, 10, 11, 18, 20, 26, 27, 28, 30, 31, 32, 34, 37, 38, 39, 40, 44, 50, 51, 52, 53, 54, 55, 59, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 75, 76, 77, 78, 79, 81, 83, 85, 90, 92, 93, 94, 96, 101, 102, 104, 105, 106, 107, 108, 109, 111, 113, 114, 115)));
        ObfuscateBlocks = OrebfuscatorConfig.IntListToTByteHashSet(OrebfuscatorConfig.GetIntList("Lists.ObfuscateBlocks", Arrays.asList(14, 15, 16, 21, 54, 56, 73, 74)));
        DarknessObfuscateBlocks = OrebfuscatorConfig.IntListToTByteHashSet(OrebfuscatorConfig.GetIntList("Lists.DarknessObfuscateBlocks", Arrays.asList(48, 52)));
        LightEmissionBlocks = OrebfuscatorConfig.IntListToTByteHashSet(OrebfuscatorConfig.GetIntList("Lists.LightEmissionBlocks", Arrays.asList(10, 11, 50, 51, 62, 74, 76, 89, 90, 91, 94)));
        RandomBlocks = OrebfuscatorConfig.IntListToByteArray(OrebfuscatorConfig.GetIntList("Lists.RandomBlocks", Arrays.asList(5, 14, 15, 16, 21, 48, 56, 73)));
        DisabledWorlds = OrebfuscatorConfig.GetStringList("Lists.DisabledWorlds", DisabledWorlds);
        config.save();
    }

    public static void Reload() {
        config.load();
        OrebfuscatorConfig.Load(config);
    }

    public static void Save() {
        config.save();
    }
}

