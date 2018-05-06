package cf.leduyquang753.nbsplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cf.leduyquang753.nbsapi.Song;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

@Mod(name="NBS Player", modid="nbsplayer", version="1.1", acceptedMinecraftVersions="[1.12, 1.12.2]", updateJSON="https://github.com/leduyquang753/NBS-Player/raw/master/autoUpdate.json")
public class Main {
	public static int currentIndex = 0;
	public static List<Song> songs = new ArrayList<Song>();
	public static List<String> names = new ArrayList<String>();
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
		if (Minecraft.getMinecraft().world == null) { stop(); return; }
		if (playing) player.onTick();
	}
	
	public static void onSongEnd() {
		if (playing) next();
	}
	
	public static void sendMsg() {
		try {
			// Massive JSON stuff! So painful for eyes!
			// Now playing: {Author} - {Song name}
			//                            ^^ Tooltip: {Song name}
			//                                        Author: ...
			//                                        Original author: ...
			//                                        Description:
			//                                        ...
			//                                        Length: ...
			String toTell = "[\"\",{\"text\":\"Now playing: \",\"color\":\"gold\"},{\"text\":\"" + names.get(currentIndex)
			+ "\",\"color\":\"green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":[\"\",{\"text\":\""
			+ (player.song.getName().trim().isEmpty() ? names.get(currentIndex) : player.song.getName())
			+ "\\n\",\"color\":\"green\"}";
			if (!player.song.getAuthor().trim().equals("")) toTell +=
					",\"Author: \",{\"text\":\"" + player.song.getAuthor()
					+ "\\n\",\"color\":\"gold\"}";
			if (!player.song.getOriginalAuthor().trim().equals("")) toTell +=
					",\"Original author: \",{\"text\":\"" + player.song.getOriginalAuthor()
					+ "\\n\",\"color\":\"gold\"}";
			if (!player.song.getDescription().trim().equals("")) toTell +=
					",\"Description: \\n\",{\"text\":\"" + player.song.getDescription()
					+ "\\n\",\"italic\":true}";
			toTell += ",\"Length: \",{\"text\":\"" + GuiSongs.getTimeString(player.length)
					+ "\",\"color\":\"gold\"}],\"color\":\"green\"}}]";
			Minecraft.getMinecraft().player.sendMessage(ITextComponent.Serializer.jsonToComponent(toTell));
		} catch(Exception e) {
			e.printStackTrace();
		}
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
		currentIndex = index;
		if (index > 0) for (int i = 0; i < index; i++) current.next();
		player = new NBSPlayer(current.next());
		playing = true;
		player.start();
		sendMsg();
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
		player = null;
		String path = Minecraft.getMinecraft().mcDataDir.getAbsolutePath() + "/songs";
		File songsFolder = new File(path);
		File[] files = songsFolder.listFiles();
		songs = new ArrayList<Song>();
		names = new ArrayList<String>();
		if (files != null) for (File f : files) {
			if (f.getName().toLowerCase().endsWith(".nbs"))
				try {
					Song s = new Song(f);
					songs.add(s);
					names.add(s.getName().trim().equals("") ? f.getName() : s.getAuthor() + " - " + s.getName());
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
	}
}
