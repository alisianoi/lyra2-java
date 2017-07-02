package at.ac.tuwien.lyra2;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Main {
    @Parameter(names={"--help"}, help=true)
    private boolean help;

    @Parameter(
            names={"--pass", "--pwd"}, required=true,
            description="password to hash"
    )
    String pass;

    @Parameter(
            names={"--salt"}, required=true,
            description="salt to hash with password"
    )
    String salt;

    @Parameter(
            names={"--klen"}, required=true,
            description="length of the produced hash"
    )
    Integer klen;

    @Parameter(
            names={"--NCOLS"}, description="number of columns"
    )
    Long NCOLS = 256L;

    @Parameter(
            names={"--BLOCK_LEN_INT64"}, description="block length in INT64"
    )
    Long BLOCK_LEN_INT64 = 12L;

    public static void main(String[] argv) {
        Main main = new Main();
        JCommander jc = JCommander.newBuilder().addObject(main).build();

        jc.parse(argv);
        if (main.help) {
            jc.usage();
            return;
        }

        System.out.println("Commandline parameters: ");
        System.out.println("Password: " + main.pass + " (" + main.pass.length() + ")");
        System.out.println("Salt    : " + main.salt + " (" + main.salt.length() + ")");

        System.out.println();

        System.out.println("Compile time parameters: ");
        System.out.println("          NCOLS: " + main.NCOLS);
        System.out.println("BLOCK_LEN_INT64: " + main.BLOCK_LEN_INT64);
    }
}
