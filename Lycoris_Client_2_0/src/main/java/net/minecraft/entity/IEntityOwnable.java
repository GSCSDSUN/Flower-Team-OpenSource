/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.entity;

import net.minecraft.entity.Entity;

public interface IEntityOwnable {
    public String getOwnerId();

    public Entity getOwner();
}

