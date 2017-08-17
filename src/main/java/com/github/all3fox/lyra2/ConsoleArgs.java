package com.github.all3fox.lyra2;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

/**
 * Define console arguments and provide help/usage messages.
 */
@Command(
        name="Lyra2",
        description="Hash your password with adjustable time and memory costs",
        showDefaultValues = true
)
public class ConsoleArgs {
    @Option(names = {"-h", "--help"}, help = true, description = "display this help message")
    boolean help;

    @Parameters(paramLabel="password", index="0", description="the password to hash")
    public String pass;

    @Parameters(paramLabel="salt", index="1", description="the salt to use with the hash")
    public String salt;

    @Parameters(paramLabel="klen", index="2", description="the number of bytes to output")
    public int klen;

    @Parameters(paramLabel="tcost", index="3", description="the time cost value")
    public int t_cost;

    @Parameters(paramLabel="mcost", index="4", description="the memory cost value")
    public int m_cost;

    @Option(names={"--blocks"}, description="the number of INT64 that make up a block")
    public int BLOCK_LEN_INT64 = 12;

    @Option(names={"--columns"}, description="the number of columns")
    public int N_COLS = 256;

    @Option(names={"--sponge"}, description="the sponge to use")
    public String SPONGE = "blake2b";

    @Option(names={"--full-rounds"}, description="the full number of sponge rounds")
    public int FULL_ROUNDS = 12;

    @Option(names={"--half-rounds"}, description="the reduced number of sponge rounds")
    public int HALF_ROUNDS = 12;
}
