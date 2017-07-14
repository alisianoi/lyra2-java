package at.ac.tuwien.lyra2;

public class LyraParams {
    public final int klen;

    public final int t_cost;
    public final int m_cost;

    public final int N_COLS;

    public final String SPONGE;

    public final int FULL_ROUNDS;
    public final int HALF_ROUNDS;

    public final int BLOCK_LEN_INT64;
    public final int BLOCK_LEN_BYTES;

    public final int ROW_LEN_INT64;
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
