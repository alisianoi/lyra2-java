package at.ac.tuwien.lyra2;

public class Go {
    public static void memcpy1(byte[] dst, int offset, int src) {
        dst[offset + 0] = (byte) (src       );
        dst[offset + 1] = (byte) (src >>>  8);
        dst[offset + 2] = (byte) (src >>> 16);
        dst[offset + 3] = (byte) (src >>> 24);
    }

    public static long[] pack_longs(byte[] bytes) {
        int div = bytes.length / 8;
        int mod = bytes.length % 8;

        long[] longs = new long[div + (mod == 0 ? 0 : 1)];

        for (int i = 0; i != div; ++i) {
            long l = 0L;

            for (int j = 0; j != 7; ++j, l <<= 8) {
                // Upcasting a negative value gives a negative value
                // So, mask the result of an upcast to last byte only
                l |= (bytes[i * 8 + j] & 0x00000000000000FFL);

            } l |= bytes[i * 8 + 7] & 0x00000000000000FFL;

            longs[i] = l;
        }

        if (mod != 0) {
            long l = 0;

            for (int i = 0; i != mod - 1; ++i) {
                l |= bytes[div * 8 + i];

                l <<= 8;
            } l |= bytes[div * 8 + mod];

            l <<= (8 * (7 - mod));

            longs[div] = l;
        }

        return longs;
    }

    public static void dump_bytes(byte[] bytes, int n, int m) {
        int div = n / m;
        int mod = n % m;

        for (int i = 0; i != div; ++i) {
            for (int j = 0; j != m; ++j) {
                System.out.printf("%02X ", bytes[i * m + j]);
            } System.out.println();
        }

        for (int i = 0; i != mod; ++i) {
            System.out.printf("%02X ", bytes[div * m + i]);
        } System.out.println();
    }

    public static void dump_bytes(byte[] bytes, int n) {
        dump_bytes(bytes, n, 16);
    }

    public static void dump_bytes(long[] longs, int n, int m) {
        byte[] bytes = new byte[n];

        int div = n / 8;
        int mod = n % 8;

        for (int i = 0; i != div; ++i) {
            for (int j = 0; j != 8; ++j) {
                bytes[8 * i + j] = (byte) (longs[i] >>> (64 - 8 - j * 8));
            }
        }

        for (int i = 0; i != mod; ++i) {
            bytes[8 * div + i] = (byte) (longs[div] >>> (64 - 8 - i * 8));
        }

        dump_bytes(bytes, n, m);
    }

    public static void dump_bytes(long[] longs, int n) {
        dump_bytes(longs, n, 16);
    }
}
