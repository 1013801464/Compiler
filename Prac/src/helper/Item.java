package helper;

/**
 * 四元式类型定义
 */
public class Item {
    String operator;
    String value1;
    String value2;
    String dest;

    public Item(String operator, String value1, String value2, String dest) {
        this.operator = operator;
        this.value1 = value1;
        this.value2 = value2;
        this.dest = dest;
    }

    @Override
    public String toString() {
        return String.format("(%s, %s, %s, %s)", operator, value1, value2, dest);
    }
}