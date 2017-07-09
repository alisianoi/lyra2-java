package at.ac.tuwien.lyra2;

import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] argv) {
        Parameters args = new Parameters();
        JCommander jc = JCommander.newBuilder().addObject(args).build();

        jc.parse(argv);
        if (args.help) {
            jc.usage();
            return;
        }

        LyraParams params = new LyraParams(
                args.klen, args.tcost, args.mcost,
                args.rounds, args.NCOLS, args.BLOCK_LEN_INT64
        );

        byte[] hash = new byte[args.klen];
        byte[] pass = args.pass.getBytes();
        byte[] salt = args.salt.getBytes();

        // TODO: you should overwrite the params.pass now

        Lyra2.phs(hash, pass, salt, params);

        System.out.println("Output:");
        echo.bytes(hash, hash.length);
    }
}
