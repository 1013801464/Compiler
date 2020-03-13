package helper;

import java.util.LinkedList;

public class ErrorReporter {
    private static ErrorReporter errorReporter;
    public boolean status;
    private LinkedList<Integer> columnsOfRows;

    /**
     * 构造函数
     */
    private ErrorReporter() {
        columnsOfRows = new LinkedList<>();
    }

    public static ErrorReporter getInstance() {
        if (errorReporter == null)
            errorReporter = new ErrorReporter();
        return errorReporter;
    }

    /**
     * 报告错误
     */
    public void error(int row, String message) {
        status = true;
        int line = 0;
        int r = 1;
        if (row == Integer.MAX_VALUE) {
            line = columnsOfRows.size() + 1;
        } else {
            try {
                while (row > columnsOfRows.get(line)) line++;
            } catch (IndexOutOfBoundsException ignored) {

            }
            r = row - columnsOfRows.get(line - 1) + 1;
        }
        System.out.println("第" + line + "行, 第" + r + "列: " + message);
    }


    public void addALine(int column) {
        columnsOfRows.add(column);
    }
}
