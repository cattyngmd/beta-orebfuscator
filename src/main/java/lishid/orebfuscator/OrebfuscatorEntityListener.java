package lishid.orebfuscator;

import lishid.orebfuscator.utils.Calculations;
import lishid.orebfuscator.utils.OrebfuscatorConfig;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityListener;

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
        for (Block block : event.blockList()) {
            Calculations.UpdateBlocksNearby(block);
        }
    }
}

