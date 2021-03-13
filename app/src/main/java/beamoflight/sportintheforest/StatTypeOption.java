package beamoflight.sportintheforest;

public class StatTypeOption
{
    String title;
    int type;

    final static int TYPE_RESULT = 1;
    final static int TYPE_EXP = 2;

    StatTypeOption(String title, int type) {
        this.title = title;
        this.type = type;
    }

    public String toString() {
        return title;
    }
}
