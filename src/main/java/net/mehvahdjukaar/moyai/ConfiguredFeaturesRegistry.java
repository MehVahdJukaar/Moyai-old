package net.mehvahdjukaar.moyai;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicate;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.RandomPatchConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleBlockConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.SimpleRandomFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.*;
import net.minecraft.world.level.levelgen.placement.*;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.List;

public class ConfiguredFeaturesRegistry {

    //helper
    private static RandomPatchConfiguration makeRandomPatch(int tries, int xzSpread, int ySpread, ConfiguredFeature<?, ?> feature, BlockPredicate placementRule) {
        return new RandomPatchConfiguration(tries, xzSpread, ySpread, () -> feature.filtered(placementRule));
    }

    private static PlacedFeature makeSimpleMoyaiFeature(Direction dir){
        return Feature.SIMPLE_BLOCK.configured(
                        new SimpleBlockConfiguration(SimpleStateProvider.simple(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING,dir))))
                .filtered(BlockPredicate.wouldSurvive
                        (Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING,dir), Vec3i.ZERO));
    }

    private static final BlockPredicate BEACH_PLACEMENT = BlockPredicate.allOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.matchesTag(Tags.Blocks.SAND, BlockPos.ZERO.below())
    );


    private static final BlockPredicate MUSHROOM_PLACEMENT = BlockPredicate.allOf(
            BlockPredicate.ONLY_IN_AIR_PREDICATE,
            BlockPredicate.anyOf(
                    BlockPredicate.matchesTag(Tags.Blocks.SAND, BlockPos.ZERO.below()),
                    BlockPredicate.matchesBlock(Blocks.MYCELIUM, BlockPos.ZERO.below())
            )
    );

    private static final BlockStateProvider MOYAI_PROVIDER = new WeightedStateProvider(
            new SimpleWeightedRandomList.Builder<BlockState>()
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING, Direction.NORTH),1)
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING, Direction.SOUTH),1)
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING, Direction.EAST),1)
                    .add(Moyai.MOYAI.get().defaultBlockState().setValue(MoyaiBlock.FACING, Direction.WEST),1));


    private static final ConfiguredFeature<SimpleRandomFeatureConfiguration, ?> MOYAI_FEATURE = Feature.SIMPLE_RANDOM_SELECTOR.configured(
            new SimpleRandomFeatureConfiguration(List.of(
                    ()->makeSimpleMoyaiFeature(Direction.NORTH),
                    ()->makeSimpleMoyaiFeature(Direction.SOUTH),
                    ()->makeSimpleMoyaiFeature(Direction.EAST),
                    ()->makeSimpleMoyaiFeature(Direction.WEST))));

    //configured features

    public static final ConfiguredFeature<RandomPatchConfiguration, ?> BEACH_MOYAI_PATCH = Feature.RANDOM_PATCH.configured(
            makeRandomPatch(35, 10, 3,
                    MOYAI_FEATURE,
                    BEACH_PLACEMENT));

    public static final ConfiguredFeature<RandomPatchConfiguration, ?> MUSHROOM_MOYAI_PATCH = Feature.RANDOM_PATCH.configured(
            makeRandomPatch(40, 9, 3,
                    MOYAI_FEATURE,
                    MUSHROOM_PLACEMENT));


    //placed features

    public static final PlacedFeature BEACH_MOYAI = BEACH_MOYAI_PATCH.placed(
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            RarityFilter.onAverageOnceEvery(45),
            InSquarePlacement.spread(),
            BiomeFilter.biome());

    public static final PlacedFeature MUSHROOM_MOYAI = MUSHROOM_MOYAI_PATCH.placed(
            PlacementUtils.HEIGHTMAP_WORLD_SURFACE,
            HeightRangePlacement.uniform(VerticalAnchor.aboveBottom(63+64), VerticalAnchor.aboveBottom(64+64+9)),
            RarityFilter.onAverageOnceEvery(6),
            InSquarePlacement.spread(),
            BiomeFilter.biome());


    protected static void registerFeatures(final FMLCommonSetupEvent event) {
        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                Moyai.res("beach_moyai"), BEACH_MOYAI_PATCH);


        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                Moyai.res("beach_moyai"), BEACH_MOYAI);

        Registry.register(BuiltinRegistries.CONFIGURED_FEATURE,
                Moyai.res("mushroom_moyai"), MUSHROOM_MOYAI_PATCH);


        Registry.register(BuiltinRegistries.PLACED_FEATURE,
                Moyai.res("mushroom_moyai"), MUSHROOM_MOYAI);

    }
}
