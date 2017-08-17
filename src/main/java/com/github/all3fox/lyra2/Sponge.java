package com.github.all3fox.lyra2;

/**
 * Represent a sponge and implement common methods: {@code absorb*, squeeze*, duplex*}
 */
public abstract class Sponge {
    static final long[] blake2b_IV = {
            0x6a09e667f3bcc908L, 0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL, 0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L, 0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL, 0x5be0cd19137e2179L
    };

    public long[] state;

    public final int N_COLS;

    public final int FULL_ROUNDS;
    public final int HALF_ROUNDS;

    public final int BLOCK_LEN_INT64;
    public final int BLOCK_LEN_BYTES;

    /**
     * Construct the sponge and initialize its state.
     * <p>
     * Initialize first half of {@code state} with zeros, second half with
     * blake2b_IV's. This implies that {@code state} is 16 * 8 = 128 bytes.
     *
     * @param params {@link LyraParams}
     */
    public Sponge(LyraParams params) {
        // initialize the sponge state:
        state = new long[16];
        // first 8 words are zeroed out
        for (int i = 0; i != 8; ++i) {
            state[i] = 0;
        }

        // second 8 words are blake2b_IV's
        for (int i = 0; i != 8; ++i) {
            state[8 + i] = mem.flip(blake2b_IV[i]);
        }

        this.N_COLS = params.N_COLS;
        this.FULL_ROUNDS = params.FULL_ROUNDS;
        this.HALF_ROUNDS = params.HALF_ROUNDS;
        this.BLOCK_LEN_INT64 = params.BLOCK_LEN_INT64;
        this.BLOCK_LEN_BYTES = params.BLOCK_LEN_BYTES;
    }

    /**
     * Absorb words into the sponge.
     *
     * @param src    a source array of words to absorb
     * @param len    a number of words to absorb from {@code src}
     * @param offset an index into {@code src} to start from
     */
    public void absorb(final long[] src, final int len, final int offset) {
        for (int i = 0; i != len; ++i) {
            state[i] ^= src[offset + i];
        }

        sponge_lyra();
    }

    /**
     * Squeeze bytes from the sponge.
     *
     * @param dst a destination array to squeeze bytes into
     * @param len a number of bytes to squeeze into {@code dst}
     */
    public void squeeze(byte[] dst, final int len) {
        final int div = len / BLOCK_LEN_BYTES;
        final int mod = len % BLOCK_LEN_BYTES;

        // Assume block size is a multiple of 8 bytes
        for (int i = 0; i != div; ++i) {
            final int offset0 = i * BLOCK_LEN_BYTES;

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                final int offset1 = offset0 + 8 * j;

                byte[] bytes = pack.bytes(state[j]);

                for (int k = 0; k != 8; ++k) {
                    dst[offset1 + k] = bytes[k];
                }
            }

            sponge_lyra();
        }

        final int div8 = mod / 8;
        final int mod8 = mod % 8;

        final int offset0 = div * BLOCK_LEN_BYTES;

        for (int i = 0; i != div8; ++i) {
            final int offset1 = offset0 + 8 * i;

            final byte[] bytes = pack.bytes(state[i]);

            for (int j = 0; j != 8; ++j) {
                dst[offset1 + j] = bytes[j];
            }
        }

        final int offset1 = offset0 + 8 * div8;

