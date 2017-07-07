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

    /**
     * Mimic byte flip that happens in c with memcpy
     *
     * @param x -- a Java long to be flipped
     */
    public static long flip_long(final long x) {
        return    (x & 0x00000000000000FFL)  << 56
                | (x & 0x000000000000FF00L)  << 40
                | (x & 0x0000000000FF0000L)  << 24
                | (x & 0x00000000FF000000L)  <<  8
                | (x & 0x000000FF00000000L) >>>  8
                | (x & 0x0000FF0000000000L) >>> 24
                | (x & 0x00FF000000000000L) >>> 40
                | (x & 0xFF00000000000000L) >>> 56
                ;
    }

    public Sponge(Parameters params) {
        for (int i = 0; i != 8; ++i) {
            state[i] = 0;
        }

        for (int i = 0; i != 8; ++i) {
            state[i + 8] = flip_long(blake2b_IV[i]);
        }

        this.rounds = params.rounds;

        this.NCOLS = params.NCOLS;
        this.BLOCK_LEN_INT64 = params.BLOCK_LEN_INT64;
    }

    /**
     * @param in -- a hard-coded 512 bits (Blake2b and BlaMka restriction)
     */
    public void absorb_block_blake2b_safe(long[] in, int offset) {
        for (int i = 0; i < 8; ++i) {
            state[i] ^= in[offset + i];
        }

        System.out.println("Echo sponge.state after first XOR:");
        Go.dump_bytes(state, 8 * state.length);

        sponge_lyra();
    }

    public static long rotr64(final long w, final int b) {
        return (w >>> b) | (w << (64 - b));
    }

    public void G(final int r, final int i, final int a, final int b, final int c, final int d) {
//        if (r == 0 && i == 0) {
//            final long xa = flip_long(state[a]);
//            final long xb = flip_long(state[b]);
//            System.out.printf("%16X + %16X = %16X\n", xa, xb, xa + xb);
//        }
        state[a] = flip_long(flip_long(state[a]) + flip_long(state[b]));

//        if (r == 0 && i == 0) {
//            final long xd = flip_long(state[d]);
//            final long xa = flip_long(state[a]);
//            System.out.printf("%16X + %16X = %16X\n", xd, xa, xd ^ xa);
//        }
        state[d] = flip_long(rotr64(flip_long(state[d]) ^ flip_long(state[a]), 32));

//        if (r == 0 && i == 0) {
//            final long xc = flip_long(state[c]);
//            final long xd = flip_long(state[d]);
//            System.out.printf("%16X + %16X = %16X\n", xc, xd, xc + xd);
//        }
        state[c] = flip_long(flip_long(state[c]) + flip_long(state[d]));

//        if (r == 0 && i == 0) {
//            final long xb = flip_long(state[b]);
//            final long xc = flip_long(state[c]);
//            System.out.printf("%16X + %16X = %16X\n", xb, xc, xb ^ xc);
//        }
        state[b] = flip_long(rotr64(flip_long(state[b]) ^ flip_long(state[c]), 24));

//        if (r == 0 && i == 0) {
//            final long xa = flip_long(state[a]);
//            final long xb = flip_long(state[b]);
//            System.out.printf("%16X + %16X = %16X\n", xa, xb, xa + xb);
//        }
        state[a] = flip_long(flip_long(state[a]) + flip_long(state[b]));

//        if (r == 0 && i == 0) {
//            final long xd = flip_long(state[d]);
//            final long xa = flip_long(state[a]);
//            System.out.printf("%16X + %16X = %16X\n", xd, xa, xd ^ xa);
//        }
        state[d] = flip_long(rotr64(flip_long(state[d]) ^ flip_long(state[a]), 16));

//        if (r == 0 && i == 0) {
//            final long xc = flip_long(state[c]);
//            final long xd = flip_long(state[d]);
//            System.out.printf("%16X + %16X = %16X\n", xc, xd, xc + xd);
//        }
        state[c] = flip_long(flip_long(state[c]) + flip_long(state[d]));

//        if (r == 0 && i == 0) {
//            final long xb = flip_long(state[b]);
//            final long xc = flip_long(state[c]);
//            System.out.printf("%16X + %16X = %16X\n", xb, xc, xb ^ xc);
//        }
        state[b] = flip_long(rotr64(flip_long(state[b]) ^ flip_long(state[c]), 63));

//        if (r == 0 && i < 3) {
//            System.out.printf("G round: %02d step: %02d\n", r, i);
//            Go.dump_bytes(state, 8 * state.length);
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
}
