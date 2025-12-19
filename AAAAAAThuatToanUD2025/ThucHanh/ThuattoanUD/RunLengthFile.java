/******************************************************************************
 * Compilation:  javac RunLengthFile.java
 * Execution:    java RunLengthFile
 * Dependencies: BinaryIn.java, BinaryOut.java
 *
 * Class này dùng để giải nén file bằng thuật toán RunLength
 * và ghi kết quả ra file nhị phân bằng BinaryOut.
 ******************************************************************************/

public class RunLengthFile {

    // Số bit dùng để đếm độ dài (theo chuẩn RunLength của Sedgewick là 8 bit)
    private static final int LG_R = 8; 

    /**
     * Hàm giải nén file
     * @param inputFileName : Tên file nén đầu vào
     * @param outputFileName : Tên file kết quả đầu ra
     */
    public static void expand(String inputFileName, String outputFileName) {
        // 1. Tạo luồng đọc file nén
        BinaryIn in = new BinaryIn(inputFileName);

        // 2. Tạo luồng ghi file kết quả
        BinaryOut out = new BinaryOut(outputFileName);

        boolean b = false; // Trạng thái bit ban đầu (RunLength mặc định bắt đầu bằng bit 0)

        System.out.println("Dang giai nen tu: " + inputFileName + " -> " + outputFileName);

        try {
            // 3. Vòng lặp đọc và giải nén
            while (!in.isEmpty()) {
                // Đọc 8 bit tiếp theo để biết độ dài chuỗi (run length)
                int run = in.readInt(LG_R);

                // Ghi số lượng bit tương ứng ra file output
                for (int i = 0; i < run; i++) {
                    out.write(b);
                }

                // Đảo trạng thái bit (hết chuỗi 0 thì đến chuỗi 1 và ngược lại)
                b = !b;
            }
        } catch (Exception e) {
            System.out.println("Co loi xay ra: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 4. CỰC KỲ QUAN TRỌNG: Đóng file để lưu dữ liệu xuống ổ cứng
            out.close();
            // in.close(); // (Tùy chọn đóng file đọc)
            System.out.println("Hoan tat! File da duoc luu.");
        }
    }

    public static void main(String[] args) {
        // --- CẤU HÌNH TÊN FILE TẠI ĐÂY ---
        
        // 1. Tên file nén đầu vào (Bạn sửa tên này thành file bạn đang có)
        String fileInput = "abra.txt"; 
        
        // 2. Tên file đầu ra
        // LƯU Ý: Windows không cho phép đặt tên file có dấu '*' (q32*48.bin).
        // Tôi đã đổi thành 'x' (q32x48.bin). Nếu bạn dùng Linux/Mac thì có thể sửa lại dấu *.
        String fileOutput = "q32x48.bin"; 

        // Gọi hàm giải nén
        expand(fileInput, fileOutput);
    }
}