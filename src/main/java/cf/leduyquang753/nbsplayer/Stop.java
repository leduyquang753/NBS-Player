package cf.leduyquang753.nbsplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

public class Stop extends CommandBase {
	@Override
	public String getName() {
		return "nbsstop";
	}

	@Override
	public String getUsage(ICommandSender arg0) {
		return "Stops playing.";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		Main.refreshSongs();
		Minecraft.getMinecraft().player.sendMessage(
				new TextComponentString("Stopped playing."));
	}
	
	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}
}



