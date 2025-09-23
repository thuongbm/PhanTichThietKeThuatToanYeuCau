/******************************************************************************
 *  Compilation:  javac BinaryStdInCau24.java
 *  Execution:    java BinaryStdInCau24 < input.txt
 *  Dependencies: none
 *
 *  Binary input. Reads bits, bytes, chars, ints, doubles, longs, etc.
 *
 ******************************************************************************/

import java.io.BufferedInputStream;
import java.io.IOException;

public final class BinaryStdInCau24 {
    private static BufferedInputStream in;
    private static int buffer;      // 8-bit buffer
    private static int n;           // number of bits left in buffer
    private static boolean isEOF;   // end of file indicator

    static {
        in = new BufferedInputStream(System.in);
        fillBuffer();
    }

    private BinaryStdInCau24() { }

    private static void fillBuffer() {
        try {
            buffer = in.read();
            n = 8;
        } catch (IOException e) {
            buffer = -1;
            n = -1;
        }
        if (buffer == -1) {
            isEOF = true;
            n = -1;
        }
    }

    public static boolean isEmpty() {
        return isEOF;
    }

    public static boolean readBoolean() {
        if (isEOF) throw new RuntimeException("Reading from empty input stream");
        n--;
        boolean bit = ((buffer >> n) & 1) == 1;
        if (n == 0) fillBuffer();
        return bit;
    }

    public static char readChar() {
        if (isEOF) throw new RuntimeException("Reading from empty input stream");
        char x = 0;
        for (int i = 0; i < 8; i++) {
            x <<= 1;
            if (readBoolean()) x |= 1;
        }
        return x;
    }

    // đọc nguyên 1 byte
    public static int readByte() {
        if (isEOF) throw new RuntimeException("Reading from empty input stream");
        int x = 0;
        for (int i = 0; i < 8; i++) {
            x <<= 1;
            if (readBoolean()) x |= 1;
        }
        return x & 0xFF;
    }

    public static int readInt() {
        int x = 0;
        for (int i = 0; i < 32; i++) {
            x <<= 1;
            if (readBoolean()) x |= 1;
        }
        return x;
    }

    public static void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new RuntimeException("Could not close BinaryStdInCau24", e);
        }
        isEOF = true;
    }
}
