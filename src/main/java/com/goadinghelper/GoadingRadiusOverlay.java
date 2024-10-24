package com.goadinghelper;

import net.runelite.api.Client;
import net.runelite.api.Perspective;
import net.runelite.api.Player;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;

import javax.inject.Inject;
import java.awt.*;
import java.awt.geom.GeneralPath;

import static net.runelite.api.Perspective.LOCAL_TILE_SIZE;

public class GoadingRadiusOverlay extends OverlayPanel
{

	private final GoadingConfig config;
	private final Client client;

	@Inject
	private GoadingRadiusOverlay(GoadingConfig config, Client client)
	{
		this.config = config;
		this.client = client;
		setPosition(OverlayPosition.DYNAMIC);
		setPriority(Overlay.PRIORITY_LOW);
		setLayer(OverlayLayer.ABOVE_SCENE);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{

		if (!config.radiusEnabled())
		{
			return null;
		}

		// Define the border stroke and color
		Stroke stroke = new BasicStroke((float) config.borderWidth());
		graphics.setStroke(stroke);

		// Draw the square overlay with correct radius and player position
		drawBox(graphics, 4, config.reminderColor(), stroke, 1);

		return null;
	}

	private void drawBox(Graphics2D graphics, int radius, Color borderColour, Stroke borderStroke, int size)
	{
		graphics.setStroke(borderStroke);
		graphics.setColor(borderColour);
		graphics.draw(getSquareAroundPlayerLocation(radius, size));
	}

	private GeneralPath getSquareAroundPlayerLocation(final int radius, final int size)
	{
		GeneralPath path = new GeneralPath(GeneralPath.WIND_EVEN_ODD);

		// Retrieve the player from the client
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return null; // No player, nothing to render
		}

		// Get the player's current local location
		LocalPoint playerLocation = player.getLocalLocation();
		if (playerLocation == null)
		{
			return null; // Player location not available, skip render
		}

		// Get world position
		WorldPoint worldPoint = WorldPoint.fromLocalInstance(client, playerLocation);
		if (worldPoint == null)
		{
			return null; // World position not available, skip render
		}

		// Calculate the corners of the square based on player's world point
		final int startX = worldPoint.getX() - radius;
		final int startY = worldPoint.getY() - radius;
		final int z = worldPoint.getPlane();
		final int diameter = 2 * radius + size;

		// Corner 1: (startX, startY)
		moveTo(path, startX, startY, z);
		// Corner 2: (startX + diameter, startY)
		lineTo(path, startX + diameter, startY, z);
		// Corner 3: (startX + diameter, startY + diameter)
		lineTo(path, startX + diameter, startY + diameter, z);
		// Corner 4: (startX, startY + diameter)
		lineTo(path, startX, startY + diameter, z);
		// Close the square back to Corner 1
		path.closePath();

		return path;
	}

	private boolean moveTo(GeneralPath path, final int x, final int y, final int z)
	{
		Point point = XYToPoint(x, y, z);
		if (point != null)
		{
			path.moveTo(point.getX(), point.getY());
			return true;
		}
		return false;
	}

	private void lineTo(GeneralPath path, final int x, final int y, final int z)
	{
		Point point = XYToPoint(x, y, z);
		if (point != null)
		{
			path.lineTo(point.getX(), point.getY());
		}
	}

	private Point XYToPoint(int x, int y, int z)
	{
		LocalPoint localPoint = LocalPoint.fromWorld(client, x, y);
		if (localPoint == null)
		{
			return null;
		}
		return Perspective.localToCanvas(
			client,
			new LocalPoint(localPoint.getX() - LOCAL_TILE_SIZE / 2, localPoint.getY() - LOCAL_TILE_SIZE / 2),
			z);
	}
}
