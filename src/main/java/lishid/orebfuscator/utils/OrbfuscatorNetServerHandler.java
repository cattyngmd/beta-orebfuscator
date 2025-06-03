package lishid.orebfuscator.utils;

import net.minecraft.server.*;

public class OrbfuscatorNetServerHandler
        extends NetServerHandler {
    public OrbfuscatorNetServerHandler(MinecraftServer minecraftserver, NetworkManager networkmanager, EntityPlayer entityplayer) {
        super(minecraftserver, networkmanager, entityplayer);
    }

    public void sendPacket(Packet packet) {
        if (packet instanceof Packet51MapChunk) {
            if (packet.k) {
                if (!OrebfuscatorCalculationThread.isRunning()) {
                    OrebfuscatorCalculationThread.startThread();
                }
                OrebfuscatorCalculationThread.Queue((Packet51MapChunk) packet, this.getPlayer());
            } else {
                packet.k = true;
                super.sendPacket(packet);
            }
        } else {
            super.sendPacket(packet);
        }
    }
}

