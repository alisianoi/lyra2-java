package io.github.all3fox.lyra2;

/**
 * A sponge that uses Blake2b as its core transformation.
 */
public class SpongeBlake2b extends Sponge {
    /**
     * {@inheritDoc}
     */
    public SpongeBlake2b(LyraParams params) {
        super(params);
    }

    @Override
    public void G(final int a, final int b, final int c, final int d) {
        state[a] = mem.flip(mem.flip(state[a]) + mem.flip(state[b]));
        state[d] = rotl64(state[d] ^ state[a], 32);

        state[c] = mem.flip(mem.flip(state[c]) + mem.flip(state[d]));
        state[b] = rotl64(state[b] ^ state[c], 24);

        state[a] = mem.flip(mem.flip(state[a]) + mem.flip(state[b]));
        state[d] = rotl64(state[d] ^ state[a], 16);

        state[c] = mem.flip(mem.flip(state[c]) + mem.flip(state[d]));
        // Cannot use the left rotation trick here: 63 % 8 != 0, so
        // individual bytes do not stay the same, they change too.
        state[b] = mem.flip(rotr64(mem.flip(state[b] ^ state[c]), 63));
    }
}
