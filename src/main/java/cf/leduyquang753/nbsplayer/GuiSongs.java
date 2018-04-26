package cf.leduyquang753.nbsplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;

/**
 * The fancy GUI of the mod... :)
 * @author Le Duy Quang
 *
 */
public class GuiSongs extends GuiScreen {
	private SongList songs;
	private GuiButton startStop;
	private float scale = 0;
	
	/**
	 * Converts number of ticks into readable time format. 
	 * Example: 2520 ticks becomes "1:02" (1 second = 40 ticks).
	 * 
	 * @param dur The input number of ticks.
	 * @return The time sring.
	 */
	private static String getTimeString(int dur) {
		int duration = dur-1;
		int secs = duration/40;
		int hours = secs/3600;
		int minutes = (secs%3600)/60;
		String minString = (minutes < 10 && hours > 0) ? "0" + minutes : minutes + "";
		int seconds = secs%60;
		String secString = (seconds < 10 && secs > 59) ? "0" + seconds : seconds + "";
		return (hours > 0 ? hours + "h" : "") + ((hours == 0 && minutes > 0) ? minString + ":" : "") + secString + (secs < 60 ? "\"" : "");
	}
	
	/**
	 * Converts a float into number of percentages.
	 * @param in The float to convert.
	 * @return The converted percentage string.
	 */
	private String getVolumeString(float in) {
		return (int) Math.floor(in * 100) + "%";
	}
	
	/**
	 * Checks if a position is in the volume bar's rectangle.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @return Whether the position is in the volume bar's rectangle, also if true, sets the "scale" variable to the value of the coordinate.
	 */
	private boolean inVolumeBar(int x, int y) {
		float left = 6;
		float right = 109;
		float top = height-33;
		float bottom = height-30;
		if (x >= left && x <= right && y >= top && y <= bottom) {
			scale = (x-left) / (right-left);
			return true;
		} else return false;
	}
	
	/**
	 * Checks if a position is in the seek bar's rectangle.
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * @return Whether the position is in the seek bar's rectangle, also if true, sets the "scale" variable to the value of the coordinate.
	 */
	private boolean inSeekBar(int x, int y) {
		float left = 115;
		float right = width-70;
		float top = height-14;
		float bottom = height-9;
		if (x >= left && x <= right && y >= top && y <= bottom) {
			scale = (x-left+1) / (right-left);
			return true;
		} else return false;
	}
	
	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(1, 5, this.height-27, 50, 20, "Refresh"));
		this.buttonList.add(startStop = new GuiButton(2, 60, this.height-27, 50, 20, (Main.playing ? "Pause" : "Play"))); // Play / Pause button.
		this.buttonList.add(new GuiButton(3, this.width-65, this.height-30, 50, 20, "Close"));
		songs = new SongList();
		songs.registerScrollButtons(10, 11);
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		super.handleMouseInput();
		songs.handleMouseInput();
	}
	
	@Override
	public void actionPerformed(GuiButton button) {
		if (button.enabled) {
			switch (button.id) {
			case 1: startStop.displayString = "Play"; Main.refreshSongs(); break;
			case 2: if (Main.playing) {
				Main.stop();
				button.displayString = "Play";
			} else {
				Main.continuu();
				button.displayString = "Pause";
			} break;
			case 3: Minecraft.getMinecraft().setIngameFocus(); break;
			default: songs.actionPerformed(button);
			}
		}
	}
	
	@Override
	public void mouseClicked(int x, int y, int button) throws IOException {
		if (inVolumeBar(x, y)) Main.volume = scale;
		if (inSeekBar(x, y)) {
			float ticks = Main.player.length * scale;
			Main.player.currentTick = (int) ticks;
		}
		super.mouseClicked(x, y, button);
	}
	
	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		songs.drawScreen(x, y, partialTicks);
		super.drawScreen(x, y, partialTicks);
		if (Main.player != null) if (Main.player.currentTick > -1) {
			this.fontRenderer.drawString(Main.player.song.getAuthor() + " - " + Main.player.song.getName(), 115, this.height-36, 16777215);
			this.fontRenderer.drawString(getTimeString(Main.player.currentTick), 115, this.height-25, 16777215);
			String all = getTimeString(Main.player.length);
			this.fontRenderer.drawString(all, this.width-70-this.fontRenderer.getStringWidth(all), this.height-25, 16777215);
			String songIndex = (Main.currentIndex+1) + "/" + Main.songs.size();
			this.fontRenderer.drawString(songIndex, this.width-70-this.fontRenderer.getStringWidth(songIndex), this.height-36, 16777215);
			Gui.drawRect(115, this.height-14, this.width-70, this.height-9, 0xFF646464);
			int pos = (int)(115 + (this.width-185)*Main.player.currentTick/Main.player.length);
			Gui.drawRect(115, this.height-14, pos, this.height-9, 0xFFFFFFFF);
		}
		Gui.drawRect(6, height-33, 109, height-30, 0xFF646464);
		int pos = (int)(6+103*Main.volume);
		Gui.drawRect(6, height-33, pos, height-30, 0xFFFFFFFF);
		if (Main.player != null) if (Main.player.currentTick > -1 && inSeekBar(x, y)) {
			float ticks = Main.player.length*scale;
			List<String> tip = new ArrayList<String>();
			tip.add(getTimeString((int) ticks));
			drawHoveringText(tip, x-8, height-15);
		}
		if (inVolumeBar(x, y)) {
			List<String> tip = new ArrayList<String>();
			tip.add("Volume");
			tip.add("Current: " + getVolumeString(Main.volume));
			tip.add("Pointed: " + getVolumeString(scale));
			drawHoveringText(tip, x-8, height-56);
		}
	}
	
	/**
	 * The song list of the GUI.
	 * @author Le Duy Quang
	 *
	 */
	class SongList extends GuiSlot {
		public SongList() {
			super(Minecraft.getMinecraft(), GuiSongs.this.width, GuiSongs.this.height, 5, GuiSongs.this.height-45, 18);
		}
		
		public int getSize() {
			return Main.songs.size();
		}
		
		public void elementClicked(int slot, boolean doubleClick, int mouseX, int mouseY) {
			Main.seek(slot);
			startStop.displayString = "Pause";
		}
		
		public boolean isSelected(int slot) {
			if (Main.player != null) return (Main.player.currentTick > -1 && Main.currentIndex == slot);
			return (Main.playing && Main.currentIndex == slot);
		}
		
		public int getContentHeight() {
			return Main.songs.size() * 18;
		}
		
		public void drawBackground() {
			GuiSongs.this.drawDefaultBackground();
		}
		
		public void drawSlot(int slot, int arg2, int arg3, int arg4, int mouseX, int mouseY, float partialTicks) {
			GuiSongs.this.drawCenteredString(GuiSongs.this.fontRenderer, Main.songs.get(slot).getAuthor() + " - " + Main.songs.get(slot).getName(), this.width/2, arg3+3, 16777215);
		}
	}
}
