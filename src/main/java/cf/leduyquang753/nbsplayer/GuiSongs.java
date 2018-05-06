package cf.leduyquang753.nbsplayer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.lwjgl.input.Mouse;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiSlot;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

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
	public static String getTimeString(int dur) {
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
		float top = height-34;
		float bottom = height-31;
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
		this.buttonList.add(new GuiButton(1, 5, this.height-28, 50, 20, "Refresh"));
		this.buttonList.add(startStop = new GuiButton(2, 60, this.height-28, 50, 20, (Main.playing ? "Pause" : "Play"))); // Play / Pause button.
		startStop.enabled = !Main.songs.isEmpty();
		this.buttonList.add(new GuiButton(3, this.width-65, this.height-32, 50, 20, "Close"));
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
			// Refresh
			case 1: startStop.displayString = "Play"; Main.refreshSongs(); startStop.enabled = !Main.songs.isEmpty(); break;
			// Play/Pause
			case 2: if (Main.playing) {
				Main.stop();
				button.displayString = "Play";
			} else {
				Main.continuu();
				button.displayString = "Pause";
			} break;
			// Close
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
		// Display a message when there aren't any songs.
		if (Main.songs.isEmpty()) {
			fontRenderer.drawString("There aren't any songs to play.", (width-fontRenderer.getStringWidth("There aren't any songs to play."))/2, 15, 16777215);
			fontRenderer.drawString("Please put some .nbs files in \"songs\" folder", (width-fontRenderer.getStringWidth("Please put some .nbs files in \\\"songs\\\" folder"))/2, 40, 16777215);
			fontRenderer.drawString("of Minecraft's directory then click \"Refresh\".", (width-fontRenderer.getStringWidth("of Minecraft's directory then click \\\"Refresh\\\"."))/2, 52, 16777215);
		}
		if (Main.player != null) { if (Main.player.currentTick > -1) {
			// Time bar
			String songIndex = (Main.currentIndex+1) + "/" + Main.songs.size();
			int maxWidth = width-185-fontRenderer.getStringWidth(songIndex);
			String original = Main.names.get(Main.currentIndex);
			String processed = fontRenderer.trimStringToWidth(original, maxWidth);
			if (!original.equals(processed) && processed.length() > 3) {
				String finale = processed.substring(0, processed.length()-3) + "...";
				processed = finale;
			}
			fontRenderer.drawString(processed, 115, height-36, 16777215);
			fontRenderer.drawString(getTimeString(Main.player.currentTick), 115, height-25, 16777215);
			String all = getTimeString(Main.player.length);
			fontRenderer.drawString(all, width-70-fontRenderer.getStringWidth(all), height-25, 16777215);
			fontRenderer.drawString(songIndex, width-70-fontRenderer.getStringWidth(songIndex), height-36, 16777215);
			Gui.drawRect(115, height-14, width-70, height-9, 0xFF646464);
			int pos = (int)(115 + (width-185)*Main.player.currentTick/Main.player.length);
			Gui.drawRect(115, height-14, pos, height-9, 0xFFFFFFFF);
		} } else fontRenderer.drawString(Main.songs.size() + " song" + (Main.songs.size() == 1 ? "" : "s"), 115, height-36, 16777215);
		// Volume bar
		Gui.drawRect(6, height-34, 109, height-31, 0xFF646464);
		int pos = (int)(6+103*Main.volume);
		Gui.drawRect(6, height-34, pos, height-31, 0xFFFFFFFF);
		
		// Tooltips
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
			drawHoveringText(tip, x-8, height-59);
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
		
		public void drawSlot(int slot, int arg2, int drawingY, int boxHeight, int mouseX, int mouseY, float partialTicks) {
			int maxWidth = right-left-23;
			String original = Main.names.get(slot);
			String processed = GuiSongs.this.fontRenderer.trimStringToWidth(original, maxWidth);
			if (!original.equals(processed) && processed.length() > 3) {
				String finale = processed.substring(0, processed.length()-3) + "...";
				processed = finale;
			}
			GuiSongs.this.drawCenteredString(GuiSongs.this.fontRenderer, processed, this.width/2, drawingY+3, 16777215);
		}
		
		@Override
		public void drawSelectionBox(int arg1, int slotYPos, int X, int Y, float partialTicks) {
			int numberOfSlots = this.getSize();
	        Tessellator tessellator = Tessellator.getInstance();
	        BufferBuilder worldrenderer = tessellator.getBuffer();

	        for (int slot = 0; slot < numberOfSlots; ++slot)
	        {
	            int drawingY = slotYPos + slot * this.slotHeight + this.headerPadding;
	            int boxHeight = this.slotHeight - 4;

	            if (isSelected(slot))
	            {
	            	int halfBoxWidth = Math.min(right-left-28, Minecraft.getMinecraft().fontRenderer.getStringWidth(Main.names.get(Main.currentIndex)))/2;
	            	int center = left+(right-left)/2;
	                int boxLeft = center-halfBoxWidth-4;
	                int boxRight = center+halfBoxWidth+4;
	                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
	                GlStateManager.disableTexture2D();
	                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
	                // Selection box's border.
	                worldrenderer.pos((double)boxLeft, (double)(drawingY + boxHeight + 2), 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
	                worldrenderer.pos((double)boxRight, (double)(drawingY + boxHeight + 2), 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
	                worldrenderer.pos((double)boxRight, (double)(drawingY - 2), 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
	                worldrenderer.pos((double)boxLeft, (double)(drawingY - 2), 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
	                // Selection box's fill.
	                worldrenderer.pos((double)(boxLeft + 1), (double)(drawingY + boxHeight + 1), 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
	                worldrenderer.pos((double)(boxRight - 1), (double)(drawingY + boxHeight + 1), 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
	                worldrenderer.pos((double)(boxRight - 1), (double)(drawingY - 1), 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
	                worldrenderer.pos((double)(boxLeft + 1), (double)(drawingY - 1), 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
	                tessellator.draw();
	                GlStateManager.enableTexture2D();
	            }
	            this.drawSlot(slot, arg1, drawingY, boxHeight, X, Y, partialTicks);
	        }
		}
		
		@Override
	    public void handleMouseInput()  // Copied from the original method with some X-axis tweaks...
	    {
	        if (this.isMouseYWithinSlotBounds(this.mouseY))
	        {
	            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom)
	            {
	                int i = left + 5;   // Tweaked
	                int j = right - 18; // Tweaked
	                int k = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
	                int l = k / this.slotHeight;

	                if (l < this.getSize() && this.mouseX >= i && this.mouseX <= j && l >= 0 && k >= 0)
	                {
	                    this.elementClicked(l, false, this.mouseX, this.mouseY);
	                    this.selectedElement = l;
	                }
	                else if (this.mouseX >= i && this.mouseX <= j && k < 0)
	                {
	                    this.clickedHeader(this.mouseX - i, this.mouseY - this.top + (int)this.amountScrolled - 4);
	                }
	            }

	            if (Mouse.isButtonDown(0) && this.getEnabled())
	            {
	                if (this.initialClickY == -1)
	                {
	                    boolean flag1 = true;

	                    if (this.mouseY >= this.top && this.mouseY <= this.bottom)
	                    {
	                        int j2 = (this.width - this.getListWidth()) / 2;
	                        int k2 = (this.width + this.getListWidth()) / 2;
	                        int l2 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
	                        int i1 = l2 / this.slotHeight;

	                        if (i1 < this.getSize() && this.mouseX >= j2 && this.mouseX <= k2 && i1 >= 0 && l2 >= 0)
	                        {
	                            boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
	                            this.elementClicked(i1, flag, this.mouseX, this.mouseY);
	                            this.selectedElement = i1;
	                            this.lastClicked = Minecraft.getSystemTime();
	                        }
	                        else if (this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0)
	                        {
	                            this.clickedHeader(this.mouseX - j2, this.mouseY - this.top + (int)this.amountScrolled - 4);
	                            flag1 = false;
	                        }

	                        int i3 = this.getScrollBarX();
	                        int j1 = i3 + 6;

	                        if (this.mouseX >= i3 && this.mouseX <= j1)
	                        {
	                            this.scrollMultiplier = -1.0F;
	                            int k1 = this.getMaxScroll();

	                            if (k1 < 1)
	                            {
	                                k1 = 1;
	                            }

	                            int l1 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
	                            l1 = net.minecraft.util.math.MathHelper.clamp(l1, 32, this.bottom - this.top - 8);
	                            this.scrollMultiplier /= (float)(this.bottom - this.top - l1) / (float)k1;
	                        }
	                        else
	                        {
	                            this.scrollMultiplier = 1.0F;
	                        }

	                        if (flag1)
	                        {
	                            this.initialClickY = this.mouseY;
	                        }
	                        else
	                        {
	                            this.initialClickY = -2;
	                        }
	                    }
	                    else
	                    {
	                        this.initialClickY = -2;
	                    }
	                }
	                else if (this.initialClickY >= 0)
	                {
	                    this.amountScrolled -= (float)(this.mouseY - this.initialClickY) * this.scrollMultiplier;
	                    this.initialClickY = this.mouseY;
	                }
	            }
	            else
	            {
	                this.initialClickY = -1;
	            }

	            int i2 = Mouse.getEventDWheel();

	            if (i2 != 0)
	            {
	                if (i2 > 0)
	                {
	                    i2 = -1;
	                }
	                else if (i2 < 0)
	                {
	                    i2 = 1;
	                }

	                this.amountScrolled += (float)(i2 * this.slotHeight / 2);
	            }
	        }
	    }
		
		@Override
		public int getScrollBarX()
	    {
	        return this.width-8;
	    }
	}
}
