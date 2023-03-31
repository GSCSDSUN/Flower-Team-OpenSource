/*
 * Decompiled with CFR 0.150.
 */
package digital.rbq.clickgui.panel.component;

import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import digital.rbq.clickgui.panel.Panel;

public abstract class Component {
    protected static final Minecraft MC = Minecraft.getMinecraft();
    protected static final Color BACKGROUND = new Color(26, 26, 26);
    protected static final FontRenderer FONT_RENDERER = Component.MC.fontRenderer;
    protected static final FontRenderer FONT_RENDERER_SMALL = Component.MC.fontRendererSmall;
    protected static final FontRenderer FONT_RENDERER_TINY = Component.MC.fontRendererTiny;
    private final Panel panel;
    private final int x;
    private final int width;
    private int y;
    protected final int height;

    public Component(Panel panel, int x, int y, int width, int height) {
        this.panel = panel;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Panel getPanel() {
        return this.panel;
    }

    public void onDraw(int mouseX, int mouseY) {
    }

    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
    }

    public void onMouseRelease(int mouseX, int mouseY, int mouseButton) {
    }

    public void onKeyPress(int typedChar, int keyCode) {
    }

    public final boolean isMouseOver(int mouseX, int mouseY) {
        int x = this.panel.getX() + this.x;
        int y = this.panel.getY() + this.y;
        return mouseX > x && mouseX < x + this.width && mouseY > y && mouseY < y + this.height;
    }

    public double getOffset() {
        return 0.0;
    }

    public int getX() {
        return this.x;
    }

    public int getWidth() {
        return this.width;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public int getHeight() {
        return this.height;
    }

    public boolean isHidden() {
        return false;
    }
}

