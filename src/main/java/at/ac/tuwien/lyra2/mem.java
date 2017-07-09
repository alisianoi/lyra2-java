package at.ac.tuwien.lyra2;

/**
 * Provide methods that mimic memory manipulation.
 *
 * The class name is lowercase because its methods are static.
 */
public class mem {
    /**
     * Copy an integer into an array of bytes starting at offset.
     * <p>
     * Reverse the order of bytes to mimic little-endian memory layout.
     *
     * @param dst       an array that receives the integer
     * @param offset    an index into dst, the first byte is written there
     * @param src       an integer to copy
     */
    public static void copy(byte[] dst, int offset, int src) {
        dst[offset + 0] = (byte) (src       );
        dst[offset + 1] = (byte) (src >>>  8);
        dst[offset + 2] = (byte) (src >>> 16);
        dst[offset + 3] = (byte) (src >>> 24);
    }

    /**
     * Do a little-endian to big-endian (or vice a versa) byte flip.
     * <p>
     * The following property holds: {@code flip(flip(x))} equals {@code x}.
     * <p>
     * Example: {@code 0xDEADBEEFL} becomes {@code 0xEFBEADDE00000000L}
     *
     * @param x a long to be flipped
     */
    public static long flip(final long x) {
        return    (x & 0x00000000000000FFL)  << 56
                | (x & 0x000000000000FF00L)  << 40
                | (x & 0x0000000000FF0000L)  << 24
                | (x & 0x00000000FF000000L)  <<  8
                | (x & 0x000000FF00000000L) >>>  8
                | (x & 0x0000FF0000000000L) >>> 24
                | (x & 0x00FF000000000000L) >>> 40
                | (x & 0xFF00000000000000L) >>> 56
                ;
    }
}
