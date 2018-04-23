package cf.leduyquang753.nbsplayer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class Start extends CommandBase {
	@Override
	public String getName() {
		return "nbsplay";
	}

	@Override
	public String getUsage(ICommandSender arg0) {
		return "Plays songs...";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Main.play();
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
}



