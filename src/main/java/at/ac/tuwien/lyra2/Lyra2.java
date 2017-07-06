package at.ac.tuwien.lyra2;

public class Lyra2 {
    private final static int SIZEOF_INT = 4;
    // Taken from Sponge.h
    private final static int BLOCK_LEN_BLAKE2_SAFE_INT64 = 8;
    private final static int BLOCK_LEN_BLAKE2_SAFE_BYTES = 64;

    private static void memcpy1(byte[] dst, int offset, int src) {
        dst[offset + 0] = (byte) (src);
        dst[offset + 1] = (byte) (src >> 8);
        dst[offset + 2] = (byte) (src >> 16);
        dst[offset + 3] = (byte) (src >> 24);
    }

    public static Long phs(
            byte[] dst, int dstlen,
            byte[] src, int srclen,
            byte[] salt, int saltlen,
            int t_cost, int m_cost,
            /* parameters below are set at compile time in C implementation */
            int N_COLS, int BLOCK_LEN_INT64) {
        return hash(dst, dstlen, src, srclen, salt, saltlen, t_cost, m_cost, N_COLS, BLOCK_LEN_INT64);
    }

    public static Long hash(
            byte[] dst, int dstlen,
            byte[] src, int srclen,
            byte[] salt, int saltlen,
            int t_cost, int m_cost,
            /* parameters below are set at compile time in C implementation */
            int N_COLS, int BLOCK_LEN_INT64) {
        Long gap = 1L;
        Long step = 1L;
        Long window = 2L;
        Long sqrt = 2L;

        Long row0 = 3L;
        Long prev0 = 2L;
        Long row1 = 1L;
        Long prev1 = 0L;

        int ROW_LEN_INT64 = BLOCK_LEN_INT64 * N_COLS;
        int ROW_LEN_BYTES =   ROW_LEN_INT64 * 8;

        int i = m_cost * ROW_LEN_BYTES;

//        System.out.println("Will allocate " + i + " bytes for whole matrix");

        // i == m_cost (3) * BLOCK_LEN_INT64 (12) * N_COLS (256) * 8
        // C allocation is in bytes, so divide
        byte[] whole_matrix = new byte[i];

        int[] memory_matrix = new int[m_cost];

        int row = 0;
        for (int ii = 0; ii < m_cost; ++ii) {
            memory_matrix[ii] = row;
            row += ROW_LEN_BYTES;
        }

        //==== Padding (password + salt + params) with 10*1 ====//
        // See comment about constant 6 in original code
        int nBlocksInput = (saltlen + srclen + 6 * SIZEOF_INT) / BLOCK_LEN_BLAKE2_SAFE_BYTES + 1;

//        System.out.println("nBlocksInput: " + nBlocksInput);
//
//        System.out.println("Going to print salt once more:");
//        for (int s = 0; s < salt.length; ++s) {
//            System.out.print(salt[s]);
//            System.out.print(" ");
//        } System.out.println();
//
//        System.out.println("Allocating that many bytes:");
//        System.out.println(nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES);
//
//        System.out.println("srclen " + srclen);
//        System.out.println("saltlen " + saltlen);

        int ii;
        for (ii = 0; ii < nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES; ++ii) {
            whole_matrix[ii] = 0;
        }

        ii = 0;
        for (int jj = 0; jj < srclen; ++jj) {
            whole_matrix[ii] = src[jj]; ii++;
        }

        for (int jj = 0; jj < saltlen; ++jj) {
            whole_matrix[ii] = salt[jj]; ii++;
        }

        memcpy1(whole_matrix, ii, dstlen);
        ii += 4;
        memcpy1(whole_matrix, ii, srclen);
        ii += 4;
        memcpy1(whole_matrix, ii, saltlen);
        ii += 4;
        memcpy1(whole_matrix, ii, t_cost);
        ii += 4;
        memcpy1(whole_matrix, ii, m_cost);
        ii += 4;
        memcpy1(whole_matrix, ii, N_COLS);
        ii += 4;

        whole_matrix[ii] = (byte) 0x80;

        whole_matrix[nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES - 1] ^= (byte) 0x01;

        System.out.println("Going to print fst of whole_matrix:");
        Go.dump_bytes(whole_matrix, nBlocksInput * BLOCK_LEN_BLAKE2_SAFE_BYTES );

        Sponge sponge = new Sponge();

        System.out.println("Going to print sponge.state:");
        Go.dump_bytes(sponge.state, 8 * sponge.state.length);

        System.out.println("Echo blake2b_IV longs:");
        Go.dump_bytes(Sponge.blake2b_IV, 64);
        return 42L;
    }
}