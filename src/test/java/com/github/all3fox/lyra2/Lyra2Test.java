package com.github.all3fox.lyra2;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import static org.junit.Assert.assertArrayEquals;

@RunWith(Parameterized.class)
public class Lyra2Test {
    private static Logger logger = Logger.getLogger("Lyra2Test");
    @Parameterized.Parameters
    public static Collection<Object[]> setupClass() {
        Constructor constructor = new Constructor(DataEntry.class);
        TypeDescription description = new TypeDescription(DataEntry.class);
        description.putListPropertyType("hash", String.class);
        constructor.addTypeDescription(description);

        Yaml yaml = new Yaml(constructor);

        String[] fnames = new String[] {
//                  "data-50-columns-256-sponge-blake2b-rounds-12-blocks-12.yml"
//                , "data-50-columns-256-sponge-blake2b-rounds-12-blocks-8.yml"
//                , "data-50-columns-256-sponge-blake2b-rounds-1-blocks-12.yml"
//                , "data-50-columns-256-sponge-blake2b-rounds-1-blocks-8.yml"
//                , "data-50-columns-96-sponge-blamka-rounds-12-blocks-12.yml"
//                , "data-50-columns-96-sponge-blamka-rounds-12-blocks-8.yml"
//                , "data-50-columns-96-sponge-blamka-rounds-1-blocks-12.yml"
                "data-50-columns-96-sponge-blamka-rounds-1-blocks-8.yml"
//                , "data-50-columns-512-sponge-half-round-blamka-rounds-12-blocks-12.yml"
//                , "data-50-columns-512-sponge-half-round-blamka-rounds-12-blocks-8.yml"
//                , "data-50-columns-512-sponge-half-round-blamka-rounds-1-blocks-12.yml"
//                , "data-50-columns-512-sponge-half-round-blamka-rounds-1-blocks-8.yml"
        };

        List<Object[]> entries = new ArrayList<>();

        for (String fname: fnames) {
            try {
                ClassLoader loader = ClassLoader.getSystemClassLoader();
                String s = loader.getResource(fname).getFile();
                FileReader reader = new FileReader(s);

                for (Object data : yaml.loadAll(reader)) {
                    entries.add(new Object[]{data});
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        return entries;
    }

    private DataEntry entry;

    public Lyra2Test(DataEntry entry) {
        this.entry = entry;
    }

    @Test
    public void simpleTest() {
        // TODO: adjust data format to contain both full and half rounds
        final int FULL_ROUNDS, HALF_ROUNDS;
        if (entry.sponge.equals("blake2b")) {
            FULL_ROUNDS = 12;
            HALF_ROUNDS = entry.rounds;
        } else if (entry.sponge.equals("blamka")) {
            FULL_ROUNDS = 12;
            HALF_ROUNDS = entry.rounds;
        } else if (entry.sponge.equals("half-round-blamka")) {
            FULL_ROUNDS = 24;
            HALF_ROUNDS = entry.rounds;
        } else {
            System.err.println("Could not recognize sponge: " + entry.sponge);

            return;
        }

        LyraParams params = new LyraParams(
                entry.klen, entry.tcost, entry.mcost,
                entry.columns, entry.sponge,
                FULL_ROUNDS, HALF_ROUNDS,
                entry.blocks
        );

        byte[] hash = new byte[entry.klen];
        byte[] pass = entry.pass.getBytes();
        byte[] salt = entry.salt.getBytes();

        Lyra2.phs(hash, pass, salt, params);

        byte[] correct_hash = pack.bytes(entry.hash);

        String message = "columns-"
                + entry.columns
                + "-sponge-"
                + entry.sponge
                + "-rounds-"
                + entry.rounds
                + "-blocks-"
                + entry.blocks
                ;

        assertArrayEquals(message, correct_hash, hash);

        logger.info(message + " ok");
    }
}

