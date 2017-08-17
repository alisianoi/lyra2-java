package com.github.all3fox.lyra2;

import java.util.List;

public class DataEntry {
    public Integer klen;
    public Integer tcost;
    public Integer mcost;

    public String pass;
    public String salt;
    public List<String> hash;

    public Integer blocks;
    public Integer rounds;
    public Integer columns;

    public Integer threads;
    public String sponge;
}
