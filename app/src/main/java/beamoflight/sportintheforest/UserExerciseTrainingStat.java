package beamoflight.sportintheforest;

import java.util.Locale;

/**
 * Created by beamoflight on 01.06.18.
 */

public class UserExerciseTrainingStat {
    int total_cnt;
    int max_competition_result;
    int max_result;
    int total_number_of_moves;
    int training_days;

    UserExerciseTrainingStat() {
        total_cnt = 0;
        max_competition_result = 0;
        max_result = 0;
        total_number_of_moves = 0;
        training_days = 0;
    }

    String getAverageResultString() {
        String avg_result_string = "-";
        if (total_number_of_moves != 0) {
            avg_result_string = String.format(Locale.ROOT, "%.2f", (float) (total_cnt) / total_number_of_moves);
        }
        return avg_result_string;
    }
}
