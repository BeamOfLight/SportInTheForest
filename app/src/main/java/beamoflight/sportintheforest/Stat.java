package beamoflight.sportintheforest;

public class Stat
{
    Stat(int year_, int month_, int value_, int position_, boolean current_period)
    {
        year = year_;
        month = month_;
        value = value_;
        position = position_;
        currentPeriod = current_period;
    }

    Stat()
    {
        year = 0;
        month = 0;
        value = 0;
    }

    protected int year;
    protected int month;
    protected int value;
    protected int day;
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

    public String getMonthName(){
        switch (month){
            case 1:
                return "Янв";

            case 2:
                return "Фев";

            case 3:
                return "Мар";

            case 4:
                return "Апр";

            case 5:
                return "Май";

            case 6:
                return "Июн";

            case 7:
                return "Июл";

            case 8:
                return "Авг";

            case 9:
                return "Сен";

            case 10:
                return "Окт";

            case 11:
                return "Ноя";

            case 12:
                return "Дек";
        }

        return "";
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
