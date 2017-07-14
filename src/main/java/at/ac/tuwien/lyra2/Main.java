package at.ac.tuwien.lyra2;

import picocli.CommandLine;

public class Main {
    public static void main(String[] argv) {
        ConsoleArgs args = CommandLine.populateCommand(new ConsoleArgs(), argv);

        if (args.help) {
            CommandLine.usage(new ConsoleArgs(), System.out);

            return;
        }

        String SPONGE = args.SPONGE = args.SPONGE.toLowerCase();
        if (!SPONGE.equals("blake2b") && !SPONGE.equals("blamka") && !SPONGE.equals("half-blamka")) {
            System.err.println("--sponge must be one of: blake2b, blamka or half-blamka");
            System.err.println("Instead, you specified --sponge " + SPONGE);

            return;
        }

        LyraParams params = new LyraParams(
                args.klen, args.t_cost, args.m_cost,
                args.N_COLS, args.SPONGE,
                args.FULL_ROUNDS, args.HALF_ROUNDS,
                args.BLOCK_LEN_INT64
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
