/*
 * Decompiled with CFR 0.150.
 */
package digital.rbq.utils.font;

import java.awt.Font;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import digital.rbq.utils.font.CustomFontRenderer;

public final class FontRendererHook
extends FontRenderer {
    private final CustomFontRenderer fontRenderer;

    public FontRendererHook(Font font, boolean antiAliasing, boolean fractionalMetrics) {
        super(Minecraft.getMinecraft().gameSettings, new ResourceLocation("textures/font/ascii.png"), Minecraft.getMinecraft().renderEngine, false);
        this.fontRenderer = new CustomFontRenderer(font, antiAliasing, fractionalMetrics);
    }

    @Override
    protected int renderString(String text, float x, float y, int color, boolean dropShadow) {
        if (text == null) {
            return 0;
        }
        if (this.bidiFlag) {
            text = this.bidiReorder(text);
        }
        if ((color & 0xFC000000) == 0) {
            color |= 0xFF000000;
        }
        if (dropShadow) {
            color = (color & 0xFCFCFC) >> 2 | color & 0xFF000000;
        }
        this.red = (float)(color >> 16 & 0xFF) / 255.0f;
        this.blue = (float)(color >> 8 & 0xFF) / 255.0f;
        this.green = (float)(color & 0xFF) / 255.0f;
        this.alpha = (float)(color >> 24 & 0xFF) / 255.0f;
        GlStateManager.color(this.red, this.blue, this.green, this.alpha);
        this.posX = x;
        this.posY = y;
        this.fontRenderer.drawString(text, x, y, color, dropShadow);
        return (int)this.posX;
    }

    @Override
    public int getStringWidth(String text) {
        return this.fontRenderer.getStringWidth(text);
    }
}

