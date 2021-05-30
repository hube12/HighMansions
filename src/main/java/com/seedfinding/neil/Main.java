package com.seedfinding.neil;

import kaptainwutax.featureutils.GenerationContext;
import kaptainwutax.featureutils.structure.Mansion;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;

import java.util.stream.LongStream;

public class Main {
	public static final MCVersion VERSION = MCVersion.v1_16_5;

	public static void main(String[] args) {
		LongStream.range(0, 1L << 48).parallel().forEach(Main::process);
	}

	public static void process(long structureSeed) {
		ChunkRand rand = new ChunkRand();
		Mansion mansion = new Mansion(VERSION);
		RPos minBound = new BPos(-3000, 0, -3000).toRegionPos(mansion.getSpacing() * 16);
		RPos maxBound = new BPos(3000, 0, 3000).toRegionPos(mansion.getSpacing() * 16);
		for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
			for (int regZ = minBound.getZ() + 1; regZ < maxBound.getZ(); regZ++) {
				CPos pos = mansion.getInRegion(structureSeed, regX, regZ, rand);
				BPos bPos = pos.toBlockPos().add(9, 0, 9);
				WorldSeed.getSisterSeeds(structureSeed).asStream().forEach(ws -> {
							GenerationContext.Context context = mansion.getContext(ws);
							if (mansion.canSpawn(pos, context.getBiomeSource())) {
								int y = context.getGenerator().getHeightOnGround(bPos.getX(), bPos.getZ());
								if (y > 90) {
									System.out.printf("y=%d, Found %s at /tp @p %d ~ %d with y=%d%n", y, ws, bPos.getX(), bPos.getZ(), y);
								}
							}
						}
				);
			}
		}

	}
}
