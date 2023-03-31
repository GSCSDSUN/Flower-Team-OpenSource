/*
 * Decompiled with CFR 0.150.
 */
package digital.rbq.clickgui.panel.component.impl;

import java.awt.Color;
import net.minecraft.client.gui.Gui;
import digital.rbq.clickgui.panel.Panel;
import digital.rbq.clickgui.panel.component.Component;
import digital.rbq.module.option.impl.EnumOption;
import digital.rbq.utils.ColorUtils;

public final class EnumOptionComponent
extends Component {
    private final EnumOption<Enum<?>> option;
    private int opacity = 120;

    public EnumOptionComponent(EnumOption<Enum<?>> option, Panel panel, int x, int y, int width, int height) {
        super(panel, x, y, width, height);
        this.option = option;
    }

    @Override
    public void onDraw(int mouseX, int mouseY) {
        Panel parent = this.getPanel();
        int x = parent.getX() + this.getX();
        int y = parent.getY() + this.getY();
        boolean hovered = this.isMouseOver(mouseX, mouseY);
        if (hovered) {
            if (this.opacity < 200) {
                this.opacity += 5;
            }
        } else if (this.opacity > 120) {
            this.opacity -= 5;
        }
        Gui.drawRect(x, y, x + this.getWidth(), y + this.getHeight(), ColorUtils.getColorWithOpacity(BACKGROUND, 255 - this.opacity).getRGB());
        int color = new Color(this.opacity, this.opacity, this.opacity).getRGB();
        FONT_RENDERER.drawStringWithShadow(String.format("%s: %s", this.option.getLabel(), this.option.getValue()), (float)x + 2.0f, (float)y + (float)this.getHeight() / 2.0f - 4.0f, color);
    }

    @Override
    public void onMouseClick(int mouseX, int mouseY, int mouseButton) {
        if (this.isMouseOver(mouseX, mouseY)) {
            this.option.setValue(this.option.getValues()[(((Enum)this.option.getValue()).ordinal() + 1) % this.option.getValues().length]);
        }
    }

    @Override
    public boolean isHidden() {
        return !this.option.check();
    }
}

