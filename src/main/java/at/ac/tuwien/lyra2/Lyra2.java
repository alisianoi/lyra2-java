package at.ac.tuwien.lyra2;

import java.util.List;

public class Lyra2 {
    public static Integer phs(
            List<Long> dst, Long dstlen,
            List<Long> src, Long srclen,
            List<Long> salt, Long saltlen,
            Long t_cost, Long m_cost,
            /* parameters below are set at compile time in C implementation */
            Long N_COLS, Long BLOCK_LEN_INT64) {
        return hash(dst, dstlen, src, srclen, salt, saltlen, t_cost, m_cost, N_COLS, BLOCK_LEN_INT64);
    }

    public static Integer hash(
            List<Long> dst, Long dstlen,
            List<Long> src, Long srclen,
            List<Long> salt, Long saltlen,
            Long t_cost, Long m_cost,
            /* parameters below are set at compile time in C implementation */
            Long N_COLS, Long BLOCK_LEN_INT64) {
        Long gap = 1L;
        Long step = 1L;
        Long window = 2L;
        Long sqrt = 2L;

        Long row0 = 3L;
        Long prev0 = 2L;
        Long row1 = 1L;
        Long prev1 = 0L;

        Long ROW_LEN_INT64 = BLOCK_LEN_INT64 * N_COLS;
        Long ROW_LEN_BYTES =   ROW_LEN_INT64 * 8;

        Long i = m_cost * ROW_LEN_BYTES;

        System.out.println("Initialize i: " + i);

        return 42;
    }
}