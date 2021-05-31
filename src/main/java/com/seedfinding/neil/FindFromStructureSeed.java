
package com.seedfinding.neil;

import kaptainwutax.featureutils.GenerationContext;
import kaptainwutax.featureutils.structure.Mansion;
import kaptainwutax.mcutils.rand.ChunkRand;
import kaptainwutax.mcutils.rand.seed.WorldSeed;
import kaptainwutax.mcutils.util.block.BlockRotation;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.CPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.mcutils.version.MCVersion;

import java.util.List;
import java.util.stream.Collectors;

public class FindFromStructureSeed {
	public static final MCVersion VERSION = MCVersion.v1_16_5;

	public static void main(String[] args) {
		process(1786808L);
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
				List<GenerationContext.Context> list = WorldSeed.getSisterSeeds(structureSeed).asStream()
						.parallel()
						.mapToObj(mansion::getContext)
						.filter(c -> mansion.canSpawn(pos, c.getBiomeSource()))
						.collect(Collectors.toList());
				System.out.println(list.size() + " " + bPos);
				for (GenerationContext.Context context : list) {
					int xx = (pos.getX() << 4) + 7;
					int zz = (pos.getZ() << 4) + 7;
					rand.setCarverSeed(context.getWorldSeed(), pos.getX(), pos.getZ(), VERSION);
					BlockRotation rotation = BlockRotation.getRandom(rand);
					int offX = 5;
					int offZ = 5;
					if (rotation == BlockRotation.CLOCKWISE_90) {
						offX = -5;
					} else if (rotation == BlockRotation.CLOCKWISE_180) {
						offX = -5;
						offZ = -5;
					} else if (rotation == BlockRotation.COUNTERCLOCKWISE_90) {
						offZ = -5;
					}
					int corner1 = context.getGenerator().getHeightOnGround(xx, zz);
					int corner2 = context.getGenerator().getHeightOnGround(xx, zz + offZ);
					int corner3 = context.getGenerator().getHeightOnGround(xx + offX, zz);
					int corner4 = context.getGenerator().getHeightOnGround(xx + offX, zz + offZ);
					int y = Math.min(Math.min(corner1, corner2), Math.min(corner3, corner4));
					if (y > 115) {
						System.out.printf("y=%d, Found %s at /tp @p %d ~ %d with y=%d%n", y, context.getWorldSeed(), bPos.getX(), bPos.getZ(), y);
					}
				}
			}
		}

	}
}


