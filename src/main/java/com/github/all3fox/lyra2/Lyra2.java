package com.github.all3fox.lyra2;

/**
 * The single-threaded version of the Lyra2 password hashing scheme.
 */
public class Lyra2 {
    /**
     * Compute password hash using salt and other parameters.
     *
     * @param hash   byte array that will contain computed hash
     * @param pass   a password that was converted to byte form
     * @param salt   a salt (defeats ahead-of-time hash computation)
     * @param params an object that holds t_cost, m_cost, etc.
     */
    public static void
    phs(byte[] hash, byte[] pass, byte[] salt, LyraParams params) {
        // TODO: check params here?
        hash(hash, pass, salt, params);
    }

    // TODO: clean up variable names and make individual phases more prominent
    /**
     * Compute password hash using salt and other parameters.
     *
     * @param hash   byte array that will contain computed hash
     * @param pass   a password that was converted to byte form
     * @param salt   a salt (defeats ahead-of-time hash computation)
     * @param params an object that holds t_cost, m_cost, etc.
     */
    public static void
    hash(byte[] hash, byte[] pass, byte[] salt, LyraParams params) {
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

        int n_pass = pass.length;
        int n_hash = hash.length;
        int n_salt = salt.length;
        int t_cost = params.t_cost;
        int m_cost = params.m_cost;

        long[] matrix = new long[m_cost * ROW_LEN_INT64];

        int[] offsets = new int[m_cost];

        for (int i = 0, row = 0; i != m_cost; ++i, row += ROW_LEN_INT64) {
            offsets[i] = row;
        }

        // See comment about constant 6 in original code: make it 8 integers total
        int nBlocksInput = (n_pass + n_salt + 6 * SIZEOF_INT) / BLOCK_LEN_BLAKE2_SAFE_BYTES + 1;

        int ii;
        for (ii = 0; ii != nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_INT64; ++ii) {
            matrix[ii] = 0;
        }

        ii = 0;
        byte[] buffer0 = new byte[nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES];

        for (int jj = 0; jj != n_pass; ++ii, ++jj) {
            buffer0[ii] = pass[jj];
        }

        for (int jj = 0; jj != n_salt; ++ii, ++jj) {
            buffer0[ii] = salt[jj];
        }

        // NOTE: the order of mem.copy calls matters
        mem.copy(buffer0, ii, n_hash); ii += 4;
        mem.copy(buffer0, ii, n_pass); ii += 4;
        mem.copy(buffer0, ii, n_salt); ii += 4;
        mem.copy(buffer0, ii, t_cost); ii += 4;
        mem.copy(buffer0, ii, m_cost); ii += 4;
        mem.copy(buffer0, ii, N_COLS); ii += 4;

        buffer0[ii] = (byte) 0x80;
        buffer0[nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES - 1] |= (byte) 0x01;

        final long[] buffer1 = pack.longs(buffer0);

        for (int jj = 0; jj != buffer1.length; ++jj) {
            matrix[jj] = buffer1[jj];
        }

        Sponge sponge;
        if (params.SPONGE.equals("blake2b")) {
            sponge = new SpongeBlake2b(params);
        } else if (params.SPONGE.equals("blamka")) {
            sponge = new SpongeBlamka(params);
        } else if (params.SPONGE.equals("half-round-blamka")) {
            sponge = new SpongeHalfBlamka(params);
        } else {
            System.err.println("Could not recognize sponge: " + params.SPONGE);

            return;
        }

        for (int jj = 0, offset = 0; jj < nBlocksInput; ++jj) {
            sponge.absorb(matrix, BLOCK_LEN_BLAKE2_SAFE_INT64, offset);

            offset += BLOCK_LEN_BLAKE2_SAFE_INT64;
        }

        // Setup phase:
        sponge.reduced_squeeze_row0(matrix, offsets[0]);

        sponge.reduced_duplex_row1_and_row2(matrix, offsets[0], offsets[1]);
        sponge.reduced_duplex_row1_and_row2(matrix, offsets[1], offsets[2]);

        // Setup phase: filling loop:
        for (row0 = 3; row0 != m_cost; ++row0) {
            sponge.reduced_duplex_row_filling(
                    matrix,
                    offsets[row1],
                    offsets[prev0],
                    offsets[prev1],
                    offsets[row0]
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

        // Wandering phase:
        for (int i = 0; i != t_cost * m_cost; ++i) {
            row0 = (int) Long.remainderUnsigned(mem.flip(sponge.state[0]), m_cost);
            row1 = (int) Long.remainderUnsigned(mem.flip(sponge.state[2]), m_cost);

            sponge.reduced_duplex_row_wandering(matrix, offsets[row0], offsets[row1], offsets[prev0], offsets[prev1]);

            prev0 = row0;
            prev1 = row1;
        }

        // Wrap-up phase:
        sponge.absorb(matrix, BLOCK_LEN_INT64, offsets[row0]);

        sponge.squeeze(hash, n_hash);
    }
}
