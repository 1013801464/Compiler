package control;

import helper.ErrorReporter;
import helper.Word;
import helper.WordType;

/**
 * 语法: 递归下降法
 */
public class SyntaxAnalysisA {
    private static final byte SUCCESS = 1;   // 识别成功
    private static final byte EXITED = 2;    // 遇到其它字符而退出
    private static final byte FAILED = 3;    // 遇到失败
    private ErrorReporter errorReporter;
    private WordList words;                  // 单词的列表

    public SyntaxAnalysisA(WordList words) {
        this.words = words;
        this.errorReporter = ErrorReporter.getInstance();
        this.errorReporter.status = false;
    }

    /**
     * 分析器入口
     */
    public boolean analysis() {
        if (语句() != FAILED) {
            if (getCurType() == WordType.END_OF_WORDS) {
                return !this.errorReporter.status;
            } else {
                errorReporter.error(getCurRow(), "存在多余字符" + getCurWord());
            }
        }
        return false;
    }

    private byte 语句() {
        byte flag;
        do {
            flag = 单语句();
        } while (flag != EXITED);
        return flag;
    }

    private byte 单语句() {
        if (getCurType() == WordType.WHILE) {
            return 循环语句();
        } else if (getCurType() == WordType.IF) {
            return 选择语句();
        } else if (getCurType() == WordType.ID) {
            return 赋值语句();
        } else if (getCurType() == WordType.INT
                || getCurType() == WordType.FLOAT
                || getCurType() == WordType.DOUBLE
                || getCurType() == WordType.STRING) {
            return 定义语句();
        }
        return EXITED;
    }

    private byte 算术表达式() {
        if (项() != FAILED) {
            if (算术表达式1() != FAILED) {
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            return FAILED;
        }
    }

    private byte 算术表达式1() {
        if (getCurType() == WordType.PL
                || getCurType() == WordType.MI) {
            // <PL><项><算术表达式1>｜<MI><项><算术表达式1>
            words.next();
            if (项() != FAILED) {
                if (算术表达式1() != FAILED) {
                    return SUCCESS;
                } else {
                    return FAILED;
                }
            } else {
                return FAILED;
            }
        } else {
            // <算术表达式1>→ε
            return EXITED;
        }
    }

    private byte 项() {
        if (因式() != FAILED) {
            if (项1() != FAILED) {
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            return FAILED;  // 不是因式
        }
    }

    private byte 项1() {
        if (getCurType() == WordType.MU
                || getCurType() == WordType.DI) {
            words.next();
            if (因式() != FAILED) {
                if (项1() != FAILED) {
                    return SUCCESS;
                } else {
                    return FAILED;
                }
            } else {
                return FAILED;
            }
        } else {
            return EXITED;      // 遇到其它字符而退出(不是出错)
        }
    }

    private byte 因式() {
        if (getCurType() == WordType.L_BRACKET) {
            words.next();
            if (算术表达式() != FAILED) {
                if (getCurType() == WordType.R_BRACKET) {
                    words.next();
                    return SUCCESS;
                } else {
                    errorReporter.error(getCurRow(), "括号不匹配");
                    return FAILED;
                }
            } else {
                return FAILED;
            }
        } else {
            if (运算对象() != FAILED) {
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
        else return WordType.END_OF_WORDS;
    }

    private byte 运算对象() {
        if (getCurType() == WordType.ID
                || getCurType() == WordType.FCON
                || getCurType() == WordType.INTCON
                || getCurType() == WordType.STRCON) {
            words.next();
            return SUCCESS;
        } else {
            errorReporter.error(getCurRow(), "需要常数或变量");
            return FAILED;
        }
    }

    private byte 逻辑表达式() {
        if (算术表达式() != FAILED) {
            if (关系() != FAILED) {
                if (算术表达式() != FAILED) {
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

    private byte 关系() {
        if (getCurType() == WordType.LE
                || getCurType() == WordType.LT
                || getCurType() == WordType.EQ
                || getCurType() == WordType.NE
                || getCurType() == WordType.GE
                || getCurType() == WordType.GT) {
            words.next();
            return SUCCESS;
        }
        return FAILED;
    }

    private byte 定义语句() {
        words.next();
        if (getCurType() == WordType.ID) {
            if (赋值语句() != FAILED) {
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            errorReporter.error(getCurRow(), "缺少变量名");
            return FAILED;
        }
    }

    private byte 赋值语句() {
        // 赋值语句的开头一定是<ID>已经在调用之前验证
        words.next();
        if (getCurType() == WordType.IS) {
            words.next();
            if (算术表达式() != FAILED) {
                return SUCCESS;
            } else {
                return FAILED;
            }
        } else {
            errorReporter.error(getCurRow(), "缺少赋值符号");
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

    private byte 选择语句() {
        // 选择语句的开头一定是if, 已经在调用之前验证
        words.next();
        if (逻辑表达式() != FAILED) {
            if (getCurType() == WordType.THEN) {
                words.next();
                if (语句() != FAILED) {
                    if (getCurType() == WordType.END) {
                        words.next();
                        return SUCCESS;
                    } else if (getCurType() == WordType.ELSE) {
                        words.next();
                        if (语句() != FAILED) {
                            if (getCurType() == WordType.END) {
                                words.next();
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

    private byte 循环语句() {
        words.next();      // 开头一定是while 已经确定
        if (逻辑表达式() != FAILED) {
            if (getCurType() == WordType.DO) {
                words.next();
                if (语句() != FAILED) {
                    if (getCurType() == WordType.END) {
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
}