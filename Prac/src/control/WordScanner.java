package control;

import helper.ErrorReporter;
import helper.Word;
import helper.WordType;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Queue;

import static helper.WordHelper.*;

/**
 * 词法分析
 */
public class WordScanner implements WordType, WordList {
    private static final char[] SPACE = new char[]{' '}; // 空格常量

    private char[] token = new char[20];            // 字符缓冲区
    private ErrorReporter errorReporter;            // 错误报告程序
    private Queue<Word> wordQueue;                  // 单词队列
    private FileWriter fileWriter;                  // 输出字符到文件
    private boolean hasOutputWord = false;
    private CharReader reader;

    /**
     * 构造函数
     * 参数分别是输入文件路径, 输出文件路径
     */
    public WordScanner(String input, String output) {
        this.wordQueue = new LinkedList<>();
        errorReporter = ErrorReporter.getInstance();
        errorReporter.addALine(0);
        reader = new CharReader(input, errorReporter);
        try {
            fileWriter = new FileWriter(output);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描入口
     */
    private void scan() {
        char ch;        // 存放临时字符
        int i;          // i是token的指针
        while ((ch = reader.read()) != END_OF_CODE && !hasOutputWord) {
            Arrays.fill(token, '\0');   // 把字符串填充成空白
            i = 0;                          // token指针归零
            if (isLetter(ch)) {
                do {
                    token[i++] = ch;        // i控制ch在token[]中的位置
                    ch = reader.read();
                } while (isLetter(ch) || isDigit(ch));
                token[i] = 0;               // 闭合字符串
                reader.back();
                int c = lookup(i);
                if (c == 0) out(ID, token); // 如果没找到, 输出这是标识符
                else out(c, SPACE);         // 输出关键字
            } else if (isDigit(ch)) {
                reader.back();
                scanNumber();
            } else if (ch > 20 && ch != ' ') {
                switch (ch) {
                    case '.':               // 小数点开头的是浮点数
                        reader.back();
                        scanNumber();
                        break;
                    case '\"':              // 双引号开始的字符串常量
                        scanString(ch);
                        break;
                    case '<':
                        ch = reader.read();
                        if (ch == '=') out(LE, SPACE);
                        else if (ch == '>') out(NE, SPACE);
                        else {
                            reader.back();    // 遇到其它字符回溯
                            out(LT, SPACE);
                        }
                        break;
                    case '>':
                        if (reader.read() == '=')
                            out(GE, SPACE);
                        else {
                            reader.back();            // 回溯
                            out(GT, SPACE);
                        }
                        break;
                    case '=':
                        out(EQ, SPACE);
                        break;
                    case ':':
                        ch = reader.read();
                        if (ch == '=') out(IS, SPACE);
                        else {
                            errorReporter.error(reader.getCount() - 1, "冒号附近存在错误");
                            reader.back();
                        }
                        break;
                    case '/':       // 除号
                        scanDivisor();
                        break;
                    case '+':
                        out(PL, SPACE);
                        break;
                    case '-':
                        out(MI, SPACE);
                        break;
                    case '*':
                        out(MU, SPACE);
                        break;
                    case '(':
                        out(L_BRACKET, SPACE);
                        break;
                    case ')':
                        out(R_BRACKET, SPACE);
                        break;
                    default:
                        errorReporter.error(reader.getCount(), "未识别的字符 \""
                                + ch + '\"' + "ASCII(" + (int) ch + ")");
                        break;
                }
            }
        }
        reader.back();
        hasOutputWord = false;          // 清除已输出标志位
    }

    /**
     * 将fileWrite中的内容输出到文件
     */
    public void save() {
        try {
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 扫描以除号开始的字符串, 包括除号和块注释、行注释
     */
    private void scanDivisor() {
        int current_status;
        current_status = 25;
        while (true) {
            char ch = reader.read();
            switch (current_status) {
                case 25:
                    if (ch == '/') {
                        current_status = 26;
                    } else if (ch == '*') {
                        current_status = 28;
                    } else {
                        current_status = 31;
                    }
                    break;
                case 26:
                    if (ch == Character.LINE_SEPARATOR) {    // 如果是换行
                        current_status = 27;
                    }       // 其它字符维持26的状态
                    break;
                case 28:
                    if (ch == '*') {
                        current_status = 29;
                    } else if (ch == END_OF_CODE) {
                        errorReporter.error(reader.getCount(), "块注释没有结束");
                    }       // 其它字符维持29的状态
                    break;
                case 29:
                    if (ch == '/') {
                        current_status = 30;
                    } else if (ch == END_OF_CODE) {
                        errorReporter.error(reader.getCount(), "块注释没有结束");
                    } else {
                        current_status = 28;    // 退回到28的状态
                    }
                    break;
            }
            if (current_status == 27 || current_status == 30) {
                break;              // 结束了块注释或者行注释
            } else if (current_status == 31) {
                out(DI, SPACE);     // 输出除号
                reader.back();                // 回溯
                break;
            }
        }
    }

    private void scanString(char ch) {
        int i;
        int current_status;
        i = 0;
        current_status = 12;
        token[i++] = ch;
        while (true) {
            ch = reader.read();
            if (current_status == 12) {     // 如果当前状态是12
                if (ch == '\\') {           // 反斜杠
                    current_status = 13;
                } else if (ch == '\"') {    // 双引号
                    current_status = 14;
                    token[i++] = ch;
                } else if (ch == END_OF_CODE) {
                    current_status = -1;
                    errorReporter.error(reader.getCount() - 1, "缺少双引号");
                } else {
                    token[i++] = ch;
                }
            } else/* if (current_status == 13)*/ {
                if (ch == '\"' || ch == '\\') {           // 双引号
                    token[i++] = ch;
                    current_status = 12;
                } else {
                    current_status = -1;
                    errorReporter.error(reader.getCount() - 1, "未识别的转义字符");
                }
            }
            if (current_status == 14) { // 当前处于结束状态
                token[i] = 0;           // 闭合字符串
                out(STRCON, token);
                break;
            } else if (current_status == -1) {
                break;
            }
        }
    }

    /**
     * 扫描数字
     * 状态转换关系参考状态转换矩阵
     */
    private void scanNumber() {
        int current_status = 0;     // 当前状态
        int n = 0;                  // 小数后面几位
        int p = 0;                  // 指数部分(无符号)
        int e = 0;                  // 指数正负
        long w = 0;                 // 整数部分
        boolean IS_DIGIT;           // 临时变量: 是否是数字
        int CURRENT_DIGIT = 0;      // 临时变量: 当前数字
        while (true) {
            char ch = reader.read();
            reader.back();
            if (IS_DIGIT = isDigit(ch))
                CURRENT_DIGIT = getDigit(ch);
            switch (current_status) {
                case 0:
                    if (IS_DIGIT) {
                        n = 0;
                        p = 0;
                        e = 1;
                        w = CURRENT_DIGIT;
                        current_status = 3;     // 把状态转换到3(整数)
                    } else if (ch == '.') {
                        n = 0;
                        p = 0;
                        e = 1;
                        w = 0;
                        current_status = 9;     // 状态转换到9
                    }
                    break;
                case 3:
                    if (IS_DIGIT) {
                        w = w * 10 + CURRENT_DIGIT;
                        current_status = 3;     // 维持在数字状态
                    } else if (ch == 'e' || ch == 'E') {
                        current_status = 5;     // 转换到指数状态 5
                    } else if (ch == '.') {
                        current_status = 10;    // 转换到小数点状态 10
                    } else {
                        current_status = 4;     // 输出整数, 终态
                    }
                    break;
                case 5:
                    if (IS_DIGIT) {
                        p = CURRENT_DIGIT;
                        current_status = 7;
                    } else if (ch == '+') {
                        current_status = 6;
                    } else if (ch == '-') {
                        e = -1;
                        current_status = 6;
                    } else {
                        current_status = -1;
                        errorReporter.error(reader.getCount() - 1, "不应该存在" + ch);
                    }
                    break;
                case 6:
                    if (IS_DIGIT) {
                        p = CURRENT_DIGIT;
                        current_status = 7;
                    } else {
                        current_status = -1;
                        errorReporter.error(reader.getCount(), "需要数字");
                    }
                    break;
                case 7:
                    if (IS_DIGIT) {
                        p = p * 10 + CURRENT_DIGIT;
                        current_status = 7;
                    } else {
                        current_status = 8;
                    }
                    break;
                case 9:
                    if (IS_DIGIT) {
                        n++;
                        w = CURRENT_DIGIT;
                        current_status = 10;
                    } else {
                        current_status = -1;
                        errorReporter.error(reader.getCount(), "不应该出现'.'");
                    }
                    break;
                case 10:
                    if (ch == 'E' || ch == 'e') {
                        current_status = 5;
                    } else if (IS_DIGIT) {
                        n++;
                        w = w * 10 + CURRENT_DIGIT;
                        current_status = 10;
                    } else {
                        current_status = 11;
                    }
            }
            if (current_status == 4) {
                out(INTCON, w);
                break;  // 跳出循环
            } else if (current_status == 8 || current_status == 11) {
                out(FCON, w * Math.pow(10.0, e * p - n));
                break;
            } else if (current_status == -1) {  // 如果出错则退出
                break;
            } else {
                reader.read();    // 光标后移
            }
        }
    }

    /**
     * 查表函数, 返回关键字的值
     */
    private int lookup(int len) {
        String to = String.copyValueOf(token, 0, len);
        switch (to) {
            case "begin": return BEGIN;
            case "end": return END;
            case "if": return IF;
            case "then": return THEN;
            case "else": return ELSE;
            case "while": return WHILE;
            case "do": return DO;
            case "id": return ID;
            case "int": return INT;
            case "float": return FLOAT;
            case "double": return DOUBLE;
            case "string": return STRING;
        }
        // 数字类型和标识符类型无法判断, 都返回0
        return 0;
    }

    private int charLength(char[] token) {
        int i = 0;
        while (i < token.length && token[i] != 0) i++;
        return i;
    }

    /**
     * 输出字符数组
     */
    private void out(int id, char[] token) {
        int len = charLength(token);
        String val = String.valueOf(token, 0, len);
        String output = "(" + getTypeName(id) + ", " + val + ")";
        output(output);
        wordQueue.offer(new Word(id, val, reader.getCount()));
    }

    /**
     * 输出整数
     */
    private void out(int id, long value) {
        String output = "(" + getTypeName(id) + ", " + value + ")";
        output(output);
        wordQueue.offer(new Word(id, value, reader.getCount()));
    }

    /**
     * 输出浮点数
     */
    private void out(int id, double value) {
        String s = "(" + getTypeName(id) + ", " + String.format("%f",value) + ")";
        output(s);
        wordQueue.offer(new Word(id, value, reader.getCount()));
    }

    /**
     * 实际输出
     */
    private void output(String output) {
        hasOutputWord = true;
        try {
            fileWriter.write(output);
            String NEW_LINE = "\r\n";
            fileWriter.write(NEW_LINE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean hasNext() {
        if (wordQueue.isEmpty()) scan();
        return !wordQueue.isEmpty();
    }

    @Override
    public void next() {
        wordQueue.poll();       // 扔掉一个元素
    }

    @Override
    public Word getCurWord() {
        if (wordQueue.isEmpty()) scan();
        return wordQueue.peek();        // 返回第一个元素
    }
}

class CharReader {
    private char last;
    private FileReader fileReader;
    private char[] buffer = new char[1];
    private BufferedReader reader;
    private boolean backed = false;
    private int count = 0;
    private int countThisLine = 0;
    private ErrorReporter reporter;

    CharReader(String file, ErrorReporter errorReporter) {
        this.reporter = errorReporter;
        try {
            fileReader = new FileReader(file);
            reader = new BufferedReader(fileReader);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    char read() {
        if (backed) {
            backed = false;
            count++;
            return last;
        }
        try {
            int len;
            do {
                len = reader.read(buffer);      // 读取一个字符
            } while (buffer[0] == 10 && len != -1); // 过滤 \n
            // 如果读取失败, 返回END_OF_CODE
            if (len == -1) return last = END_OF_CODE;
            // 否则返回读取的字符
            if (buffer[0] == 13) {
                reporter.addALine(++countThisLine);
            } else {
                countThisLine++;
            }
            count++;
            return last = buffer[0];
        } catch (IOException e) {
            e.printStackTrace();
            return END_OF_CODE;
        }
    }

    int getCount() {
        return count;
    }

    void back() {
        if (backed) throw new IllegalStateException("禁止使用两次回溯");
        backed = true;
        count--;
    }

    @Override
    protected void finalize() {
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileReader != null) {
            try {
                fileReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}