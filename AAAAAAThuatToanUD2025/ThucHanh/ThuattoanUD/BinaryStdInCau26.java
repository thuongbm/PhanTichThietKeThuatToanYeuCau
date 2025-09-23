import java.io.*;
import java.util.NoSuchElementException;

public final class BinaryStdInCau26 {
    private static final int EOF = -1;
    private static BufferedInputStream in;
    private static int buffer;
    private static int n;
    private static boolean isInitialized;

    private BinaryStdInCau26() { }

    private static void initialize() {
        in = new BufferedInputStream(System.in);
        buffer = 0;
        n = 0;
        fillBuffer();
        isInitialized = true;
    }

    private static void fillBuffer() {
        try {
            buffer = in.read();
            n = 8;
        } catch (IOException e) {
            buffer = EOF;
            n = -1;
        }
    }

    public static boolean isEmpty() {
        if (!isInitialized) initialize();
        return buffer == EOF;
    }

    public static int readInt(int r) {
        if (r < 1 || r > 32) throw new IllegalArgumentException("Illegal value of r = " + r);
        int x = 0;
        for (int i = 0; i < r; i++) {
            x <<= 1;
            if (readBoolean()) x |= 1;
        }
        return x;
    }

    public static boolean readBoolean() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        n--;
        boolean bit = ((buffer >> n) & 1) == 1;
        if (n == 0) fillBuffer();
        return bit;
    }

    public static byte[] readAllBytes() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while (!isEmpty()) {
                baos.write(readChar());
            }
            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public static char readChar() {
        if (isEmpty()) throw new NoSuchElementException("Reading from empty input stream");
        if (n == 8) {
            int x = buffer;
            fillBuffer();
            return (char) (x & 0xff);
        }
        int x = buffer;
        x <<= (8 - n);
        int oldN = n;
        fillBuffer();
        n = oldN;
        x |= (buffer >>> n);
        return (char) (x & 0xff);
    }

    public static void close() {
        try {
            in.close();
        } catch (IOException e) {
            throw new IllegalStateException("Could not close BinaryStdIn", e);
        }
    }
}
