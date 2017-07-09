package at.ac.tuwien.lyra2;

/**
 * Echo bytes to System.out as hexadecimal values.
 *
 * The class name is lowercase because its methods are static.
 */
public class echo {

    /**
     * Echo bytes to System.out as hex in an n-by-m grid
     *
     * @param bytes echo these bytes to console
     * @param n     try to have n rows
     * @param m     try to have m cols
     * @param s     skip s bytes ahead
     */
    public static void bytes(byte[] bytes, int n, int m, int s) {
        int div = n / m;
        int mod = n % m;

        if (s + n > bytes.length) {
            System.out.println("You ask to echo " + (s + n) + " byte(s)");
            System.out.println("Buffer only has " + bytes.length + " byte(s)");
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

    /**
     * Echo bytes to System.out as hex in an n-by-16 grid.
     *
     * @param bytes echo these bytes to console
     * @param n     try to have n rows
     */
    public static void bytes(byte[] bytes, int n) {
        echo.bytes(bytes, n, 16, 0);
    }

    /**
     * Echo bytes to System.out as hex in an n-by-m grid.
     *
     * @param longs echo these bytes to console
     * @param n     try to have n rows
     * @param m     try to have n cols
     * @param s     skip s bytes ahead
     */
    public static void bytes(long[] longs, int n, int m, int s) {
        echo.bytes(pack.bytes(longs), n, m, s);
    }

    /**
     * Echo bytes to System.out as hex in an n-by-16 grid.
     *
     * @param longs echo these bytes to console
     * @param n     try to have n rows
     */
    public static void bytes(long[] longs, int n) {
        echo.bytes(longs, n, 16, 0);
    }
}
