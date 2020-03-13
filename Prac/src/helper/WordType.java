package helper;

public interface WordType {
    int BEGIN = 1;
    int END = 2;
    int IF = 3;
    int THEN = 4;
    int ELSE = 5;
    int WHILE = 6;
    int DO = 7;
    int INT = 8;
    int FLOAT = 9;
    int DOUBLE = 10;
    int STRING = 11;
    int ID = 50;
    int FCON = 51;
    int INTCON = 52;
    int STRCON = 53;
    int LT = 60;    // <
    int LE = 61;    // <=
    int EQ = 62;    // =
    int NE = 63;    // <>
    int GT = 64;    // >
    int GE = 65;    // >=
    int IS = 66;    // :=
    int PL = 67;    // +
    int MI = 68;    // -
    int MU = 69;    // *
    int DI = 70;    // /
    int COMMENT_L = 90;     // 行注释
    int COMMENT_B = 91;     // 块注释
    int L_BRACKET = 71;
    int R_BRACKET = 72;
    int END_OF_WORDS = 100;
    char END_OF_CODE = (char) -1;
}
