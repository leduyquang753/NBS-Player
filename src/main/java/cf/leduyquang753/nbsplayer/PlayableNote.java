package cf.leduyquang753.nbsplayer;

import cf.leduyquang753.nbsapi.Note;

public class PlayableNote {
	public Note note;
	public float volume;
	
	public PlayableNote(Note note, float volume) {
		this.note = note;
		this.volume = volume;
	}
}
