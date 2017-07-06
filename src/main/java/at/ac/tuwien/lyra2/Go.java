package at.ac.tuwien.lyra2;

public class Go {
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
                bytes[8 * i + j] = (byte) (longs[i] >> (64 - 8 - j * 8));
            }
        }

        for (int i = 0; i != mod; ++i) {
            bytes[8 * div + i] = (byte) (longs[div] >> (64 - 8 - i * 8));
        }

        dump_bytes(bytes, n, m);
    }

    public static void dump_bytes(long[] longs, int n) {
        dump_bytes(longs, n, 16);
    }
}
