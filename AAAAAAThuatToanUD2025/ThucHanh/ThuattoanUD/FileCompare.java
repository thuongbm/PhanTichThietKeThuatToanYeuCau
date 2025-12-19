/******************************************************************************
 * Compilation:  javac FileCompare.java
 * Execution:    java FileCompare
 * Dependencies: BinaryIn.java
 *
 * Class này dùng để so sánh 2 file nhị phân xem chúng có giống hệt nhau không.
 ******************************************************************************/

public class FileCompare {

    public static void main(String[] args) {
        
        // --- CẤU HÌNH TÊN 2 FILE CẦN SO SÁNH ---
        // File 1: File gốc ban đầu (trước khi nén)
        String fileGoc = "abra.txt"; 
        
        // File 2: File sau khi bạn đã giải nén (bung ra)
        String fileMoi = "q32x48.bin"; 

        System.out.println("Dang so sanh: " + fileGoc + " va " + fileMoi);

        // Gọi hàm so sánh
        boolean result = compare(fileGoc, fileMoi);

        if (result) {
            System.out.println(">>> KET QUA: THANH CONG! Hai file giong het nhau.");
        } else {
            System.out.println(">>> KET QUA: THAT BAI. Hai file khac nhau.");
        }
    }

    /**
     * Hàm đọc 2 file và so sánh từng byte
     */
    public static boolean compare(String name1, String name2) {
        BinaryIn in1 = null;
        BinaryIn in2 = null;

        try {
            in1 = new BinaryIn(name1);
            in2 = new BinaryIn(name2);

            // Vòng lặp đọc đến khi nào hết file
            while (!in1.isEmpty() && !in2.isEmpty()) {
                // Đọc từng byte (8 bit) từ mỗi file
                char c1 = in1.readChar();
                char c2 = in2.readChar();

                // Nếu khác nhau thì dừng ngay lập tức
                if (c1 != c2) {
                    System.out.println("Phat hien khac biet tai ky tu: " + c1 + " != " + c2);
                    return false;
                }
            }

            // Kiểm tra độ dài file:
            // Nếu file 1 đã hết mà file 2 vẫn còn (hoặc ngược lại) -> Khác nhau về độ dài
            if (in1.isEmpty() != in2.isEmpty()) {
                System.out.println("Do dai hai file khong bang nhau.");
                return false;
            }

            // Nếu chạy đến đây nghĩa là nội dung và độ dài đều giống nhau
            return true;

        } catch (Exception e) {
            System.out.println("Khong tim thay file hoac loi doc file.");
            e.printStackTrace();
            return false;
        }
        // BinaryIn trong thư viện algs4 thường tự đóng hoặc không cần close() nghiêm ngặt như Out,
        // nhưng Java chuẩn thì nên close. Với bài tập BlueJ thì code trên là đủ.
    }
}