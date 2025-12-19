import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.charset.StandardCharsets;

public class TimXauChung {

    // Hàm main để chạy chương trình
    public static void main(String[] args) {
        try {
            // 1. Đọc nội dung 2 file báo cáo (Lưu ý: Phải dùng UTF-8 cho tiếng Việt)
            // Bạn nhớ đổi tên file cho đúng với file trong máy bạn
            String s1 = readFile("baocao1.txt");
            String s2 = readFile("baocao2.txt");

            System.out.println("Đang xử lý...");
            System.out.println("Độ dài bài 1: " + s1.length());
            System.out.println("Độ dài bài 2: " + s2.length());

            // 2. Tìm xâu con chung
            String ketQua = findLongestCommonSubstring(s1, s2);

            // 3. In kết quả
            System.out.println("------------------------------------------------");
            if (ketQua.length() > 0) {
                System.out.println("Xâu con chung dài nhất (" + ketQua.length() + " ký tự):");
                System.out.println("\"" + ketQua + "\"");
            } else {
                System.out.println("Không tìm thấy xâu chung nào đáng kể.");
            }

        } catch (IOException e) {
            System.out.println("Lỗi đọc file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Hàm logic chính sử dụng SuffixArray của bạn
    public static String findLongestCommonSubstring(String s1, String s2) {
        // Nối 2 chuỗi với ký tự ngăn cách đặc biệt (ví dụ '\uFFFF' hoặc '#')
        // Dùng ký tự lạ để tránh trùng với nội dung văn bản
        String separator = "#"; 
        String text = s1 + separator + s2;
        int n1 = s1.length();

        // Tạo SuffixArray từ class bạn đã có
        SuffixArray sa = new SuffixArray(text);

        String longestSubstring = "";
        int maxLen = 0;

        // Duyệt qua mảng hậu tố để tìm LCP lớn nhất
        for (int i = 1; i < text.length(); i++) {
            // Lấy độ dài tiền tố chung của hậu tố i và i-1
            int len = sa.lcp(i);

            if (len > maxLen) {
                // Lấy vị trí bắt đầu của 2 hậu tố này trong chuỗi gốc
                int index1 = sa.index(i);
                int index2 = sa.index(i - 1);

                // KIỂM TRA QUAN TRỌNG:
                // Hai hậu tố phải nằm ở 2 văn bản khác nhau (một cái trước separator, một cái sau)
                boolean fromText1 = (index1 < n1);
                boolean fromText2 = (index2 < n1);

                // Nếu một cái thuộc s1, cái kia thuộc s2 (tức là fromText1 != fromText2)
                if (fromText1 != fromText2) {
                    maxLen = len;
                    longestSubstring = text.substring(index1, index1 + len);
                }
            }
        }
        return longestSubstring;
    }

    // Hàm phụ trợ để đọc file tiếng Việt
    private static String readFile(String fileName) throws IOException {
        File f = new File(fileName);
        if (!f.exists()) {
            throw new IOException("Không tìm thấy file: " + fileName);
        }
        // Đọc toàn bộ file thành String với mã hóa UTF-8
        return Files.readString(f.toPath(), StandardCharsets.UTF_8);
    }
}