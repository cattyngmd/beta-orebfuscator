package lishid.orebfuscator;

import lishid.orebfuscator.utils.Calculations;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

import java.util.ArrayList;

public class OrebfuscatorEntityListener
        extends EntityListener {
    Orebfuscator plugin;

    public OrebfuscatorEntityListener(Orebfuscator scrap) {
        this.plugin = scrap;
    }

    public void onEntityExplode(EntityExplodeEvent event) {
        if (event.isCancelled() || !OrebfuscatorConfig.UpdateOnExplosion() || !OrebfuscatorConfig.Enabled()) {
            return;
        }
        ArrayList<Block> blocks = new ArrayList<Block>();
        for (Block block : event.blockList()) {
            Calculations.GetAjacentBlocks(block.getWorld(), blocks, block, 1);
        }
        for (Block block : blocks) {
            Calculations.UpdateBlock(block);
        }
    }
}

