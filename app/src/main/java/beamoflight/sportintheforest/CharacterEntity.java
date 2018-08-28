package beamoflight.sportintheforest;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseIntArray;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by beamoflight on 02.05.18.
 */
abstract class CharacterEntity {
    public class Move {
        SkillView preAction;
        SkillView action;
        int targetTeamId;
        int targetId;
        int result;
        boolean isReady;

        Move(SkillView pre_action, SkillView action, int target_id, int result, boolean is_ready)
        {
            this.preAction = pre_action;
            this.action = action;
            this.targetId = target_id;
            this.result = result;
            this.isReady = is_ready;
        }
    }

    protected DBHelper dbHelper;
    GameHelper gameHelper;
    protected Move move;
    protected String name;
    protected int currentFitnessPoints;
    protected int initialFitnessPoints;
    protected float multiplier;
    protected int resistance;
    protected float bonusChance;
    protected float bonusMultiplier;
    protected int idxInTeam;
    protected int teamIdx;
    protected boolean isActive;

    protected SparseArray<SkillView> activeSkills;
    protected SparseIntArray alreadyUsedActiveSkills;

    CharacterEntity(Context current)
    {
        move = new Move(null, null, 0, 0, false);
        activeSkills = new SparseArray<>();
        alreadyUsedActiveSkills = new SparseIntArray();
        dbHelper = new DBHelper(current);
        gameHelper = new GameHelper(current);
    }

    public CharacterView getView()
    {
        CharacterView data = new CharacterView();
        data.name = getName();
        data.currentFitnessPoints = getCurrentFitnessPoints();
        data.initialFitnessPoints = getInitialFitnessPoints();
        data.multiplier = getMultiplier();
        data.resistance = getResistance();
        data.resistanceInPercents = gameHelper.getResistanceInPercents(getResistance());
        data.bonusChance = getBonusChance();
        data.bonusMultiplier = getBonusMultiplier();
        data.idxInTeam = getIdxInTeam();
        data.teamIdx = getTeamIdx();
        data.isPlayer = isPlayer();
        data.activeSkills = getActiveSkillsList();
        data.avgResult = getAvgResult();

        return data;
    }

    public boolean canReuseActiveSkill(int skill_group_id)
    {
        return alreadyUsedActiveSkills.get(skill_group_id, 0) == 0;
    }

    public void useActiveSkill(int skill_group_id, int reuse)
    {
        alreadyUsedActiveSkills.put(skill_group_id, reuse);
    }

    void recalculateActiveSkillsReuse()
    {
        for(int idx = 0; idx < alreadyUsedActiveSkills.size(); idx++) {
            int key = alreadyUsedActiveSkills.keyAt(idx);
            int reuse = alreadyUsedActiveSkills.get(key);
            reuse -= 1;
            if (reuse <= 0) {
                alreadyUsedActiveSkills.delete(key);
            } else {
                alreadyUsedActiveSkills.put(key, reuse);
            }
        }

        Log.d("APP", alreadyUsedActiveSkills.toString());
    }

    public SparseArray<SkillView> getActiveSkills()
    {
        return activeSkills;
    }

    List<SkillView> getActiveSkillsList()
    {
        List<SkillView> values = new ArrayList<>();
        for (int idx = 0; idx < activeSkills.size(); idx++) {
            int key = activeSkills.keyAt(idx);
            SkillView active_skill = activeSkills.get(key);
            if (active_skill != null) {
                values.add(new SkillView(active_skill));
            }
        }
        return values;
    }

    void recalculateActiveSkillsDuration()
    {
        for(int idx = 0; idx < getActiveSkills().size(); idx++) {
            int key = getActiveSkills().keyAt(idx);
            SkillView active_skill = getActiveSkills().get(key);
            if (active_skill != null) {
                active_skill.duration -= 1;
                if (active_skill.duration <=0) {
                    getActiveSkills().put(key, null);
                }
            }
        }
    }

    boolean addActiveSkill(SkillView skill)
    {
        boolean status = false;
        if (skill.groupId > 0) {
            if (activeSkills.get(skill.groupId) == null || (activeSkills.get(skill.groupId) != null && activeSkills.get(skill.groupId).level <= skill.level)) {
                activeSkills.put(skill.groupId, new SkillView(skill));
                status = true;
            }
        }
        return status;
    }

    CharacterEntity setIdxInTeam(int idx_in_team)
    {
        idxInTeam = idx_in_team;
        return this;
    }

    void calculateStatus()
    {
        isActive = false;
        if (getCurrentFitnessPoints() > 0) {
            isActive = true;
        }
    }

    CharacterEntity setTeamIdx(int team_idx)
    {
        teamIdx = team_idx;
        return this;
    }

    public String getName()
    {
        return name;
    }

    public CharacterEntity setName(String name_)
    {
        name = name_;
        return this;
    }

    CharacterEntity setResistance(int resistance_)
    {
        resistance = resistance_;
        return this;
    }

    CharacterEntity setInitialFitnessPoints(int initial_fitness_points_)
    {
        initialFitnessPoints = initial_fitness_points_;
        return this;
    }

