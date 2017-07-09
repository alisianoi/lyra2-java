package at.ac.tuwien.lyra2;

public class Sponge {
    static final long[] blake2b_IV = {
            0x6a09e667f3bcc908L, 0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL, 0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L, 0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL, 0x5be0cd19137e2179L
    };

    public long[] state = new long[16];

    public final int rounds;
    public final int NCOLS;
    public final int BLOCK_LEN_INT64;

    public Sponge(Parameters params) {
        for (int i = 0; i != 8; ++i) {
            state[i] = 0;
        }

        for (int i = 0; i != 8; ++i) {
            state[i + 8] = mem.flip(blake2b_IV[i]);
        }

        this.rounds = params.rounds;

        this.NCOLS = params.NCOLS;
        this.BLOCK_LEN_INT64 = params.BLOCK_LEN_INT64;
    }

    /**
     * @param src -- a hard-coded 512 bits (Blake2b and BlaMka restriction)
     */
    public void absorb_block_blake2b_safe(final long[] src, final int offset) {
        for (int i = 0; i < 8; ++i) {
            state[i] ^= src[offset + i];
        }

        sponge_lyra();
    }

    public void absorb_column(long[] src, int offset) {
        for (int i = 0; i != BLOCK_LEN_INT64; ++i) {
            state[i] ^= src[offset + i];
        }

        sponge_lyra();
    }

    // TODO: at least two final loops could be optimized
    public void squeeze(byte[] dst, final int len) {
        final int BLOCK_LEN_BYTES = 8 * BLOCK_LEN_INT64; // 8 * 12

        final int div = len / BLOCK_LEN_BYTES; // complete blocks, 96 bytes each
        final int mod = len % BLOCK_LEN_BYTES; // incomplete block, [0..96] bytes

        // Assume block size is a multiple of 8 bytes
        for (int i = 0; i != div; ++i) {
            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                byte[] bytes = pack.bytes(state[j]);

                for (int k = 0; k != 8; ++k) {
                    dst[i * BLOCK_LEN_BYTES + 8 * j + k] = bytes[k];
                }
            }

            sponge_lyra();
        }

        final int div8 = mod / 8;
        final int mod8 = mod % 8;

        for (int i = 0; i != div8; ++i) {
            final byte[] bytes = pack.bytes(state[i]);

            for (int j = 0; j != 8; ++j) {
                dst[div * BLOCK_LEN_BYTES + 8 * i + j] = bytes[j];
            }
        }

