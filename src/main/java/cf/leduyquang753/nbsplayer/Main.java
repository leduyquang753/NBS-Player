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

@Mod(name="NBS Player", modid="nbsplayer", version="0.3", acceptedMinecraftVersions="[1.12, 1.12.2]")
public class Main {
	public static int currentIndex = 0;
	public static List<Song> songs = new ArrayList<Song>();
	private static Iterator<Song> current = songs.iterator();
	public static boolean playing = false;
	public static NBSPlayer player;
	public static boolean shouldOpenGui = false;
	public static float volume = 1;
	
	@EventHandler
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		Keys.register();
		MinecraftForge.EVENT_BUS.register(new Keys());
		ClientCommandHandler.instance.registerCommand(new Start());   // nbsplay
		ClientCommandHandler.instance.registerCommand(new OpenGui()); // nbsgui
		ClientCommandHandler.instance.registerCommand(new Stop());    // nbsstop
		ClientCommandHandler.instance.registerCommand(new Next());    // nbsnext
		ClientCommandHandler.instance.registerCommand(new Refresh()); // nbsrefresh
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
		// Now playing: {Song author} - {Song name}
		Minecraft.getMinecraft().player.sendMessage(new TextComponentString(
				TextFormatting.GOLD + "Now playing: " +
				TextFormatting.GREEN + player.song.getAuthor() + " - " + player.song.getName()
				));
	}
	
	public static boolean isPlaying() {
		return playing;
	}
	
	/**
	 * Plays index-th song.
	 * @param index The numerical order of the song in the list.
	 */
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
	
	/**
	 * Plays the first song.
	 */
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
	
	/**
	 * Plays the next song.
	 */
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
	
	/**
	 * Pauses playing.
	 */
	public static void stop() {
		if (playing) player.stop();
		playing = false;
	}
	
	/**
	 * Continues playing ("continue" cannot be used since it's a reserved word).
	 */
	public static void continuu() {
		if (player != null) player.continuu(); else play();
		playing = true;
	}
	
	/**
	 * Refreshes the song list.
	 */
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
