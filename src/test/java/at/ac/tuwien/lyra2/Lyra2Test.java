package at.ac.tuwien.lyra2;

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

        List<Object[]> entries = new ArrayList<>();

        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            String s = loader.getResource("data.yml").getFile();
            FileReader reader = new FileReader(s);

            for (Object data: yaml.loadAll(reader)) {
                entries.add(new Object[] {data});
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return entries;
    }

    private DataEntry entry;

    public Lyra2Test(DataEntry entry) {
        this.entry = entry;
    }

    @Test
    public void simpleTest() {
        LyraParams params = new LyraParams(
                entry.klen, entry.tcost, entry.mcost,
                entry.columns, entry.sponge, entry.rounds, entry.blocks
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

