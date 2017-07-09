package at.ac.tuwien.lyra2;

public class mem {
    public static void copy(byte[] dst, int offset, int src) {
        dst[offset + 0] = (byte) (src       );
        dst[offset + 1] = (byte) (src >>>  8);
        dst[offset + 2] = (byte) (src >>> 16);
        dst[offset + 3] = (byte) (src >>> 24);
    }
}
