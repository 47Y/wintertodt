package me.mc47y;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameObjectSpawned;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Instant;
import java.util.*;
import java.util.List;

@Slf4j
@PluginDescriptor(
	name = "Wintertodt Highlight"
)
public class WintertodtPlugin extends Plugin {

	// Game Objects
	private static final int SNOW_FALLING_ID = 26690;

	// Points
	private static final WorldPoint SW_BRAZIER_CENTER = new WorldPoint(1621, 3998, 0);
	private static final WorldPoint NW_BRAZIER_CENTER = new WorldPoint(1621, 4016, 0);
	private static final WorldPoint NE_BRAZIER_CENTER = new WorldPoint(1639, 4016, 0);
	private static final WorldPoint SE_BRAZIER_CENTER = new WorldPoint(1639, 3998,0);
	private static final List<WorldPoint> BRAZIER_CENTERS = Arrays.asList( SW_BRAZIER_CENTER, NW_BRAZIER_CENTER, NE_BRAZIER_CENTER, SE_BRAZIER_CENTER );

	// Timers
	private static final int FALLING_MS = 3600;
	private static final int BRAZIER_BREAK_MS = 1800;
	private static final int PYRO_MS = 3000;
	private static final int BRAZIER_HIT_MS = 2400;

	// Lists
	private List<WorldPoint> spawned_this_tick = new ArrayList<>();

	@Getter
	private final Map<WorldPoint, DrawObject> gameObjects = new HashMap<>();

	@Inject
	private Client client;

	@Inject
	private WintertodtConfig config;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private WintertodtHighlightOverlay wintertodtHighlightOverlay;

	@Override
	protected void startUp() throws Exception {
		overlayManager.add(wintertodtHighlightOverlay);
	}

	@Override
	protected void shutDown() throws Exception {
		overlayManager.remove(wintertodtHighlightOverlay);

	}

	@Subscribe
	public void onGameObjectSpawned(GameObjectSpawned gameObjectSpawned) {
		if (gameObjectSpawned.getGameObject().getId() == SNOW_FALLING_ID) {
			spawned_this_tick.add(gameObjectSpawned.getGameObject().getWorldLocation());
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick) {
		spawned_this_tick.forEach(this::createDrawObject);
		spawned_this_tick = new ArrayList<>();
		List<WorldPoint> temp = new ArrayList<>();

		gameObjects.forEach((wp, drawObject) -> {
			if (drawObject.getTicks() == 0) temp.add(wp);
			drawObject.setTicks(drawObject.getTicks()-1);
		});

		temp.forEach(gameObjects::remove);
	}

	@Provides
	WintertodtConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(WintertodtConfig.class);
	}

	private void createDrawObject(WorldPoint wp) {
		if (BRAZIER_CENTERS.contains(wp) && hasFaces(wp)) { // Center of the brazier break
			gameObjects.put(wp, new DrawObject(wp, Instant.now(), BRAZIER_BREAK_MS, config.brazierBreakColor(), 5, BRAZIER_BREAK_MS/600));
		} else if (hasCorner(wp)) { // Center of a snowfall
			if (!hasCorners(wp)) return;
			else gameObjects.put(wp, new DrawObject(wp, Instant.now(), FALLING_MS, config.snowfallColor(), 3, FALLING_MS / 600));
		} else {
			if (BRAZIER_CENTERS.contains(wp)) gameObjects.put(wp, new DrawObject(wp, Instant.now(), BRAZIER_HIT_MS, config.brazierUnlightColor(), 3, BRAZIER_HIT_MS/600));
			else gameObjects.put(wp, new DrawObject(wp, Instant.now(), PYRO_MS, config.pyroDamageColor(), 1, PYRO_MS/600));
		}
	}

	private boolean hasCorner(WorldPoint wp) {
		return (spawned_this_tick.contains(wp.dx(1).dy(1)) || spawned_this_tick.contains(wp.dx(-1).dy(-1)) || spawned_this_tick.contains(wp.dx(-1).dy(1)) || spawned_this_tick.contains(wp.dx(1).dy(-1)));
	}

	private boolean hasCorners(WorldPoint wp) {
		return (spawned_this_tick.contains(wp.dx(1).dy(1)) && spawned_this_tick.contains(wp.dx(-1).dy(-1)) && spawned_this_tick.contains(wp.dx(-1).dy(1)) && spawned_this_tick.contains(wp.dx(1).dy(-1)));
	}

	private boolean hasFaces(WorldPoint wp) {
		return (spawned_this_tick.contains((wp.dx(1))) || spawned_this_tick.contains((wp.dx(-1))) || spawned_this_tick.contains((wp.dy(1))) || spawned_this_tick.contains((wp.dy(-1))));
	}
}
