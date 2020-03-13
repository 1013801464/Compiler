package helper;

public class VerbalT implements VerbalType {
    private int type;

    public VerbalT(int type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "[终]" + WordHelper.getTypeName(type);
    }

    @Override
    public int getType() {
        return type;
    }
}