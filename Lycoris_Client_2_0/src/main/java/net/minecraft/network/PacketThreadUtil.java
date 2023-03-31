/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.network;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.ThreadQuickExitException;
import net.minecraft.util.IThreadListener;

public class PacketThreadUtil {
    public static <T extends INetHandler> void checkThreadAndEnqueue(Packet<T> p_180031_0_, T p_180031_1_, IThreadListener p_180031_2_) throws ThreadQuickExitException {
        if (!p_180031_2_.isCallingFromMinecraftThread()) {
            p_180031_2_.addScheduledTask(() -> p_180031_0_.processPacket(p_180031_1_));
            throw ThreadQuickExitException.field_179886_a;
        }
    }
}

