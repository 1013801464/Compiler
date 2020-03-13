package helper;

import java.util.HashMap;
import java.util.Map;

public class Memory {
    private static int seq = 0;
    private Map<String, Word> varMap;

    public Memory() {
        varMap = new HashMap<>();
    }

    /**
     * 将一个变量放入变量表中
     * 如果变量已存在, 返回false, 否则返回true
     */
    public boolean put(String value, Word word) {
        return varMap.put(value, word) == null;
    }

    /**
     * 返回某个变量的类型
     */
    public int getType(String value) {
        Word t = varMap.get(value);
        if (t != null) return t.getInnerType();
        return 0;
    }

    public Word newTempWord(int idType) {
        Word w = new Word(WordType.ID, "Temp" + (seq++), 0);
        w.setInnerType(idType);
        return w;
    }
}