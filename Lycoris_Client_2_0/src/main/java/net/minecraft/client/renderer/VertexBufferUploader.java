/*
 * Decompiled with CFR 0.150.
 */
package net.minecraft.client.renderer;

import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexBuffer;

public class VertexBufferUploader
extends WorldVertexBufferUploader {
    private VertexBuffer vertexBuffer = null;

    @Override
    public void func_181679_a(WorldRenderer p_181679_1_) {
        p_181679_1_.reset();
        this.vertexBuffer.func_181722_a(p_181679_1_.getByteBuffer());
    }

    public void setVertexBuffer(VertexBuffer vertexBufferIn) {
        this.vertexBuffer = vertexBufferIn;
    }
}

