package lishid.orebfuscator.utils;

import net.minecraft.server.Packet51MapChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.LinkedList;
import java.util.Queue;

public class ObfuscatedPlayer {
    CraftPlayer player;
    Queue<Packet51MapChunk> packetQueue = new LinkedList<Packet51MapChunk>();

    public ObfuscatedPlayer(CraftPlayer player) {
        this.player = player;
    }

    public void EnQueue(Packet51MapChunk packet) {
        this.packetQueue.add(packet);
    }
}

