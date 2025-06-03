package lishid.orebfuscator.utils;

import gnu.trove.set.hash.TByteHashSet;
import lishid.orebfuscator.Orebfuscator;
import net.minecraft.server.*;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.zip.Deflater;

public class Calculations {
    private static final int CHUNK_SIZE = 81920;
    private static final int REDUCED_DEFLATE_THRESHOLD = 20480;
    private static final int DEFLATE_LEVEL_CHUNKS = 6;
    private static final int DEFLATE_LEVEL_PARTS = 1;
    private static final Deflater deflater = new Deflater();
    private static byte[] deflateBuffer = new byte[82020];

    public static void UpdateBlocksNearby(Block block) {
        if (!OrebfuscatorConfig.Enabled() || OrebfuscatorConfig.isTransparent((byte) block.getTypeId())) {
            return;
        }
        HashSet<Block> blocks = Calculations.GetAjacentBlocks(block.getWorld(), new HashSet<Block>(), block, OrebfuscatorConfig.UpdateRadius());
        Calculations.UpdateBlock(block);
        for (Block nearbyBlock : blocks) {
            Calculations.UpdateBlock(nearbyBlock);
        }
    }

    public static HashSet<Block> GetAjacentBlocks(World world, HashSet<Block> allBlocks, Block block, int countdown) {
        Calculations.AddBlockCheck(allBlocks, block);
        if (countdown == 0) {
            return allBlocks;
        }
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.UP), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.DOWN), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.NORTH), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.SOUTH), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.EAST), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.WEST), countdown - 1);
        return allBlocks;
    }

    public static void AddBlockCheck(HashSet<Block> allBlocks, Block block) {
        if (block == null) {
            return;
        }
        if (OrebfuscatorConfig.isObfuscated((byte) block.getTypeId()) || OrebfuscatorConfig.isDarknessObfuscated((byte) block.getTypeId())) {
            allBlocks.add(block);
        }
    }

    public static void UpdateBlock(Block block) {
        if (block == null) {
            return;
        }
        HashSet<CraftPlayer> players = new HashSet<CraftPlayer>();
        for (Player player : block.getWorld().getPlayers()) {
            if (!(Math.abs(player.getLocation().getX() - (double) block.getX()) < 176.0) || !(Math.abs(player.getLocation().getZ() - (double) block.getZ()) < 176.0))
                continue;
            players.add((CraftPlayer) player);
        }
        for (CraftPlayer craftPlayer : players) {
            craftPlayer.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
        }
    }

    public static boolean GetAjacentBlocksTypeID(BlockInfo info, TByteHashSet IDPool, int index, int x, int y, int z, int countdown) {
        byte id = 0;
        if (y > 126) {
            return true;
        }
        if (y < info.sizeY && y >= 0 && x < info.sizeX && x >= 0 && z < info.sizeZ && z >= 0 && index > 0 && info.original.length > index) {
            id = info.original[index];
        } else if (info.startY >= 0) {
            id = (byte) info.world.getTypeId(x + info.startX, y + info.startY, z + info.startZ);
        }
        if (!IDPool.contains(id) && OrebfuscatorConfig.isTransparent(id)) {
            return true;
        }
        if (!IDPool.contains(id)) {
            IDPool.add(id);
        }
        if (countdown == 0) {
            return false;
        }
        if (Calculations.GetAjacentBlocksTypeID(info, IDPool, index + 1, x, y + 1, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksTypeID(info, IDPool, index - 1, x, y - 1, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksTypeID(info, IDPool, index + info.sizeY * info.sizeZ, x + 1, y, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksTypeID(info, IDPool, index - info.sizeY * info.sizeZ, x - 1, y, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksTypeID(info, IDPool, index + info.sizeY, x, y, z + 1, countdown - 1)) {
            return true;
        }
        return Calculations.GetAjacentBlocksTypeID(info, IDPool, index - info.sizeY, x, y, z - 1, countdown - 1);
    }

    public static boolean GetAjacentBlocksHaveLight(BlockInfo info, int index, int x, int y, int z, int countdown) {
        if (info.world.getLightLevel(x + info.startX, y + info.startY, z + info.startZ) > 0) {
            return true;
        }
        if (countdown == 0) {
            return false;
        }
        if (Calculations.GetAjacentBlocksHaveLight(info, index + 1, x, y + 1, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksHaveLight(info, index - 1, x, y - 1, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksHaveLight(info, index + info.sizeY * info.sizeZ, x + 1, y, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksHaveLight(info, index - info.sizeY * info.sizeZ, x - 1, y, z, countdown - 1)) {
            return true;
        }
        if (Calculations.GetAjacentBlocksHaveLight(info, index + info.sizeY, x, y, z + 1, countdown - 1)) {
            return true;
        }
        return Calculations.GetAjacentBlocksHaveLight(info, index - info.sizeY, x, y, z - 1, countdown - 1);
    }

    public static void Obfuscate(Packet51MapChunk packet, CraftPlayer player) {
        int dataSize;
        NetServerHandler handler = player.getHandle().netServerHandler;
        if (!Orebfuscator.usingSpout) {
            packet.k = false;
        }
        BlockInfo info = new BlockInfo();
        info.world = player.getHandle().world.getWorld().getHandle();
        info.startX = packet.a;
        info.startY = packet.b;
        info.startZ = packet.c;
        info.sizeX = packet.d;
        info.sizeY = packet.e;
        info.sizeZ = packet.f;
        TByteHashSet blockList = new TByteHashSet();
        if (!(info.world.getWorld().getEnvironment() != World.Environment.NORMAL || OrebfuscatorConfig.worldDisabled(info.world.getServer().getName()) || OrebfuscatorConfig.NoObfuscationForPermission() && PermissionRelay.hasPermission(player, "Orebfuscator.deobfuscate") || OrebfuscatorConfig.NoObfuscationForOps() && player.isOp() || !OrebfuscatorConfig.Enabled())) {
            info.original = new byte[packet.rawData.length];
            System.arraycopy(packet.rawData, 0, info.original, 0, packet.rawData.length);
            if (info.sizeY > 1) {
                int index = 0;
                int x = 0;
                while (x < info.sizeX) {
                    int z = 0;
                    while (z < info.sizeZ) {
                        int y = 0;
                        while (y < info.sizeY) {
                            boolean Obfuscate = false;
                            blockList.clear();
                            if (OrebfuscatorConfig.isObfuscated(info.original[index])) {
                                if (OrebfuscatorConfig.InitialRadius() == 0) {
                                    Obfuscate = true;
                                } else {
                                    boolean bl = Obfuscate = !Calculations.GetAjacentBlocksTypeID(info, blockList, index, x, y, z, OrebfuscatorConfig.InitialRadius());
                                }
                            }
                            if (!Obfuscate && OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.isDarknessObfuscated(info.original[index])) {
                                if (OrebfuscatorConfig.InitialRadius() == 0) {
                                    Obfuscate = true;
                                } else if (!Calculations.GetAjacentBlocksHaveLight(info, index, x, y, z, OrebfuscatorConfig.InitialRadius())) {
                                    Obfuscate = true;
                                }
                            }
                            if (Obfuscate) {
                                if (OrebfuscatorConfig.EngineMode() == 1) {
                                    packet.rawData[index] = 1;
                                } else if (OrebfuscatorConfig.EngineMode() == 2) {
                                    packet.rawData[index] = OrebfuscatorConfig.GenerateRandomBlock();
                                }
                            }
                            ++index;
                            ++y;
                        }
                        ++z;
                    }
                    ++x;
                }
            }
        }
        if (deflateBuffer.length < (dataSize = packet.rawData.length) + 100) {
            deflateBuffer = new byte[dataSize + 100];
        }
        deflater.reset();
        deflater.setLevel(dataSize < 20480 ? 1 : 6);
        deflater.setInput(packet.rawData);
        deflater.finish();
        int size = deflater.deflate(deflateBuffer);
        if (size == 0) {
            size = deflater.deflate(deflateBuffer);
        }
        packet.g = new byte[size];
        packet.h = size;
        System.arraycopy(deflateBuffer, 0, packet.g, 0, size);
        while (!Calculations.GetNetworkManagerQueue(handler.networkManager, 0x100000 - 2 * (18 + packet.h))) {
            try {
                Thread.sleep(5L);
            } catch (Exception z) {
                // empty catch block
            }
        }
        handler.networkManager.queue(packet);
        Object[] list = info.world.getTileEntities(info.startX, info.startY, info.startZ, info.startX + info.sizeX, info.startY + info.sizeY, info.startZ + info.sizeZ).toArray();
        int i = 0;
        while (i < list.length) {
            Packet p;
            TileEntity tileentity = (TileEntity) list[i];
            if (tileentity != null && (p = tileentity.f()) != null) {
                handler.sendPacket(p);
            }
            ++i;
        }
    }

    public static boolean GetNetworkManagerQueue(NetworkManager networkManager, int number) {
        try {
            Field p = networkManager.getClass().getDeclaredField("x");
            p.setAccessible(true);
            return Integer.parseInt(p.get(networkManager).toString()) < number;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void LightingUpdate(Block block, boolean skipCheck) {
    }

    public static String MD5(byte[] data) {
        try {
            MessageDigest algorithm = MessageDigest.getInstance("MD5");
            algorithm.reset();
            algorithm.update(data);
            byte[] messageDigest = algorithm.digest();
            StringBuffer hexString = new StringBuffer();
            int i = 0;
            while (i < messageDigest.length) {
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
                ++i;
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public int getIndex(int x, int y, int z) {
        return (x & 0xF) << 11 | (z & 0xF) << 7 | y & 0x7F;
    }
}

