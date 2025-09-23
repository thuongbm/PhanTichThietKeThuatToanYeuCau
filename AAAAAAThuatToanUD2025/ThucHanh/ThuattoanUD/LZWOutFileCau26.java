import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class LZWOutFileCau26 {
    private static final int R = 256;        // số lượng ký hiệu cơ bản (byte)
    private static final int L = 4096;       // số lượng codeword = 2^W
    private static final int W = 12;         // độ rộng codeword (12 bit)

    private LZWOutFileCau26() { }

    // Hàm nén
    public static void compress() {
        byte[] input = BinaryStdInCau26.readAllBytes();
        Map<String, Integer> st = new HashMap<>();

        // Khởi tạo bảng mã với tất cả byte (0..255)
        for (int i = 0; i < R; i++)
            st.put("" + (char) i, i);

        int code = R + 1;  // R là EOF
        String data = new String(input, StandardCharsets.ISO_8859_1);

        while (data.length() > 0) {
            String s = longestPrefix(st, data);       // tìm prefix dài nhất
            BinaryStdOutFileCau26.write(st.get(s), W);     // ghi mã codeword
            int t = s.length();
            if (t < data.length() && code < L)
                st.put(data.substring(0, t + 1), code++);
            data = data.substring(t);
        }
        BinaryStdOutFileCau26.write(R, W); // EOF
        BinaryStdOutFileCau26.close();
    }

    // Hàm tìm prefix dài nhất trong HashMap (thay cho TST)
    private static String longestPrefix(Map<String, Integer> st, String s) {
        int max = 1;
        for (int i = 1; i <= s.length(); i++) {
            String sub = s.substring(0, i);
            if (st.containsKey(sub)) max = i;
            else break;
        }
        return s.substring(0, max);
    }

    // Hàm giải nén
    public static void expand() {
        String[] st = new String[L];
        int i;

        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = "";

        int codeword = BinaryStdInCau26.readInt(W);
        if (codeword == R) return;
        String val = st[codeword];

        while (true) {
            // ghi dữ liệu (nhị phân)
            BinaryStdOutFileCau26.write(val.getBytes(StandardCharsets.ISO_8859_1));
            codeword = BinaryStdInCau26.readInt(W);
            if (codeword == R) break;
            String s = st[codeword];
            if (i == codeword) s = val + val.charAt(0);
            if (i < L) st[i++] = val + s.charAt(0);
            val = s;
        }
        BinaryStdOutFileCau26.close();
    }

    public static void main(String[] args) throws IOException {
        System.setIn(new FileInputStream(new File("VanBanTiengVietCau26.txt")));
        System.setIn(new FileInputStream(new File("outtxt1.txt")));
        if      (args[0].equals("-")) compress();
        else if (args[0].equals("+")) expand();
        else throw new IllegalArgumentException("Illegal command line argument");
    }
}
