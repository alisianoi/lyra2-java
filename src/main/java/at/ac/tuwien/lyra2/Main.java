package at.ac.tuwien.lyra2;

import com.beust.jcommander.JCommander;

public class Main {
    public static void main(String[] argv) {
        Parameters params = new Parameters();
        JCommander jc = JCommander.newBuilder().addObject(params).build();

        jc.parse(argv);
        if (params.help) {
            jc.usage();
            return;
        }

        byte[] hash = new byte[params.klen];
        byte[] pass = params.pass.getBytes();
        byte[] salt = params.salt.getBytes();

        // TODO: you should overwrite the params.pass now

        Lyra2.phs(hash, pass, salt, params);
    }
}
