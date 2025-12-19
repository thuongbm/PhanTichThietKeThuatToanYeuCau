import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class LRS {

    public static void main(String[] args) {
        // Kiểm tra đầu vào
        if (args.length == 0) {
            System.out.println("Cach dung: java LRS <ten_file_code_C>");
            return;
        }

        // Đọc file code C vào một chuỗi String duy nhất
        String text = new In(args[0]).readAll();
        
        // Chuẩn hóa: Thay thế mọi dấu xuống dòng/tab thành 1 khoảng trắng
        // Điều này giúp tìm được code lặp lại kể cả khi định dạng (indentation) hơi khác nhau
        // Nếu bạn muốn tìm lặp chính xác tuyệt đối cả xuống dòng, hãy bỏ lệnh .replaceAll(...)
        String processedText = text.replaceAll("\\s+", " ");

        int n = processedText.length();
        SuffixArray sa = new SuffixArray(processedText);

        String lrs = ""; // Chuỗi lặp dài nhất
        
        // Duyệt qua mảng LCP để tìm giá trị lớn nhất
        for (int i = 1; i < n; i++) {
            int length = sa.lcp(i);
            if (length > lrs.length()) {
                // sa.select(i) trả về suffix thứ i
                // ta cắt lấy đúng độ dài lcp
                lrs = sa.select(i).substring(0, length);
            }
        }

        StdOut.println("--- KET QUA PHAN TICH ---");
        StdOut.println("Do dai doan lap lon nhat: " + lrs.length() + " ky tu.");
        StdOut.println("Noi dung doan lap:");
        StdOut.println("--------------------------------------------------");
        StdOut.println(lrs);
        StdOut.println("--------------------------------------------------");
    }
}