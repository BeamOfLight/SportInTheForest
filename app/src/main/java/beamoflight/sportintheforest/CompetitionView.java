package beamoflight.sportintheforest;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * CompetitionView
 * Created by beamoflight on 28.05.18.
 */
class CompetitionView {
    boolean isFinishedCompetition() {
        return finishedCompetition;
    }

    List<Map<String, String>> getLogData() {
        return logData;
    }

    List<List<CharacterView>> getTeamsData() {
        return teamsData;
    }

    @SerializedName("finished_competition")
    @Expose
    boolean finishedCompetition;

    @SerializedName("main_player_team_idx")
    @Expose
    int mainPlayerTeamIdx;

    @SerializedName("mainPlayerIdx")
    @Expose
    int mainPlayerIdx;

    @SerializedName("log_data")
    @Expose
    ArrayList<Map<String, String>> logData;

    @SerializedName("teams_data")
    @Expose
    List<List<CharacterView>> teamsData;

    @SerializedName("exercise_name")
    @Expose
    String exerciseName;

    CompetitionView()
    {
        finishedCompetition = false;
        mainPlayerTeamIdx = -1;
        mainPlayerIdx = -1;
        logData = new ArrayList<>();
        teamsData = new ArrayList<>();
        teamsData.add(new ArrayList<CharacterView>());
        teamsData.add(new ArrayList<CharacterView>());
        exerciseName = "";
    }
}
