package lishid.orebfuscator;

import lishid.orebfuscator.utils.Calculations;
import lishid.orebfuscator.utils.OrbfuscatorNetServerHandler;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.NetworkManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftServer;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.lang.reflect.Field;

public class OrebfuscatorPlayerListener
        extends PlayerListener {
    private static final double MAX_REACH_DIST_SQR = 5 * 5;
    Orebfuscator plugin;

    public OrebfuscatorPlayerListener(Orebfuscator plugin) {
        this.plugin = plugin;
    }

    public void onPlayerJoin(PlayerJoinEvent event) {
        this.TryUpdateNetServerHandler(event.getPlayer());
    }

    public void onPlayerQuit(PlayerQuitEvent event) {
        OrebfuscatorBlockListener.playerLog.remove(event.getPlayer());
    }

    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.isCancelled() || !OrebfuscatorConfig.DarknessHideBlocks() || !OrebfuscatorConfig.Enabled() || event.useInteractedBlock() == Event.Result.DENY || event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        Block bp = event.getClickedBlock();
        if (bp.getX() * bp.getX() + bp.getZ() * bp.getZ() >= MAX_REACH_DIST_SQR) {
            return;
        }

        if (event.getMaterial().getId() == 10 || event.getMaterial().getId() == 11 || event.getMaterial().getId() == 327) {
            Calculations.LightingUpdate(event.getClickedBlock(), true);
        }
    }

    public void TryUpdateNetServerHandler(Player player) {
        try {
            this.updateNetServerHandler(player);
        } catch (Exception e) {
            System.out.println("Error updating NerServerHandler.");
            e.printStackTrace();
        }
    }

    public void updateNetServerHandler(Player player) {
        CraftPlayer cp = (CraftPlayer) player;
        CraftServer server = (CraftServer) Bukkit.getServer();
        if (!cp.getHandle().netServerHandler.getClass().equals(OrbfuscatorNetServerHandler.class)) {
            NetServerHandler oldHandler = cp.getHandle().netServerHandler;
            Location loc = player.getLocation();
            OrbfuscatorNetServerHandler handler = new OrbfuscatorNetServerHandler(server.getHandle().server, cp.getHandle().netServerHandler.networkManager, cp.getHandle());
            handler.a(loc.getX(), loc.getY(), loc.getZ(), loc.getYaw(), loc.getPitch());
            cp.getHandle().netServerHandler = handler;
            NetworkManager nm = cp.getHandle().netServerHandler.networkManager;
            this.setNetServerHandler(nm, handler);
            oldHandler.disconnected = true;
            ((CraftServer) player.getServer()).getServer().networkListenThread.a(handler);
        }
    }

    public void setNetServerHandler(NetworkManager nm, NetServerHandler nsh) {
        try {
            Field p = nm.getClass().getDeclaredField("p");
            p.setAccessible(true);
            p.set(nm, nsh);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}

