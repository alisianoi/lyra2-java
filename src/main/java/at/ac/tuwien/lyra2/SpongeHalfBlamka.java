package at.ac.tuwien.lyra2;

/**
 * A sponge that uses Half-round BlaMka as its core transformation.
 */
public class SpongeHalfBlamka extends SpongeBlamka {
    /**
     * {@inheritDoc}
     */
    public SpongeHalfBlamka(LyraParams params) {
        super(params);
    }

    private void diagonalize() {
        long t0, t1, t2;

        t0 = state[4];

        state[4] = state[5];
        state[5] = state[6];
        state[6] = state[7];
        state[7] = t0;

        t0 = state[8];
        t1 = state[9];

        state[ 8] = state[10];
        state[ 9] = state[11];
        state[10] = t0;
        state[11] = t1;

        t0 = state[12];
        t1 = state[13];
        t2 = state[14];

        state[12] = state[15];
        state[13] = t0;
        state[14] = t1;
        state[15] = t2;
    }

    @Override
    public void sponge_lyra(final int rounds) {
        for (int round = 0; round != rounds; ++round) {
            G(0, 4,  8, 12);
            G(1, 5,  9, 13);
            G(2, 6, 10, 14);
            G(3, 7, 11, 15);

            diagonalize();
        }
    }

    @Override
    public void sponge_lyra() {
        sponge_lyra(FULL_ROUNDS);
    }
}
