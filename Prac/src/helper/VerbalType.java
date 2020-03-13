package helper;

/**
 * 非终结符号类型
 */
public interface VerbalType extends WordType {
    int START_OF_VN = 200;      // 非终结符号的开始位置

    int 语句 = 201;
    int 单语句 = 202;
    int 定义语句 = 203;
    int 赋值语句 = 204;
    int 选择语句 = 205;
    int 循环语句 = 206;
    int 类型 = 207;
    int 算术表达式 = 208;
    int 逻辑表达式 = 209;
    int 关系 = 210;
    int 项 = 211;
    int 算术表达式1 = 212;
    int 因式 = 213;
    int 项1 = 214;
    int 运算对象 = 215;
    int ELSE语句 = 216;

    int getType();
}