package cf.leduyquang753.nbsplayer;

import java.io.IOException;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;

public class GuiSongs extends GuiScreen {
	private SongList songs;
	private GuiButton startStop;
	
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
	
	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.add(new GuiButton(1, 5, this.height-30, 50, 20, "Refresh"));
		this.buttonList.add(startStop = new GuiButton(2, 60, this.height-30, 50, 20, (Main.playing ? "Pause" : "Play")));
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
			case 1: Main.refreshSongs(); break;
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
	public void mouseClicked(int x, int y, int button) {
		float right = 115;
		float left = this.width-70;
		float top = this.height-14;
		float bottom = this.height-9;
		if (x >= right && x <= left && y >= top && y <= bottom) {
			float scale = (x-right) / (left-right);
			float res = Main.player.length*scale;
			Main.player.currentTick = (int) res;
		}
		try {
			super.mouseClicked(x, y, button);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void drawScreen(int x, int y, float partialTicks) {
		songs.drawScreen(x, y, partialTicks);
		if (Main.player != null) if (Main.player.currentTick > -1) {
			this.fontRenderer.drawString(Main.player.song.getAuthor() + " - " + Main.player.song.getName(), 115, this.height-36, 16777215);
			this.fontRenderer.drawString(getTimeString(Main.player.currentTick), 115, this.height-25, 16777215);
			String all = getTimeString(Main.player.length);
			this.fontRenderer.drawString(all, this.width-70-this.fontRenderer.getStringWidth(all), this.height-25, 16777215);
			Gui.drawRect(115, this.height-14, this.width-70, this.height-9, 0xFF646464);
			int pos = (int)(115 + (this.width-185)*Main.player.currentTick/Main.player.length);
			Gui.drawRect(115, this.height-14, pos, this.height-9, 0xFFFFFFFF);
		}
		super.drawScreen(x, y, partialTicks);
	}
	
	@Override
	public boolean doesGuiPauseGame() {
		return false;
	}
	
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
