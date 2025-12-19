import java.math.BigInteger;
import java.util.Random;
class RabinKarpSolver {
    private String pattern;  // Chuỗi mẫu
    private int M;           // Độ dài mẫu
    private long patHash;    // Giá trị Hash của mẫu
    private long Q;          // Số nguyên tố lớn (Modulus)
    private int R;           // Cơ số (Radix), thường là 256 hoặc 65536
    private long RM;         // Hệ số mũ cao nhất: R^(M-1) % Q

    // Constructor: Cài đặt thông số
    public RabinKarpSolver(String pattern) {
        this.pattern = pattern;
        this.M = pattern.length();
        this.R = 65536; // Cài đặt cơ số ký tự
        this.Q = laySoNguyenToNgauNhien(); // Hoặc thay bằng số cố định nếu đề bài yêu cầu (vd: 101)

        // Tính trước RM = R^(M-1) % Q
        RM = 1;
        for (int i = 1; i <= M - 1; i++) {
            RM = (R * RM) % Q;
        }

        //TÍNH HASH CỦA MẪU 
        this.patHash = tinhHash(pattern, M);

        System.out.println("[INFO] Modulus Q = " + Q);
        System.out.println("[INFO] Hash(\"" + pattern + "\") = " + patHash);
    }


    private long tinhHash(String key, int doDai) {
        long h = 0;
        for (int j = 0; j < doDai; j++) {
            h = (R * h + key.charAt(j)) % Q;
        }
        return h;
    }

    /**
     * HÀM 2: Rolling Hash (Băm trượt)
     * Tính Hash mới từ Hash cũ trong O(1)
     * oldHash: Hash của đoạn trước
     * removeChar: Ký tự bị loại bỏ (đầu đoạn cũ)
     * addChar: Ký tự mới thêm vào (cuối đoạn mới)
     */
    private long capNhatHashTruot(long oldHash, char removeChar, char addChar) {
        //Loại bỏ ký tự đầu tiên: (Hash - val*RM)
        long newHash = (oldHash + Q - (RM * removeChar) % Q) % Q;
        //Dịch trái (nhân R) và cộng ký tự mới
        newHash = (newHash * R + addChar) % Q;
        return newHash;
    }

    /**
     * Kiểm tra khớp từng ký tự (để xử lý va chạm Hash)
     */
    private boolean kiemTraKyTu(String text, int viTriBatDau) {
        for (int j = 0; j < M; j++) {
            if (pattern.charAt(j) != text.charAt(viTriBatDau + j))
                return false;
        }
        return true;
    }

    /**
     * Hàm tìm kiếm chính (kết hợp các hàm trên)
     */
    public int timKiem(String txt) {
        int N = txt.length();
        if (N < M) return -1;

        //Tính Hash của đoạn đầu 
        long txtHash = tinhHash(txt, M);
        System.out.println("[DEBUG] Hash đoạn text đầu (\"" + txt.substring(0, M) + "\"): " + txtHash);

        //So sánh đoạn đầu tiên
        if ((patHash == txtHash) && kiemTraKyTu(txt, 0)) {
            return 0;
        }

        //Vòng lặp trượt (Rolling Hash) từ vị trí M đến hết
        for (int i = M; i < N; i++) {
            // Gọi hàm cập nhật Hash trượt
            char kyTuBoDi = txt.charAt(i - M);
            char kyTuThem = txt.charAt(i);
            
            txtHash = capNhatHashTruot(txtHash, kyTuBoDi, kyTuThem);

            // Kiểm tra tại vị trí mới
            int viTriHienTai = i - M + 1;
            if ((patHash == txtHash) && kiemTraKyTu(txt, viTriHienTai)) {
                return viTriHienTai;
            }
        }

        return -1;
    }

    //Sinh số nguyên tố ngẫu nhiên
    private static long laySoNguyenToNgauNhien() {
        return BigInteger.probablePrime(31, new Random()).longValue();
    }
}
public class BaiTapRabinKarp {

    public static void main(String[] args) {
        String mauCanTim = "lop cntt3";
        String vanBan    = "tkb lop cntt3 k64";

        System.out.println("=== THÔNG TIN ĐẦU VÀO ===");
        System.out.println("Mẫu (Pattern): " + mauCanTim);
        System.out.println("Văn bản (Text): " + vanBan);
        System.out.println("=========================\n");

        // Khởi tạo thuật toán
        RabinKarpSolver rk = new RabinKarpSolver(mauCanTim);
        
        // Gọi hàm tìm kiếm
        int viTri = rk.timKiem(vanBan);

        // In kết quả cuối cùng
        if (viTri != -1) {
            System.out.println("\n=> TÌM THẤY MẪU TẠI VỊ TRÍ: " + viTri);
            // In minh họa
            System.out.println("Text:  " + vanBan);
            System.out.print("Match: ");
            for(int i=0; i<viTri; i++) System.out.print(" ");
            System.out.println(mauCanTim);
        } else {
            System.out.println("\n=> KHÔNG TÌM THẤY.");
        }
    }
}
