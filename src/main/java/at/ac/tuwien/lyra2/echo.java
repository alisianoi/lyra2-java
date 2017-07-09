package at.ac.tuwien.lyra2;

public class echo {

    /**
     * Echo bytes into System.out as hex in an n-by-m grid
     *
     * @param bytes - echo these bytes to console
     * @param n     - try to have n rows
     * @param m     - try to have m cols
     * @param s     - skip s bytes ahead
     */
    public static void bytes(byte[] bytes, int n, int m, int s) {
        int div = n / m;
        int mod = n % m;

        if (s + n > bytes.length) {
            System.out.println("You ask to dump " + (s + n) + " byte(s)");
            System.out.println("Buffer has only " + bytes.length + " byte(s)");
            return;
        }

        for (int i = 0; i != div; ++i) {
            for (int j = 0; j != m; ++j) {
                System.out.printf("%02X ", bytes[s + i * m + j]);
            } System.out.println();
        }

        for (int i = 0; i != mod; ++i) {
            System.out.printf("%02X ", bytes[s + div * m + i]);
        } System.out.println();
    }

    public static void bytes(byte[] bytes, int n) {
        echo.bytes(bytes, n, 16, 0);
    }

    public static void bytes(long[] longs, int n, int m, int s) {
        echo.bytes(pack.bytes(longs), n, m, s);
    }

    public static void bytes(long[] longs, int n) {
        echo.bytes(longs, n, 16, 0);
    }
}
