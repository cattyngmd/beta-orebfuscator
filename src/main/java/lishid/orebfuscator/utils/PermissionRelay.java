package lishid.orebfuscator.utils;

import org.bukkit.entity.Player;

public class PermissionRelay {
    public static boolean hasPermission(Player player, String permission) {
        return player.hasPermission(permission);
    }
}

