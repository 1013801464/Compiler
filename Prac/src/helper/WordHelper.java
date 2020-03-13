package helper;

public abstract class WordHelper implements WordType, VerbalType {

    public static int getDigit(char ch) {
        return ch - '0';
    }

    public static boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    public static boolean isLetter(char ch) {
        // Ascii码表字母表上大小写字母是分开的
        return ch >= 'A' && ch <= 'Z' || ch >= 'a' && ch <= 'z';
    }

    /**
     * 根据类型数字返回类型字符串
     */
    public static String getTypeName(int type) {
        switch (type) {
            case BEGIN: return "BEGIN";
            case END: return "END";
            case IF: return "IF";
            case THEN: return "THEN";
            case ELSE: return "ELSE";
            case WHILE: return "WHILE";
            case DO: return "DO";
            case INT: return "INT";
            case FLOAT: return "FLOAT";
            case DOUBLE: return "DOUBLE";
            case STRING: return "STRING";
            case ID: return "ID";
            case FCON: return "FCON";
            case INTCON: return "INTCON";
            case STRCON: return "STRCON";
            case LT: return "LT";
            case LE: return "LE";
            case EQ: return "EQ";
            case NE: return "NE";
            case GT: return "GT";
            case GE: return "GE";
            case IS: return "IS";
            case PL: return "PL";
            case MI: return "MI";
            case MU: return "MU";
            case DI: return "DI";
            case COMMENT_L: return "L_COMMENT";
            case COMMENT_B: return "B_COMMENT";
            case L_BRACKET: return "L_BRACKET";
            case R_BRACKET: return "R_BRACKET";
            case END_OF_WORDS: return "#(结束符号)";
            case 语句: return "语句";
            case 单语句: return "单语句";
            case 定义语句: return "定义语句";
            case 赋值语句: return "赋值语句";
            case 选择语句: return "选择语句";
            case 循环语句: return "循环语句";
            case 类型: return "类型";
            case 算术表达式: return "算术表达式";
            case 逻辑表达式: return "逻辑表达式";
            case 关系: return "关系";
            case 项: return "项";
            case 算术表达式1: return "算术表达式1";
            case 因式: return "因式";
            case 项1: return "项1";
            case 运算对象: return "运算对象";
            case ELSE语句: return "ELSE语句";
            default: return "error_type";
        }
    }

    public static int getTypeId(String type) throws Exception {
        switch (type.toUpperCase()) {
            case "BEGIN": return BEGIN;
            case "END": return END;
            case "IF": return IF;
            case "THEN": return THEN;
            case "ELSE": return ELSE;
            case "WHILE": return WHILE;
            case "DO": return DO;
            case "INT": return INT;
            case "FLOAT": return FLOAT;
            case "DOUBLE": return DOUBLE;
            case "STRING": return STRING;
            case "ID": return ID;
            case "FCON": return FCON;
            case "INTCON": return INTCON;
            case "STRCON": return STRCON;
            case "LT": return LT;
            case "LE": return LE;
            case "EQ": return EQ;
            case "NE": return NE;
            case "GT": return GT;
            case "GE": return GE;
            case "IS": return IS;
            case "PL": return PL;
            case "MI": return MI;
            case "MU": return MU;
            case "DI": return DI;
            case "COMMENT_L": return COMMENT_L;
            case "COMMENT_B": return COMMENT_B;
            case "L_BRACKET": return L_BRACKET;
            case "R_BRACKET": return R_BRACKET;
            case "语句": return 语句;
            case "单语句": return 单语句;
            case "定义语句": return 定义语句;
            case "赋值语句": return 赋值语句;
            case "选择语句": return 选择语句;
            case "循环语句": return 循环语句;
            case "类型": return 类型;
            case "算术表达式": return 算术表达式;
            case "逻辑表达式": return 逻辑表达式;
            case "关系": return 关系;
            case "项": return 项;
            case "算术表达式1": return 算术表达式1;
            case "因式": return 因式;
            case "项1": return 项1;
            case "运算对象": return 运算对象;
            case "ELSE语句": return ELSE语句;
            default: throw new Exception("未识别的字符串");
        }
    }
}
