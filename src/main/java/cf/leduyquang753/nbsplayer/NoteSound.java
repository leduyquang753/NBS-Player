package cf.leduyquang753.nbsplayer;

import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.Sound;
import net.minecraft.client.audio.SoundEventAccessor;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;

public class NoteSound implements ISound {
	protected ResourceLocation rl;
	protected float pitch, x, y, z;
	
	public NoteSound(String name, float pitch, double posX, double posY, double posZ) {
		rl = new ResourceLocation("minecraft", name);
		this.pitch = pitch;
		x = (float) posX; y = (float) posY; z = (float) posZ;
	}
	
	public ResourceLocation getSoundLocation() {
		return rl;
	};

    public boolean canRepeat() {
    	return false;
    };

    public int getRepeatDelay() {
    	return 0;
    };

    public float getVolume() {
    	return 1F;
    };

    public float getPitch() {
    	return pitch;
    };

    public float getXPosF() {;
    	return x;
    }

    public float getYPosF() {
    	return y;
    };

    public float getZPosF() {
    	return z;
    };

    public ISound.AttenuationType getAttenuationType() {
    	return ISound.AttenuationType.NONE;
    }

	@Override
	public SoundEventAccessor createAccessor(SoundHandler handler) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Sound getSound() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SoundCategory getCategory() {
		// TODO Auto-generated method stub
		return null;
	};
}
