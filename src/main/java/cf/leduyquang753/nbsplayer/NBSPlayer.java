package cf.leduyquang753.nbsplayer;

import java.util.ArrayList;
import java.util.List;

import cf.leduyquang753.nbsapi.Layer;
import cf.leduyquang753.nbsapi.Note;
import cf.leduyquang753.nbsapi.Song;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

/**
 * Song player engine, the heart of the mod.
 * @author Le Duy Quang
 *
 */
public class NBSPlayer {
	public Song song;
	private static boolean playing = false;
	private int speed = 4;
	public int currentTick = 0;
	public int length = 0;
	private static final String[] names = {"harp", "bass", "basedrum", "snare", "hat", "guitar", "flute", "bell", "chime", "xylophone"};
	
	public void onTick() {
		if (playing) currentTick++;
		if (currentTick > length) {
			stop();
			Main.onSongEnd();
		}
		// Plays the notes at the current tick.
		if (currentTick%speed == 0 && Minecraft.getMinecraft().world != null) {
			List<PlayableNote> toPlay = getNotesAt(currentTick/speed);
			if (toPlay.size() > 0) for (PlayableNote n : getNotesAt(currentTick/speed)) {
				float pitch = (float)Math.pow(2.0D, (double)(n.note.getPitch() - 45) / 12.0D);
				Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("minecraft", "block.note." + names[n.note.getInstrument().getID()])), Main.volume * n.volume, pitch);
			}
		}
	}
	
	private int getSongLength() {
		int res = -1;
		for (Layer l : song.getSongBoard()) {
			int max = -1;
			for (int i : l.getNoteList().keySet()) max = Math.max(max, i);
			res = Math.max(res, max);
		}
		return res*speed+120;
	}
	
	private List<PlayableNote> getNotesAt(int tick) {
		List<PlayableNote> res = new ArrayList<PlayableNote>();
		for (Layer l : song.getSongBoard()) {
			Note n = l.getNoteList().get(tick);
			if (n != null) res.add(new PlayableNote(n, l.getVolume()/100f));
		}
		return res;
	}
	
	public NBSPlayer(Song song) {
		this.song = song;
		speed = 40/(this.song.getTempo()/100);
		length = getSongLength();
	}
	
	public void start() {
		currentTick = -1;
		playing = true;
	}
	
	public void continuu() {
		playing = true;
	}
	
	public void stop() {
		playing = false;
	}
}
