package digital.rbq.module.implement.Combat;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.item.ItemBow;
import net.minecraft.network.play.client.C03PacketPlayer;
import digital.rbq.event.TickEvent;
import digital.rbq.module.Category;
import digital.rbq.module.Module;
import digital.rbq.module.value.ModeValue;

public class Fastbow extends Module {
    public static ModeValue mode = new ModeValue("Fastbow","Mode", "NCP", "NCP", "Vanilla");

    public Fastbow(){
        super("Fastbow", Category.Combat, mode);
    }

    @EventTarget
    public void onTick(TickEvent event) {
        if (this.mc.thePlayer.getItemInUseDuration() >= 15 || mode.getValue().equals("Vanilla")) {
            if (this.mc.thePlayer.onGround && this.mc.thePlayer.getItemInUse().getItem() instanceof ItemBow) {
                for (int i = 0; i < (mode.getValue().equals("Vanilla") ? 20 : 8); ++i)
                    this.mc.getNetHandler().addToSendQueue(new C03PacketPlayer(this.mc.thePlayer.onGround));
                this.mc.playerController.onStoppedUsingItem(this.mc.thePlayer);
            }
        }
    }
}
