package lishid.orebfuscator;

import org.bukkit.util.config.Configuration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class OrebfuscatorConfig {
    private static Configuration config;
    private static Byte[] TransparentBlocks;
    private static Byte[] ObfuscateBlocks;
    private static Byte[] DarknessObfuscateBlocks;
    private static Byte[] LightEmissionBlocks;
    private static Byte[] RandomBlocks;
    private static final Random randomGenerator;
    private static int EngineMode;
    private static int UpdateRadius;
    private static int InitialRadius;
    private static boolean UpdateOnBreak;
    private static boolean UpdateOnDamage;
    private static boolean UpdateOnExplosion;
    private static boolean DarknessHideBlocks;
    private static boolean NoObfuscationForOps;
    private static boolean NoObfuscationForPermission;
    private static boolean Enabled;

    static {
        TransparentBlocks = new Byte[0];
        ObfuscateBlocks = new Byte[0];
        DarknessObfuscateBlocks = new Byte[0];
        LightEmissionBlocks = new Byte[0];
        RandomBlocks = new Byte[0];
        randomGenerator = new Random();
    }

    public static int EngineMode() {
        return EngineMode;
    }

    public static int UpdateRadius() {
        return UpdateRadius;
    }

    public static int InitialRadius() {
        return InitialRadius;
    }

    public static boolean UpdateOnBreak() {
        return UpdateOnBreak;
    }

    public static boolean UpdateOnDamage() {
        return UpdateOnDamage;
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
        if (id == -127) {
            return false;
        }
        Byte[] byteArray = TransparentBlocks;
        int n = TransparentBlocks.length;
        int n2 = 0;
        while (n2 < n) {
            byte i = byteArray[n2];
            if (id == i) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public static boolean isObfuscated(byte id) {
        if (id == 1) {
            return true;
        }
        Byte[] byteArray = ObfuscateBlocks;
        int n = ObfuscateBlocks.length;
        int n2 = 0;
        while (n2 < n) {
            byte i = byteArray[n2];
            if (id == i) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public static boolean isDarknessObfuscated(byte id) {
        Byte[] byteArray = DarknessObfuscateBlocks;
        int n = DarknessObfuscateBlocks.length;
        int n2 = 0;
        while (n2 < n) {
            byte i = byteArray[n2];
            if (id == i) {
                return true;
            }
            ++n2;
        }
        return false;
    }

    public static boolean emitsLight(byte id) {
        Byte[] byteArray = LightEmissionBlocks;
        int n = LightEmissionBlocks.length;
        int n2 = 0;
        while (n2 < n) {
            byte i = byteArray[n2];
            if (id == i) {
                return true;
            }
            ++n2;
        }
        return false;
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

    public static void SetUpdateOnBreak(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.UpdateOnBreak", data);
        UpdateOnBreak = data;
    }

    public static void SetUpdateOnDamage(boolean data) {
        OrebfuscatorConfig.SetData("Booleans.UpdateOnDamage", data);
        UpdateOnDamage = data;
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

    private static boolean GetBoolean(String path, boolean defaultData) {
        return config.getBoolean(path, defaultData);
    }

    private static int GetInt(String path, int defaultData) {
        return config.getInt(path, defaultData);
    }

    private static List<Integer> GetIntList(String path, List<Integer> defaultData) {
        return config.getIntList(path, defaultData);
    }

    private static void SetData(String path, Object data) {
        config.setProperty(path, data);
        config.save();
    }

    private static Byte[] IntListToByteArray(List<Integer> list) {
        Byte[] byteArray = new Byte[list.size()];
        int i = 0;
        while (i < byteArray.length) {
            byteArray[i] = (byte) list.get(i).intValue();
            ++i;
        }
        return byteArray;
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
        UpdateOnBreak = OrebfuscatorConfig.GetBoolean("Booleans.UpdateOnBreak", true);
        UpdateOnDamage = OrebfuscatorConfig.GetBoolean("Booleans.UpdateOnDamage", true);
        UpdateOnExplosion = OrebfuscatorConfig.GetBoolean("Booleans.UpdateOnExplosion", true);
        DarknessHideBlocks = OrebfuscatorConfig.GetBoolean("Booleans.DarknessHideBlocks", true);
        NoObfuscationForOps = OrebfuscatorConfig.GetBoolean("Booleans.NoObfuscationForOps", true);
        NoObfuscationForPermission = OrebfuscatorConfig.GetBoolean("Booleans.NoObfuscationForPermission", true);
        Enabled = OrebfuscatorConfig.GetBoolean("Booleans.Enabled", true);
        TransparentBlocks = OrebfuscatorConfig.IntListToByteArray(OrebfuscatorConfig.GetIntList("Lists.TransparentBlocks", Arrays.asList(6, 8, 9, 10, 11, 18, 20, 26, 27, 28, 30, 31, 32, 34, 37, 38, 39, 40, 44, 50, 51, 52, 53, 54, 55, 59, 63, 64, 65, 66, 67, 68, 69, 70, 71, 72, 75, 76, 77, 78, 79, 81, 83, 85, 90, 92, 93, 94, 96, 101, 102, 104, 105, 106, 107, 108, 109, 111, 113, 114, 115)));
        ObfuscateBlocks = OrebfuscatorConfig.IntListToByteArray(OrebfuscatorConfig.GetIntList("Lists.ObfuscateBlocks", Arrays.asList(14, 15, 16, 21, 54, 56, 73, 74)));
        DarknessObfuscateBlocks = OrebfuscatorConfig.IntListToByteArray(OrebfuscatorConfig.GetIntList("Lists.DarknessObfuscateBlocks", Arrays.asList(48, 52)));
        LightEmissionBlocks = OrebfuscatorConfig.IntListToByteArray(OrebfuscatorConfig.GetIntList("Lists.LightEmissionBlocks", Arrays.asList(10, 11, 50, 51, 62, 74, 76, 89, 90, 91, 94)));
        RandomBlocks = OrebfuscatorConfig.IntListToByteArray(OrebfuscatorConfig.GetIntList("Lists.RandomBlocks", Arrays.asList(14, 15, 16, 21, 56, 73)));
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

