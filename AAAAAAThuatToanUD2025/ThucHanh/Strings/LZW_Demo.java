import java.util.*;

public class LZW_Demo {

    // Kích thước từ điển (4096 như trong sách)
    private static final int L = 4096;
    private static final int R = 256; // Bảng mã ASCII mở rộng

    public static void main(String[] args) {
        String input = "ABCAEOABCAABCCA";
        
        System.out.println("=== BẮT ĐẦU MÔ PHỎNG LZW ===");
        System.out.println("Chuỗi đầu vào: " + input);
        System.out.println("\n------------------------------------------------");
        
        // 1. NÉN (COMPRESS)
        System.out.println(String.format("%-10s %-15s %-10s %-20s", "Bước", "Input còn lại", "Mã ra", "Thêm vào từ điển"));
        System.out.println("------------------------------------------------");
        
        List<Integer> compressedCodes = compress(input);
        
        System.out.println("\nKẾT QUẢ MÃ HÓA (Dạng số): " + compressedCodes);
        System.out.println("------------------------------------------------\n");

        // 2. GIẢI NÉN (EXPAND)
        System.out.println(String.format("%-10s %-10s %-15s %-20s", "Bước", "Mã vào", "Chuỗi ra", "Thêm vào từ điển"));
        System.out.println("------------------------------------------------");
        
        String decodedString = expand(compressedCodes);
        
        System.out.println("\n------------------------------------------------");
        System.out.println("KẾT QUẢ GIẢI MÃ: " + decodedString);
        System.out.println("KHỚP VỚI GỐC?  : " + (input.equals(decodedString) ? "CÓ (THÀNH CÔNG)" : "KHÔNG"));
    }

    // --- HÀM NÉN ---
    public static List<Integer> compress(String input) {
        // Thay thế TST bằng HashMap để không cần thư viện ngoài
        Map<String, Integer> dictionary = new HashMap<>();
        
        // Khởi tạo từ điển cơ bản (0-255)
        for (int i = 0; i < R; i++) {
            dictionary.put("" + (char) i, i);
        }

        int code = R + 1; // Bắt đầu mã mới từ 257 (256 là EOF)
        List<Integer> result = new ArrayList<>();
        int step = 1;

        while (input.length() > 0) {
            // Tìm chuỗi tiền tố dài nhất có trong từ điển
            // (Mô phỏng hàm st.longestPrefixOf)
            String s = "";
            for (int len = input.length(); len >= 1; len--) {
                String sub = input.substring(0, len);
                if (dictionary.containsKey(sub)) {
                    s = sub;
                    break;
                }
            }

            // In ra mã của chuỗi s
            int outputCode = dictionary.get(s);
            result.add(outputCode);

            // Logic thêm vào từ điển: input hiện tại + ký tự tiếp theo
            int t = s.length();
            String addedToDict = "-"; // Để in log
            
            if (t < input.length() && code < L) {
                String newEntry = input.substring(0, t + 1);
                dictionary.put(newEntry, code);
                addedToDict = code + " (" + newEntry + ")";
                code++;
            }

            // In log chi tiết
            System.out.println(String.format("%-10d %-15s %-10d %-20s", 
                step++, 
                (input.length() > 10 ? input.substring(0,10)+"..." : input), 
                outputCode, 
                addedToDict));

            // Cắt bỏ phần đã xử lý khỏi input
            input = input.substring(t);
        }
        
        // Thêm mã kết thúc (EOF)
        result.add(R); 
        return result;
    }

    // --- HÀM GIẢI NÉN ---
    public static String expand(List<Integer> codes) {
        // Mảng chuỗi làm từ điển ngược
        String[] st = new String[L];
        int i; 

        // Khởi tạo từ điển
        for (i = 0; i < R; i++)
            st[i] = "" + (char) i;
        st[i++] = ""; // chừa chỗ cho EOF (256)

        int codeIdx = 0;
        int codeword = codes.get(codeIdx++);
        
        // Đọc mã đầu tiên
        String val = st[codeword];
        StringBuilder result = new StringBuilder();
        result.append(val);
        
        System.out.println(String.format("%-10d %-10d %-15s %-20s", 1, codeword, val, "-"));

        int step = 2;
        while (codeIdx < codes.size()) {
            codeword = codes.get(codeIdx++);
            
            if (codeword == R) break; // Gặp EOF thì dừng

            String s = st[codeword];
            
            // Trường hợp đặc biệt (cScSc): nếu mã chưa có trong bảng
            if (s == null) {
                s = val + val.charAt(0);
            }

            result.append(s);

            // Thêm vào từ điển: Chuỗi cũ + ký tự đầu của chuỗi mới
            String addedToDict = "-";
            if (i < L) {
                st[i] = val + s.charAt(0);
                addedToDict = i + " (" + st[i] + ")";
                i++;
            }
            
            // In log chi tiết
            System.out.println(String.format("%-10d %-10d %-15s %-20s", step++, codeword, s, addedToDict));

            val = s;
        }
        return result.toString();
    }
}