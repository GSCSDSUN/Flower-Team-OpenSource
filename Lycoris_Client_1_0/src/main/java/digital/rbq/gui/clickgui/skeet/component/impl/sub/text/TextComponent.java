package digital.rbq.gui.clickgui.skeet.component.impl.sub.text;

import digital.rbq.gui.fontRenderer.FontUtils;
import digital.rbq.gui.clickgui.skeet.LockedResolution;
import digital.rbq.gui.clickgui.skeet.SkeetClickGUI;
import digital.rbq.gui.clickgui.skeet.component.Component;

public final class TextComponent extends Component {
    private static final FontUtils FONT_RENDERER = SkeetClickGUI.FONT_RENDERER;
    private final String text;

    public TextComponent(Component parent, String text, float x, float y) {
        super(parent, x, y, FONT_RENDERER.getStringWidth(text), FONT_RENDERER.getHeight(text));
        this.text = text;
    }

    @Override
    public void drawComponent(LockedResolution resolution, int mouseX, int mouseY) {
        if (SkeetClickGUI.shouldRenderText()) {
            FONT_RENDERER.drawString(this.text, this.getX(), this.getY(), SkeetClickGUI.getColor(0xE6E6E6));
        }
    }
}

