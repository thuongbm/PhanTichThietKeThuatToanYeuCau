 import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;
import java.util.Scanner;

public class BST<Key extends Comparable<Key>, Value> {
    private Node root;             // root of BST

    private class Node {
        private Key key;           // sorted by key
        private Value val;         // associated data
        private Node left, right;  // left and right subtrees
        private int size;          // number of nodes in subtree

        public Node(Key key, Value val, int size) {
            this.key = key;
            this.val = val;
            this.size = size;
        }
    }

    public boolean isEmpty() {
        return size() == 0;
    }

    public int size() {
        return size(root);
    }

    private int size(Node x) {
        if (x == null) return 0;
        else return x.size;
    }

    public int size(Key lo, Key hi) {
        if (lo == null) throw new IllegalArgumentException("first argument to size() is null");
        if (hi == null) throw new IllegalArgumentException("second argument to size() is null");
        if (lo.compareTo(hi) > 0) return 0;
        
        return size(root, lo, hi);
    }

    private int size(Node x, Key lo, Key hi) {
        if (x == null) return 0;

        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);

        // Trường hợp 1: Node hiện tại (x) nằm trong khoảng [lo, hi]
        // -> Đếm node này (1) + đếm tiếp bên trái + đếm tiếp bên phải
        if (cmplo <= 0 && cmphi >= 0) {
            return 1 + size(x.left, lo, hi) + size(x.right, lo, hi);
        } 
        // Trường hợp 2: Node hiện tại nhỏ hơn lo (x.key < lo)
        // -> Khoảng cần tìm nằm hoàn toàn bên phải
        else if (cmplo > 0) {
            return size(x.right, lo, hi);
        } 
        // Trường hợp 3: Node hiện tại lớn hơn hi (x.key > hi)
        // -> Khoảng cần tìm nằm hoàn toàn bên trái
        else {
            return size(x.left, lo, hi);
        }
    }
    // ===========================================================================

    public boolean contains(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to contains() is null");
        return get(key) != null;
    }

    public Value get(Key key) {
        return get(root, key);
    }

    private Value get(Node x, Key key) {
        if (key == null) throw new IllegalArgumentException("calls get() with a null key");
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return get(x.left, key);
        else if (cmp > 0) return get(x.right, key);
        else return x.val;
    }

    public void put(Key key, Value val) {
        if (key == null) throw new IllegalArgumentException("calls put() with a null key");
        if (val == null) {
            delete(key);
            return;
        }
        root = put(root, key, val);
    }

    private Node put(Node x, Key key, Value val) {
        if (x == null) return new Node(key, val, 1);
        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = put(x.left, key, val);
        else if (cmp > 0) x.right = put(x.right, key, val);
        else x.val = val;
        x.size = 1 + size(x.left) + size(x.right);
        return x;
    }

    public void deleteMin() {
        if (isEmpty()) throw new NoSuchElementException("Symbol table underflow");
        root = deleteMin(root);
    }

    private Node deleteMin(Node x) {
        if (x.left == null) return x.right;
        x.left = deleteMin(x.left);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void deleteMax() {
        if (isEmpty()) throw new NoSuchElementException("Symbol table underflow");
        root = deleteMax(root);
    }

    private Node deleteMax(Node x) {
        if (x.right == null) return x.left;
        x.right = deleteMax(x.right);
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    public void delete(Key key) {
        if (key == null) throw new IllegalArgumentException("calls delete() with a null key");
        root = delete(root, key);
    }

    private Node delete(Node x, Key key) {
        if (x == null) return null;

        int cmp = key.compareTo(x.key);
        if (cmp < 0) x.left = delete(x.left, key);
        else if (cmp > 0) x.right = delete(x.right, key);
        else {
            if (x.right == null) return x.left;
            if (x.left == null) return x.right;
            Node t = x;
            x = min(t.right);
            x.right = deleteMin(t.right);
            x.left = t.left;
        }
        x.size = size(x.left) + size(x.right) + 1;
        return x;
    }

    public Key min() {
        if (isEmpty()) throw new NoSuchElementException("calls min() with empty symbol table");
        return min(root).key;
    }

    private Node min(Node x) {
        if (x.left == null) return x;
        else return min(x.left);
    }

    public Key max() {
        if (isEmpty()) throw new NoSuchElementException("calls max() with empty symbol table");
        return max(root).key;
    }

    private Node max(Node x) {
        if (x.right == null) return x;
        else return max(x.right);
    }

    public Key floor(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to floor() is null");
        if (isEmpty()) throw new NoSuchElementException("calls floor() with empty symbol table");
        Node x = floor(root, key);
        if (x == null) return null;
        else return x.key;
    }

    private Node floor(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp < 0) return floor(x.left, key);
        Node t = floor(x.right, key);
        if (t != null) return t;
        else return x;
    }

    public Key ceiling(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to ceiling() is null");
        if (isEmpty()) throw new NoSuchElementException("calls ceiling() with empty symbol table");
        Node x = ceiling(root, key);
        if (x == null) return null;
        else return x.key;
    }

    private Node ceiling(Node x, Key key) {
        if (x == null) return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0) return x;
        if (cmp < 0) {
            Node t = ceiling(x.left, key);
            if (t != null) return t;
            else return x;
        }
        return ceiling(x.right, key);
    }

    public Key select(int k) {
        if (k < 0 || k >= size()) {
            throw new IllegalArgumentException("argument to select() is invalid: " + k);
        }
        Node x = select(root, k);
        return x.key;
    }

    private Node select(Node x, int k) {
        if (x == null) return null;
        int t = size(x.left);
        if (t > k) return select(x.left, k);
        else if (t < k) return select(x.right, k - t - 1);
        else return x;
    }

    public int rank(Key key) {
        if (key == null) throw new IllegalArgumentException("argument to rank() is null");
        return rank(key, root);
    }

    private int rank(Key key, Node x) {
        if (x == null) return 0;
        int cmp = key.compareTo(x.key);
        if (cmp < 0) return rank(key, x.left);
        else if (cmp > 0) return 1 + size(x.left) + rank(key, x.right);
        else return size(x.left);
    }

    public Iterable<Key> keys() {
        if (isEmpty()) return new LinkedList<>();
        return keys(min(), max());
    }

    public Iterable<Key> keys(Key lo, Key hi) {
        if (lo == null) throw new IllegalArgumentException("first argument to keys() is null");
        if (hi == null) throw new IllegalArgumentException("second argument to keys() is null");

        Queue<Key> queue = new LinkedList<>();
        keys(root, queue, lo, hi);
        return queue;
    }

    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
        if (x == null) return;
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        if (cmplo < 0) keys(x.left, queue, lo, hi);
        if (cmplo <= 0 && cmphi >= 0) queue.add(x.key);
        if (cmphi > 0) keys(x.right, queue, lo, hi);
    }

    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null) return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    public Iterable<Key> levelOrder() {
        Queue<Key> keys = new LinkedList<>();
        Queue<Node> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            Node x = queue.remove();
            if (x == null) continue;
            keys.add(x.key);
            queue.add(x.left);
            queue.add(x.right);
        }
        return keys;
    }

    // Phương thức search hỗ trợ cho hàm test (để in ra các phần tử trong khoảng)
    public Queue<Key> search(Key lo, Key hi) {
        Queue<Key> result = new LinkedList<>();
        search(root, lo, hi, result);
        return result;
    }

    private void search(Node x, Key lo, Key hi, Queue<Key> result) {
        if (x == null) return;
        int cmpLo = lo.compareTo(x.key);
        int cmpHi = hi.compareTo(x.key);
        if (cmpLo < 0) search(x.left, lo, hi, result);
        if (cmpLo <= 0 && cmpHi >= 0) result.add(x.key);
        if (cmpHi > 0) search(x.right, lo, hi, result);
    }

    // --- MAIN TEST ---
    public static void main(String[] args) {
        BST<Integer, String> bst = new BST<>();

        // Nạp dữ liệu từ file (nếu có)
        try {
            File file = new File("src/buoi9/test_data.txt");
            Scanner sc = new Scanner(file);
            while (sc.hasNext()) {
                if (sc.hasNextInt()) {
                    bst.put(sc.nextInt(), sc.next());
                }
            }
            sc.close();
            System.out.println("Đã nạp dữ liệu từ file thành công.\n");
        } catch (FileNotFoundException e) {
            System.out.println("Không tìm thấy file 'test_data.txt'. Đang nạp dữ liệu mẫu tự động...");
            // Dữ liệu mẫu thay thế nếu không có file
            int[] keys = {50, 30, 20, 40, 70, 60, 80};
            for (int k : keys) bst.put(k, "Data" + k);
            System.out.println("Dữ liệu mẫu: 20, 30, 40, 50, 60, 70, 80\n");
        }

        // Test các trường hợp
        test(bst, 30, 70); // Trường hợp bình thường
        test(bst, 10, 15); // Trường hợp không có phần tử nào
        test(bst, 20, 80); // Trường hợp toàn bộ cây
    }

    private static void test(BST<Integer, String> bst, int lo, int hi) {
        System.out.println("--- Kiểm tra khoảng [" + lo + ", " + hi + "] ---");
        System.out.println("size(" + lo + ", " + hi + "): " + bst.size(lo, hi));

        System.out.print("search(" + lo + ", " + hi + "):");
        Queue<Integer> result = bst.search(lo, hi);
        for (Integer key : result) {
            System.out.print(" " + key);
        }
        System.out.println("\n");
    }
}
