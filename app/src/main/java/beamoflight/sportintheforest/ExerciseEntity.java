package beamoflight.sportintheforest;

public class ExerciseEntity
{
    private int id;
    private String name;
    private int difficulty;

    ExerciseEntity(int id_, String name_, int difficulty_)
    {
        id = id_;
        name = name_;
        difficulty = difficulty_;
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

    public int getDifficulty()
    {
        return difficulty;
    }

    public ExerciseEntity setDifficulty(int difficulty_)
    {
        difficulty = difficulty_;
        return this;
    }

    public String toString()
    {
        return name;
    }
}
