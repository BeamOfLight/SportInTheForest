package beamoflight.sportintheforest;

public class ExerciseEntity
{
    private int id;
    private String name;

    ExerciseEntity(int id_, String name_)
    {
        id = id_;
        name = name_;
    }

    public int getId()
    {
        return id;
    }

    public ExerciseEntity setId(int id_)
    {
        id = id_;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public ExerciseEntity setName(String name_)
    {
        name = name_;
        return this;
    }

    public String toString()
    {
        return name;
    }
}
