/**
 * Represents a hotspot (frequent substring) discovered in leaked passwords
 * and matched in a candidate password.
 */
public class Hotspot {
    // Global statistics from the leaked dataset
    public String ngram;       // the substring itself
    public int freq;           // total occurrences across all leaks
    public int docFreq;        // number of distinct leaked passwords containing it
    public int beginCount;     // how many times this n-gram appeared at BEGINNING in leaks
    public int middleCount;    // how many times this n-gram appeared at MIDDLE in leaks
    public int endCount;       // how many times this n-gram appeared at END in leaks

    // Candidate-specific statistics
    public boolean candidateAtBegin;   // true if hotspot appears at BEGINNING in candidate
    public int candidateMiddleCount;   // number of times hotspot appears in MIDDLE in candidate
    public boolean candidateAtEnd;     // true if hotspot appears at END in candidate

    public Hotspot(String ngram, int freq, int docFreq,
                   int beginCount, int middleCount, int endCount,
                   boolean candidateAtBegin,
                   int candidateMiddleCount,
                   boolean candidateAtEnd) {
        this.ngram = ngram;
        this.freq = freq;
        this.docFreq = docFreq;
        this.beginCount = beginCount;
        this.middleCount = middleCount;
        this.endCount = endCount;
        this.candidateAtBegin = candidateAtBegin;
        this.candidateMiddleCount = candidateMiddleCount;
        this.candidateAtEnd = candidateAtEnd;
    }

    @Override
    public String toString() {
        return String.format(
            "%s (freq=%d, docFreq=%d, begin=%d, middle=%d, end=%d, candBegin=%b, candMiddleCount=%d, candEnd=%b)",
            ngram, freq, docFreq, beginCount, middleCount, endCount,
            candidateAtBegin, candidateMiddleCount, candidateAtEnd
        );
    }
}
