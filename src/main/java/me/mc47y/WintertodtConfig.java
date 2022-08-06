package me.mc47y;

import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

import java.awt.*;

@ConfigGroup("mc47y")
public interface WintertodtConfig extends Config {
	@Alpha
	@ConfigItem(
			keyName = "snowfallColor",
			name = "Snowfall",
			description = "Color of the snowfall highlight"
	)
	default Color snowfallColor()
	{
		return new Color(255, 0, 0, 255);
	}

	@Alpha
	@ConfigItem(
			keyName = "pyroDamageColor",
			name = "Pyromancer Damage",
			description = "Color of the pyromancer damage highlight"
	)
	default Color pyroDamageColor()
	{
		return new Color(255, 200, 0, 255);
	}

	@Alpha
	@ConfigItem(
			keyName = "brazierUnlightColor",
			name = "Brazier Extinguish",
			description = "Color of the brazier extinguish highlight"
	)
	default Color brazierUnlightColor()
	{
		return new Color(0, 67, 160, 255);
	}

	@Alpha
	@ConfigItem(
			keyName = "brazierBreakColor",
			name = "Brazier Break",
			description = "Color of the brazier break highlight"
	)
	default Color brazierBreakColor()
	{
		return new Color(255, 0, 0, 255);
	}
}
