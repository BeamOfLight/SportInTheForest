package beamoflight.sportintheforest;

public class Stat
{
    Stat(int year_, int month_, int day_, int week_of_year, int value_, int position_, boolean current_period)
    {
        year = year_;
        month = month_;
        day = day_;
        weekOfYear = week_of_year;
        value = value_;
        position = position_;
        currentPeriod = current_period;
    }

    Stat()
    {
        year = 0;
        month = 0;
        value = 0;
        day = 0;
        weekOfYear = 0;
        position = 0;
    }

    protected int year;
    protected int month;
    protected int value;
    protected int day;
    protected int weekOfYear;
    protected int position;
    protected boolean currentPeriod;

    public boolean isCurrentPeriod() {
        return currentPeriod;
    }

    public Stat setCurrentPeriod(boolean currentPeriod) {
        this.currentPeriod = currentPeriod;
        return this;
    }

    public int getDay()
    {
        return day;
    }

    public Stat setDay(int day)
    {
        this.day = day;
        return this;
    }

    public int getWeekOfYear() {
        return weekOfYear;
    }

    public void setWeekOfYear(int weekOfYear) {
        this.weekOfYear = weekOfYear;
    }

    public int getPosition()
    {
        return position;
    }

    public Stat setPosition(int position)
    {
        this.position = position;
        return this;
    }

    public int getYear()
    {
        return year;
    }

    public Stat setYear(int year)
    {
        this.year = year;
        return this;
    }

    public int getMonth()
    {
        return month;
    }

    public Stat setMonth(int month)
    {
        this.month = month;
        return this;
    }

    public int getValue()
    {
        return value;
    }

    public Stat setValue(int value) {
        this.value = value;
        return this;
    }
}
