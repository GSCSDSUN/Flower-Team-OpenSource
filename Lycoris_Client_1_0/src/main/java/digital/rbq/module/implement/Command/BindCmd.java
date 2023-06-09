package digital.rbq.module.implement.Command;

import org.lwjgl.input.Keyboard;

import net.minecraft.util.EnumChatFormatting;
import digital.rbq.Lycoris;
import digital.rbq.module.Command;
import digital.rbq.module.Command.Info;
import digital.rbq.module.Module;
import digital.rbq.module.ModuleManager;
import digital.rbq.utility.ChatUtils;

@Info(name = "bind", syntax = { "<module> <key> | clear" }, help = "Bind a module to a key")
public class BindCmd extends Command {
    @Override
    public void execute(String[] args) throws Error {
        if (args.length < 1) {
            this.syntaxError();
            return;
        }

        if (args[0].equalsIgnoreCase("clear")) {
            for (Module mod : ModuleManager.getModList()) {
                mod.setBind(Keyboard.KEY_NONE);
            }
            ChatUtils.sendMessageToPlayer("Cleared all binds.");
            return;
        }

        if (args.length < 2) {
            this.syntaxError();
            return;
        }

        Module mod = Lycoris.INSTANCE.getModuleManager().getModuleByName(args[0]);
        if (mod != null) {
            int key = Keyboard.getKeyIndex(args[1].toUpperCase());
            if (key != -1) {
                mod.setBind(key);
                ChatUtils.sendMessageToPlayer(EnumChatFormatting.GOLD + mod.getName() + EnumChatFormatting.RESET + " was bound to " + EnumChatFormatting.GOLD + Keyboard.getKeyName(mod.getBind()) + EnumChatFormatting.RESET + " Key.");
            } else {
                ChatUtils.sendMessageToPlayer("Invalid key code (" + args[1] + ")");
            }
        } else {
            ChatUtils.sendMessageToPlayer("Invalid module name (" + args[0] + ")");
        }

    }
}
