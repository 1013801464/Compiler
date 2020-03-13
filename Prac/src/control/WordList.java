package control;

import helper.Word;

public interface WordList {
    boolean hasNext();

    void next();

    Word getCurWord();
}
