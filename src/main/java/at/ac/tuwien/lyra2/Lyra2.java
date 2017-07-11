package at.ac.tuwien.lyra2;

public class Lyra2 {
    public static void phs(byte[] dst, byte[] src, byte[] salt, LyraParams params) {
        // TODO: check parameters here
        hash(dst, src, salt, params);
    }

    public static void hash(byte[] dst, byte[] src, byte[] salt, LyraParams params) {
        int    gap = 1;
        int   step = 1;
        int window = 2;
        int   sqrt = 2;

        int  row0 = 3;
        int prev0 = 2;
        int  row1 = 1;
        int prev1 = 0;

        int N_COLS = params.N_COLS;

        int SIZEOF_INT = params.SIZEOF_INT;
        int BLOCK_LEN_INT64 = params.BLOCK_LEN_INT64;
        int BLOCK_LEN_BLAKE2_SAFE_INT64 = params.BLOCK_LEN_BLAKE2_SAFE_INT64;
        int BLOCK_LEN_BLAKE2_SAFE_BYTES = params.BLOCK_LEN_BLAKE2_SAFE_BYTES;

        int ROW_LEN_INT64 = params.ROW_LEN_INT64;
        int ROW_LEN_BYTES = params.ROW_LEN_BYTES;

        int srclen = src.length;
        int dstlen = dst.length;
        int sltlen = salt.length;
        int t_cost = params.t_cost;
        int m_cost = params.m_cost;

        long[] whole_matrix = new long[m_cost * ROW_LEN_INT64];

        int[] memory_matrix = new int[m_cost];

        for (int i = 0, row = 0; i != m_cost; ++i, row += ROW_LEN_INT64) {
            memory_matrix[i] = row;
        }

        //==== Padding (password + salt + params) with 10*1 ====//
        // See comment about constant 6 in original code: make it 8 integers total
        int nBlocksInput = (srclen + sltlen + 6 * SIZEOF_INT) / BLOCK_LEN_BLAKE2_SAFE_BYTES + 1;

        int ii;
        for (ii = 0; ii < nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_INT64; ++ii) {
            whole_matrix[ii] = 0;
        }

        ii = 0;
        byte[] buffer0 = new byte[nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES];

        for (int jj = 0; jj < srclen; ++ii, ++jj) {
            buffer0[ii] = src[jj];
        }

        for (int jj = 0; jj < sltlen; ++ii, ++jj) {
            buffer0[ii] = salt[jj];
        }

        // NOTE: the order of mem.copy calls matters
        mem.copy(buffer0, ii, dstlen); ii += 4;
        mem.copy(buffer0, ii, srclen); ii += 4;
        mem.copy(buffer0, ii, sltlen); ii += 4;
        mem.copy(buffer0, ii, t_cost); ii += 4;
        mem.copy(buffer0, ii, m_cost); ii += 4;
        mem.copy(buffer0, ii, N_COLS); ii += 4;

        buffer0[ii] = (byte) 0x80;
        buffer0[nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES - 1] |= (byte) 0x01;

        final long[] buffer1 = pack.longs(buffer0);

        for (int jj = 0; jj != buffer1.length; ++jj) {
            whole_matrix[jj] = buffer1[jj];
        }

        // Wrap-up phase:
        Sponge sponge = new Sponge(params);

//        System.out.println("echo sponge.state after sponge init:");
//        echo.bytes(sponge.state, 8 * sponge.state.length);

        for (int jj = 0, offset = 0; jj < nBlocksInput; ++jj) {
            sponge.absorb_block_blake2b_safe(whole_matrix, offset);

            offset += BLOCK_LEN_BLAKE2_SAFE_INT64;
        }

        // Setup phase:
//        System.out.println("echo sponge.state after first absorb:");
//        echo.bytes(sponge.state, 8 * sponge.state.length);

        sponge.reduced_squeeze_row0(whole_matrix, memory_matrix[0]);

//        System.out.println("echo sponge.state after reduced squeeze row0:");
//        echo.bytes(sponge.state, 8 * sponge.state.length);
//        System.out.println("echo whole_matrix after reduced squeeze row0:");
//        echo.bytes(whole_matrix, 128, 16, 8 * memory_matrix[0]);

        sponge.reduced_duplex_row1_and_row2(whole_matrix, memory_matrix[0], memory_matrix[1]);

//        System.out.println("echo sponge.state after reduced duplex row1 and row2 (1):");
//        echo.bytes(sponge.state, 8 * sponge.state.length);
//        System.out.println("echo whole_matrix after reduced duplex row1 and row2 (1):");
//        echo.bytes(whole_matrix, 128, 16, 8 * memory_matrix[1]);

        sponge.reduced_duplex_row1_and_row2(whole_matrix, memory_matrix[1], memory_matrix[2]);

//        System.out.println("echo sponge.state after reduced duplex row1 and row2 (2):");
//        echo.bytes(sponge.state, 8 * sponge.state.length);
//        System.out.println("echo whole_matrix after reduced duplex row1 and row2 (2):");
//        echo.bytes(whole_matrix, 128, 16, 8 * memory_matrix[2]);

        // Setup phase: filling loop:
        for (row0 = 3; row0 != m_cost; ++row0) {
            sponge.reduced_duplex_row_filling(
                    whole_matrix,
                    memory_matrix[row1],
                    memory_matrix[prev0],
                    memory_matrix[prev1],
                    memory_matrix[row0]
            );

            prev0 = row0;
            prev1 = row1;

            row1 = (row1 + step) & (window - 1);

            if (row1 == 0) {
                window *= 2;
                step = sqrt + gap;
                gap = -gap;

                if (gap == -1) {
                    sqrt *= 2;
                }
            }
        }

//        System.out.println("echo sponge.state before wandering phase:");
//        echo.bytes(sponge.state, 8 * sponge.state.length);

        // Wandering phase:
        // Wandering phase: visitation loop
        for (int i = 0; i != t_cost * m_cost; ++i) {
//            final long st0 = mem.flip(sponge.state[0]);
//            final long st2 = mem.flip(sponge.state[2]);
//
//            System.out.printf("nRows: %16X\n", m_cost);
//            System.out.printf("sponge.state[0]: %16X %20d\n", st0, st0);
//            System.out.printf("sponge.state[2]: %16X %20d\n", st2, st2);

            row0 = (int) Long.remainderUnsigned(mem.flip(sponge.state[0]), m_cost);
            row1 = (int) Long.remainderUnsigned(mem.flip(sponge.state[2]), m_cost);

//            System.out.printf("Wandering phase picks row0: %16X\n", row0);
//            System.out.printf("Wandering phase picks row1: %16X\n", row1);
//            System.out.printf("Wandering phase picks prev0: %16X\n", prev0);
//            System.out.printf("Wandering phase picks prev1: %16X\n", prev1);

            sponge.reduced_duplex_row_wandering(
                    whole_matrix,
                    memory_matrix[row0],
                    memory_matrix[row1],
                    memory_matrix[prev0],
                    memory_matrix[prev1]
            );

//            System.out.println("echo reduced duplex row wandering");
//            System.out.printf("whole_matrix for row0: (%16X)\n", row0);
//            echo.bytes(whole_matrix, 128, 16, 8 * memory_matrix[row0]);
//            System.out.printf("whole_matrix for row1: (%16X)\n", row1);
//            echo.bytes(whole_matrix, 128, 16, 8 * memory_matrix[row1]);
//            System.out.printf("whole_matrix for prev0: (%16X)\n", prev0);
//            echo.bytes(whole_matrix, 128, 16, 8 * memory_matrix[prev0]);
//            System.out.printf("whole_matrix for prev1: (%16X)\n", prev1);
//            echo.bytes(whole_matrix, 128, 16, 8 * memory_matrix[prev1]);

            prev0 = row0;
            prev1 = row1;
        }

        // Wrap-up phase:
        sponge.absorb_column(whole_matrix, memory_matrix[row0]);

//        System.out.println("echo sponge state after absorb column");
//        echo.bytes(sponge.state, 128);

        sponge.squeeze(dst, dstlen);
    }
}
