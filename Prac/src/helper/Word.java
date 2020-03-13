package helper;

/**
 * 单词类
 */
public class Word {
    private int type;       // 单词类型
    private Object val;     // 单词的值
    private int row;        // 单词的列号
    private int innerType;     // 如果单词是id, 那么type == ID, innerType = INT|DOUBLE等等

    public Word(int type, Object val, int row) {
        this.type = type;
        this.val = val;
        this.row = row;
        if (type == WordType.INTCON || type == WordType.FCON || type == WordType.STRCON) {
            this.innerType = type;
        } else {
            this.innerType = 0;
        }
    }

    public int getInnerType() {
        return innerType;
    }

    public void setInnerType(int innerType) {
        this.innerType = innerType;
    }

    public int getType() {
        return type;
    }

    public int getRow() {
        return row;
    }

    public String getStringValue() {
        return (String) val;
    }

    public String getName() {
        String str_val = "";
        if (type == WordType.FCON) {
            str_val = String.format("%f", (Double) val);
        } else if (type == WordType.INTCON) {
            str_val = Long.toString((Long) val);
        } else if (type == WordType.STRCON) {
            str_val = (String) val;
        } else if (type == WordType.ID) {
            str_val = (String) val;
        }
        return str_val;
    }

    @Override
    public String toString() {
        String str_val = "";
        if (type == WordType.FCON) {
            str_val = Double.toString((Double) val);
        } else if (type == WordType.INTCON) {
            str_val = Long.toString((Long) val);
        } else if (type == WordType.STRCON) {
            str_val = (String) val;
        }
        return WordHelper.getTypeName(type) + " " + str_val;
    }
}
