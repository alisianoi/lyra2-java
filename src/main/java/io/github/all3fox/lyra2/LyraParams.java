package io.github.all3fox.lyra2;

/**
 * Store various {@link Lyra2} related parameters in one place.
 */
public class LyraParams {
    /**
     * Resulting hash length in bytes.
     */
    public final int klen;

    /**
     * Time cost, an integer in {@code [1, +Inf)}
     * <p>
     * Roughly proportional to the number of iterations during the wandering phase.
     */
    public final int t_cost;

    /**
     * Memory cost, an integer in {@code [3, +Inf)}
     * <p>
     * Roughly proportional to the number of iterations during the wandering phase.
     * Roughly proportional to the size of the matrix used throughout the process.
     */
    public final int m_cost;

    /**
     * Number of columns in the matrix.
     * <p>
     * Each column holds {@code BLOCK_LEN_INT64 8-byte} blocks.
     */
    public final int N_COLS;

    /**
     * Type of sponge being used, one of: Blake2b, BlaMka or Half-round BlaMka.
     * <p>
     * The exact name or capitalization is subject to change.
     */
    public final String SPONGE;

    /**
     * Number of rounds during sponge state permutation, typically 12.
     */
    public final int FULL_ROUNDS;

    /**
     * Reduced number of rounds during sponge state permutation, typically 12.
     */
    public final int HALF_ROUNDS;

    /**
     * Size of one block in {@code 8-byte} chunks.
     */
    public final int BLOCK_LEN_INT64;

    /**
     * Size of one block in {@code bytes}, equals to {@code 8 * BLOCK_LEN_INT64}.
     */
    public final int BLOCK_LEN_BYTES;

    /**
     * Size of one row of the matrix in {@code 8-byte} chunks.
     */
    public final int ROW_LEN_INT64;

    /**
     * Size of one row of the matrix  in {@code bytes}.
     */
    public final int ROW_LEN_BYTES;

    public final int SIZEOF_INT   = 4;
    public final int SIZEOF_INT64 = 8;

    public final int BLOCK_LEN_BLAKE2_SAFE_INT64 = 8;
    public final int BLOCK_LEN_BLAKE2_SAFE_BYTES = 64;

    public LyraParams(
            int klen, int t_cost, int m_cost,
            int N_COLS, String SPONGE,
            int FULL_ROUNDS, int HALF_ROUNDS, int BLOCK_LEN_INT64
    ) {
        this.klen = klen;

        this.t_cost = t_cost;
        this.m_cost = m_cost;

        this.N_COLS = N_COLS;

        this.SPONGE = SPONGE;
        this.FULL_ROUNDS = FULL_ROUNDS;
        this.HALF_ROUNDS = HALF_ROUNDS;

        this.BLOCK_LEN_INT64 =     BLOCK_LEN_INT64;
        this.BLOCK_LEN_BYTES = 8 * BLOCK_LEN_INT64;

        this.ROW_LEN_INT64 =     N_COLS * BLOCK_LEN_INT64;
        this.ROW_LEN_BYTES = 8 * N_COLS * BLOCK_LEN_INT64;
    }
}
