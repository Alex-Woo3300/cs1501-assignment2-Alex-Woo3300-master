import java.util.*;

public class DLBHotspotDetector implements HotspotDetector {

    private Node root;

    private static class Node {
        char ch;
        Node child;
        Node sibling;
        boolean isTerminal;

        int freq;
        int docFreq;
        int beginCount;
        int middleCount;
        int endCount;

        Node(char ch) {
            this.ch = ch;
        }
    }

    private static class CandidateStats {
        boolean atBegin = false;
        boolean atEnd = false;
        int middleCount = 0;
    }

    @Override
    public void addLeakedPassword(String leakedPassword, int minN, int maxN) {

        if (leakedPassword == null || minN < 1 || maxN < minN) {
            throw new IllegalArgumentException();
        }

        int len = leakedPassword.length();
        Set<String> seen = new HashSet<>();

        for (int n = minN; n <= maxN; n++) {
            for (int i = 0; i + n <= len; i++) {

                String sub = leakedPassword.substring(i, i + n);

                boolean begin = (i == 0);
                boolean end = (i + n == len);
                boolean middle = (!begin && !end);

                Node node = insert(sub);

                node.freq++;

                if (begin) node.beginCount++;
                else if (end) node.endCount++;
                else node.middleCount++;

                if (!seen.contains(sub)) {
                    node.docFreq++;
                    seen.add(sub);
                }
            }
        }
    }

    @Override
    public Set<Hotspot> hotspotsIn(String candidatePassword) {

        if (candidatePassword == null) {
            throw new IllegalArgumentException();
        }

        Map<String, CandidateStats> map = new LinkedHashMap<>();

        int len = candidatePassword.length();

        for (int start = 0; start < len; start++) {

            Node current = findNode(root, candidatePassword.charAt(start));
            int pos = start;

            while (current != null && pos < len) {

                if (current.ch != candidatePassword.charAt(pos)) break;

                if (current.isTerminal) {

                    String sub = candidatePassword.substring(start, pos + 1);

                    CandidateStats stats = map.get(sub);
                    if (stats == null) {
                        stats = new CandidateStats();
                        map.put(sub, stats);
                    }

                    boolean begin = (start == 0);
                    boolean end = (pos + 1 == len);
                    boolean middle = (!begin && !end);

                    if (begin) stats.atBegin = true;
                    else if (end) stats.atEnd = true;
                    else stats.middleCount++;
                }

                pos++;

                if (pos < len) {
                    current = findNode(current.child, candidatePassword.charAt(pos));
                }
            }
        }

        Set<Hotspot> result = new LinkedHashSet<>();

        for (String sub : map.keySet()) {

            Node node = search(sub);

            CandidateStats cs = map.get(sub);

            result.add(new Hotspot(
                    sub,
                    node.freq,
                    node.docFreq,
                    node.beginCount,
                    node.middleCount,
                    node.endCount,
                    cs.atBegin,
                    cs.middleCount,
                    cs.atEnd
            ));
        }

        return result;
    }

    private Node insert(String word) {

        if (root == null) {
            root = new Node(word.charAt(0));
        }

        Node current = root;

        for (int i = 0; i < word.length(); i++) {

            char c = word.charAt(i);

            current = findOrCreate(current, c);

            if (i == word.length() - 1) {
                current.isTerminal = true;
                return current;
            }

            if (current.child == null) {
                current.child = new Node(word.charAt(i + 1));
            }

            current = current.child;
        }

        return current;
    }

    private Node findOrCreate(Node node, char c) {

        Node prev = null;

        while (node != null && node.ch != c) {
            prev = node;
            node = node.sibling;
        }

        if (node == null) {
            node = new Node(c);
            prev.sibling = node;
        }

        return node;
    }

    private Node findNode(Node node, char c) {

        while (node != null) {
            if (node.ch == c) return node;
            node = node.sibling;
        }

        return null;
    }

    private Node search(String word) {

        Node current = root;

        for (int i = 0; i < word.length(); i++) {

            current = findNode(current, word.charAt(i));

            if (current == null) return null;

            if (i < word.length() - 1) {
                current = current.child;
            }
        }

        return current;
    }
}