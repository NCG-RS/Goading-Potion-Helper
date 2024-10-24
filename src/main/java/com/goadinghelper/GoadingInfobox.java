package com.goadinghelper;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

import java.awt.*;

public class GoadingInfobox extends InfoBox
{

	@Getter
	@Setter
	private int timer = -1;

	public GoadingInfobox(Plugin plugin)
	{
		super(null, plugin);
	}

	@Override
	public String getText()
	{
		// count 6 -> 1 instead of 5 -> 0
		return Integer.toString(timer + 1);
	}

	@Override
	public Color getTextColor()
	{
		return timer == 5 ? Color.GREEN : Color.WHITE;
	}

	@Override
	public String getTooltip()
	{
		return "Goading Potion";
	}
}
