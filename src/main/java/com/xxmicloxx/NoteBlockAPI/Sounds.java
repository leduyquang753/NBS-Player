package com.xxmicloxx.NoteBlockAPI;

import java.util.*;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Sounds {
	public static String[] names = {"harp", "bass", "basedrum", "snare", "click", "guitar", "flute", "bell", "chime", "xylophone"};
	public static List<SoundEvent> sounds = new ArrayList<SoundEvent>();
	
	@SubscribeEvent
	public void onSoundRegistry(RegistryEvent.Register<SoundEvent> event) {
		for (String s : names) {
			sounds.add(new SoundEvent(new ResourceLocation("minecraft", "block.note." + s)));
		}
		for (SoundEvent e : sounds) event.getRegistry().register(e);
	}
}
