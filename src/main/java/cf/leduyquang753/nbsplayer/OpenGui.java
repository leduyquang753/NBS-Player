package cf.leduyquang753.nbsplayer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class OpenGui extends CommandBase {
	@Override
	public String getName() {
		return "nbsgui";
	}

	@Override
	public String getUsage(ICommandSender arg0) {
		return "Displays NBS Player's GUI.";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Main.shouldOpenGui = true;
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
}



