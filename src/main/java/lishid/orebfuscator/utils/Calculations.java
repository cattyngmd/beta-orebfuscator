package lishid.orebfuscator.utils;

import lishid.orebfuscator.Orebfuscator;
import lishid.orebfuscator.OrebfuscatorConfig;
import net.minecraft.server.NetServerHandler;
import net.minecraft.server.Packet;
import net.minecraft.server.Packet51MapChunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.zip.Deflater;

public class Calculations {
    public static ArrayList<Block> GetAjacentBlocks(World world, ArrayList<Block> allBlocks, Block block, int countdown) {
        if (allBlocks == null) {
            allBlocks = new ArrayList();
        }
        Calculations.AddBlockCheck(allBlocks, block);
        if (countdown <= 0) {
            return allBlocks;
        }
        Calculations.AddBlockCheck(allBlocks, block.getRelative(BlockFace.UP));
        Calculations.AddBlockCheck(allBlocks, block.getRelative(BlockFace.DOWN));
        Calculations.AddBlockCheck(allBlocks, block.getRelative(BlockFace.NORTH));
        Calculations.AddBlockCheck(allBlocks, block.getRelative(BlockFace.SOUTH));
        Calculations.AddBlockCheck(allBlocks, block.getRelative(BlockFace.EAST));
        Calculations.AddBlockCheck(allBlocks, block.getRelative(BlockFace.WEST));
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.UP), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.DOWN), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.NORTH), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.SOUTH), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.EAST), countdown - 1);
        Calculations.GetAjacentBlocks(world, allBlocks, block.getRelative(BlockFace.WEST), countdown - 1);
        return allBlocks;
    }

    public static void AddBlockCheck(ArrayList<Block> allBlocks, Block block) {
        if (block == null) {
            return;
        }
        if (!allBlocks.contains(block) && OrebfuscatorConfig.isObfuscated((byte) block.getTypeId())) {
            allBlocks.add(block);
        }
    }

    public static void UpdateBlock(Block block) {
        if (block == null) {
            return;
        }
        int TypeID = block.getTypeId();
        block.setTypeId(1);
        block.setTypeId(TypeID);
        ArrayList<CraftPlayer> players = new ArrayList<CraftPlayer>();
        for (Player player : block.getWorld().getPlayers()) {
            if (!(Math.abs(player.getLocation().getX() - (double) block.getX()) < 176.0) || !(Math.abs(player.getLocation().getZ() - (double) block.getZ()) < 176.0))
                continue;
            players.add((CraftPlayer) player);
        }
        for (CraftPlayer craftPlayer : players) {
            craftPlayer.sendBlockChange(block.getLocation(), block.getTypeId(), block.getData());
        }
    }

    private static boolean isTransparentId(byte id) {
        return OrebfuscatorConfig.isTransparent(id);
    }

    public static ArrayList<Byte> GetAjacentBlocks(BlockInfo info, ArrayList<Byte> IDPool, int index, int x, int y, int z, int countdown) {
        if (IDPool == null) {
            IDPool = new ArrayList();
        }
        byte id = 1;
        if (y <= info.sizeY - 1 && y >= 0 && x <= info.sizeX - 1 && x >= 0 && z < info.sizeZ - 1 && z >= 0 && index > 0 && info.original.length > index) {
            id = info.original[index];
        } else if (info.startY >= 0) {
            id = (byte) info.world.getTypeId(x + info.startX, y + info.startY, z + info.startZ);
        }
        if (!IDPool.contains(id)) {
            IDPool.add(id);
        }
        if (countdown <= 0) {
            return IDPool;
        }
        Calculations.GetAjacentBlocks(info, IDPool, index + 1, x, y + 1, z, countdown - 1);
        Calculations.GetAjacentBlocks(info, IDPool, index - 1, x, y - 1, z, countdown - 1);
        Calculations.GetAjacentBlocks(info, IDPool, index + info.sizeY * info.sizeZ, x + 1, y, z, countdown - 1);
        Calculations.GetAjacentBlocks(info, IDPool, index - info.sizeY * info.sizeZ, x - 1, y, z, countdown - 1);
        Calculations.GetAjacentBlocks(info, IDPool, index + info.sizeY, x, y, z + 1, countdown - 1);
        Calculations.GetAjacentBlocks(info, IDPool, index - info.sizeY, x, y, z - 1, countdown - 1);
        return IDPool;
    }

    public static void Obfuscate(Packet51MapChunk packet, CraftPlayer player) {
        NetServerHandler handler = player.getHandle().netServerHandler;
        if (!Orebfuscator.usingSpout) {
            packet.k = false;
        }
        if (!(OrebfuscatorConfig.NoObfuscationForPermission() && PermissionRelay.hasPermission(player, "Orebfuscator.deobfuscate") || OrebfuscatorConfig.NoObfuscationForOps() && player.isOp() || !OrebfuscatorConfig.Enabled())) {
            BlockInfo info = new BlockInfo();
            info.sizeX = packet.d;
            info.sizeY = packet.e;
            info.sizeZ = packet.f;
            info.startX = packet.a;
            info.startY = packet.b;
            info.startZ = packet.c;
            info.world = player.getHandle().world.getWorld().getHandle();
            info.original = new byte[packet.rawData.length];
            System.arraycopy(packet.rawData, 0, info.original, 0, packet.rawData.length);
            if (info.sizeY > 1) {
                int blocks = info.sizeX * info.sizeY * info.sizeZ;
                boolean half_byte = false;
                int light_offset = blocks + blocks / 2;
                int sky_light_offset = blocks * 2;
                byte[] lightingarray = new byte[blocks];
                int index = 0;
                while (index < blocks) {
                    int lighting;
                    if (!half_byte) {
                        lighting = packet.rawData[sky_light_offset] & 0xF;
                    } else {
                        lighting = packet.rawData[sky_light_offset] >> 4;
                        ++sky_light_offset;
                    }
                    if (!half_byte) {
                        lighting |= (packet.rawData[light_offset] & 0xF) << 4;
                    } else {
                        lighting |= packet.rawData[light_offset] & 0xF0;
                        ++light_offset;
                    }
                    lightingarray[index] = (byte) lighting;
                    half_byte = !half_byte;
                    ++index;
                }
                int x = 0;
                while (x < info.sizeX) {
                    int z = 0;
                    while (z < info.sizeZ) {
                        int y = 0;
                        while (y < info.sizeY) {
                            int index2 = y + z * info.sizeY + x * info.sizeY * info.sizeZ;
                            boolean Obfuscate = false;
                            if (OrebfuscatorConfig.isObfuscated(info.original[index2])) {
                                ArrayList<Byte> IDs = Calculations.GetAjacentBlocks(info, null, index2, x, y, z, OrebfuscatorConfig.InitialRadius());
                                Obfuscate = true;
                                for (byte id : IDs) {
                                    if (!Calculations.isTransparentId(id)) continue;
                                    Obfuscate = false;
                                    break;
                                }
                            }
                            if (OrebfuscatorConfig.DarknessHideBlocks() && OrebfuscatorConfig.isDarknessObfuscated(info.original[index2]) && lightingarray[index2] == 0) {
                                Obfuscate = true;
                            }
                            if (Obfuscate) {
                                if (OrebfuscatorConfig.EngineMode() == 1) {
                                    packet.rawData[index2] = 1;
                                } else if (OrebfuscatorConfig.EngineMode() == 2) {
                                    packet.rawData[index2] = OrebfuscatorConfig.GenerateRandomBlock();
                                }
                            }
                            ++y;
                        }
                        ++z;
                    }
                    ++x;
                }
            }
        }
        Deflater deflater = new Deflater();
        byte[] deflateBuffer = new byte[82020];
        int dataSize = packet.rawData.length;
        if (deflateBuffer.length < dataSize + 100) {
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
        handler.sendPacket(packet);
    }

    public static void LightingUpdate(Block block, boolean skipCheck) {
        if (OrebfuscatorConfig.emitsLight((byte) block.getTypeId()) || skipCheck) {
            int x = block.getWorld().getChunkAt(block.getLocation()).getX();
            int z = block.getWorld().getChunkAt(block.getLocation()).getZ();
            block.getWorld().refreshChunk(x, z);
            block.getWorld().refreshChunk(x, z + 1);
            block.getWorld().refreshChunk(x, z - 1);
            block.getWorld().refreshChunk(x + 1, z);
            block.getWorld().refreshChunk(x + 1, z + 1);
            block.getWorld().refreshChunk(x + 1, z - 1);
            block.getWorld().refreshChunk(x - 1, z);
            block.getWorld().refreshChunk(x - 1, z + 1);
            block.getWorld().refreshChunk(x - 1, z - 1);
        }
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
}

