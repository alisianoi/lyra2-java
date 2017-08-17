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

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class Lyra2Test {
    @Parameterized.Parameters
    public static Collection<Object[]> setupClass() {
        Constructor constructor = new Constructor(DataEntry.class);
        TypeDescription description = new TypeDescription(DataEntry.class);
        description.putListPropertyType("hash", String.class);
        constructor.addTypeDescription(description);

        Yaml yaml = new Yaml(constructor);

        String[] fnames = new String[] {
                "data-15-blake2b.yml",
                "data-15-blamka.yml",
                "data-15-half-round-blamka.yml"
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

        for (int i = 0; i != entry.klen; ++i) {
            assertEquals(correct_hash[i], hash[i]);
        }
    }
}

