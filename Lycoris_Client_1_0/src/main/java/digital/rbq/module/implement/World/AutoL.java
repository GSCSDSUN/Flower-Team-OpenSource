package digital.rbq.module.implement.World;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import digital.rbq.Lycoris;
import digital.rbq.event.TickEvent;
import digital.rbq.module.Category;
import digital.rbq.module.Module;
import digital.rbq.module.value.BooleanValue;
import digital.rbq.module.value.ModeValue;
import digital.rbq.utility.AbuseUtil;
import digital.rbq.utility.TimeHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AutoL extends Module {
	public static final String LClient = "Novoline";
	public static TimeHelper LTimer = new TimeHelper();
	public static BooleanValue abuse = new BooleanValue("AutoInsult", "Insult", false);
	public static BooleanValue autowdr = new BooleanValue("AutoInsult", "AutoWDR", false);
	public static BooleanValue clientname = new BooleanValue("AutoInsult", "Client Name", true);
	public static BooleanValue autoL = new BooleanValue("AutoInsult", "AutoL", false);
	public static List<EntityPlayer> power = new ArrayList<EntityPlayer>();

	public static ModeValue mode = new ModeValue("AutoInsult", "Language", "English");

	public AutoL() {
		super("AutoInsult", Category.World, mode);
	}

	public static String getAutoLMessage(String PlayerName) {
		String abuse = "";
		if (AutoL.mode.isCurrentMode("Chinese")) {
//			abuse = getSB();
		} else if(AutoL.mode.isCurrentMode("English")) {
			abuse = AbuseUtil.getAbuseGlobal();
		}
		return "/ac " + (AutoL.clientname.getValueState() ? "[" + Lycoris.NAME + "] " : "")  + PlayerName + (AutoL.autoL.getValueState() ? " L" : "")+ (AutoL.abuse.getValueState() ? " " + abuse : "");
	}

}
