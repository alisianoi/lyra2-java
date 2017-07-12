package at.ac.tuwien.lyra2;

public class LyraParams {
    public final int klen;

    public final int t_cost;
    public final int m_cost;

    public final int ROUNDS;

    public final int N_COLS;

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
            int ROUNDS, int N_COLS, int BLOCK_LEN_INT64
    ) {
        this.klen = klen;

        this.t_cost = t_cost;
        this.m_cost = m_cost;

        this.ROUNDS = ROUNDS;
        this.N_COLS = N_COLS;

        this.BLOCK_LEN_INT64 =     BLOCK_LEN_INT64;
        this.BLOCK_LEN_BYTES = 8 * BLOCK_LEN_INT64;

        this.ROW_LEN_INT64 =     N_COLS * BLOCK_LEN_INT64;
        this.ROW_LEN_BYTES = 8 * N_COLS * BLOCK_LEN_INT64;
    }
}
