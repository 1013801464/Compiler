package helper;

import java.util.ArrayList;
import java.util.List;

public class ResultTable {
    protected List<Item> list;

    public ResultTable() {
        list = new ArrayList<>();
        list.add(new Item("-","-","-","-"));
    }

    public int put(Item item) {
        list.add(item);             // 将元素添加到列表
        return list.size() - 1;     // 返回指向这个元素的指针
    }

    public int getLength() {
        return list.size();
    }

    // 修改dest变量
    public void change(int location, String dest) {
        list.get(location).dest = dest;
    }

    public List<Item> getList() {
        return list;
    }
}