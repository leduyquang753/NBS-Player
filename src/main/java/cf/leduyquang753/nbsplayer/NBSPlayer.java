package cf.leduyquang753.nbsplayer;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cf.leduyquang753.nbsapi.Layer;
import cf.leduyquang753.nbsapi.Note;
import cf.leduyquang753.nbsapi.Song;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;

public class NBSPlayer {
	public Song song;
	private static boolean playing = false;
	private int speed = 4;
	private int currentTick = 0;
	private int length = 0;
	private static final String[] names = {"harp", "bass", "basedrum", "snare", "hat", "guitar", "flute", "bell", "chime", "xylophone"};
	
	public void onTick() {
		if (playing) currentTick++;
		if (currentTick > length) {
			stop();
			Main.onSongEnd();
		}
		if (currentTick%speed == 0) {
			List<Note> toPlay = getNotesAt(currentTick/speed);
			if (toPlay.size() > 0) for (Note n : getNotesAt(currentTick/speed)) {
				float pitch = (float)Math.pow(2.0D, (double)(n.getPitch() - 45) / 12.0D);
				Minecraft.getMinecraft().player.playSound(new SoundEvent(new ResourceLocation("minecraft", "block.note."+names[n.getInstrument().getID()])), 1F, pitch);
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
		return (res+10)*speed;
	}
	
	private List<Note> getNotesAt(int tick) {
		List<Note> res = new ArrayList<Note>();
		for (Layer l : song.getSongBoard()) {
			Note n = l.getNoteList().get(tick);
			if (n != null) res.add(n);
		}
		return res;
	}
	
	public NBSPlayer(File file) {
		try {
			song = new Song(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		speed = 40/(song.getTempo()/100);
		length = getSongLength();
	}
	
	public void start() {
		currentTick = -1;
		playing = true;
	}
	
	public void stop() {
		playing = false;
	}
}
