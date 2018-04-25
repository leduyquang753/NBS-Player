package cf.leduyquang753.nbsplayer;

import org.lwjgl.input.Keyboard;

import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;

public class Keys {
	public static KeyBinding startStop;
	public static KeyBinding openGui;
	
	public static void register() {
		startStop = new KeyBinding("Start/Pause", Keyboard.KEY_DOWN, "NBS Player");
		ClientRegistry.registerKeyBinding(startStop);
		openGui = new KeyBinding("Open GUI", Keyboard.KEY_UP, "NBS Player");
		ClientRegistry.registerKeyBinding(openGui);
	}
	
	@SubscribeEvent
	public void onKeyPress(KeyInputEvent event) {
		if (startStop.isPressed()) {
			if (Main.playing) {
				Main.stop();
				Minecraft.getMinecraft().player.sendMessage(new TextComponentString("Stopped playing."));
			} else Main.continuu();
		}
		if (openGui.isPressed()) Minecraft.getMinecraft().displayGuiScreen(new GuiSongs());
	}
}
