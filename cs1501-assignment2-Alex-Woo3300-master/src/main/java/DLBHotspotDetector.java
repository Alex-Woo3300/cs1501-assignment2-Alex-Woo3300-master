import java.util.*;

public class DLBHotspotDetector implements HotspotDetector {

    private static class Node {
        char ch;
        Node child;
        Node sibling;
        boolean terminal;

        int freq;
        int docFreq;
        int beginCount;
        int middleCount;
        int endCount;

        int lastDocSeen = -1;

        Node(char c) {
            this.ch = c;
        }
    }

    private Node root = new Node('\0');
    private int docId = 0;

    private Node getChild(Node node, char c) {
        Node curr = node.child;
        while (curr != null) {
            if (curr.ch == c) return curr;
            curr = curr.sibling;
        }
        return null;
    }

    private Node addChild(Node node, char c) {
        Node n = new Node(c);
        n.sibling = node.child;
        node.child = n;
        return n;
    }

    @Override
    public void addLeakedPassword(String leakedPassword, int minN, int maxN) {
        if (leakedPassword == null || minN < 1 || maxN < minN) {
            throw new IllegalArgumentException();
        }

        docId++;
        int L = leakedPassword.length();

        for (int start = 0; start < L; start++) {

            Node curr = root;

            for (int len = 1; start + len <= L && len <= maxN; len++) {

                char c = leakedPassword.charAt(start + len - 1);

                Node next = getChild(curr, c);
                if (next == null) {
                    next = addChild(curr, c);
                }

                curr = next;

                if (len >= minN) {

                    curr.terminal = true;
                    curr.freq++;

                    if (curr.lastDocSeen != docId) {
                        curr.docFreq++;
                        curr.lastDocSeen = docId;
                    }

                    if (start == 0) {
                        curr.beginCount++;
                    } else if (start + len == L) {
                        curr.endCount++;
                    } else {
                        curr.middleCount++;
                    }
                }
            }
        }
    }

    @Override
    public Set<Hotspot> hotspotsIn(String candidatePassword) {

        if (candidatePassword == null) {
            throw new IllegalArgumentException();
        }

        Map<String, Hotspot> results = new LinkedHashMap<>();
        int L = candidatePassword.length();

        for (int start = 0; start < L; start++) {

            Node curr = root;
            StringBuilder sb = new StringBuilder();

            for (int j = start; j < L; j++) {

                curr = getChild(curr, candidatePassword.charAt(j));
                if (curr == null) break;

                sb.append(candidatePassword.charAt(j));

                if (curr.terminal) {

                    String ngram = sb.toString();
                    boolean atBegin = start == 0;
                    boolean atEnd = j == L - 1;
                    boolean middle = !atBegin && !atEnd;

                    Hotspot h = results.get(ngram);

                    if (h == null) {

                        h = new Hotspot(
                                ngram,
                                curr.freq,
                                curr.docFreq,
                                curr.beginCount,
                                curr.middleCount,
                                curr.endCount,
                                atBegin,
                                middle ? 1 : 0,
                                atEnd
                        );

                        results.put(ngram, h);

                    } else {

                        if (atBegin) h.candidateAtBegin = true;
                        if (atEnd) h.candidateAtEnd = true;
                        if (middle) h.candidateMiddleCount++;
                    }
                }
            }
        }

        return new LinkedHashSet<>(results.values());
    }
}