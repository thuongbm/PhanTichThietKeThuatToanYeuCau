//cau 9
import java.io.*;
import java.util.*;
import java.util.Queue;
import java.util.LinkedList;

/**
 *  TrieSTUp: Trie symbol table hỗ trợ Unicode (dùng HashMap thay vì mảng 256).
 *  Hỗ trợ các thao tác put, get, contains, delete, size, keysWithPrefix,
 *  keysThatMatch (có wildcard '.').
 */
public class TrieSTUp<Value> {
    private Node root;      // gốc của trie
    private int n;          // số lượng key trong trie

    // node của trie
    private static class Node {
        private Object val;
        private Map<Character, Node> next = new HashMap<>();
    }

    public TrieSTUp() {
    }

    // Lấy giá trị theo key
    public Value get(String key) {
        if (key == null) throw new IllegalArgumentException("argument to get() is null");
        Node x = get(root, key, 0);
        if (x == null) return null;
        return (Value) x.val;
    }

    private Node get(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) return x;
        char c = key.charAt(d);
        return get(x.next.get(c), key, d+1);
    }

    // Kiểm tra tồn tại key
    public boolean contains(String key) {
        return get(key) != null;
    }

    // Thêm key-value
    public void put(String key, Value val) {
        if (key == null) throw new IllegalArgumentException("first argument to put() is null");
        if (val == null) delete(key);
        else root = put(root, key, val, 0);
    }

    private Node put(Node x, String key, Value val, int d) {
        if (x == null) x = new Node();
        if (d == key.length()) {
            if (x.val == null) n++;
            x.val = val;
            return x;
        }
        char c = key.charAt(d);
        x.next.put(c, put(x.next.get(c), key, val, d+1));
        return x;
    }

    public int size() { return n; }

    public boolean isEmpty() { return size() == 0; }

    // Lấy tất cả key
    public Iterable<String> keys() {
        return keysWithPrefix("");
    }

    // Lấy key có prefix
    public Iterable<String> keysWithPrefix(String prefix) {
        Queue<String> results = new LinkedList<>();
        Node x = get(root, prefix, 0);
        collect(x, new StringBuilder(prefix), results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, Queue<String> results) {
        if (x == null) return;
        if (x.val != null) results.add(prefix.toString());
        for (Map.Entry<Character, Node> entry : x.next.entrySet()) {
            char c = entry.getKey();
            prefix.append(c);
            collect(entry.getValue(), prefix, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    // keysThatMatch với wildcard '.'
    public Iterable<String> keysThatMatch(String pattern) {
        Queue<String> results = new LinkedList<>();
        collect(root, new StringBuilder(), pattern, results);
        return results;
    }

    private void collect(Node x, StringBuilder prefix, String pattern, Queue<String> results) {
        if (x == null) return;
        int d = prefix.length();
        if (d == pattern.length()) {
            if (x.val != null) results.add(prefix.toString());
            return;
        }
        char c = pattern.charAt(d);
        if (c == '.') {
            for (Map.Entry<Character, Node> entry : x.next.entrySet()) {
                char ch = entry.getKey();
                prefix.append(ch);
                collect(entry.getValue(), prefix, pattern, results);
                prefix.deleteCharAt(prefix.length() - 1);
            }
        } else {
            prefix.append(c);
            collect(x.next.get(c), prefix, pattern, results);
            prefix.deleteCharAt(prefix.length() - 1);
        }
    }

    // longestPrefixOf
    public String longestPrefixOf(String query) {
        if (query == null) throw new IllegalArgumentException("argument to longestPrefixOf() is null");
        int length = longestPrefixOf(root, query, 0, -1);
        if (length == -1) return null;
        else return query.substring(0, length);
    }

    private int longestPrefixOf(Node x, String query, int d, int length) {
        if (x == null) return length;
        if (x.val != null) length = d;
        if (d == query.length()) return length;
        char c = query.charAt(d);
        return longestPrefixOf(x.next.get(c), query, d+1, length);
    }

    // Xoá key
    public void delete(String key) {
        if (key == null) throw new IllegalArgumentException("argument to delete() is null");
        root = delete(root, key, 0);
    }

    private Node delete(Node x, String key, int d) {
        if (x == null) return null;
        if (d == key.length()) {
            if (x.val != null) n--;
            x.val = null;
        } else {
            char c = key.charAt(d);
            x.next.put(c, delete(x.next.get(c), key, d+1));
        }
        if (x.val != null) return x;
        if (!x.next.isEmpty()) return x;
        return null;
    }

    // Test
    public static void main(String[] args) throws IOException {
        TrieSTUp<Integer> st = new TrieSTUp<>();

        // nạp từ file "tuvung.txt"
        try (Scanner sc = new Scanner(new File("tuvung.txt"), "UTF-8")) {
            int i = 0;
            while (sc.hasNext()) {
                String key = sc.next();
                st.put(key, i++);
            }
        }

        System.out.println("Tổng số từ: " + st.size());

        System.out.println("\nCác từ trong Trie:");
        for (String key : st.keys()) {
            System.out.println(key + " -> " + st.get(key));
        }

        System.out.println("\nlongestPrefixOf(\"xinh xắn\"): " + st.longestPrefixOf("xinh xắn"));
        System.out.println("longestPrefixOf(\"xinh\"): " + st.longestPrefixOf("xinh"));

        System.out.println("\nkeysWithPrefix(\"xinh\"):");
        for (String s : st.keysWithPrefix("xi"))
            System.out.println(s);

        System.out.println("\nkeysThatMatch(\".he.l.\"):");
        for (String s : st.keysThatMatch(".he.l."))
            System.out.println(s);
    }
}
