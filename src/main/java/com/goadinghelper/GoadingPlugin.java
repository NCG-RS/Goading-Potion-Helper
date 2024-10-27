package com.goadinghelper;

import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.Notifier;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;

import java.util.Arrays;

@Slf4j
@PluginDescriptor(
	name = "Goading Helper"
)
public class GoadingPlugin extends Plugin
{
	private static final int NO_GOADING = -1;

	private static final int GOADING_INTERVAL_TICKS = 6;

	private static final int[] GOADING_POTION_IDS = new int[]{
		ItemID.GOADING_POTION1,
		ItemID.GOADING_POTION2,
		ItemID.GOADING_POTION3,
		ItemID.GOADING_POTION4
	};

	@Inject
	private Client client;

	@Inject
	private GoadingConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private ItemManager itemManager;

	private GoadingInfobox infoBox;

	@Inject
	private Notifier notifier;

	@Inject
	private GoadingOverlay overlay;

	@Inject
	private GoadingRadiusOverlay radiusOverlay;

	private int nextGoadingTicks = NO_GOADING;

	private boolean hasGoadingPotion = false;

	@Override
	protected void startUp() throws Exception
	{
		this.infoBox = new GoadingInfobox(this);
		this.infoBoxManager.addInfoBox(this.infoBox);

		this.nextGoadingTicks = NO_GOADING;

		this.infoBox.setImage(itemManager.getImage(ItemID.GOADING_POTION4));
	}

	@Override
	protected void shutDown() throws Exception
	{
		this.infoBoxManager.removeInfoBox(this.infoBox);
		this.overlayManager.remove(this.overlay);
		this.overlayManager.remove(this.radiusOverlay);
	}

	@Subscribe
	protected void onGameTick(GameTick tick)
	{
		--this.nextGoadingTicks;
		updateInfoBox();
	}

	@Subscribe
	protected void onVarbitChanged(VarbitChanged change)
	{
		if (change.getVarbitId() == Varbits.BUFF_GOADING_POTION)
		{
			int value = change.getValue();
			if (value > 0)
			{
				this.nextGoadingTicks = GOADING_INTERVAL_TICKS;
			}
			else
			{
				this.nextGoadingTicks = NO_GOADING;
				this.onGoadingPotionExpired();
			}
		}
	}

	@Subscribe
	protected void onItemContainerChanged(ItemContainerChanged itemContainerChanged)
	{
		if (itemContainerChanged.getContainerId() != InventoryID.INVENTORY.getId())
		{
			return;
		}
		this.hasGoadingPotion = Arrays.stream(GOADING_POTION_IDS).anyMatch(itemContainerChanged.getItemContainer()::contains);
		this.updateOverlay();
	}

	@Subscribe
	protected void onConfigChanged(ConfigChanged event)
	{
		this.updateOverlay();
		this.updateInfoBox();
	}

	private boolean isGoadingActive()
	{
		return this.nextGoadingTicks >= 0;
	}

	private void onGoadingPotionExpired()
	{
		this.updateOverlay();
		if (config.getNotification().isEnabled())
		{
			notifier.notify(config.getNotification(), "Your goading potion has expired");
		}

	}

	private void updateOverlay()
	{
		if (this.isGoadingActive())
		{
			this.overlayManager.add(this.radiusOverlay);
		} else {
			this.overlayManager.remove(this.radiusOverlay);
		}
		if (this.isGoadingActive() || !this.hasGoadingPotion || !config.reminderEnabled())
		{
			this.overlayManager.remove(this.overlay);
			return;
		}
		if (!this.overlayManager.anyMatch(o -> o instanceof GoadingOverlay))
		{
			this.overlayManager.add(this.overlay);
		}
	}

	private void updateInfoBox()
	{
		if (!this.isGoadingActive() || !config.timerEnabled())
		{
			this.infoBoxManager.removeInfoBox(this.infoBox);
			return;
		}
		this.infoBox.setTimer(this.nextGoadingTicks);
		if (!this.infoBoxManager.getInfoBoxes().contains(this.infoBox))
		{
			this.infoBoxManager.addInfoBox(this.infoBox);
		}
	}

	@Provides
	GoadingConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(GoadingConfig.class);
	}
}
