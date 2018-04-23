package com.xxmicloxx.NoteBlockAPI;

public class Instrument {

    public static String getInstrument(byte instrument) {
        switch (instrument) {
            case 0: return "block.note.harp";
            case 1: return "block.note.bass";
            case 2: return "block.note.basedrum";
            case 3: return "block.note.snare";
            case 4: return "block.note.hat";
            case 5: return "block.note.guitar";
            case 6: return "block.note.flute";
            case 7: return "block.note.bell";
            case 8: return "block.note.chime";
            case 9: return "block.note.xylophone";
            default: return "block.note.harp";
        }
    }
}