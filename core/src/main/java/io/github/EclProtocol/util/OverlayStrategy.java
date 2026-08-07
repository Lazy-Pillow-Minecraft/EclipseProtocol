package io.github.EclProtocol.util;

import io.github.EclProtocol.blocks.BlockState;
import io.github.EclProtocol.worldgen.World;

public class OverlayStrategy {
    public static String upOverlayShade(World world, int worldX, int worldY, int worldZ) {
        int mask1 = 0;
        int mask2 = 0;
        int checkY = worldY + 1;
        mask1 |= (!check(world, worldX + 1, checkY, worldZ - 1) ? 1 : 0);
        mask2 |= (check(world, worldX + 1, checkY, worldZ) ? 1 : 0);
        mask1 |= (!check(world, worldX + 1, checkY, worldZ + 1) ? 1 : 0) << 1;
        mask2 |= (check(world, worldX, checkY, worldZ + 1) ? 1 : 0) << 1;
        mask1 |= (!check(world, worldX - 1, checkY, worldZ + 1) ? 1 : 0) << 2;
        mask2 |= (check(world, worldX - 1, checkY, worldZ) ? 1 : 0) << 2;
        mask1 |= (!check(world, worldX - 1, checkY, worldZ - 1) ? 1 : 0) << 3;
        mask2 |= (check(world, worldX, checkY, worldZ - 1) ? 1 : 0) << 3;

        if (mask2 == 15) {
            if (mask1 == 0) {return "overlay/overlay_g_g";}
            if (mask1 == 15) {return "overlay/overlay_d_d";}

            if ((mask1 & 0b0111) == 0b0111) {return "overlay/overlay_f_d";}
            if ((mask1 & 0b1101) == 0b1101) {return "overlay/overlay_d_f";}
            if ((mask1 & 0b1110) == 0b1110) {return "overlay/overlay_e_d";}
            if ((mask1 & 0b1011) == 0b1011) {return "overlay/overlay_d_e";}

            if ((mask1 & 0b0101) == 0b0101) {return "overlay/overlay_f_f";}
            if ((mask1 & 0b1000) == 0b1000) {return "overlay/overlay_e_g";}
            if ((mask1 & 0b1001) == 0b1001) {return "overlay/overlay_d_g";}
            if ((mask1 & 0b0110) == 0b0110) {return "overlay/overlay_g_d";}
            if ((mask1 & 0b0011) == 0b0011) {return "overlay/overlay_f_e";}
            if ((mask1 & 0b1100) == 0b1100) {return "overlay/overlay_e_f";}
            if ((mask1 & 0b1010) == 0b1010) {return "overlay/overlay_e_e";}

            if ((mask1 & 0b0001) == 0b0001) {return "overlay/overlay_f_g";}
            if ((mask1 & 0b0100) == 0b0100) {return "overlay/overlay_g_f";}
            if ((mask1 & 0b0010) == 0b0010) {return "overlay/overlay_g_e";}
        }

        if ((mask2 & 0b1110) == 0b1110 && (mask1 & 0b1100) == 0b1100) {return "overlay/overlay_d_b";}
        if ((mask2 & 0b1011) == 0b1011 && (mask1 & 0b0011) == 0b0011) {return "overlay/overlay_d_c";}
        if ((mask2 & 0b0111) == 0b0111 && (mask1 & 0b0110) == 0b0110) {return "overlay/overlay_c_d";}
        if ((mask2 & 0b1101) == 0b1101 && (mask1 & 0b1001) == 0b1001) {return "overlay/overlay_b_d";}

        if ((mask2 & 0b1101) == 0b1101 && (mask1 & 0b0001) == 0b0001) {return "overlay/overlay_a_g";}
        if ((mask2 & 0b1101) == 0b1101 && (mask1 & 0b1000) == 0b1000) {return "overlay/overlay_b_g";}
        if ((mask2 & 0b0111) == 0b0111 && (mask1 & 0b0010) == 0b0010) {return "overlay/overlay_e_a";}
        if ((mask2 & 0b0111) == 0b0111 && (mask1 & 0b0100) == 0b0100) {return "overlay/overlay_f_a";}
        if ((mask2 & 0b1110) == 0b1110 && (mask1 & 0b1000) == 0b1000) {return "overlay/overlay_a_e";}
        if ((mask2 & 0b1110) == 0b1110 && (mask1 & 0b0100) == 0b0100) {return "overlay/overlay_b_e";}
        if ((mask2 & 0b1011) == 0b1011 && (mask1 & 0b0001) == 0b0001) {return "overlay/overlay_a_f";}
        if ((mask2 & 0b1011) == 0b1011 && (mask1 & 0b0010) == 0b0010) {return "overlay/overlay_b_f";}

        if ((mask2 & 0b0011) == 0b0011 && (mask1 & 0b0010) == 0b0010) {return "overlay/overlay_c_c";}
        if ((mask2 & 0b1001) == 0b1001 && (mask1 & 0b0001) == 0b0001) {return "overlay/overlay_b_c";}
        if ((mask2 & 0b0110) == 0b0110 && (mask1 & 0b0100) == 0b0100) {return "overlay/overlay_c_b";}
        if ((mask2 & 0b1100) == 0b1100 && (mask1 & 0b1000) == 0b1000) {return "overlay/overlay_b_b";}

        if ((mask2 & 0b1101) == 0b1101) {return "overlay/overlay_c_g";}
        if ((mask2 & 0b0111) == 0b0111) {return "overlay/overlay_g_a";}
        if ((mask2 & 0b1110) == 0b1110) {return "overlay/overlay_c_e";}
        if ((mask2 & 0b1011) == 0b1011) {return "overlay/overlay_c_f";}

        if ((mask2 & 0b1010) == 0b1010) {return "overlay/overlay_d_a";}
        if ((mask2 & 0b0101) == 0b0101) {return "overlay/overlay_a_d";}
        if ((mask2 & 0b1100) == 0b1100) {return "overlay/overlay_e_b";}
        if ((mask2 & 0b1001) == 0b1001) {return "overlay/overlay_e_c";}
        if ((mask2 & 0b0011) == 0b0011) {return "overlay/overlay_f_c";}
        if ((mask2 & 0b0110) == 0b0110) {return "overlay/overlay_f_b";}

        if ((mask2 & 0b1000) == 0b1000) {return "overlay/overlay_b_a";}
        if ((mask2 & 0b0010) == 0b0010) {return "overlay/overlay_c_a";}
        if ((mask2 & 0b0100) == 0b0100) {return "overlay/overlay_a_b";}
        if ((mask2 & 0b0001) == 0b0001) {return "overlay/overlay_a_c";}

        return "overlay/overlay_a_a";
    }

    private static boolean check(World world, int x, int y, int z) {
        BlockState state = world.getBlock(x, y, z);
        if (state == null) return true;
        return state.getBlock().ifLightTransmission();
    }
}
