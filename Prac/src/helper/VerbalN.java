package helper;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 非终结符号
 * 判断是不是非终结符号用instance of <tt>Word</tt>
 * 或者 instance of <tt>VerbalN</tt>
 */
public class VerbalN implements VerbalType {
    private List<List<VerbalType>> lists;
    /**
     * 第一个参数必须是VerbalType里面的值
     */
    private Map<Integer, List<VerbalType>> firstListMap;
    private List<Integer> followList;
    private List<VerbalType> curList;
    private int type;       // 自身的类型(其实不重要 但是为了显示方便)

    public VerbalN(int type) {
        this.type = type;
        firstListMap = new HashMap<>();
        followList = new LinkedList<>();
        lists = new LinkedList<>();
    }

    // 添加到first集
    public void addToFirst(int source, List<VerbalType> seq) {
        firstListMap.put(source, seq);
    }

    // 添加到first集
    public void addToFirst(int[] sources, List<VerbalType> seq) {
        for (int s : sources) {
            addToFirst(s, seq);
        }
    }

    // 添加到follow集
    public void addToFollow(int source) {
        followList.add(source);
    }

    public void addToFollow(int[] source) {
        for (int i : source) {
            followList.add(i);
        }
    }

    // 本函数在读取文法阶段调用
    public void newList() {
        curList = new LinkedList<>();
        lists.add(curList);
    }

    // 本函数在读取文法阶段调用
    public void addValue(VerbalType e) {
        curList.add(e);
    }

    // 本函数在读取文法阶段调用
    public void removeList() {
        if (curList.size() == 0) lists.remove(curList);
    }

    // 本函数在读取文法阶段调用
    public List<VerbalType> find(int type) throws Exception {
        for (List<VerbalType> v : lists) {
            if (v.get(0).getType() == type)
                return v;
        }
        throw new Exception("在<" + toString() + ">中没有找到<" + WordHelper.getTypeName(type) + ">.");
    }

    /**
     * 输入一个非终结符号集中的元素, 返回符号列表
     *
     * @param vn 非终结符号集中的元素
     * @return 符号列表, 可能为NULL
     */
    public List<VerbalType> getSet(int vn) {
        return firstListMap.get(vn);
    }

    // 检查某个类型是否属于Follow集
    public boolean allowInFollow(int vn) {
        return followList.contains(vn);
    }

    @Override
    public String toString() {
        return WordHelper.getTypeName(type);
    }

    @Override
    public int getType() {
        return type;
    }
}
