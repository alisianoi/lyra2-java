package at.ac.tuwien.lyra2;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;

public class Main {
    @Parameter(names={"--help"}, help=true)
    private boolean help;

    @Parameter(
        names={"--pass"}, required=true,
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
        names={"--tcost"}, required=true,
        description="time cost"
    )
    Integer tcost;

    @Parameter(
        names={"--mcost"}, required=true,
        description="memory cost (number of rows)"
    )
    Integer mcost;

    @Parameter(
        names={"--NCOLS"},
        description="number of columns"
    )
    Integer NCOLS = 256;

    @Parameter(
        names={"--BLOCK_LEN_INT64"},
        description="block length in INT64"
    )
    Integer BLOCK_LEN_INT64 = 12;

    public static void dump_bytes(byte[] bytes, int n, int m) {
        int div = n / m;
        int mod = n % m;

        for (int i = 0; i != div; ++i) {
            for (int j = 0; j != m; ++j) {
                System.out.printf("%02X ", bytes[i * m + j]);
            } System.out.println();
        }

        for (int i = 0; i != mod; ++i) {
            System.out.printf("%02X ", bytes[div * m + i]);
        } System.out.println();
    }

    public static void dump_bytes(byte[] bytes, int n) {
        Main.dump_bytes(bytes, n, 16);
    }

    public static void main(String[] argv) {
        Main main = new Main();
        JCommander jc = JCommander.newBuilder().addObject(main).build();

        jc.parse(argv);
        if (main.help) {
            jc.usage();
            return;
        }

//        System.out.println("Commandline parameters: ");
//        System.out.println("Password: " + main.pass + " (" + main.pass.length() + ")");
//        System.out.println("Salt    : " + main.salt + " (" + main.salt.length() + ")");
//
//        System.out.println();
//
//        System.out.println("Compile time parameters: ");
//        System.out.println("          NCOLS: " + main.NCOLS);
//        System.out.println("BLOCK_LEN_INT64: " + main.BLOCK_LEN_INT64);

        byte[] hash = new byte[main.klen];
        byte[] pass = main.pass.getBytes();
        byte[] salt = main.salt.getBytes();

//        System.out.println("Going to print pass:");
//        Main.dump_bytes(pass, pass.length);
//        System.out.println("Going to print salt:");
//        Main.dump_bytes(salt, salt.length);

        Lyra2.phs(
                hash, hash.length,
                pass, pass.length,
                salt, salt.length,
                main.tcost, main.mcost,
                main.NCOLS, main.BLOCK_LEN_INT64
        );
    }
}
