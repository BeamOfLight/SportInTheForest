package beamoflight.sportintheforest;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by beamoflight on 02.05.18.
 */

public class PlayerEntity extends CharacterEntity {
    private int userId;
    private int exerciseId;
    private int sumResult;
    private int maxResult;

    public long getCurrentTrainingId() {
        return currentTrainingId;
    }

    public void setCurrentTrainingId(long currentTrainingId) {
        this.currentTrainingId = currentTrainingId;
    }

    private long currentTrainingId;

    public boolean isPlayer()
    {
        return true;
    }

    public PlayerEntity(Context current, int user_id, int exercise_id)
    {
        super(current);
        userId = user_id;
        exerciseId = exercise_id;
        sumResult = 0;
        maxResult = 0;
    }

    public int getUserId()
    {
        return userId;
    }

    public int getExerciseId()
    {
        return exerciseId;
    }

    public int getSumResult()
    {
        return sumResult;
    }

    public int getMaxResult()
    {
        return maxResult;
    }

    float getAvgResult()
    {
        float total_avg_result = 0;
        int total_number_of_moves = dbHelper.getUserExerciseTrainingTotalNumberOfMoves(getUserId(), getExerciseId());
        if (total_number_of_moves != 0) {
            int total_training_sum_result = dbHelper.getTrainingSumResult(getUserId(), getExerciseId());
            total_avg_result = (float) total_training_sum_result / total_number_of_moves;
        }

        return total_avg_result;
    }

    public PlayerEntity setSumResult(int sum_result)
    {
        sumResult = sum_result;
        return this;
    }

    public PlayerEntity setMaxResult(int max_result)
    {
        maxResult = max_result;
        return this;
    }

    public int getExp()
    {
        return dbHelper.getTrainingSumResult(userId, exerciseId) / 50;
    }
}
