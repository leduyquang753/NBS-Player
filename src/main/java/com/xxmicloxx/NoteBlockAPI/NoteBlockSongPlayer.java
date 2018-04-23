package com.xxmicloxx.NoteBlockAPI;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class NoteBlockSongPlayer extends SongPlayer {
    public NoteBlockSongPlayer(Song song) {
        super(song);
    }
    
    @SideOnly(Side.CLIENT)
    @Override
    public void playTick(int tick) {
    	EntityPlayerSP player = Minecraft.getMinecraft().player;
    	World world = player.getEntityWorld();
    	if (world == null) return;
        for (Layer l : song.getLayerHashMap().values()) {
        	//        	Minecraft.getMinecraft().thePlayer.addChatComponentMessage(new ChatComponentText("Play."));
        	Note note = l.getNote(tick);
            if (note == null) {
                continue;
            }
            int pitch = note.getKey()-33;
            float p = (float)Math.pow(2.0D, (double)(pitch - 12) / 12.0D);
//            String name = Instrument.getInstrument(note.getInstrument());
//            Minecraft.getMinecraft().theWorld.playSoundEffect(
//            		pos.posX, pos.posY, pos.posZ,
//            		Instrument.getInstrument(note.getInstrument()), 1F,
//            		(float)Math.pow(2.0D, (double)(pitch - 12) / 12.0D)
//            		);
           
//            Minecraft.getMinecraft().getSoundHandler().playSound(new NoteSound(name, p, player.posX, player.posY, player.posZ));
            world.playSound(player.posX, player.posY, player.posZ, Sounds.sounds.get((int) note.getInstrument()), SoundCategory.RECORDS, 1f, p, false);
//            new SPacketCustomSound(name, SoundCategory.RECORDS, player.posX, player.posY, player.posZ, 1F, p).processPacket(Minecraft.getMinecraft().getConnection());
        }
    }
}
