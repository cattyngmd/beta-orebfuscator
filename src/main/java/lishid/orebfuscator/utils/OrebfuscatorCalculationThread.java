package lishid.orebfuscator.utils;

import net.minecraft.server.Packet51MapChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicBoolean;

public class OrebfuscatorCalculationThread
        extends Thread
        implements Runnable {
    private static final OrebfuscatorCalculationThread instance = new OrebfuscatorCalculationThread();
    private static int TotalPackets = 0;
    private static Thread thread = null;
    private static boolean runs = false;
    private final Queue<ObfuscatedPlayer> queue = new LinkedList<ObfuscatedPlayer>();
    private final AtomicBoolean kill = new AtomicBoolean(false);

    public static boolean isRunning() {
        return runs;
    }

    public static void startThread() {
        if (!runs) {
            runs = true;
            thread = new Thread(instance);
            thread.start();
        }
    }

    public static void endThread() {
        OrebfuscatorCalculationThread.instance.kill.set(true);
        if (thread != null) {
            thread.interrupt();
        }
        try {
            thread.join();
        } catch (InterruptedException interruptedException) {
            // empty catch block
        }
        thread = null;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        while (thread != null && !thread.isInterrupted() && !this.kill.get()) {
            if (TotalPackets == 0) {
                Queue<ObfuscatedPlayer> queue = this.queue;
                synchronized (queue) {
                    try {
                        this.queue.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                this.handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handle() {
        try {
            if (!this.queue.isEmpty()) {
                ObfuscatedPlayer player;
                synchronized(this.queue) {
                    player = this.queue.poll();
                }

                if (!player.packetQueue.isEmpty()) {
                    Calculations.Obfuscate(player.packetQueue.poll(), player.player);
                    --TotalPackets;
                }

                if (player.player.isOnline()) {
                    synchronized(this.queue) {
                        this.queue.add(player);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void Queue(Packet51MapChunk packet, CraftPlayer player) {
        ++TotalPackets;
        synchronized(instance.queue) {
            for(ObfuscatedPlayer playerQueue : instance.queue) {
                if (playerQueue.player == player) {
                    playerQueue.EnQueue(packet);
                    instance.queue.notify();
                    return;
                }
            }

            ObfuscatedPlayer playerQueue = new ObfuscatedPlayer(player);
            playerQueue.EnQueue(packet);
            instance.queue.add(playerQueue);
            instance.queue.notify();
        }
    }
}