        for (int i = 0; i != mod8; ++i) {
            final byte[] bytes = pack.bytes(state[div8]);

            for (int j = 0; j != mod8; ++j) {
                dst[div * BLOCK_LEN_BYTES + 8 * div8 + j] = bytes[j];
            }
        }
    }

    public static long rotr64(final long w, final int b) {
        return (w >>> b) | (w << (64 - b));
    }

    public void G(final int r, final int i, final int a, final int b, final int c, final int d) {
//        if (r == 0 && i == 0) {
//            final long xa = mem.flip(state[a]);
//            final long xb = mem.flip(state[b]);
//            System.out.printf("%16X + %16X = %16X\n", xa, xb, xa + xb);
//        }
        state[a] = mem.flip(mem.flip(state[a]) + mem.flip(state[b]));

//        if (r == 0 && i == 0) {
//            final long xd = mem.flip(state[d]);
//            final long xa = mem.flip(state[a]);
//            System.out.printf("%16X + %16X = %16X\n", xd, xa, xd ^ xa);
//        }
        state[d] = mem.flip(rotr64(mem.flip(state[d]) ^ mem.flip(state[a]), 32));

//        if (r == 0 && i == 0) {
//            final long xc = mem.flip(state[c]);
//            final long xd = mem.flip(state[d]);
//            System.out.printf("%16X + %16X = %16X\n", xc, xd, xc + xd);
//        }
        state[c] = mem.flip(mem.flip(state[c]) + mem.flip(state[d]));

//        if (r == 0 && i == 0) {
//            final long xb = mem.flip(state[b]);
//            final long xc = mem.flip(state[c]);
//            System.out.printf("%16X + %16X = %16X\n", xb, xc, xb ^ xc);
//        }
        state[b] = mem.flip(rotr64(mem.flip(state[b]) ^ mem.flip(state[c]), 24));

//        if (r == 0 && i == 0) {
//            final long xa = mem.flip(state[a]);
//            final long xb = mem.flip(state[b]);
//            System.out.printf("%16X + %16X = %16X\n", xa, xb, xa + xb);
//        }
        state[a] = mem.flip(mem.flip(state[a]) + mem.flip(state[b]));

//        if (r == 0 && i == 0) {
//            final long xd = mem.flip(state[d]);
//            final long xa = mem.flip(state[a]);
//            System.out.printf("%16X + %16X = %16X\n", xd, xa, xd ^ xa);
//        }
        state[d] = mem.flip(rotr64(mem.flip(state[d]) ^ mem.flip(state[a]), 16));

//        if (r == 0 && i == 0) {
//            final long xc = mem.flip(state[c]);
//            final long xd = mem.flip(state[d]);
//            System.out.printf("%16X + %16X = %16X\n", xc, xd, xc + xd);
//        }
        state[c] = mem.flip(mem.flip(state[c]) + mem.flip(state[d]));

//        if (r == 0 && i == 0) {
//            final long xb = mem.flip(state[b]);
//            final long xc = mem.flip(state[c]);
//            System.out.printf("%16X + %16X = %16X\n", xb, xc, xb ^ xc);
//        }
        state[b] = mem.flip(rotr64(mem.flip(state[b]) ^ mem.flip(state[c]), 63));

//        if (r == 0 && i < 3) {
//            System.out.printf("G round: %02d step: %02d\n", r, i);
//            mem.dump_bytes(state, 8 * state.length);
//        }
    }

    public void round_lyra(int round) {
        G(round, 0, 0, 4,  8, 12);
        G(round, 1, 1, 5,  9, 13);
        G(round, 2, 2, 6, 10, 14);
        G(round, 3, 3, 7, 11, 15);
        G(round, 4, 0, 5, 10, 15);
        G(round, 5, 1, 6, 11, 12);
        G(round, 6, 2, 7,  8, 13);
        G(round, 7, 3, 4,  9, 14);
    }

    public void sponge_lyra() {
        for (int round = 0; round != 12; ++round) {
            round_lyra(round);
        }
    }

    public void reduced_sponge_lyra() {
        for (int round = 0; round != this.rounds; ++round) {
            round_lyra(round);
        }
    }

    /**
     * TODO: below, either word or i could be optimized out
     */
    public void reduced_squeeze_row0(long[] out, int offset) {
        int word = (NCOLS - 1) * BLOCK_LEN_INT64;

        for (int i = 0; i != NCOLS; ++i) {
            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                out[offset + word + j] = state[j];
            }

            word -= BLOCK_LEN_INT64;

            reduced_sponge_lyra();
        }
    }

    public void reduced_duplex_row1_and_row2(long[] out, int offset1, int offset2) {
        int word1 = 0;
        int word2 = (NCOLS - 1) * BLOCK_LEN_INT64;

        for (int i = 0; i != NCOLS; ++i) {
            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                state[j] ^= out[offset1 + word1 + j];
            }

            reduced_sponge_lyra();

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                out[offset2 + word2 + j] = out[offset1 + word1 + j] ^ state[j];
            }

            word1 += BLOCK_LEN_INT64;
            word2 -= BLOCK_LEN_INT64;
        }
    }

    /**
     * Do a duplexing operation
     *
     * All of the offsets are indecies into @param{out} that denote a start of some *row* of bytes.
     *
     * @param out     -- a matrix that both provides and receives bytes
     * @param offset0 -- a row that provides bytes and receives bytes too
     * @param offset1 -- a row that provides bytes (latest initialized row)
     * @param offset2 -- a row that provides bytes (latest revisited and updated row)
     * @param offset3 -- a row that receives bytes
     */
    public void reduced_duplex_row_filling(long out[], int offset0, int offset1, int offset2, int offset3) {
        int word0 = offset0;
        int word1 = offset1;
        int word2 = offset2;
        int word3 = offset3 + (NCOLS - 1) * BLOCK_LEN_INT64;

        for (int i = 0; i != NCOLS; ++i) {
            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                state[j] ^= mem.flip(
                        mem.flip(out[word0 + j]) + mem.flip(out[word1 + j]) + mem.flip(out[word2 + j])
                );
            }

            reduced_sponge_lyra();

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                out[word3 + j] = out[word1 + j] ^ state[j];
            }

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                out[word0 + j] ^= state[(j + 2) % BLOCK_LEN_INT64];
            }

            word0 += BLOCK_LEN_INT64;
            word1 += BLOCK_LEN_INT64;
            word2 += BLOCK_LEN_INT64;
            word3 -= BLOCK_LEN_INT64;
        }
    }

    /**
     * Do a duplexing operation
     *
     * All of the offsets are indecies into @param{out} that denote a start of some *row* of bytes.
     *
     * @param offset0 -- a row that provides bytes and receives bytes
     * @param offset1 -- a row that provides bytes and receives bytes after rotation
     * @param offset2 -- a row that provides bytes
     * @param offset3 -- a row that provides bytes
     */
    public void reduced_duplex_row_wandering(long[] out, int offset0, int offset1, int offset2, int offset3) {
        int word0 = offset0;
        int word1 = offset1;

        for (int i = 0; i != NCOLS; ++i) {

//            final int st4 = (int) mem.flip(state[4]);
//            final int st6 = (int) mem.flip(state[6]);

            final int rndcol0 = Math.floorMod((int) mem.flip(state[4]), NCOLS) * BLOCK_LEN_INT64;
            final int rndcol1 = Math.floorMod((int) mem.flip(state[6]), NCOLS) * BLOCK_LEN_INT64;

//            System.out.printf("state[4]: %16X\n", st4);
//            System.out.printf("state[6]: %16X\n", st6);
//            System.out.printf("state[4] %% NCOLS: %16X\n", Math.floorMod(st4, NCOLS));
//            System.out.printf("state[6] %% NCOLS: %16X\n", Math.floorMod(st6, NCOLS));
//            System.out.printf("rndcol0: %16X\n", rndcol0);
//            System.out.printf("rndcol1: %16X\n", rndcol1);

            final int word2 = offset2 + rndcol0;
            final int word3 = offset3 + rndcol1;

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                state[j] ^= mem.flip(mem.flip(out[word0 + j])
                        + mem.flip(out[word1 + j])
                        + mem.flip(out[word2 + j])
                        + mem.flip(out[word3 + j])
                );
            }

//            System.out.println("state after first loop:");
//            mem.dump_bytes(state, 128);

            reduced_sponge_lyra();

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                out[word0 + j] ^= state[j];
            }

//            System.out.println("out[word0 + j] after xor");
//            mem.dump_bytes(out, 128, 16, 8 * word0);

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                out[word1 + j] ^= state[(j + 2) % BLOCK_LEN_INT64];
            }

            word0 += BLOCK_LEN_INT64;
            word1 += BLOCK_LEN_INT64;
        }
    }
}
