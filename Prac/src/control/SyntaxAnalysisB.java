package control;

import helper.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

public class SyntaxAnalysisB {
    private Map<String, VerbalN> verbalMap = new HashMap<>();
    private WordList wordList;
    private ErrorReporter e;

    public SyntaxAnalysisB(WordList words) throws Exception {
        this.wordList = words;
        e = ErrorReporter.getInstance();
        e.status = false;
        String[] inputs = new String[]{
                "<语句>→<单语句><语句>｜ε",
                "<单语句>→<定义语句>｜<赋值语句>｜<选择语句>｜<循环语句>",
                "<定义语句>→<类型><赋值语句>",
                "<赋值语句>→<ID><IS><算术表达式>",
                "<选择语句>→<IF><逻辑表达式><THEN><语句><ELSE语句><END>",
                "<ELSE语句>→<ELSE><语句>｜ε",
                "<循环语句>→<WHILE><逻辑表达式><DO><语句><END>",
                "<逻辑表达式>→<算术表达式><关系><算术表达式>",
                "<算术表达式>→<项><算术表达式1>",
                "<算术表达式1>→<PL><项><算术表达式1>｜<MI><项><算术表达式1>｜ε",
                "<项>→<因式><项1>",
                "<项1>→<MU><因式><项1>｜<DI><因式><项1>｜ε",
                "<因式>→<运算对象>｜<L_BRACKET><算术表达式><R_BRACKET>",
                "<运算对象>→<ID>｜<FCON>｜<INTCON>｜<STRCON>",
                "<类型>→<INT>｜<FLOAT>｜<DOUBLE>｜<STRING>",
                "<关系>→<LE>｜<LT>｜<EQ>｜<NE>｜<GE>｜<GT>"
        };
        // 开始分析所有的非终结符
        for (String input : inputs) {
            if (input.length() > 0 && input.charAt(0) == '<') {
                // 寻找 >
                int j = 1;
                while (input.charAt(j) != '>') {
                    j++;
                }
                String value = input.substring(1, j);   // [1,j)
                verbalMap.put(value, new VerbalN(WordHelper.getTypeId(value)));
            }
        }
        // 分析所有的非终结符号集
        for (String input : inputs) {  // 对于每一行
            VerbalN from = new VerbalN(0);
            if (input.length() > 0 && input.charAt(0) == '<') {
                // 寻找>
                int j = 1;
                while (input.charAt(j) != '>') j++;
                String value = input.substring(1, j);   // [1,j)
                from = verbalMap.get(value);
            }
            from.newList();
            int s = 1, e = 1;
            while (s < input.length() && e < input.length()) {
                s = e;
                while (s < input.length()
                        && input.charAt(s) != '<'
                        && input.charAt(s) != 'ε'
                        && input.charAt(s) != '｜') {
                    s++;
                }
                if (s != input.length()) {
                    if (input.charAt(s) == '<') {
                        s = s + 1;                  // 起始值
                        e = s;
                        while (input.charAt(e) != '>') {
                            e++;
                        }
                        String value = input.substring(s, e);
                        VerbalN verbal = verbalMap.get(value);
                        if (verbal == null) {
                            int id = WordHelper.getTypeId(value);
                            if (id >= VerbalType.START_OF_VN) {     // ID大于等于非终结符号, 则判断为非终结符号
                                VerbalN vn = new VerbalN(WordHelper.getTypeId(value));
                                from.addValue(vn);
                            } else {
                                VerbalT vt = new VerbalT(WordHelper.getTypeId(value));
                                from.addValue(vt);
                            }
                        } else {
                            from.addValue(verbal);
                        }
                    } else if (input.charAt(s) == 'ε') {
                        e = s + 1;
                        from.removeList();      // 移除最近的空列表
                    } else if (input.charAt(s) == '｜') {
                        e = s + 1;
                        from.newList();
                    }
                }
            }
        }

        VerbalN v1 = verbalMap.get("语句");
        v1.addToFirst(new int[]{WordType.INT, WordType.FLOAT, WordType.DOUBLE, WordType.STRING, WordType.ID, WordType.IF, WordType.WHILE}, v1.find(VerbalType.单语句));
        v1.addToFollow(new int[]{WordType.END_OF_WORDS, WordType.END, WordType.ELSE});
        v1 = verbalMap.get("单语句");
        v1.addToFirst(new int[]{WordType.INT, WordType.FLOAT, WordType.DOUBLE, WordType.STRING}, v1.find(VerbalType.定义语句));
        v1.addToFirst(WordType.ID, v1.find(VerbalType.赋值语句));
        v1.addToFirst(WordType.IF, v1.find(VerbalType.选择语句));
        v1.addToFirst(WordType.WHILE, v1.find(VerbalType.循环语句));
        v1 = verbalMap.get("定义语句");
        v1.addToFirst(new int[]{WordType.INT, WordType.FLOAT, WordType.DOUBLE, WordType.STRING}, v1.find(VerbalType.类型));
        v1 = verbalMap.get("赋值语句");
        v1.addToFirst(WordType.ID, v1.find(WordType.ID));
        v1 = verbalMap.get("选择语句");
        v1.addToFirst(WordType.IF, v1.find(VerbalType.IF));
        v1 = verbalMap.get("ELSE语句");
        v1.addToFirst(WordType.ELSE, v1.find(VerbalType.ELSE));
        v1.addToFollow(WordType.END);
        v1 = verbalMap.get("循环语句");
        v1.addToFirst(WordType.WHILE, v1.find(VerbalType.WHILE));
        v1 = verbalMap.get("逻辑表达式");
        v1.addToFirst(new int[]{WordType.ID, WordType.INTCON, WordType.FCON, WordType.STRCON, WordType.L_BRACKET}, v1.find(VerbalType.算术表达式));
        v1 = verbalMap.get("算术表达式");
        v1.addToFirst(new int[]{WordType.ID, WordType.INTCON, WordType.FCON, WordType.STRCON, WordType.L_BRACKET}, v1.find(VerbalType.项));
        v1 = verbalMap.get("算术表达式1");
        v1.addToFirst(WordType.PL, v1.find(VerbalType.PL));
        v1.addToFirst(WordType.MI, v1.find(VerbalType.MI));
        v1.addToFollow(new int[]{WordType.END_OF_WORDS, WordType.LE, WordType.LT, WordType.EQ, WordType.NE, WordType.GE, WordType.GT, WordType.THEN, WordType.DO, WordType.INT, WordType.FLOAT, WordType.DOUBLE, WordType.STRING, WordType.ID, WordType.IF, WordType.WHILE, WordType.END, WordType.ELSE, WordType.R_BRACKET});
        v1 = verbalMap.get("项");
        v1.addToFirst(new int[]{WordType.L_BRACKET, WordType.ID, WordType.INTCON, WordType.FCON, WordType.STRCON}, v1.find(VerbalType.因式));
        v1 = verbalMap.get("项1");
        v1.addToFirst(WordType.MU, v1.find(VerbalType.MU));
        v1.addToFirst(WordType.DI, v1.find(VerbalType.DI));
        v1.addToFollow(new int[]{WordType.END_OF_WORDS, WordType.MU, WordType.DI, WordType.LE, WordType.LT, WordType.EQ, WordType.NE, WordType.GE, WordType.GT, WordType.THEN, WordType.ELSE, WordType.DO, WordType.INT, WordType.FLOAT, WordType.DOUBLE, WordType.STRING, WordType.ID, WordType.IF, WordType.WHILE, WordType.PL, WordType.MI, WordType.END, WordType.R_BRACKET});
        v1 = verbalMap.get("因式");
        v1.addToFirst(new int[]{WordType.ID, WordType.INTCON, WordType.FCON, WordType.STRCON}, v1.find(VerbalType.运算对象));
        v1.addToFirst(WordType.L_BRACKET, v1.find(VerbalType.L_BRACKET));
        v1 = verbalMap.get("运算对象");
        v1.addToFirst(WordType.ID, v1.find(VerbalType.ID));
        v1.addToFirst(WordType.INTCON, v1.find(VerbalType.INTCON));
        v1.addToFirst(WordType.FCON, v1.find(VerbalType.FCON));
        v1.addToFirst(WordType.STRCON, v1.find(VerbalType.STRCON));
        v1 = verbalMap.get("类型");
        v1.addToFirst(WordType.INT, v1.find(VerbalType.INT));
        v1.addToFirst(WordType.FLOAT, v1.find(VerbalType.FLOAT));
        v1.addToFirst(WordType.DOUBLE, v1.find(VerbalType.DOUBLE));
        v1.addToFirst(WordType.STRING, v1.find(VerbalType.STRING));
        v1 = verbalMap.get("关系");
        v1.addToFirst(WordType.LE, v1.find(VerbalType.LE));
        v1.addToFirst(WordType.LT, v1.find(VerbalType.LT));
        v1.addToFirst(WordType.EQ, v1.find(VerbalType.EQ));
        v1.addToFirst(WordType.NE, v1.find(VerbalType.NE));
        v1.addToFirst(WordType.GE, v1.find(VerbalType.GE));
        v1.addToFirst(WordType.GT, v1.find(VerbalType.GT));
    }

