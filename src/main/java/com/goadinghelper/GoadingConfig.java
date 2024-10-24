package com.goadinghelper;

import net.runelite.client.config.*;

import java.awt.*;

@ConfigGroup("goading")
public interface GoadingConfig extends Config
{
	@ConfigSection(name = "Notifications",
		description = "Configuration for notifications",
		position = 1)
	String notificationSection = "notificationSection";

	@ConfigSection(name = "Timers",
		description = "Configuration for timers",
		position = 2)
	String timerSection = "timerSection";

	@ConfigSection(name = "Radius",
		description = "Configuration for aggression radius",
		position = 3)
	String radiusSection = "radiusSection";

	@ConfigItem(
		keyName = "reminderEnabled",
		name = "Enable reminder panel",
		description = "Show an overlay reminding you to sip a Goading Potion",
		position = 1,
		section = notificationSection
	)
	default boolean reminderEnabled()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "reminderColor",
		name = "Reminder Color",
		description = "The color to use for the infobox.",
		position = 2,
		section = notificationSection
	)
	default Color reminderColor()
	{
		return new Color(255, 0, 0, 150);
	}

	@ConfigItem(
		keyName = "notificationEnabled",
		name = "Enable runelite notification",
		description = "Uses notification configured in Runelite settings when goading effect expires",
		position = 3,
		section = notificationSection
	)
	default boolean notificationEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "timerEnabled",
		name = "Enable goading timer",
		description = "Show an infobox with a timer ticking down to the next aggression check",
		section = timerSection
	)
	default boolean timerEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "enableGoadingRadius",
		name = "Enable Goading Radius",
		description = "Goading radius border width",
		position = 1,
		section = radiusSection
	)
	default boolean radiusEnabled()
	{
		return false;
	}

	@ConfigItem(
		keyName = "borderWidth",
		name = "Border width",
		description = "Goading radius border width",
		position = 2,
		section = radiusSection
	)
	default int borderWidth()
	{
		return 3;
	}
}
