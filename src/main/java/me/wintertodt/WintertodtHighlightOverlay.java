package me.wintertodt;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.Instant;

public class WintertodtHighlightOverlay extends Overlay {

	private static final int MAX_DISTANCE = 3000;
	private static final int PIE_DIAMETER = 20;

	private final Client client;
	private final WintertodtPlugin plugin;
	private final WintertodtConfig config;

	@Inject
	private WintertodtHighlightOverlay(Client client, WintertodtPlugin plugin, WintertodtConfig config) {
		super(plugin);
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_SCENE);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
	}

	@Override
	public Dimension render(Graphics2D graphics) {
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null) return null;

		LocalPoint playerLocation = localPlayer.getLocalLocation();
		Instant now = Instant.now();

		highlightGameObjects(graphics, playerLocation, now);
		return null;
	}

	private void highlightGameObjects(Graphics2D graphics, LocalPoint playerLocation, Instant now) {
		plugin.getGameObjects().values().forEach((drawObject) -> {
			WorldPoint wp = drawObject.getWp();
			LocalPoint lp = LocalPoint.fromWorld(client, wp);

			if (lp.distanceTo(playerLocation) < MAX_DISTANCE) {
				if (drawObject.getSize() == 0) return;
				Polygon poly = Perspective.getCanvasTileAreaPoly(client, lp, drawObject.getSize());

				if (poly != null) {
					OverlayUtil.renderPolygon(graphics, poly, drawObject.getColor());
				}
			}

			if (drawObject.getDuration() > 0 && lp.distanceTo(playerLocation) < MAX_DISTANCE) {
				float percent = (now.toEpochMilli() - drawObject.getStartTime().toEpochMilli()) / (float) drawObject.getDuration();
				ProgressPieComponent ppc = new ProgressPieComponent();
				ppc.setBorderColor(drawObject.getColor());
				ppc.setFill(drawObject.getColor());
				ppc.setProgress(percent);
				ppc.setDiameter(Math.min(PIE_DIAMETER * drawObject.getSize(), 45));
				ppc.setPosition(Perspective.localToCanvas(client, lp, 0));
				ppc.render(graphics);
			}
		});
	}
}
