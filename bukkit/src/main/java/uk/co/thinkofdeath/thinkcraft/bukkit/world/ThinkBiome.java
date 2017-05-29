/*
 * Copyright 2014 Matthew Collins
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.co.thinkofdeath.thinkcraft.bukkit.world;

import org.bukkit.block.Biome;

import java.util.Arrays;

public class ThinkBiome {
    public static int bukkitToId(Biome biome) {
        switch (biome) {
            // Snowy
            case FROZEN_OCEAN:
                return 10;
            case FROZEN_RIVER:
                return 11;
            case ICE_FLATS:
                return 12;
            case MUTATED_TAIGA:
                return 140;
            case COLD_BEACH:
                return 26;
            case TAIGA_COLD:
                return 30;
            case MUTATED_JUNGLE:
                return 158;
            // Cold
            case SKY:
                return 3;
            case MUTATED_SAVANNA_ROCK:
                return 131;
            case DESERT:
                return 5;
            case MUTATED_EXTREME_HILLS:
                return 133;
            case SWAMPLAND:
                return 9;
            case REDWOOD_TAIGA:
                return 32;
            case MUTATED_SAVANNA:
                return 160;
            case EXTREME_HILLS_WITH_TREES:
                return 34;
            case STONE_BEACH:
                return 25;
            // Medium/Lush
            default:
            case FOREST:
                return 1;
            case VOID:
                return 129;
            case PLAINS:
                return 4;
            case MUTATED_DESERT:
                return 132;
            case OCEAN:
                return 6;
            case MUTATED_FOREST:
                return 134;
            case HELL:
                return 7;
            case MUSHROOM_ISLAND:
                return 14;
            case MUSHROOM_ISLAND_SHORE:
                return 15;
            case BEACHES:
                return 16;
            case JUNGLE:
                return 21;
            case MUTATED_SWAMPLAND:
                return 149;
            case JUNGLE_EDGE:
                return 23;
            case MUTATED_ICE_FLATS:
                return 151;
            case BIRCH_FOREST:
                return 27;
            case MUTATED_REDWOOD_TAIGA:
                return 155;
            case ROOFED_FOREST:
                return 29;
            case MUTATED_EXTREME_HILLS_WITH_TREES:
                return 157;
            // Dry/Warm
            case EXTREME_HILLS:
                return 2;
            case MUTATED_PLAINS:
                return 130;
            case TAIGA:
                return 8;
            case SAVANNA:
                return 35;
            case MUTATED_JUNGLE_EDGE:
                return 163;
            case MESA:
                return 37;
            case MUTATED_BIRCH_FOREST_HILLS:
                return 165;
            case SAVANNA_ROCK:
                return 36;
            case MESA_ROCK:
                return 38;
            case MESA_CLEAR_ROCK:
                return 39;
            case MUTATED_BIRCH_FOREST:
                return 164;
            case MUTATED_ROOFED_FOREST:
                return 166;
            case MUTATED_TAIGA_COLD:
                return 167;
            // Neutral
            case RIVER:
                return 0;
            case DEEP_OCEAN:
                return 24;
            case ICE_MOUNTAINS:
                return 13;
            case DESERT_HILLS:
                return 17;
            case FOREST_HILLS:
                return 18;
            case TAIGA_HILLS:
                return 19;
            case JUNGLE_HILLS:
                return 22;
            case BIRCH_FOREST_HILLS:
                return 28;
            case TAIGA_COLD_HILLS:
                return 31;
            case REDWOOD_TAIGA_HILLS:
                return 33;
            case MUTATED_REDWOOD_TAIGA_HILLS:
                return 156;
            case MUTATED_MESA_ROCK:
                return 161;
        }
    }
}
