import java.io.*;

public final class BinaryStdOutFileCau26 {
    private static BufferedOutputStream out;
    private static int buffer;
    private static int n;

    static {
        try {
            File fw = new File("outtxt1.txt");
            FileOutputStream fos = new FileOutputStream(fw, false); // ghi đè
            out = new BufferedOutputStream(fos);
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private BinaryStdOutFileCau26() { }

    private static void writeBit(boolean bit) {
        buffer <<= 1;
        if (bit) buffer |= 1;
        n++;
        if (n == 8) clearBuffer();
    }

    private static void writeByte(int x) {
        if (n == 0) {
            try {
                out.write(x);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        for (int i = 0; i < 8; i++) {
            boolean bit = ((x >>> (8 - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    private static void clearBuffer() {
        if (n == 0) return;
        if (n > 0) buffer <<= (8 - n);
        try {
            out.write(buffer);
        } catch (IOException e) {
            e.printStackTrace();
        }
        n = 0;
        buffer = 0;
    }

    public static void flush() {
        clearBuffer();
        try {
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void close() {
        flush();
        try {
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void write(int x, int r) {
        for (int i = 0; i < r; i++) {
            boolean bit = ((x >>> (r - i - 1)) & 1) == 1;
            writeBit(bit);
        }
    }

    public static void write(byte[] data) {
        for (byte b : data) {
            writeByte(b & 0xff);
        }
    }
}
