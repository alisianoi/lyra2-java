package at.ac.tuwien.lyra2;

import com.beust.jcommander.Parameter;

public class Parameters {
    @Parameter(names={"--help"}, help=true)
    public boolean help;

    @Parameter(
            names={"--pass"}, required=true,
            description="password to hash"
    )
    public String pass;

    @Parameter(
            names={"--salt"}, required=true,
            description="salt to hash with password"
    )
    public String salt;

    @Parameter(
            names={"--klen"}, required=true,
            description="length of the produced hash"
    )
    public int klen;

    @Parameter(
            names={"--tcost"}, required=true,
            description="time cost"
    )
    public int tcost;

    @Parameter(
            names={"--mcost"}, required=true,
            description="memory cost (number of rows)"
    )
    public int mcost;

    @Parameter(
            names={"--N_COLS"},
            description="number of columns"
    )
    public int NCOLS = 256;

    @Parameter(
            names={"--BLOCK_LEN_INT64"},
            description="block length in INT64"
    )
    public int BLOCK_LEN_INT64 = 12;

    @Parameter(
            names={"--ROUNDS"},
            description="ROUNDS for reduced sponge"
    )
    public int rounds = 1;

    public final int SIZEOF_INT = 4;

    public final int BLOCK_LEN_BLAKE2_SAFE_INT64 = 8;
    public final int BLOCK_LEN_BLAKE2_SAFE_BYTES = 64;
}
