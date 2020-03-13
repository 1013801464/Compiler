import control.SyntaxAnalysis;
import control.WordScanner;

/**语义分析部分*/
public class Debug3 {
    private static WordScanner s;
    private static SyntaxAnalysis a;

    public static void main(String[] args) {
        s = new WordScanner("D:\\abc.txt", "D:\\outputLine.txt");
        a = new SyntaxAnalysis(s);
        a.analysis();
        s.save();
        a.output("D:\\four.txt");
    }
}
