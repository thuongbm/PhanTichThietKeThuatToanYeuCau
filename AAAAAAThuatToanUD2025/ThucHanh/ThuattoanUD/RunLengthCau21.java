import java.io.*;

public class RunLengthCau21 {
    private static final int R    = 256;
    private static final int LG_R = 8;

    // Do not instantiate.
    private RunLengthCau21() { }

    /**
     * Reads a sequence of bits from standard input (that are encoded
     * using run-length encoding with 8-bit run lengths); decodes them;
     * and writes the results to standard output.
     */
    public static void expand() {
        BinaryOut out = new BinaryOut();   // dùng BinaryOut thay BinaryStdOut
        boolean b = false;
        while (!BinaryStdIn.isEmpty()) {
            int run = BinaryStdIn.readInt(LG_R);
            for (int i = 0; i < run; i++)
                out.write(b);
            b = !b;
        }
        out.close();
    }

    /**
     * Reads a sequence of bits from standard input; compresses
     * them using run-length coding with 8-bit run lengths; and writes the
     * results to standard output.
     */
    public static void compress() {
        BinaryOut out = new BinaryOut();   // dùng BinaryOut thay BinaryStdOut
        char run = 0;
        boolean old = false;
        while (!BinaryStdIn.isEmpty()) {
            boolean b = BinaryStdIn.readBoolean();
            if (b != old) {
                out.write(run, LG_R);
                run = 1;
                old = !old;
            }
            else {
                if (run == R-1) {
                    out.write(run, LG_R);
                    run = 0;
                    out.write(run, LG_R);
                }
                run++;
            }
        }
        out.write(run, LG_R);
        out.close();
    }


    /**
     * Sample client that calls {@code compress()} if the command-line
     * argument is "-" and {@code expand()} if it is "+".
     *
     * @param args the command-line arguments
     */
    public static void main(String[] args) throws IOException {
        System.setIn(new FileInputStream(new File("abra.txt")));
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }

}
