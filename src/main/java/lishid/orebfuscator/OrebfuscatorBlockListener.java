package lishid.orebfuscator;

import lishid.orebfuscator.utils.Calculations;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.block.*;

import java.util.ArrayList;
import java.util.HashMap;

public class OrebfuscatorBlockListener
        extends BlockListener {
    public static HashMap<Player, Block> playerLog = new HashMap();
    Orebfuscator plugin;

    public OrebfuscatorBlockListener(Orebfuscator plugin) {
        this.plugin = plugin;
    }

    public void onBlockBreak(BlockBreakEvent event) {
        if (event.isCancelled() || !OrebfuscatorConfig.UpdateOnBreak() || !OrebfuscatorConfig.Enabled()) {
            return;
        }
        if (OrebfuscatorConfig.isTransparent((byte) event.getBlock().getTypeId())) {
            return;
        }
        CraftBlock eventBlock = (CraftBlock) event.getBlock();
        ArrayList<Block> blocks = Calculations.GetAjacentBlocks(eventBlock.getWorld(), new ArrayList<Block>(), event.getBlock(), OrebfuscatorConfig.UpdateRadius());
        for (Block block : blocks) {
            Calculations.UpdateBlock(block);
        }
    }

    public void onBlockDamage(BlockDamageEvent event) {
        if (event.isCancelled() || !OrebfuscatorConfig.UpdateOnDamage() || !OrebfuscatorConfig.Enabled()) {
            return;
        }
        if (playerLog.containsKey(event.getPlayer()) && playerLog.get(event.getPlayer()).equals(event.getBlock())) {
            return;
        }
        playerLog.put(event.getPlayer(), event.getBlock());
        if (OrebfuscatorConfig.isTransparent((byte) event.getBlock().getTypeId())) {
            return;
        }
        CraftBlock eventBlock = (CraftBlock) event.getBlock();
        ArrayList<Block> blocks = Calculations.GetAjacentBlocks(eventBlock.getWorld(), new ArrayList<Block>(), event.getBlock(), OrebfuscatorConfig.UpdateRadius());
        for (Block block : blocks) {
            Calculations.UpdateBlock(block);
        }
    }

    public void onBlockPlace(BlockPlaceEvent event) {
        if (event.isCancelled() || !OrebfuscatorConfig.DarknessHideBlocks() || !OrebfuscatorConfig.Enabled()) {
            return;
        }
        Calculations.LightingUpdate(event.getBlock(), false);
    }

    public void onBlockIgnite(BlockIgniteEvent event) {
        if (event.isCancelled() || !OrebfuscatorConfig.DarknessHideBlocks() || !OrebfuscatorConfig.Enabled()) {
            return;
        }
        Calculations.LightingUpdate(event.getBlock(), true);
    }
}

