/******************************************************************************
 *  Compilation:  javac HuffmanCau24.java
 *  Execution:    
 *    java HuffmanCau24 - input.txt compressed.huff
 *    java HuffmanCau24 + compressed.huff
 *
 *  Dependencies: BinaryStdInCau24.java, BinaryStdOutCau24.java
 ******************************************************************************/

import java.io.*;

public class HuffmanCau24 {
    private static final int R = 256;   // số byte có thể (0–255)

    // node của cây Huffman
    private static class Node implements Comparable<Node> {
        private final int ch;
        private final int freq;
        private final Node left, right;

        Node(int ch, int freq, Node left, Node right) {
            this.ch = ch;
            this.freq = freq;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return (left == null && right == null);
        }

        public int compareTo(Node that) {
            return this.freq - that.freq;
        }
    }

    // xây dựng cây Huffman từ tần suất
    private static Node buildTrie(int[] freq) {
        MinPQ<Node> pq = new MinPQ<>();
        for (int i = 0; i < R; i++) {
            if (freq[i] > 0) pq.insert(new Node(i, freq[i], null, null));
        }
        while (pq.size() > 1) {
            Node left = pq.delMin();
            Node right = pq.delMin();
            Node parent = new Node(-1, left.freq + right.freq, left, right);
            pq.insert(parent);
        }
        return pq.delMin();
    }

    // xây bảng mã
    private static void buildCode(String[] st, Node x, String s) {
        if (!x.isLeaf()) {
            buildCode(st, x.left,  s + '0');
            buildCode(st, x.right, s + '1');
        } else {
            st[x.ch] = s;
        }
    }

    // ghi trie ra file nén
    private static void writeTrie(Node x) {
        if (x.isLeaf()) {
            BinaryStdOutCau24.write(true);
            BinaryStdOutCau24.write((byte) x.ch);
            return;
        }
        BinaryStdOutCau24.write(false);
        writeTrie(x.left);
        writeTrie(x.right);
    }

    // đọc trie từ file nén
    private static Node readTrie() {
        boolean isLeaf = BinaryStdInCau24.readBoolean();
        if (isLeaf) {
            return new Node(BinaryStdInCau24.readByte() & 0xFF, -1, null, null);
        } else {
            return new Node(-1, -1, readTrie(), readTrie());
        }
    }

    // nén
    public static void compress() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        while (!BinaryStdInCau24.isEmpty()) {
            baos.write(BinaryStdInCau24.readByte() & 0xFF);
        }
        byte[] input = baos.toByteArray();

        int[] freq = new int[R];
        for (byte b : input) freq[b & 0xFF]++;

        Node root = buildTrie(freq);

        String[] st = new String[R];
        buildCode(st, root, "");

        writeTrie(root);
        BinaryStdOutCau24.write(input.length);

        for (byte b : input) {
            String code = st[b & 0xFF];
            for (int i = 0; i < code.length(); i++) {
                BinaryStdOutCau24.write(code.charAt(i) == '1');
            }
        }
        BinaryStdOutCau24.close();
    }

    // giải nén (in thẳng ra console)
    public static void expand() {
        Node root = readTrie();
        int length = BinaryStdInCau24.readInt();
        for (int i = 0; i < length; i++) {
            Node x = root;
            while (!x.isLeaf()) {
                boolean bit = BinaryStdInCau24.readBoolean();
                x = bit ? x.right : x.left;
            }
            System.out.print((char) x.ch); // in ký tự ra console
        }
        System.out.flush();
    }


    // main theo yêu cầu
    public static void main(String[] args) {
        try {
            if (args.length < 2) {
                System.err.println("Usage:");
                System.err.println("  java HuffmanCau24 - input.txt compressed.huff");
                System.err.println("  java HuffmanCau24 + compressed.huff");
                return;
            }

            String mode = args[0];
            String inputFile = args[1];

            if (mode.equals("-")) {
                if (args.length < 3) {
                    System.err.println("Need output file for compression!");
                    return;
                }
                String outputFile = args[2];
                System.setIn(new FileInputStream(inputFile));
                System.setOut(new PrintStream(new FileOutputStream(outputFile)));
                compress();
            } else if (mode.equals("+")) {
                System.setIn(new FileInputStream(inputFile));
                expand(); // in ra console
            } else {
                throw new IllegalArgumentException("Illegal command line argument: " + mode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
