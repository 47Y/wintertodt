package me.wintertodt;

import java.awt.Color;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.runelite.api.coords.WorldPoint;


@Setter
@Getter
@AllArgsConstructor
class DrawObject
{
	private final WorldPoint wp;
	private Instant startTime;
	private final int duration;
	private Color color;
	private int size;
	private int ticks;
}