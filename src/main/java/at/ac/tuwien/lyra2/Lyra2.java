package at.ac.tuwien.lyra2;

public class Lyra2 {
    public static Long phs(byte[] dst, byte[] src, byte[] salt, Parameters params) {
        return hash(dst, src, salt, params);
    }

    public static Long hash(byte[] dst, byte[] src, byte[] salt, Parameters params) {
        Long    gap = 1L;
        Long   step = 1L;
        Long window = 2L;
        Long   sqrt = 2L;

        Long  row0 = 3L;
        Long prev0 = 2L;
        Long  row1 = 1L;
        Long prev1 = 0L;

        int NCOLS = params.NCOLS;

        int SIZEOF_INT = params.SIZEOF_INT;
        int BLOCK_LEN_INT64 = params.BLOCK_LEN_INT64;
        int BLOCK_LEN_BLAKE2_SAFE_INT64 = params.BLOCK_LEN_BLAKE2_SAFE_INT64;
        int BLOCK_LEN_BLAKE2_SAFE_BYTES = params.BLOCK_LEN_BLAKE2_SAFE_BYTES;

        int ROW_LEN_INT64 = NCOLS * BLOCK_LEN_INT64;
        int ROW_LEN_BYTES =     8 *   ROW_LEN_INT64;

        int srclen = src.length;
        int dstlen = dst.length;
        int sltlen = salt.length;
        int tcost = params.tcost;
        int mcost = params.mcost;

        // i == m_cost (3) * BLOCK_LEN_INT64 (12) * N_COLS (256) * 8
        // C allocation is in bytes, so divide
        long[] whole_matrix = new long[mcost * ROW_LEN_INT64];

        int[] memory_matrix = new int[mcost];

        for (int ii = 0, row = 0; ii < mcost; ++ii, row += ROW_LEN_INT64) {
            memory_matrix[ii] = row;
        }

        //==== Padding (password + salt + params) with 10*1 ====//
        // See comment about constant 6 in original code
        int nBlocksInput = (srclen + sltlen + 6 * SIZEOF_INT) / BLOCK_LEN_BLAKE2_SAFE_BYTES + 1;

//        System.out.println("nBlocksInput: " + nBlocksInput);
//
//        System.out.println("Allocating that many bytes:");
//        System.out.println(nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES);
//
//        System.out.println("srclen " + srclen);
//        System.out.println("saltlen " + saltlen);

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

        // NOTE: the order of Go.memcpy1 calls matters
        Go.memcpy1(buffer0, ii, dstlen); ii += 4;
        Go.memcpy1(buffer0, ii, srclen); ii += 4;
        Go.memcpy1(buffer0, ii, sltlen); ii += 4;
        Go.memcpy1(buffer0, ii, tcost); ii += 4;
        Go.memcpy1(buffer0, ii, mcost); ii += 4;
        Go.memcpy1(buffer0, ii, NCOLS); ii += 4;

        buffer0[ii] = (byte) 0x80;
        buffer0[nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES - 1] |= (byte) 0x01;

        final long[] buffer1 = Go.pack_longs(buffer0);

        for (int jj = 0; jj != buffer1.length; ++jj) {
            whole_matrix[jj] = buffer1[jj];
        }

        System.out.println("Going to print whole_matrix:");
        Go.dump_bytes(whole_matrix, buffer0.length);

        Sponge sponge = new Sponge(params);

        System.out.println("Echo sponge.state after sponge init:");
        Go.dump_bytes(sponge.state, 8 * sponge.state.length);

        for (int jj = 0, offset = 0; jj < nBlocksInput; ++jj) {
            sponge.absorb_block_blake2b_safe(whole_matrix, offset);

            offset += BLOCK_LEN_BLAKE2_SAFE_INT64;
        }

        System.out.println("Echo sponge.state after first absorb:");
        Go.dump_bytes(sponge.state, 8 * sponge.state.length);

        return 42L;
    }
}