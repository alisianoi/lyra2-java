package at.ac.tuwien.lyra2;

public class Sponge {
    static final Long[] blake2b_IV = {
            0x6a09e667f3bcc908L, 0xbb67ae8584caa73bL,
            0x3c6ef372fe94f82bL, 0xa54ff53a5f1d36f1L,
            0x510e527fade682d1L, 0x9b05688c2b3e6c1fL,
            0x1f83d9abfb41bd6bL, 0x5be0cd19137e2179L
    };

    public static byte[] state = new byte[128];

    public Sponge() {
        for (int i = 0; i != 64; ++i) {
            state[i] = 0;
        }

        for (int i = 0; i != 8; ++i) {
            for (int j = 0; j != 8; ++j) {
                state[64 + i * 8 + j] = (byte) (blake2b_IV[i] >>> j * 8);
            }
        }
    }
}
