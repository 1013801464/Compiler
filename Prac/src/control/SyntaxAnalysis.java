package control;

import helper.*;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

public class SyntaxAnalysis {
    private static final byte SUCCESS = 1;   // 识别成功
    private static final byte EXITED = 2;    // 遇到其它字符而退出
    private static final byte FAILED = 3;    // 遇到失败
    private ErrorReporter errorReporter;
    private WordList words;                  // 单词的列表
    private ResultTable resultTable;
    private Memory memory;

    public SyntaxAnalysis(WordList words) {
        this.words = words;
        this.errorReporter = ErrorReporter.getInstance();
        errorReporter.status = false;
        this.memory = new Memory();
        resultTable = new ResultTable();
    }

    public void output(String file) {
        FileOutputStream fos;
        try {
            FileWriter fw = new FileWriter(file);
            for (int i = 1; i < resultTable.getLength(); i++) {
                fw.write(resultTable.getList().get(i).toString());
                fw.write("\r\n");
            }
            fw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean analysis() {
        if (语句() == EXITED) {
            if (getCurType() == WordType.END_OF_WORDS) {
                return !errorReporter.status;
            } else {
                errorReporter.error(getCurRow(), "存在多余字符" + getCurName());
                return false;
            }
        } else {
            while (getCurType() != WordType.END_OF_WORDS) {
                if (语句() == EXITED) {
                    errorReporter.error(getCurRow(), "存在多余字符" + getCurWord());
                    words.next();
                }
            }
            return false;
        }
    }

    private String getCurName() {
        if (getCurWord() != null)
            return getCurWord().getName();
        else
            return "";
    }

    private byte 语句() {
        byte flag;
        do {
            flag = 单语句();
        } while (flag == SUCCESS);
        return flag;
    }

    private byte 单语句() {
        if (getCurType() == WordType.WHILE) {
            return 循环语句();
        } else if (getCurType() == WordType.IF) {
            return 选择语句();
        } else if (getCurType() == WordType.ID) {
            return 赋值语句(null, 0);
        } else if (getCurType() == WordType.INT
                || getCurType() == WordType.FLOAT
                || getCurType() == WordType.DOUBLE
                || getCurType() == WordType.STRING) {
            return 定义语句();
        }
        return EXITED;
    }

    private byte 算术表达式(Word[] ret) {
        Word[] t = new Word[1];
        if (项(t) != FAILED) {
            Word[] t2 = new Word[1];
            if (算术表达式1(t2, t[0]) != FAILED) {
                ret[0] = t2[0];
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            return FAILED;
        }
    }

    private byte 算术表达式1(Word[] ret, Word param) {
        if (getCurType() == WordType.PL
                || getCurType() == WordType.MI) {
            // <PL><项><算术表达式1>｜<MI><项><算术表达式1>
            int type = getCurType();
            words.next();
            Word[] t = new Word[1];
            if (项(t) != FAILED) {
                Word[] pc = {param};
                Word w = memory.newTempWord(convertWordType(pc, t));
                resultTable.put(new Item(WordHelper.getTypeName(type), pc[0].getName(), t[0].getName(), w.getName()));
                Word[] t2 = new Word[1];
                if (算术表达式1(t2, w) != FAILED) {
                    ret[0] = t2[0];
                    return SUCCESS;
                } else {
                    return FAILED;
                }
            } else {
                return FAILED;
            }
        } else {
            // <算术表达式1>→ε
            ret[0] = param;
            return EXITED;
        }
    }

    private byte 项(Word[] ret) {
        Word[] t = new Word[1];
        Word[] t2 = new Word[1];
        if (因式(t) != FAILED) {
            if (项1(t2, t[0]) != FAILED) {
                ret[0] = t2[0];     // 读取返回的值
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            return FAILED;  // 不是因式
        }
    }

    private byte 项1(Word[] ret, Word param) {
        if (getCurType() == WordType.MU
                || getCurType() == WordType.DI) {
            int wordtype = getCurType();
            words.next();
            Word[] t = new Word[1];
            if (因式(t) != FAILED) {
                Word[] pc = {param};
                Word w = memory.newTempWord(convertWordType(pc, t));
                resultTable.put(new Item(WordHelper.getTypeName(wordtype), pc[0].getName(), t[0].getName(), w.getName()));
                Word[] t2 = new Word[1];
                if (项1(t2, w) != FAILED) {
                    ret[0] = t2[0];
                    return SUCCESS;
                } else {
                    return FAILED;
                }
            } else {
                return FAILED;
            }
        } else {
            ret[0] = param;     // 直接将参数返回
            return EXITED;      // 遇到其它字符而退出(不是出错)
        }
    }

    private byte 因式(Word[] ret) {
        if (getCurType() == WordType.L_BRACKET) {
            Word[] t = new Word[1];
            words.next();
            if (算术表达式(t) != FAILED) {
                if (getCurType() == WordType.R_BRACKET) {
                    words.next();
                    ret[0] = t[0];      // 所以给ret赋值的都是返回
                    return SUCCESS;
                } else {
                    errorReporter.error(getCurRow(), "括号不匹配");    // 括号不匹配: 缺少右括号
                    return FAILED;
                }
            } else {
                return FAILED;
            }
        } else {
            Word[] t = new Word[1];
            if (运算对象(t) != FAILED) {
                ret[0] = t[0];          // 返回运算对象获取的值
                return SUCCESS;
            } else {
                return FAILED;
            }
        }
    }

    private Word getCurWord() {
        return words.getCurWord();
    }

    private int getCurType() {
        if (words.hasNext()) return getCurWord().getType();
        else return WordType.END_OF_WORDS;      // 如果后面没了 返回结束标志
    }

    private byte 运算对象(Word[] ret) {
        if (getCurType() == WordType.ID
                || getCurType() == WordType.FCON
                || getCurType() == WordType.INTCON
                || getCurType() == WordType.STRCON) {
            ret[0] = getCurWord();
            if (getCurType() == WordType.ID) {
                int type = memory.getType(ret[0].getName());
                if (type != 0) {
                    ret[0].setInnerType(type);
                } else {
                    errorReporter.error(getCurRow(), "[语义]使用了未初始化的变量" + ret[0].getName());
                }
            }
            words.next();
            return SUCCESS;
        } else {
            errorReporter.error(getCurRow(), "需要常数或变量");
            return FAILED;
        }
    }

    private byte 逻辑表达式(int[] ret) {
        Word[] w1 = new Word[1];
        if (算术表达式(w1) != FAILED) {
            int[] relate = new int[1];
            if (关系(relate) != FAILED) {
                Word[] w2 = new Word[1];
                if (算术表达式(w2) != FAILED) {
                    convertWordType(w1, w2);
                    resultTable.put(new Item("j" + WordHelper.getTypeName(relate[0]), w1[0].getName(), w2[0].getName(), Integer.toString(resultTable.getLength() + 2)));
                    int loc = resultTable.put(new Item("j", "0", "0", ""));
                    ret[0] = loc;
                    return SUCCESS;
                } else {
                    return FAILED;
                }
            } else {
                errorReporter.error(getCurRow(), "需要关系运算符");
                return FAILED;
            }
        } else {
            return FAILED;
        }
    }

    private byte 关系(int[] ret) {
        if (getCurType() == WordType.LE
                || getCurType() == WordType.LT
                || getCurType() == WordType.EQ
                || getCurType() == WordType.NE
                || getCurType() == WordType.GE
                || getCurType() == WordType.GT) {
            ret[0] = getCurType();
            words.next();
            return SUCCESS;
        }
        return FAILED;
    }

    private byte 定义语句() {
        // 开头一定是类型, 已经检查
        Word wType = getCurWord();
        words.next();
        if (getCurType() == WordType.ID) {
            Word[] w = new Word[1];
            int type = 0;
            if (wType.getType() == WordType.INT) {
                type = WordType.INTCON;
            } else if (wType.getType() == WordType.FLOAT || wType.getType() == WordType.DOUBLE) {
                type = WordType.FCON;
            } else if (wType.getType() == WordType.STRING) {
                type = WordType.STRCON;
            }
            if (赋值语句(w, type) != FAILED) {
                if (!memory.put(w[0].getName(), w[0])) {
                    errorReporter.error(getCurRow(), "[语义]重复定义变量 " + w[0].getName());
                }
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            errorReporter.error(getCurRow(), "缺少变量名");
            return FAILED;
        }
    }

    /**
     * 参数1 返回第一个等于号左侧的单词
     * 参数2 如果是定义语句调用的, 则指示第一个单词的类型, 否则传入0
     */
    private byte 赋值语句(Word[] ret, int leftType) {
        Word left = getCurWord();
        // 赋值语句的开头一定是<ID>已经在调用之前验证
        words.next();
        if (getCurType() == WordType.IS) {
            words.next();
            Word[] t = new Word[1];     // 算术表达式的返回值
            if (算术表达式(t) != FAILED) {
                if (ret != null) ret[0] = left;
                if (leftType != 0) left.setInnerType(leftType);
                int type = leftType;
                if (leftType == 0) {
                    type = memory.getType(left.getName());
                    if (type == 0) {
                        errorReporter.error(getCurRow(), "[语义]使用了未定义的变量 " + left.getName());
                    }
                }
                if (type != 0) {
                    convertWordType(t, type);
                    resultTable.put(new Item("IS", t[0].getName(), "0", left.getName()));
                }
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            errorReporter.error(getCurRow(), "缺少赋值符号");
            return FAILED;
        }
    }

    private byte 选择语句() {
        // 选择语句的开头一定是if, 已经在调用之前验证
        words.next();
        int[] loc = new int[1];
        if (逻辑表达式(loc) != FAILED) {
            if (getCurType() == WordType.THEN) {
                words.next();
                if (语句() != FAILED) {
                    if (getCurType() == WordType.END) {
                        words.next();
                        resultTable.change(loc[0], Integer.toString(resultTable.getLength()));
                        return SUCCESS;
                    } else if (getCurType() == WordType.ELSE) {
                        words.next();
                        int loc2 = resultTable.put(new Item("j", "0", "0", ""));
                        resultTable.change(loc[0], Integer.toString(resultTable.getLength()));
                        if (语句() != FAILED) {
                            if (getCurType() == WordType.END) {
                                words.next();
                                resultTable.change(loc2, Integer.toString(resultTable.getLength()));
                                return SUCCESS;
                            } else {
                                return FAILED;
                            }
                        } else {
                            return FAILED;
                        }
                    } else {
                        return FAILED;
                    }
                } else {
                    return FAILED;
                }
            } else {
                errorReporter.error(getCurRow(), "缺少then");
                return FAILED;
            }
        } else {
            return FAILED;
        }
    }

    private int getCurRow() {
        if (getCurWord() != null) {
            return getCurWord().getRow();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private byte 循环语句() {
        words.next();      // 开头一定是while 已经确定
        int loc = resultTable.getLength();
        int[] r = new int[1];
        if (逻辑表达式(r) != FAILED) {
            if (getCurType() == WordType.DO) {
                words.next();
                if (语句() != FAILED) {
                    if (getCurType() == WordType.END) {
                        resultTable.put(new Item("j", "0", "0", Integer.toString(loc)));
                        resultTable.change(r[0], Integer.toString(resultTable.getLength()));
                        words.next();
                        return SUCCESS;
                    } else {
                        errorReporter.error(getCurRow(), "缺少end");
                        return FAILED;
                    }
                } else {
                    return FAILED;
                }
            } else {
                errorReporter.error(getCurRow(), "缺少do");
                return FAILED;
            }
        } else {
            return FAILED;
        }
    }

    /**
     * 如果两个单词类型匹配, 直接返回类型
     * 如果不匹配, 将两个单词中的某一个进行转换, 生成四元式,
     * 同时将生成的临时单词返回, 将最大类型返回
     */
    private int convertWordType(Word[] w1, Word[] w2) {
        if (w1[0].getInnerType() == w2[0].getInnerType())
            return w1[0].getInnerType();
        if (w1[0].getInnerType() == WordType.INTCON && w2[0].getInnerType() == WordType.FCON) {
            Word t = memory.newTempWord(WordType.FCON);
            resultTable.put(new Item("itr", w1[0].getName(), "0", t.getName()));
            w1[0] = t;
            return WordType.FCON;
        } else if (w1[0].getInnerType() == WordType.FCON && w2[0].getInnerType() == WordType.INTCON) {
            Word t = memory.newTempWord(WordType.FCON);
            resultTable.put(new Item("itr", w2[0].getName(), "0", t.getName()));
            w2[0] = t;
            return WordType.FCON;
        }
        errorReporter.error(getCurRow(), "[语义]使用了未初始化的或者不支持的变量。");
        return 0;
    }

    /**
     * 强制转换一个单词类型
     */
    private void convertWordType(Word[] word, int destType) {
        if (word[0].getInnerType() == destType) return;
        else if (destType == WordType.INTCON && word[0].getInnerType() == WordType.FCON) {
            errorReporter.error(getCurRow(), "[语义]无法将浮点数转换为int");
        } else if (destType == WordType.FCON && word[0].getInnerType() == WordType.INTCON) {
            Word t = memory.newTempWord(WordType.FCON);
            resultTable.put(new Item("itr", word[0].getName(), "0", t.getName()));
            word[0] = t;
        } else {
            errorReporter.error(getCurRow(), "[语义]出现了无法转换的类型");
        }
    }

}