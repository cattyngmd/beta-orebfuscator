package lishid.orebfuscator.utils;

import net.minecraft.server.Packet51MapChunk;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.util.ArrayList;
import java.util.concurrent.LinkedBlockingDeque;

public class OrebfuscatorCalculationThread
        extends Thread
        implements Runnable {
    private static final int QUEUE_CAPACITY = 10240;
    private static final LinkedBlockingDeque<ObfuscatedPlayerPacket> queue = new LinkedBlockingDeque(10240);
    private static final ArrayList<OrebfuscatorCalculationThread> threads = new ArrayList();
    private boolean kill = false;

    public static int getThreads() {
        return threads.size();
    }

    public static boolean CheckThreads() {
        return threads.size() == OrebfuscatorConfig.ProcessingThreads();
    }

    public static void SyncThreads() {
        block4:
        {
            int extra;
            block3:
            {
                if (threads.size() == OrebfuscatorConfig.ProcessingThreads()) {
                    return;
                }
                extra = threads.size() - OrebfuscatorConfig.ProcessingThreads();
                if (extra <= 0) break block3;
                int i = extra;
                while (i > 0) {
                    OrebfuscatorCalculationThread.threads.get(i - 1).kill = true;
                    threads.remove(i - 1);
                    --i;
                }
                break block4;
            }
            if (extra >= 0) break block4;
            int i = 0;
            while (i < -extra) {
                OrebfuscatorCalculationThread thread = new OrebfuscatorCalculationThread();
                thread.start();
                thread.setName("Orebfuscator Calculation Thread");
                threads.add(thread);
                ++i;
            }
        }
    }

    public static void Queue(Packet51MapChunk packet, CraftPlayer player) {
        while (true) {
            try {
                queue.put(new ObfuscatedPlayerPacket(player, packet));
                return;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while (!this.isInterrupted() && !this.kill) {
            try {
                this.handle();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handle() {
        try {
            ObfuscatedPlayerPacket packet = queue.take();
            Calculations.Obfuscate(packet.packet, packet.player);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

