package com.github.all3fox.lyra2;

/**
 * A sponge that uses BlaMka as its core transformation.
 */
public class SpongeBlamka extends Sponge {
    public SpongeBlamka(LyraParams params) {
        super(params);
    }

    private long fBlaMka(final long x, final long y) {
        long lessX = 0x00000000FFFFFFFFL & x;
        long lessY = 0x00000000FFFFFFFFL & y;

        lessX *= lessY;

        lessX <<= 1;

        return lessX + x + y;
    }

    @Override
    public void G(final int a, final int b, final int c, final int d) {
        state[a] = mem.flip(fBlaMka(mem.flip(state[a]), mem.flip(state[b])));
        state[d] = rotl64(state[d] ^ state[a], 32);

        state[c] = mem.flip(fBlaMka(mem.flip(state[c]), mem.flip(state[d])));
        state[b] = rotl64(state[b] ^ state[c], 24);

        state[a] = mem.flip(fBlaMka(mem.flip(state[a]), mem.flip(state[b])));
        state[d] = rotl64(state[d] ^ state[a], 16);

        state[c] = mem.flip(fBlaMka(mem.flip(state[c]), mem.flip(state[d])));
        // Cannot use the left rotation trick here: 63 % 8 != 0, so
        // individual bytes do not stay the same, they change too.
        state[b] = mem.flip(rotr64(mem.flip(state[b] ^ state[c]), 63));
    }
}
