package digital.rbq.module.implement.Movement.longjump;

import com.darkmagician6.eventapi.EventTarget;
import digital.rbq.module.implement.Movement.LongJump;
import digital.rbq.module.implement.Movement.Speed;
import digital.rbq.event.MoveEvent;
import digital.rbq.event.PostUpdateEvent;
import digital.rbq.module.ModuleManager;
import digital.rbq.module.SubModule;
import digital.rbq.utility.ChatUtils;
import digital.rbq.utility.MathUtils;

/**
 * Created by John on 2016/12/16.
 */
public class Normal extends SubModule {
    private int stage;
    private double moveSpeed, lastDist;
    private double boost = 5.0;

    public Normal(){
        super("Normal", "LongJump");
        this.stage = 1;
        this.moveSpeed = 0.1873;
    }

    @Override
    public void onEnable(){
        super.onEnable();

        this.stage = 1;
        this.moveSpeed = 0.1873;
        this.jumped = false;
        this.mc.getTimer().timerSpeed = 1.0f;
    }

    @Override
    public void onDisable() {
        super.onDisable();

        mc.timer.timerSpeed = 1;
        this.moveSpeed = 0.1873;
        this.lastDist = 0;
        this.stage = 4;
        this.jumped = false;
        this.mc.getTimer().timerSpeed = 1.0f;
    }
    private boolean jumped;
    @EventTarget
    public void onMove(MoveEvent event) {
        this.mc.getTimer().timerSpeed = 1.0f;
        if (MathUtils.roundToPlace(this.mc.thePlayer.posY - (int) this.mc.thePlayer.posY, 3) == MathUtils.roundToPlace(0.41, 3)) {
            mc.thePlayer.motionY = 0;
        }
        if (this.mc.thePlayer.moveStrafing < 0 && this.mc.thePlayer.moveForward < 0) {
            this.stage = 1;
        }
        if (MathUtils.round(this.mc.thePlayer.posY - (int) this.mc.thePlayer.posY, 3) == MathUtils.round(0.943, 3)) {
            mc.thePlayer.motionY = 0;
        }

        if (this.stage == 1 && (this.mc.thePlayer.moveForward != 0.0f || this.mc.thePlayer.moveStrafing != 0.0f) && this.mc.thePlayer.isCollidedVertically) {
            //start jump
            this.stage = 2;
            this.moveSpeed = this.boost * 0.1873 - 0.01;

        } else if (this.stage == 2) {
            this.stage = 3;
            this.mc.thePlayer.motionY = 0.399;
            event.y = 0.399;
            this.moveSpeed *= 2.149802;

            this.jumped = true;
        } else if (this.stage == 3) {
            this.stage = 4;
            final double difference = 0.66 * (this.lastDist - 0.1873);
            this.moveSpeed = this.lastDist - difference;
        } else if(this.stage == 4) {
            this.mc.getTimer().timerSpeed = 0.7f;

            this.moveSpeed = this.lastDist - this.lastDist / 159.0;

            if (mc.theWorld.getCollidingBoundingBoxes(this.mc.thePlayer, this.mc.thePlayer.getEntityBoundingBox().offset(0.0, this.mc.thePlayer.motionY, 0.0)).size() > 0 || this.mc.thePlayer.isCollidedVertically) {
                this.stage = 1;

                if(LongJump.autoToggle.getValue() && this.jumped) {
                    ModuleManager.longJumpMod.toggle();
                }

                //GLIDE
                if(event.y < 0) {
                    event.y *= 0.5;
                }
            }
        }

        this.moveSpeed = Math.max(this.moveSpeed, 0.1873);
        Speed.setMoveSpeed(event, this.moveSpeed);
        ChatUtils.debug(this.moveSpeed);

    }

    @EventTarget
    public void onUpdate(PostUpdateEvent event) {
        double xDist = this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX;
        double zDist = this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ;
        this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    }
}
