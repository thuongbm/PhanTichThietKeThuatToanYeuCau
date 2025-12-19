import java.io.*;
import java.util.*;

/**
 * Class LZWTiengViet
 * Hỗ trợ nén file Tiếng Việt (UTF-8) và mọi loại file nhị phân.
 * Chạy trực tiếp trên BlueJ không cần thư viện ngoài.
 */
public class LZWTiengViet {

    // Kích thước từ điển 12-bit (4096 mục)
    private static final int DICT_SIZE = 4096; 
    private static final int CODE_WIDTH = 12; 

    // =========================================================
    // PHẦN 1: NÉN (COMPRESS)
    // =========================================================
    public static void compress(String inputFile, String outputFile) {
        System.out.println("Đang nén file: " + inputFile + " ...");
        
        try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(inputFile));
             BitOutputStream out = new BitOutputStream(new FileOutputStream(outputFile))) {
            
            // Khởi tạo từ điển cơ bản (0-255)
            Map<String, Integer> dictionary = new HashMap<>();
            for (int i = 0; i < 256; i++) {
                dictionary.put("" + (char) i, i);
            }

            String w = "";
            int nextCode = 256;
            int byteRead;

            // Đọc từng byte (không đọc char để tránh lỗi Unicode)
            while ((byteRead = in.read()) != -1) {
                char c = (char) byteRead; // Ép kiểu byte sang char (0-255) để ghép chuỗi
                String wc = w + c;
                
                if (dictionary.containsKey(wc)) {
                    w = wc;
                } else {
                    out.write(dictionary.get(w), CODE_WIDTH);
                    
                    if (nextCode < DICT_SIZE) {
                        dictionary.put(wc, nextCode++);
                    }
                    w = "" + c;
                }
            }
            
            // Ghi mã cuối cùng nếu còn
            if (!w.equals("")) {
                out.write(dictionary.get(w), CODE_WIDTH);
            }
            // Ghi mã kết thúc EOF (dùng mã 4095 làm dấu hiệu kết thúc file)
            out.write(4095, CODE_WIDTH); 
            
            System.out.println("-> Nén hoàn tất! File kết quả: " + outputFile);

        } catch (FileNotFoundException e) {
            System.err.println("LỖI: Không tìm thấy file '" + inputFile + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // =========================================================
    // PHẦN 2: GIẢI NÉN (EXPAND)
    // =========================================================
    public static void expand(String inputFile, String outputFile) {
        System.out.println("Đang giải nén file: " + inputFile + " ...");

        try (BitInputStream in = new BitInputStream(new FileInputStream(inputFile));
             BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(outputFile))) {

            // Khởi tạo từ điển ngược (Code -> Chuỗi byte)
            Map<Integer, String> dictionary = new HashMap<>();
            for (int i = 0; i < 256; i++) {
                dictionary.put(i, "" + (char) i);
            }

            int nextCode = 256;
            int oldCode = in.read(CODE_WIDTH);
            
            if (oldCode == -1 || oldCode == 4095) return; // File rỗng hoặc chỉ có EOF
            
            String w = dictionary.get(oldCode);
            writeStringAsBytes(out, w);
            
            int newCode;
            while ((newCode = in.read(CODE_WIDTH)) != -1) {
                if (newCode == 4095) break; // Gặp mã EOF thì dừng

                String s;
                if (dictionary.containsKey(newCode)) {
                    s = dictionary.get(newCode);
                } else if (newCode == nextCode) {
                    s = w + w.charAt(0); // Trường hợp đặc biệt cScSc
                } else {
                    throw new IllegalStateException("File nén bị lỗi!");
                }
                
                writeStringAsBytes(out, s);

                if (nextCode < DICT_SIZE) {
                    dictionary.put(nextCode++, w + s.charAt(0));
                }
                w = s;
            }
            
            System.out.println("-> Giải nén hoàn tất! File kết quả: " + outputFile);

        } catch (FileNotFoundException e) {
            System.err.println("LỖI: Không tìm thấy file '" + inputFile + "'");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Hàm phụ: Ghi chuỗi (chứa các byte 0-255) ra file thực
    private static void writeStringAsBytes(OutputStream out, String s) throws IOException {
        for (int i = 0; i < s.length(); i++) {
            out.write((byte) s.charAt(i));
        }
    }

    static class BitOutputStream implements AutoCloseable {
        private OutputStream out;
        private int buffer;
        private int bitsInBuffer;

        public BitOutputStream(OutputStream out) {
            this.out = out;
        }

        public void write(int code, int numBits) throws IOException {
            buffer |= (code << bitsInBuffer);
            bitsInBuffer += numBits;
            while (bitsInBuffer >= 8) {
                out.write(buffer & 0xFF);
                buffer >>>= 8;
                bitsInBuffer -= 8;
            }
        }

        @Override
        public void close() throws IOException {
            if (bitsInBuffer > 0) out.write(buffer & 0xFF);
            out.close();
        }
    }

    // Đọc các bit từ file
    static class BitInputStream implements AutoCloseable {
        private InputStream in;
        private int buffer;
        private int bitsInBuffer;
        private boolean isEOF;

        public BitInputStream(InputStream in) {
            this.in = in;
        }

        public int read(int numBits) throws IOException {
            while (bitsInBuffer < numBits) {
                int byteRead = in.read();
                if (byteRead == -1) {
                    if (bitsInBuffer == 0) return -1;
                    isEOF = true;
                    break; 
                }
                buffer |= (byteRead << bitsInBuffer);
                bitsInBuffer += 8;
            }
            if (isEOF && bitsInBuffer < numBits) return -1;
            int result = buffer & ((1 << numBits) - 1);
            buffer >>>= numBits;
            bitsInBuffer -= numBits;
            return result;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
        
    }
    
    public static void main(String[] args) {
        // CÁCH 1: Nếu người dùng điền tham số trong ô BlueJ (ví dụ: {"1", "input.txt", "out.lzw"})
        if (args != null && args.length >= 3) {
            String mode = args[0];
            String input = args[1];
            String output = args[2];
            
            if (mode.equals("1") || mode.equals("-")) compress(input, output);
            else if (mode.equals("2") || mode.equals("+")) expand(input, output);
            else System.out.println("Tham số đầu tiên sai (dùng 1 để nén, 2 để giải nén).");
            
            return; // Chạy xong thoát luôn
        }

    }
}