        for (int i = 0; i != mod8; ++i) {
            final byte[] bytes = pack.bytes(state[div8]);

            for (int j = 0; j != mod8; ++j) {
                dst[offset1 + j] = bytes[j];
            }
        }
    }

    /**
     * Rotate a word by several bits to the right.
     *
     * @param word a word to rotate to the right
     * @param b    a number of bits to rotate by
     * @return a new word, the result of rotation
     */
    public static long rotr64(final long word, final int b) {
        return (word << (64 - b)) | (word >>> b);
    }

    /**
     * Rotate a word by several bits to the left.
     *
     * @param word a word to rotate to the left
     * @param b    a number of bits to rotate by
     * @return a new word, the result of rotation
     */
    public static long rotl64(final long word, final int b) {
        return (word << b) | (word >>> (64 - b));
    }

    public abstract void G(final int a, final int b, final int c, final int d);

    // TODO: consider renaming to "permute_state" or similar
    /**
     * Update the state of the sponge.
     *
     * @param rounds roughly the number of state permutations
     */
    public void sponge_lyra(final int rounds) {
        for (int round = 0; round != rounds; ++round) {
            G(0, 4,  8, 12);
            G(1, 5,  9, 13);
            G(2, 6, 10, 14);
            G(3, 7, 11, 15);
            G(0, 5, 10, 15);
            G(1, 6, 11, 12);
            G(2, 7,  8, 13);
            G(3, 4,  9, 14);
        }
    }

    /**
     * Update the state of the sponge, run the full amount of rounds by default.
     */
    public void sponge_lyra() {
        sponge_lyra(FULL_ROUNDS);
    }

    public void reduced_squeeze_row0(long[] dst, final int offset) {
        int word = (N_COLS - 1) * BLOCK_LEN_INT64;

        for (int i = 0; i != N_COLS; ++i) {
            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                dst[offset + word + j] = state[j];
            }

            word -= BLOCK_LEN_INT64;

            sponge_lyra(HALF_ROUNDS);
        }
    }

    public void reduced_duplex_row1_and_row2(long[] dst, final int offset1, final int offset2) {
        int word1 = 0;
        int word2 = (N_COLS - 1) * BLOCK_LEN_INT64;

        for (int i = 0; i != N_COLS; ++i) {
            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                state[j] ^= dst[offset1 + word1 + j];
            }

            sponge_lyra(HALF_ROUNDS);

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                dst[offset2 + word2 + j] = dst[offset1 + word1 + j] ^ state[j];
            }

            word1 += BLOCK_LEN_INT64;
            word2 -= BLOCK_LEN_INT64;
        }
    }

    /**
     * Do a duplexing operation
     * <p>
     * All of the offsets point into {@code dst} and denote a start of some *row* of bytes.
     *
     * @param dst     a matrix that both provides and receives bytes
     * @param offset0 a row that provides bytes and receives bytes too
     * @param offset1 a row that provides bytes (latest initialized row)
     * @param offset2 a row that provides bytes (latest revisited and updated row)
     * @param offset3 a row that receives bytes
     */
    public void reduced_duplex_row_filling(long dst[], final int offset0, final int offset1, final int offset2, final int offset3) {
        int word0 = offset0;
        int word1 = offset1;
        int word2 = offset2;
        int word3 = offset3 + (N_COLS - 1) * BLOCK_LEN_INT64;

        for (int i = 0; i != N_COLS; ++i) {
            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                state[j] ^= mem.flip(
                      mem.flip(dst[word0 + j])
                    + mem.flip(dst[word1 + j])
                    + mem.flip(dst[word2 + j])
                );
            }

            sponge_lyra(HALF_ROUNDS);

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                dst[word3 + j] = dst[word1 + j] ^ state[j];
            }

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                dst[word0 + j] ^= state[(j + 2) % BLOCK_LEN_INT64];
            }

            word0 += BLOCK_LEN_INT64;
            word1 += BLOCK_LEN_INT64;
            word2 += BLOCK_LEN_INT64;
            word3 -= BLOCK_LEN_INT64;
        }
    }

    /**
     * Do a duplexing operation
     * <p>
     * All of the offsets point into {@code dst} and denote a start of some *row* of bytes.
     *
     * @param dst     a matrix that both provides and receives bytes
     * @param offset0 a row that provides bytes and receives bytes
     * @param offset1 a row that provides bytes and receives bytes after rotation
     * @param offset2 a row that provides bytes
     * @param offset3 a row that provides bytes
     */
    public void reduced_duplex_row_wandering(long[] dst, final int offset0, final int offset1, final int offset2, final int offset3) {
        int word0 = offset0;
        int word1 = offset1;

        for (int i = 0; i != N_COLS; ++i) {
            final int rndcol0 = Math.floorMod((int) mem.flip(state[4]), N_COLS) * BLOCK_LEN_INT64;
            final int rndcol1 = Math.floorMod((int) mem.flip(state[6]), N_COLS) * BLOCK_LEN_INT64;

            final int word2 = offset2 + rndcol0;
            final int word3 = offset3 + rndcol1;

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                state[j] ^= mem.flip(
                      mem.flip(dst[word0 + j])
                    + mem.flip(dst[word1 + j])
                    + mem.flip(dst[word2 + j])
                    + mem.flip(dst[word3 + j])
                );
            }

            sponge_lyra(HALF_ROUNDS);

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                dst[word0 + j] ^= state[j];
            }

            for (int j = 0; j != BLOCK_LEN_INT64; ++j) {
                dst[word1 + j] ^= state[(j + 2) % BLOCK_LEN_INT64];
            }

            word0 += BLOCK_LEN_INT64;
            word1 += BLOCK_LEN_INT64;
        }
    }
}