    CharacterEntity setCurrentFitnessPoints(int current_fitness_points_)
    {
        currentFitnessPoints = current_fitness_points_;
        return this;
    }

    CharacterEntity setBonusChance(float bonus_chance_)
    {
        bonusChance = bonus_chance_;
        return this;
    }

    CharacterEntity setBonusMultiplier(float bonus_multiplier_)
    {
        bonusMultiplier = bonus_multiplier_;
        return this;
    }

    CharacterEntity setMultiplier(float multiplier_)
    {
        multiplier = multiplier_;
        return this;
    }

    boolean isActive()
    {
        return isActive;
    }

    int getResult()
    {
        return move.result;
    }

    abstract boolean isPlayer();
    abstract int getExp();
    abstract float getAvgResult();

    int getIdxInTeam()
    {
        return idxInTeam;
    }

    int getTeamIdx()
    {
        return teamIdx;
    }

    int getCurrentFitnessPoints()
    {
        return currentFitnessPoints;
    }

    float getMultiplier()
    {
        float extra = 0;
        float ratio2 = 1;
        for(int idx = 0; idx < getActiveSkills().size(); idx++) {
            int key = getActiveSkills().keyAt(idx);
            SkillView active_skill = getActiveSkills().get(key);
            if (active_skill != null) {
                Map<String, String> extra_params = dbHelper.getExtraParametersFromActiveSkill(active_skill.groupId, active_skill.level);
                extra += Float.parseFloat(extra_params.get("extra_multiplier"));
                ratio2 += Float.parseFloat(extra_params.get("extra_multiplier_ratio2"));
            }
        }

        return (multiplier + extra) * ratio2;
    }

    int getResistance()
    {
        float extra = 0;
        float ratio2 = 1;
        for(int idx = 0; idx < getActiveSkills().size(); idx++) {
            int key = getActiveSkills().keyAt(idx);
            SkillView active_skill = getActiveSkills().get(key);
            if (active_skill != null) {
                Map<String, String> extra_params = dbHelper.getExtraParametersFromActiveSkill(active_skill.groupId, active_skill.level);
                extra += Integer.parseInt(extra_params.get("extra_resistance"));
                ratio2 += Float.parseFloat(extra_params.get("extra_resistance_ratio2"));
            }
        }

        return (int)((resistance + extra) * ratio2);
    }

    int getInitialFitnessPoints()
    {
        float extra = 0;
        float ratio2 = 1;
        for(int idx = 0; idx < getActiveSkills().size(); idx++) {
            int key = getActiveSkills().keyAt(idx);
            SkillView active_skill = getActiveSkills().get(key);
            if (active_skill != null) {
                Map<String, String> extra_params = dbHelper.getExtraParametersFromActiveSkill(active_skill.groupId, active_skill.level);
                extra += Integer.parseInt(extra_params.get("extra_fitness_points"));
                ratio2 += Float.parseFloat(extra_params.get("extra_fitness_points_ratio2"));
            }
        }

        return (int)((initialFitnessPoints + extra) * ratio2);
    }

    float getBonusChance()
    {
        float extra = 0;
        float ratio2 = 1;
        for(int idx = 0; idx < getActiveSkills().size(); idx++) {
            int key = getActiveSkills().keyAt(idx);
            SkillView active_skill = getActiveSkills().get(key);
            if (active_skill != null) {
                Map<String, String> extra_params = dbHelper.getExtraParametersFromActiveSkill(active_skill.groupId, active_skill.level);
                extra += Float.parseFloat(extra_params.get("extra_bonus_chance"));
                ratio2 += Float.parseFloat(extra_params.get("extra_bonus_chance_ratio2"));
            }
        }

        return (bonusChance + extra) * ratio2;
    }

    float getBonusMultiplier()
    {
        float extra = 0;
        float ratio2 = 1;
        for(int idx = 0; idx < getActiveSkills().size(); idx++) {
            int key = getActiveSkills().keyAt(idx);
            SkillView active_skill = getActiveSkills().get(key);
            if (active_skill != null) {
                Map<String, String> extra_params = dbHelper.getExtraParametersFromActiveSkill(active_skill.groupId, active_skill.level);
                extra += Float.parseFloat(extra_params.get("extra_bonus_multiplier"));
                ratio2 += Float.parseFloat(extra_params.get("extra_bonus_multiplier_ratio2"));
            }
        }

        return (bonusMultiplier + extra) * ratio2;
    }

    int getRegeneration()
    {
        float regeneration = 0;
        for(int idx = 0; idx < getActiveSkills().size(); idx++) {
            int key = getActiveSkills().keyAt(idx);
            SkillView active_skill = getActiveSkills().get(key);
            if (active_skill != null) {
                Map<String, String> extra_params = dbHelper.getExtraParametersFromActiveSkill(active_skill.groupId, active_skill.level);
                int extra = Integer.parseInt(extra_params.get("extra_regeneration_base"));
                float ratio = Float.parseFloat(extra_params.get("extra_regeneration_ratio"));
                regeneration += extra + ratio * active_skill.result;
            }
        }
        return Math.round(regeneration);
    }
}
