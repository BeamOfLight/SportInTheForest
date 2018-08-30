package beamoflight.sportintheforest;

import android.util.SparseArray;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by beamoflight on 28.05.18.
 */

public class CharacterView {
    @SerializedName("name")
    @Expose
    protected String name;

    @SerializedName("specialisation_id")
    @Expose
    protected int specialisationId;

    @SerializedName("level")
    @Expose
    protected int level;

    @SerializedName("current_fitness_points")
    @Expose
    protected int currentFitnessPoints;

    @SerializedName("initial_fitness_points")
    @Expose
    protected int initialFitnessPoints;

    @SerializedName("multiplier")
    @Expose
    protected float multiplier;

    @SerializedName("resistance")
    @Expose
    protected int resistance;

    @SerializedName("resistance_in_percents")
    @Expose
    protected float resistanceInPercents;

    @SerializedName("bonus_chance")
    @Expose
    protected float bonusChance;

    @SerializedName("bonus_multiplier")
    @Expose
    protected float bonusMultiplier;

    @SerializedName("idx_in_team")
    @Expose
    protected int idxInTeam;

    @SerializedName("team_idx")
    @Expose
    protected int teamIdx;

    @SerializedName("is_player")
    @Expose
    protected boolean isPlayer;

    @SerializedName("avg_result")
    @Expose
    protected float avgResult;

    @SerializedName("active_skills")
    @Expose
    protected List<SkillView> activeSkills;

    public String toString()
    {
        return name;
    }
    float getAvgResult() {
        return avgResult;
    }
    List<SkillView> getActiveSkills() {
        return activeSkills;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    int getCurrentFitnessPoints() {
        return currentFitnessPoints;
    }
    int getInitialFitnessPoints() {
        return initialFitnessPoints;
    }
    float getMultiplier() {
        return multiplier;
    }
    int getResistance() { return resistance; }
    float getBonusChance() {
        return bonusChance;
    }
    float getBonusMultiplier() {
        return bonusMultiplier;
    }
    float getResistanceInPercents() { return resistanceInPercents; }
    public int getSpecialisationId() { return specialisationId; }
    public int getLevel() { return level; }
}
