package cf.leduyquang753.nbsplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cf.leduyquang753.nbsapi.Song;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod(name="NBS Player", modid="nbsplayer", version="0.2", acceptedMinecraftVersions="[1.12, 1.12.2]")
public class Main {
	public static int currentIndex = 0;
	public static List<Song> songs = new ArrayList<Song>();
	private static Iterator<Song> current = songs.iterator();
	public static boolean playing = false;
	public static NBSPlayer player;
	public static boolean shouldOpenGui = false;
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		Keys.register();
		MinecraftForge.EVENT_BUS.register(new Keys());
		ClientCommandHandler.instance.registerCommand(new Start());
		ClientCommandHandler.instance.registerCommand(new OpenGui());
		ClientCommandHandler.instance.registerCommand(new Stop());
		ClientCommandHandler.instance.registerCommand(new Next());
		ClientCommandHandler.instance.registerCommand(new Refresh());
		refreshSongs();
	}
	
	@SubscribeEvent
	public void onTick(ClientTickEvent event) {
		if (shouldOpenGui) {
			Minecraft.getMinecraft().displayGuiScreen(new GuiSongs());
			shouldOpenGui = false;
		}
		if (Minecraft.getMinecraft().world == null) stop();
		if (playing) player.onTick();
	}
	
	public static void onSongEnd() {
		if (playing) next();
	}
	
	public static void sendMsg() {
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
				TextFormatting.GOLD + "Now playing: " +
				TextFormatting.GREEN + player.song.getAuthor() + " - " + player.song.getName()
				));
	}
	
	public static boolean isPlaying() {
		return playing;
	}
	
	public static void seek(int index) {
		if (playing) player.stop();
		current = songs.iterator();
		if (index > 0) for (int i = 0; i < index; i++) current.next();
		player = new NBSPlayer(current.next());
		playing = true;
		player.start();
		sendMsg();
		currentIndex = index;
	}
	
	public static void play() {
		if (playing) player.stop();
		if (songs.isEmpty()) {
			Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
					TextFormatting.RED + "There are not any songs in songs folder..."
					));
			return;
		}
		current = songs.iterator();
		currentIndex = 0;
		playing = true;
		player = new NBSPlayer(current.next());
		player.start();
		sendMsg();
	}
	
	public static void next() {
		player.stop();
		if (!current.hasNext()) {
			current = songs.iterator();
			currentIndex = 0;
		} else currentIndex++;
		player = new NBSPlayer(current.next());
		player.start();
		sendMsg();
	}
	
	public static void stop() {
		if (playing) player.stop();
		playing = false;
	}
	
	public static void continuu() {
		if (player != null) player.continuu(); else play();
		playing = true;
	}
	
	public static void refreshSongs() {
		stop();
		String path = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/songs";
		File songsFolder = new File(path);
		File[] files = songsFolder.listFiles();
		songs = new ArrayList<Song>();
		if (files != null) for (File f : files) {
			if (f.getName().toLowerCase().endsWith(".nbs"))
				try {
					songs.add(new Song(f));
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
