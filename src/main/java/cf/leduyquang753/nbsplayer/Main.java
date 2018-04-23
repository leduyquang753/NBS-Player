package cf.leduyquang753.nbsplayer;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.xxmicloxx.NoteBlockAPI.NBSDecoder;
import com.xxmicloxx.NoteBlockAPI.NoteBlockSongPlayer;
import com.xxmicloxx.NoteBlockAPI.SongEndEvent;
import com.xxmicloxx.NoteBlockAPI.Sounds;

import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod(name="NBS Player", modid="nbsplayer", version="1.0", acceptedMinecraftVersions="[1.12, 1.12.2]")
public class Main {
	public static List<File> songs = new ArrayList<File>();
	private static Iterator<File> current = songs.iterator();
	public static boolean playing = false;
	public static NoteBlockSongPlayer player;
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(new Sounds());
		ClientCommandHandler.instance.registerCommand(new Start());
		ClientCommandHandler.instance.registerCommand(new Stop());
		ClientCommandHandler.instance.registerCommand(new Refresh());
		refreshSongs();
	}
	
	@SubscribeEvent
	public void onSongEnd(SongEndEvent event) {
		if (playing) next();
	}
	
	public static void sendMsg() {
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
				TextFormatting.GOLD + "Now playing: " +
				TextFormatting.GREEN + player.getSong().getAuthor() + " - " + player.getSong().getTitle()
				));
	}
	
	public static void play() {
		if (playing) player.destroy();
		if (songs.isEmpty()) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
					TextFormatting.RED + "There are not any songs in songs folder..."
					));
			return;
		}
		current = songs.iterator();
		playing = true;
		player = new NoteBlockSongPlayer(NBSDecoder.parse(current.next()));
		player.createThread();
		sendMsg();
	}
	
	public static void next() {
		player.destroy();
		if (!current.hasNext()) current = songs.iterator();
		player = new NoteBlockSongPlayer(NBSDecoder.parse(current.next()));
		player.createThread();
		sendMsg();
	}
	
	public static void stop() {
		if (playing) player.destroy();
		playing = false;
	}
	
	public static void refreshSongs() {
		stop();
		String path = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/songs";
		File songsFolder = new File(path);
		File[] files = songsFolder.listFiles();
		songs = new ArrayList<File>();
		for (File f : files) {
			if (f.getName().toLowerCase().endsWith(".nbs")) songs.add(f);
		}
	}
}
