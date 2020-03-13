import control.SyntaxAnalysisA;
import control.SyntaxAnalysisB;
import control.WordScanner;

import java.io.FileNotFoundException;

public class Debug2 {
    private static WordScanner s;
    private static SyntaxAnalysisA a;
    private static SyntaxAnalysisB b;

    public static void main(String[] args) {
        try {
            s = new WordScanner("D:\\abc.txt", "D:\\output1.txt");
            a = new SyntaxAnalysisA(s);
            System.out.println("===词法分析(递归下降法)===");
            System.out.println(a.analysis() ? "RIGHT" : "ERROR");

            s = new WordScanner("D:\\abc.txt", "D:\\output1.txt");
            b = new SyntaxAnalysisB(s);
            System.out.println("===词法分析(预测分析法)===");
            System.out.println(b.analysis() ? "RIGHT" : "ERROR");
        } catch (FileNotFoundException e) {
            System.out.println("没有找到文件.");
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
