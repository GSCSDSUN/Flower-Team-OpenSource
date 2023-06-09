package digital.rbq.module.implement.Player;

import com.darkmagician6.eventapi.EventTarget;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.ChatComponentText;
import digital.rbq.event.PacketReceiveEvent;
import digital.rbq.event.PacketSendEvent;
import digital.rbq.module.Category;
import digital.rbq.module.Module;
import digital.rbq.module.SubModule;
import digital.rbq.utility.PlayerUtils;

public class ChatBypass extends Module {
    public ChatBypass() {
        super("ChatBypass", Category.Player, false);
    }

    @EventTarget
    public void onPacketRe(PacketReceiveEvent e) {
        if (e.getPacket() instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat) e.getPacket();
            if (packet.getChatComponent().getUnformattedText().contains("\u061c")) {
                packet.chatComponent = new ChatComponentText(packet.getChatComponent().getUnformattedText().replace("\u061c", ""));
            }
        }
    }

    @EventTarget
    public void onSendPacket(PacketSendEvent event) {
        if (event.getPacket() instanceof C01PacketChatMessage) {
            final C01PacketChatMessage packetChatMessage = (C01PacketChatMessage)event.getPacket();
            if (packetChatMessage.getMessage().startsWith("/")) {
                return;
            }
            event.setCancelled(true);
            final StringBuilder msg = new StringBuilder();
            for (final char character : packetChatMessage.getMessage().toCharArray()) {
                msg.append(character + "\u061c");
            }
            mc.getNetHandler().getNetworkManager().sendPacketNoEvent(new C01PacketChatMessage(msg.toString().replaceFirst("%", "")));
        }
    }
}
