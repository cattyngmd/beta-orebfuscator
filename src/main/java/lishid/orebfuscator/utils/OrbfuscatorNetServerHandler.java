package lishid.orebfuscator.utils;

import net.minecraft.server.*;

public class OrbfuscatorNetServerHandler
        extends NetServerHandler {
    public OrbfuscatorNetServerHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }

    public void sendPacket(Packet packet) {
        if (packet instanceof Packet51MapChunk) {
            if (!OrebfuscatorCalculationThread.CheckThreads()) {
                OrebfuscatorCalculationThread.SyncThreads();
            }
            OrebfuscatorCalculationThread.Queue((Packet51MapChunk) packet, this.getPlayer());
        } else {
            super.sendPacket(packet);
        }
    }
}

