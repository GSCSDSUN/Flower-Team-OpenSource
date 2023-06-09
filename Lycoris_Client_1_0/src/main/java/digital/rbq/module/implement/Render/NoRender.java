package digital.rbq.module.implement.Render;

import com.darkmagician6.eventapi.EventTarget;

import net.minecraft.entity.item.EntityItem;
import digital.rbq.event.PreUpdateEvent;
import digital.rbq.module.Category;
import digital.rbq.module.Module;
import digital.rbq.module.value.BooleanValue;

public class NoRender extends Module {

    public static BooleanValue droppedItem = new BooleanValue("NoRender", "Dropped Item", false);
    public static BooleanValue tablist = new BooleanValue("NoRender", "Tablist", true);
    public static BooleanValue chat = new BooleanValue("NoRender", "Chatbox", true);
    public static BooleanValue players = new BooleanValue("NoRender", "Players", false);
    public static BooleanValue blockAnimation = new BooleanValue("NoRender", "Block Animation", false);

    public NoRender() {
        super("NoRender", Category.Render, false);
    }

    @EventTarget
    public void onUpdate(final PreUpdateEvent event) {
    	for (final Object o : this.mc.theWorld.loadedEntityList) {
    		if (o instanceof EntityItem && droppedItem.getValue()) {
    			final EntityItem i = (EntityItem) o;
    			this.mc.theWorld.removeEntity(i);
    		}
    	}
    }
}