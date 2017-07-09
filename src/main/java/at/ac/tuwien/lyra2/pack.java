package at.ac.tuwien.lyra2;

/**
 * Pack bytes as longs and longs as bytes.
 *
 * The class name is lowercase because its methods are static.
 */
public class pack {
    /**
     * Pack a single long into an array of bytes.
     * <p>
     * Most significant bits go into {@literal 0th} place.
     * <p>
     * Example:
     * {@code 0xDEADBEEFL} becomes {@code [0x00, 0x00, 0x00, 0x00, 0xDE, 0xAD, 0xBE, 0xEF]}
     *
     * @param x a long to pack
     * @return an array of resulting bytes
     */
    public static byte[] bytes(long x) {
        byte[] bytes = new byte[8];

        for (int i = 0; i != 8; ++i) {
            bytes[i] = (byte) (x >>> (56 - 8 * i));
        }

        return bytes;
    }

    // TODO: rewrite using pack.bytes(long x)
    /**
     * Pack an array of longs into an array of bytes.
     * <p>
     * Most significant bits have a lower array index.
     * <p>
     * Example:
     * {@code [0xDEADBEEFL]} becomes {@code [0x00, 0x00, 0x00, 0x00, 0xDE, 0xAD, 0xBE, 0xEF]}
     *
     * @param longs an array of longs to pack
     * @return an array of resulting bytes
     */
    public static byte[] bytes(long[] longs) {
        byte[] bytes = new byte[8 * longs.length];

        for (int i = 0; i != longs.length; ++i) {
            for (int j = 0; j != 8; ++j) {
                bytes[8 * i + j] = (byte) (longs[i] >>> (56 - j * 8));
            }
        }

        return bytes;
    }

    /**
     * Pack an array of bytes into an array of longs.
     * <p>
     * A lower array index means a more significant byte. If the number of
     * provided bytes is not a multiple of 8 then the last long will have its
     * least significant bits padded with zeroes.
     * <p>
     * Example:
     * {@code [0xDE, 0xAD, 0xBE, 0xEF]} becomes {@code [0xDEADBEEF00000000L]}
     *
     * @param bytes an array of bytes to pack
     * @return      an array of resulting longs
     */
    public static long[] longs(byte[] bytes) {
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
                l |= (bytes[div * 8 + i] & 0x00000000000000FFL);

                l <<= 8;
            } l |= (bytes[div * 8 + mod - 1] & 0x00000000000000FFL);

            l <<= (8 * (8 - mod));

            longs[div] = l;
        }

        return longs;
    }
}
