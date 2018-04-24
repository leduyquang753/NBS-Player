package cf.leduyquang753.nbsplayer;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;

public class Next extends CommandBase {
	@Override
	public String getName() {
		return "nbsnext";
	}

	@Override
	public String getUsage(ICommandSender arg0) {
		return "Skips to the next song.";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Main.next();
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
}



