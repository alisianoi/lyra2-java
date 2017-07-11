# [Lyra2][3]: a password hashing scheme

[![Travis](https://img.shields.io/travis/all3fox/lyra2-java.svg?style=flat-square)]()
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg?style=flat-square)](https://raw.githubusercontent.com/all3fox/lyra2-java/master/LICENSE)

> a password hashing scheme (PHS) based on cryptographic
  sponges. Lyra2 was designed to be strictly sequential (i.e., not
  easily parallelizable), providing strong security even against
  attackers that uses multiple processing cores (e.g., custom hardware
  or a powerful GPU). At the same time, it is very simple to implement
  in software and allows legitimate users to fine tune its memory and
  processing costs according to the desired level of security against
  brute force password-guessing. Lyra2 is an improvement of the
  recently proposed Lyra algorithm, providing an even higher security
  level against different attack venues and overcoming some
  limitations of this and other existing schemes.

[Paper in the Cryptology ePrint Archive][2] and [a reference implementation in C][1] by:

*Marcos A. Simplicio Jr., Leonardo C. Almeida, Ewerton R. Andrade, Paulo C. F. dos Santos and Paulo S. L. M. Barreto*

This repository is a spin-off implementation in Java by me, Aleksandr Lisianoi, as part of my Bachelor's thesis at TU Wien.

# How to compile and run?
```
git clone https://github.com/all3fox/lyra2-java.git
cd lyra2-java
mvn package
```

After maven packages the project, you can run it like so:

```
java -jar ./target/*-with-dependencies.jar password salt 3 3 3
```

You can get help about command line switches with `--help`:

```
java -jar ./target/*-with-dependencies.jar --help
```

# How to run tests?

```
mvn test
```

Currently, just one configuration is tested, its parameters are:

memory matrix with 256 columns, each block is 12 `int64`'s long, the
sponge is based on Blake2b and makes 12 rounds, the mode is
single-threaded.

The resulting hash is byte-level compatible with the [original C
implementation][1]. It means that if you match the build- and runtime
parameters and supply the same password/salt and desired output
length, you will get the same hash as output.

[1]: https://github.com/leocalm/Lyra
[2]: https://eprint.iacr.org/2015/136
[3]: http://lyra-2.net/
[4]: https://en.wikipedia.org/wiki/Lyra2