    public boolean analysis() {
        boolean errorHasReported = false;
        Stack<VerbalType> stkLeft = new Stack<>();
        stkLeft.push(new VerbalT(WordType.END_OF_WORDS));
        stkLeft.push(verbalMap.get("语句"));  // 把语句压进去
        while (stkLeft.peek().getType() != WordType.END_OF_WORDS) {  // 只要不是栈底
            VerbalType v = stkLeft.pop();
            if (v instanceof VerbalN) {
                VerbalN vn = (VerbalN) v;
                List<VerbalType> vnl = vn.getSet(getCurType());
                if (vnl != null) {
                    for (int i = vnl.size() - 1; i >= 0; i--) {
                        stkLeft.push(vnl.get(i));
                    }       // 逆序入栈
                } else {
                    if (!vn.allowInFollow(getCurType())) {
                        if (!errorHasReported) {
                            e.error(getCurRow(), "Unexpected token \"" + getCurName() + '\"');
                            errorHasReported = true;
                        }
                    }
                }
            } else if (v instanceof VerbalT) {
                VerbalT vt = (VerbalT) v;
                if (vt.getType() == getCurType()) {
                    wordList.next();
                    errorHasReported = false;
                } else {
                    if (!errorHasReported) {
                        e.error(getCurRow(), "Unexpected token " + getCurName());
                        errorHasReported = true;
                    }
                }
            }
        }
        if (!wordList.hasNext())
            return !e.status;
        else {
            if (!errorHasReported)
                e.error(getCurRow(), "存在多余字符" + getCurWord());
        }
        return false;
    }

    private String getCurName() {
        if (getCurWord() != null) {
            return getCurWord().getName();
        } else {
            return "";
        }
    }

    private int getCurRow() {
        if (getCurWord() != null) {
            return getCurWord().getRow();
        } else {
            return Integer.MAX_VALUE;
        }
    }

    private Word getCurWord() {
        return wordList.getCurWord();
    }

    private int getCurType() {
        if (wordList.hasNext()) return getCurWord().getType();
        else return WordType.END_OF_WORDS;
    }
}