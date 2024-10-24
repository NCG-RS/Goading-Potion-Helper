package com.goadinghelper;

import net.runelite.api.Client;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;

import javax.inject.Inject;
import java.awt.*;

public class GoadingOverlay extends OverlayPanel
{

	private final GoadingConfig config;
	private final Client client;

	@Inject
	private GoadingOverlay(GoadingConfig config, Client client)
	{
		this.config = config;
		this.client = client;
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final String text = "You need to sip a Goading Potion";

		panelComponent.getChildren().clear();

		panelComponent.getChildren().add((LineComponent.builder())
			.left(text)
			.build());

		panelComponent.setPreferredSize(new Dimension(graphics.getFontMetrics().stringWidth(text) - 20, 0));

		panelComponent.setBackgroundColor(config.reminderColor());

		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		return panelComponent.render(graphics);
	}

}
