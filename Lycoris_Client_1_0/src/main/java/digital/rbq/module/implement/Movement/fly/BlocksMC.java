package digital.rbq.module.implement.Movement.fly;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.util.EnumChatFormatting;
import digital.rbq.event.MoveEvent;
import digital.rbq.module.SubModule;
import digital.rbq.utility.ChatUtils;

public class BlocksMC extends SubModule {

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    public BlocksMC() {
        super("BlocksMC", "Fly");
    }

    @EventTarget
    public void onMove2(MoveEvent event) {
        if (mc.thePlayer.fallDistance >= 1) {
            mc.thePlayer.fallDistance = 0;
            mc.thePlayer.jump();
            event.setY(0.42D);
        }
    }
}
