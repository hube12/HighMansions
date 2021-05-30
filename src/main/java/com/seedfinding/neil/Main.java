package com.seedfinding.neil;

import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.featureutils.structure.Mansion;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.StructureSeed;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.terrainutils.terrain.OverworldTerrainGenerator;

import java.util.stream.LongStream;

public class Main {
	public static final MCVersion VERSION = MCVersion.v1_16_5;

	public static void main(String[] args) {
		LongStream.range(1L << 42, 1L << 48).parallel().forEach(Main::process);
	}

	public static void process(long structureSeed) {
		ChunkRand rand = new ChunkRand();
		Mansion mansion = new Mansion(VERSION);
		RPos minBound = new BPos(-3000, 0, -3000).toRegionPos(mansion.getSpacing() * 16);
		RPos maxBound = new BPos(3000, 0, 3000).toRegionPos(mansion.getSpacing() * 16);
		for (int regX = minBound.getX() + 1; regX <= maxBound.getX(); regX++) {
			for (int regZ = minBound.getZ() + 1; regZ < maxBound.getZ(); regZ++) {
				CPos cpos = mansion.getInRegion(structureSeed, regX, regZ, rand);
				BPos bPos = cpos.toBlockPos().add(9, 0, 9);
				for (int upper = 0; upper < 1 << 16L; upper++) {
					long ws = StructureSeed.toWorldSeed(structureSeed, upper);
					OverworldBiomeSource overworldBiomeSource = new OverworldBiomeSource(VERSION, ws);
					if (mansion.canSpawn(cpos, overworldBiomeSource)) {
						OverworldTerrainGenerator terrainGenerator = new OverworldTerrainGenerator(overworldBiomeSource);
						int y = terrainGenerator.getHeightOnGround(bPos.getX(), bPos.getZ());
						if (y > 90) {
							System.out.printf("y=%d, Found %s at /tp @p %d ~ %d with y=%d%n", y, ws, bPos.getX(), bPos.getZ(), y);
							System.out.println("SAVE %s" + structureSeed);
							break;
						}
					}
				}
			}
		}

	}
}
