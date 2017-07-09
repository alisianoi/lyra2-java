package at.ac.tuwien.lyra2;

import picocli.CommandLine.Option;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(
        name="Lyra2",
        description="Hash your password with adjustable time and memory costs",
        footer="Java version by Aleksandr Lisianoi"
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

    @Option(names={"--columns"}, description="the number of columns")
    public int N_COLS = 256;

    @Option(names={"--rounds"}, description="the number of sponge rounds in reduced operations")
    public int ROUNDS = 1;

    @Option(names={"--blocks"}, description="the number of INT64 that make up a block")
    public int BLOCK_LEN_INT64 = 12;
}
