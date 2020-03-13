import control.WordScanner;

public class Debug1 {
    private static WordScanner s;

    public static void main(String[] args) {
        s = new WordScanner("D:\\abc.txt", "D:\\output.txt");
        while (s.hasNext()) {
            s.next();
        }
        s.save();
    }
}
