package beamoflight.sportintheforest;

import android.content.Context;
import android.util.SparseArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * CompetitionEngine
 * Created by beamoflight on 02.05.18.
 */
class CompetitionEngine {
    final static int LEFT_TEAM_IDX = 0;
    final static int RIGHT_TEAM_IDX = 1;

    private final static int MAX_CHARACTERS_COUNT_PER_TEAM = 5;
    private ArrayList<Map<String, String>> logData;
    private List<List<CharacterEntity>> teamsData;
    private LocationPositionEntity locationPosition;
    private GameHelper gameHelper;
    private DBHelper dbHelper;
    private String logMessage;
    private int numberOfMoves;
    private Long competitionStartTime;
    boolean finishedCompetition;
    private int mainPlayerTeamIdx;
    private int mainPlayerIdx;
    private int exerciseId;

    class MoveRequest {
        String name;
        int teamIdx;
        int idxInTeam;
        int userId;
        int exerciseId;
    }

    CompetitionView getCompetitionView()
    {
        CompetitionView view = new CompetitionView();
        view.finishedCompetition = finishedCompetition;
        view.mainPlayerTeamIdx = mainPlayerTeamIdx;
        view.mainPlayerIdx = mainPlayerIdx;
        view.logData = logData;
        view.teamsData = new ArrayList<>();
        view.teamsData.add(getCharacters(LEFT_TEAM_IDX));
        view.teamsData.add(getCharacters(RIGHT_TEAM_IDX));
        view.exerciseId = exerciseId;

        return view;
    }

    void setLocationPosition(LocationPositionEntity location_position)
    {
        locationPosition = location_position;
    }

    CompetitionEngine(Context current, int exercise_id) {
        logData = new ArrayList<>();
        teamsData = new ArrayList<>();
        teamsData.add(new ArrayList<CharacterEntity>());
        teamsData.add(new ArrayList<CharacterEntity>());
        gameHelper = new GameHelper(current);
        dbHelper = new DBHelper(current);
        finishedCompetition = true;
        exerciseId = exercise_id;
    }

    void start() {
        numberOfMoves = 0;
        competitionStartTime = System.currentTimeMillis()/1000;

        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isPlayer()) {
                    PlayerEntity player_entity = (PlayerEntity) character;
                    dbHelper.addCompetition2UserExerciseStat(player_entity.getUserId(), player_entity.getExerciseId());
                    player_entity.setCurrentTrainingId(
                            dbHelper.addTraining(player_entity.getUserId(), player_entity.getExerciseId(), locationPosition.getLevel(), player_entity.getSumResult(), player_entity.getMaxResult(), 0, 0, GameHelper.RESULT_STATE_UNFINISHED, locationPosition.getLocationId(), locationPosition.getPosition(), 0, getQuestOwner(player_entity), getTeamFP(mainPlayerTeamIdx), getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)), "")
                    );
                }
            }
        }

        addCompetitionLogMessage("Соревнование началось!", false);
        finishedCompetition = false;
    }

    void leave(int location_id, int position, int level)
    {
        int duration = (int)(System.currentTimeMillis()/1000 - competitionStartTime);
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isPlayer()) {
                    PlayerEntity player_entity = (PlayerEntity) character;
                    int state = GameHelper.RESULT_STATE_UNFINISHED;
                    if (player_entity.getTeamIdx() == mainPlayerTeamIdx && player_entity.getIdxInTeam() == mainPlayerIdx) {
                        state = GameHelper.RESULT_STATE_LEFT;
                    }
                    dbHelper.updateTraining(
                            player_entity.getCurrentTrainingId(),
                            player_entity.getUserId(),
                            player_entity.getExerciseId(),
                            level,
                            player_entity.getSumResult(),
                            player_entity.getMaxResult(),
                            0,
                            numberOfMoves,
                            state,
                            location_id,
                            position,
                            duration,
                            getQuestOwner(player_entity),
                            getTeamFP(mainPlayerTeamIdx),
                            getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)),
                            player_entity.getResults()
                    );

                }
            }
        }

    }

    ArrayList<Map<String, String>> getLogData() {
        return logData;
    }

    CompetitionEngine setMainPlayerTeamIdx(int team_idx)
    {
        mainPlayerTeamIdx = team_idx;
        return this;
    }

    CompetitionEngine setMainPlayerIdx(int player_idx)
    {
        mainPlayerIdx = player_idx;
        return this;
    }

    private int getOppositeTeamIdx(int team_idx) {
        if (team_idx == LEFT_TEAM_IDX) {
            return RIGHT_TEAM_IDX;
        } else {
            return LEFT_TEAM_IDX;
        }
    }

    List<UserEntity> filterUserList(List<UserEntity> src_users, int exercise_id)
    {
        List<UserEntity> dst_users = new ArrayList<>();
        for (UserEntity src_user : src_users) {
            if (checkExerciseUserIsNew(src_user.id, exercise_id)) {
                dst_users.add(src_user);
            }
        }

        return dst_users;
    }

    private boolean checkExerciseUserIsNew(int user_id, int exercise_id)
    {
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isPlayer()) {
                    PlayerEntity player = (PlayerEntity) character;
                    if (player.getUserId() == user_id && player.getExerciseId() == exercise_id) {
                        return false;
                    }
                }
            }
        }

        return true;
    }

    private boolean checkPlayerIsNew(PlayerEntity player_entity)
    {
        return checkExerciseUserIsNew(player_entity.getUserId(), player_entity.getExerciseId());
    }

    boolean addCharacter(int team_idx, CharacterEntity character_entity) {
        boolean status = false;
        if (character_entity.isPlayer()) {
            PlayerEntity player_entity = (PlayerEntity) character_entity;
            if (!checkPlayerIsNew(player_entity)) {
                return false;
            }
        }

        if (teamsData.get(team_idx).size() < MAX_CHARACTERS_COUNT_PER_TEAM) {
            character_entity.setTeamIdx(team_idx).setIdxInTeam(teamsData.get(team_idx).size());
            teamsData.get(team_idx).add(character_entity);
            status = true;
        }

        return status;
    }

    private int getTeamFP(int team_idx) {
        int total_fp = 0;
        for (CharacterEntity character : teamsData.get(team_idx)) {
            total_fp += character.getCurrentFitnessPoints();
        }

        return total_fp;
    }

    private int getTeamInitialFP(int team_idx) {
        int total_fp = 0;
        for (CharacterEntity character : teamsData.get(team_idx)) {
            total_fp += character.getInitialFitnessPoints();
        }

        return total_fp;
    }

    private long getTeamExp(int team_idx) {
        int total_exp = 0;
        for (CharacterEntity character : teamsData.get(team_idx)) {
            total_exp += character.getExp();
        }

        return total_exp;
    }

    private void makeMove(NonPlayerCharacterEntity current_character) {
        current_character.move.action = null;
        String[] possible_actions = current_character.getActions().split(";");
        if (!possible_actions[0].equals("")) {
            for (String possible_action_str : possible_actions) {
                int possible_action_id = Integer.parseInt(possible_action_str);
                if (current_character.canReuseActiveSkill(possible_action_id) && Math.random() < 0.6) {
                    current_character.move.action = dbHelper.getSkillView(possible_action_id, current_character.getName());
                }
            }
        }

        current_character.move.preAction = null;
        String[] possible_pre_actions = current_character.getPreActions().split(";");
        if (!possible_pre_actions[0].equals("")) {
            for (String possible_pre_action_str : possible_pre_actions) {
                int possible_pre_action_id = Integer.parseInt(possible_pre_action_str);
                if (current_character.canReuseActiveSkill(possible_pre_action_id) && Math.random() < 0.6) {
                    current_character.move.preAction = dbHelper.getSkillView(possible_pre_action_id, current_character.getName());
                }
            }
        }

        current_character.move.result = (int) Math.round(current_character.maxRes * 0.6 + current_character.maxRes * 0.4 * Math.random());
        current_character.move.targetId = 0;
        for (CharacterEntity character : teamsData.get(getOppositeTeamIdx(current_character.teamIdx))) {
            if (character.isActive()) {
                current_character.move.targetId = character.idxInTeam;
            }
        }
        current_character.move.isReady = true;
        if (current_character.move.action != null) {
            current_character.move.action.result = current_character.move.result;
        }
    }

    private float getBonusRate(float bonusChance, float bonusDefaultRate) {
        float bonusRate = 1;
        float val = (float) Math.random();
        if (val <= bonusChance) {
            bonusRate = bonusDefaultRate;
        }

        return bonusRate;
    }

    private void calculateMove4Target(CharacterEntity current_character, CharacterEntity target_character, float splash_multiplier) {
        float bonus_rate = getBonusRate(current_character.getBonusChance(), current_character.getBonusMultiplier());
        float final_rate = splash_multiplier * current_character.getMultiplier() * bonus_rate;
        float final_result = current_character.getResult() * final_rate;
        int int_final_result = Math.round(final_result);

        int targetFPDiff = gameHelper.getTargetFitnessPointsDifference(target_character.getCurrentFitnessPoints(), target_character.getResistance(), final_result);
        target_character.setCurrentFitnessPoints(target_character.getCurrentFitnessPoints() - targetFPDiff);

        logMessage += String.format(
                Locale.ROOT,
                " %s => %s Результат %d x %.2f %s= %d %s (сопр. %d%%) ФО изменены на %d до %d.",
                current_character.getName(),
                target_character.getName(),
                current_character.getResult(),
                final_rate,
                bonus_rate > 1 ? "(бонус!) " : "",
                int_final_result,
                gameHelper.getCorrectPointWordRU(int_final_result),
                (int) (gameHelper.getResistanceInPercents(target_character.getResistance())),
                targetFPDiff,
                target_character.getCurrentFitnessPoints()
        );
    }

    private void calculatePlayerStat(CharacterEntity current_character)
    {
        current_character.addActionPointsFromCurrentMove();
        if (current_character.isPlayer()) {
            PlayerEntity current_player = (PlayerEntity) current_character;
            if (current_player.getResult() > current_player.getMaxResult()) {
                current_player.setMaxResult(current_character.getResult());
            }
            current_player.addResultFromCurrentMove();
            current_player.addSumResult(current_character.getResult());
            gameHelper.saveLastSelection2Preferences(current_player.getUserId(), current_player.getExerciseId(), current_player.getResult());
        }
    }

    private void calculateMove(CharacterEntity current_character) {
        calculatePlayerStat(current_character);

        if (current_character.move.action == null) {
            CharacterEntity target_character = teamsData.get(getOppositeTeamIdx(current_character.teamIdx)).get(current_character.move.targetId);
            calculateMove4Target(current_character, target_character, 1);
        } else {
            switch (current_character.move.action.targetType) {
                case SkillView.TARGET_TYPE_SELF:
                case SkillView.TARGET_TYPE_ACTIVE_MY_TEAM:
                case SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM:
                case SkillView.TARGET_TYPE_SINGLE_INACTIVE_FROM_MY_TEAM:
                case SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_TEAMMATES:
                case SkillView.TARGET_TYPE_ACTIVE_ALL:
                    return;
                case SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM:
                    CharacterEntity target_character = teamsData.get(getOppositeTeamIdx(current_character.teamIdx)).get(current_character.move.targetId);
                    calculateMove4Target(current_character, target_character, 1);
                    break;
                case SkillView.TARGET_TYPE_ACTIVE_OPPOSITE_TEAM:
                    for (CharacterEntity opposite_team_character : teamsData.get(getOppositeTeamIdx(current_character.getTeamIdx()))) {
                        calculateMove4Target(current_character, opposite_team_character, current_character.move.action.splashMultiplier);
                    }
                    break;
            }
        }
    }

    void startNewRound() {
        numberOfMoves++;
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                character.move.isReady = false;
                character.calculateStatus();
            }
        }
    }

    boolean needMove() {
        boolean needMove = false;
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isActive()) {
                    needMove |= !character.move.isReady;
                }
            }
        }

        return needMove;
    }

    MoveRequest getMoveRequest() {
        MoveRequest request = new MoveRequest();
        if (needMove()) {
            for (List<CharacterEntity> teamData : teamsData) {
                for (CharacterEntity character : teamData) {
                    if (character.isActive()) {
                        if (!character.move.isReady && character.isPlayer()) {
                            request.name = character.getName();
                            request.teamIdx = character.teamIdx;
                            request.idxInTeam = character.idxInTeam;
                            request.userId = ((PlayerEntity) character).getUserId();
                            request.exerciseId = ((PlayerEntity) character).getExerciseId();
                            break;
                        }
                    }
                }
            }
        }

        return request;
    }

    void decreaseNumberOfMoves()
    {
        numberOfMoves--;
    }

    boolean setMove(int team_idx, int idx_in_team, SkillView pre_action, SkillView action, int target_team_idx, int target_idx, int result) {
        if (action != null) {
            action.result = result;
        }
        teamsData.get(team_idx).get(idx_in_team).move.preAction = pre_action;
        teamsData.get(team_idx).get(idx_in_team).move.action = action;
        teamsData.get(team_idx).get(idx_in_team).move.targetTeamId = target_team_idx;
        teamsData.get(team_idx).get(idx_in_team).move.targetId = target_idx;
        teamsData.get(team_idx).get(idx_in_team).move.result = result;
        teamsData.get(team_idx).get(idx_in_team).move.isReady = true;
        return true;
    }

    void proceedNPCsStep1() {
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isActive() && !character.isPlayer()) {
                    makeMove((NonPlayerCharacterEntity) character);
                }
            }
        }
    }

    private void cleanLogMessage()
    {
        logMessage = "";
    }

    private void saveLogMessage()
    {
        addCompetitionLogMessage(logMessage, true);
    }

    void proceed() {
        cleanLogMessage();

        // Main loop start
        proceedActiveSkillsAndTargets();
        proceedMoveCalculation();
        calculateFitnessPoints();
        // Main loop finish

        proceedActiveSkillsDurationAndReuseRecalculation();
        saveLogMessage();
        checkWinConditions();
    }

    private void addActiveSkillFromPreActionOrAction(CharacterEntity character, SkillView skill_view)
    {
        int current_target_id;
        switch (skill_view.targetType) {
            case SkillView.TARGET_TYPE_SELF:
                character.addActiveSkill(new SkillView(skill_view));
                break;
            case SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM:
                current_target_id = 0;
                for (CharacterEntity my_team_character : teamsData.get(character.getTeamIdx())) {
                    if (my_team_character.isActive() && character.move.targetId == current_target_id) {
                        my_team_character.addActiveSkill(new SkillView(skill_view));
                        current_target_id++;
                    }
                }
                break;
            case SkillView.TARGET_TYPE_SINGLE_INACTIVE_FROM_MY_TEAM:
                current_target_id = 0;
                for (CharacterEntity my_team_character : teamsData.get(character.getTeamIdx())) {
                    if (!my_team_character.isActive() && character.move.targetId == current_target_id) {
                        my_team_character.addActiveSkill(new SkillView(skill_view));
                        current_target_id++;
                    }
                }
                break;
            case SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_TEAMMATES:
                current_target_id = 0;
                for (CharacterEntity my_team_character : teamsData.get(character.getTeamIdx())) {
                    if (my_team_character.isActive() && character.move.targetId == current_target_id && character != my_team_character) {
                        my_team_character.addActiveSkill(new SkillView(skill_view));
                        current_target_id++;
                    }
                }
                break;
            case SkillView.TARGET_TYPE_ACTIVE_MY_TEAM:
                for (CharacterEntity my_team_character : teamsData.get(character.getTeamIdx())) {
                    if (my_team_character.isActive()) {
                        my_team_character.addActiveSkill(new SkillView(skill_view));
                    }
                }
                break;
            case SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM:
                current_target_id = 0;
                for (CharacterEntity opposite_team_character : teamsData.get(getOppositeTeamIdx(character.getTeamIdx()))) {
                    if (opposite_team_character.isActive() && character.move.targetId == current_target_id) {
                        opposite_team_character.addActiveSkill(new SkillView(skill_view));
                        current_target_id++;
                    }
                }
                break;
            case SkillView.TARGET_TYPE_ACTIVE_OPPOSITE_TEAM:
                for (CharacterEntity opposite_team_character : teamsData.get(getOppositeTeamIdx(character.getTeamIdx()))) {
                    if (opposite_team_character.isActive()) {
                        opposite_team_character.addActiveSkill(new SkillView(skill_view));
                    }
                }
                break;
            case SkillView.TARGET_TYPE_ACTIVE_ALL:
                for (List<CharacterEntity> teamData : teamsData) {
                    for (CharacterEntity current_character : teamData) {
                        if (current_character.isActive()) {
                            current_character.addActiveSkill(new SkillView(skill_view));
                        }
                    }
                }
                break;
        }
    }

    private void proceedActiveSkillsAndTargets() {
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isActive()) {
                    if (character.move.preAction != null && character.canReuseActiveSkill(character.move.preAction.groupId)) {
                        character.useActiveSkill(character.move.preAction.groupId, character.move.preAction.reuse);
                        addActiveSkillFromPreActionOrAction(character, character.move.preAction);
                        addSkillUseLogMessage(character, character.move.preAction.name);
                    } else {
                        character.move.preAction = null;
                    }

                    if (character.move.action != null && character.canReuseActiveSkill(character.move.action.groupId)) {
                        character.useActiveSkill(character.move.action.groupId, character.move.action.reuse);
                        addActiveSkillFromPreActionOrAction(character, character.move.action);
                        addSkillUseLogMessage(character, character.move.action.name);
                    } else {
                        character.move.action = null;
                    }
                }
            }
        }
    }

    private void addSkillUseLogMessage(CharacterEntity character, String message)
    {
        logMessage += String.format(
                Locale.ROOT,
                "%s использовал(а) \"%s\". ",
                character.getName(),
                message
        );
    }

    private void proceedMoveCalculation() {
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isActive()) {
                    calculateMove(character);
                }
            }
        }
    }

    private void proceedActiveSkillsDurationAndReuseRecalculation() {
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                character.recalculateActiveSkillsDuration();
                character.recalculateActiveSkillsReuse();
            }
        }
    }

    private void calculateFitnessPoints() {
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.getCurrentFitnessPoints() > 0) {
                    int old_fitness_points = character.getCurrentFitnessPoints();
                    int player_regeneration = 0;

                    //TODO: regeneration for non players
                    if (character.isPlayer()) {
                        PlayerEntity player_entity = (PlayerEntity) character;
                        player_regeneration = dbHelper.getUserRegeneration(player_entity.getUserId(), player_entity.getExerciseId());
                    }
                    int new_fitness_points = Math.min(
                            character.getCurrentFitnessPoints() + player_regeneration + character.getRegeneration(),
                            character.getInitialFitnessPoints()
                    );
                    character.setCurrentFitnessPoints(new_fitness_points);
                    int regeneration = new_fitness_points - old_fitness_points;
                    if (regeneration != 0) {
                        logMessage += String.format(
                                Locale.ROOT,
                                " %s: %s %d ФО.",
                                character.getName(),
                                regeneration > 0 ? "восстановлено" : "уменьшено",
                                Math.abs(regeneration)
                        );
                    }
                }
            }
        }
    }

    private void checkWinConditions() {
        if (getTeamFP(mainPlayerTeamIdx) == 0 && getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)) == 0) {
            processDraw();
        } else if (getTeamFP(mainPlayerTeamIdx) > 0 && getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)) == 0) {
            processWin();
        } else if (getTeamFP(mainPlayerTeamIdx) == 0 && getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)) > 0) {
            processDefeat();
        } else {
            int duration = (int)(System.currentTimeMillis()/1000 - competitionStartTime);
            for (List<CharacterEntity> teamData : teamsData) {
                for (CharacterEntity character : teamData) {
                    if (character.isPlayer()) {
                        PlayerEntity player_entity = (PlayerEntity) character;
                        dbHelper.updateTraining(
                                player_entity.getCurrentTrainingId(),
                                player_entity.getUserId(),
                                player_entity.getExerciseId(),
                                locationPosition.getLevel(),
                                player_entity.getSumResult(),
                                player_entity.getMaxResult(),
                                0,
                                numberOfMoves,
                                GameHelper.RESULT_STATE_UNFINISHED,
                                locationPosition.getLocationId(),
                                locationPosition.getPosition(),
                                duration,
                                getQuestOwner(player_entity),
                                getTeamFP(mainPlayerTeamIdx),
                                getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)),
                                player_entity.getResults()
                        );
                    }
                }
            }
        }
    }

    private void processDraw()
    {
        int duration = (int)(System.currentTimeMillis()/1000 - competitionStartTime);
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isPlayer()) {
                    PlayerEntity player_entity = (PlayerEntity) character;
                    int result_exp = Math.round(getTeamExp(getOppositeTeamIdx(character.getTeamIdx())) * (float)(0.2) * getExpRatio(player_entity.getTeamIdx()));
                    dbHelper.updateTraining(
                            player_entity.getCurrentTrainingId(),
                            player_entity.getUserId(),
                            player_entity.getExerciseId(),
                            locationPosition.getLevel(),
                            player_entity.getSumResult(),
                            player_entity.getMaxResult(),
                            result_exp,
                            numberOfMoves,
                            GameHelper.RESULT_STATE_DRAW,
                            locationPosition.getLocationId(),
                            locationPosition.getPosition(),
                            duration,
                            getQuestOwner(player_entity),
                            getTeamFP(mainPlayerTeamIdx),
                            getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)),
                            player_entity.getResults()
                    );
                    dbHelper.addDraw2UserExerciseStat(player_entity.getUserId(), player_entity.getExerciseId());
                    if (character.getTeamIdx() == mainPlayerTeamIdx && character.getIdxInTeam() == mainPlayerIdx) {
                        addCompetitionLogMessage(
                                String.format(
                                        Locale.ROOT,
                                        "Ничья! Вы получили %d %s опыта.",
                                        result_exp,
                                        gameHelper.getCorrectPointWordRU(result_exp)
                                ),
                                false
                        );
                        dbHelper.updateUserInfoWithLevelCheck();
                    }
                }

            }
        }

        finishedCompetition = true;
    }

    private float getExpRatio(int team_idx)
    {
        int teammates_count = teamsData.get(team_idx).size();
        return (float)((0.8 + 0.2 * teammates_count) / (float) teammates_count);
    }

    private int processWin4Player(PlayerEntity player_entity, int level, int location_id, int position, int duration)
    {
        int exp = Math.round(getTeamExp(getOppositeTeamIdx(player_entity.getTeamIdx())) * getExpRatio(player_entity.getTeamIdx()));
        dbHelper.updateTraining(
            player_entity.getCurrentTrainingId(),
            player_entity.getUserId(),
            player_entity.getExerciseId(),
            level,
            player_entity.getSumResult(),
            player_entity.getMaxResult(),
            exp,
            numberOfMoves,
            GameHelper.RESULT_STATE_WIN,
            location_id,
            position,
            duration,
            getQuestOwner(player_entity),
            getTeamFP(mainPlayerTeamIdx),
            getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)),
            player_entity.getResults()
        );
        dbHelper.addWin2UserExerciseStat(player_entity.getUserId(), player_entity.getExerciseId());

        return exp;
    }

    private boolean getQuestOwner(PlayerEntity player_entity)
    {
        return (player_entity.getUserId() == gameHelper.getUserId() && player_entity.getExerciseId() == gameHelper.getExerciseId());
    }

    private int processDefeat4Player(PlayerEntity player_entity, int level, int location_id, int position, int duration)
    {
        int exp = Math.round(((1 - (float) getTeamFP(getOppositeTeamIdx(player_entity.getTeamIdx())) / getTeamInitialFP(getOppositeTeamIdx(player_entity.getTeamIdx()))) * ((float) getTeamExp(getOppositeTeamIdx(player_entity.getTeamIdx())) / 10)) * getExpRatio(player_entity.getTeamIdx()));

        dbHelper.updateTraining(
                player_entity.getCurrentTrainingId(),
                player_entity.getUserId(),
                player_entity.getExerciseId(),
                level,
                player_entity.getSumResult(),
                player_entity.getMaxResult(),
                exp,
                numberOfMoves,
                GameHelper.RESULT_STATE_DEFEAT,
                location_id,
                position,
                duration,
                getQuestOwner(player_entity),
                getTeamFP(mainPlayerTeamIdx),
                getTeamFP(getOppositeTeamIdx(mainPlayerTeamIdx)),
                player_entity.getResults()
        );

        return exp;
    }

    private void processWin()
    {
        int duration = (int)(System.currentTimeMillis()/1000 - competitionStartTime);
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isPlayer()) {
                    PlayerEntity player_entity = (PlayerEntity) character;
                    if (character.getTeamIdx() == mainPlayerTeamIdx) {
                        int team_exp = processWin4Player(player_entity, locationPosition.getLevel(), locationPosition.getLocationId(), locationPosition.getPosition(), duration);
                        if (character.getIdxInTeam() == mainPlayerIdx) {
                            addCompetitionLogMessage(
                                    String.format(
                                            Locale.ROOT,
                                            "Победа! Вы получили %d %s опыта.",
                                            team_exp,
                                            gameHelper.getCorrectPointWordRU(team_exp)
                                    ),
                                    false
                            );

                            if (locationPosition.getLevel() < locationPosition.getQuestCnt()) {
                                dbHelper.levelUpForNPC(player_entity.getUserId(), player_entity.getExerciseId(), locationPosition.getLocationId(), locationPosition.getPosition());
                            }

                            processQuestLogic();
                            dbHelper.updateUserInfoWithLevelCheck();
                        }

                    } else {
                        processDefeat4Player(player_entity, locationPosition.getLevel(), locationPosition.getLocationId(), locationPosition.getPosition(), duration);
                    }
                }
            }
        }

        finishedCompetition = true;
    }

    private void processDefeat()
    {
        int duration = (int)(System.currentTimeMillis()/1000 - competitionStartTime);
        for (List<CharacterEntity> teamData : teamsData) {
            for (CharacterEntity character : teamData) {
                if (character.isPlayer()) {
                    PlayerEntity player_entity = (PlayerEntity) character;
                    if (character.getTeamIdx() == mainPlayerTeamIdx) {
                        int result_exp = processDefeat4Player(player_entity, locationPosition.getLevel(), locationPosition.getLocationId(), locationPosition.getPosition(), duration);
                        if (character.getIdxInTeam() == mainPlayerIdx) {
                            addCompetitionLogMessage(
                                    String.format(
                                            Locale.ROOT,
                                            "Поражение! Вы получили %d %s опыта.",
                                            result_exp,
                                            gameHelper.getCorrectPointWordRU(result_exp)
                                    ),
                                    false
                            );
                            dbHelper.updateUserInfoWithLevelCheck();
                        }

                    } else {
                        processWin4Player(player_entity, locationPosition.getLevel(), locationPosition.getLocationId(), locationPosition.getPosition(), duration);
                    }
                }
            }
        }

        finishedCompetition = true;
    }

    boolean isFinished()
    {
        return finishedCompetition;
    }

    private void addCompetitionLogMessage(String src_msg, boolean isMove) {
        Map<String, String> m = new HashMap<String, String>();
        String dst_msg;
        if (isMove) {
            dst_msg = String.format(
                    Locale.ROOT,
                    "[%s]  Подход %d. %s",
                    gameHelper.getShortCurrentTime(),
                    numberOfMoves,
                    src_msg
            );
        } else {
            dst_msg = String.format(
                    Locale.ROOT,
                    "[%s]  %s",
                    gameHelper.getShortCurrentTime(),
                    src_msg
            );
        }


        m.put("log_msg", dst_msg);
        logData.add(0, m);
    }

    private void processQuestLogic()
    {
        if (locationPosition.getWins() + 1 >= locationPosition.getQuestCnt()) {
            if (!dbHelper.checkUserExerciseQuest(locationPosition.getLocationId(), locationPosition.getPosition())) {
                completeQuest();
                openNewLocation();
            }
        } else {
            addCompetitionLogMessage(
                    String.format(
                            Locale.ROOT,
                            "Прогресс выполнения задания: %d / %d",
                            locationPosition.getWins() + 1,
                            locationPosition.getQuestCnt()
                    ),
                    false
            );
        }
    }

    private void openNewLocation()
    {
        if (locationPosition.getPosition() == 5) {
            if (dbHelper.openLocationForCurrentUserExercise(locationPosition.getLocationId() + 1)) {
                String location_name = dbHelper.getLocationName(locationPosition.getLocationId() + 1);
                if (location_name != null) {
                    addCompetitionLogMessage(
                            String.format(
                                    Locale.ROOT,
                                    "Вы открыли новое место: %s",
                                    location_name
                            ),
                            false
                    );
                }
            }
        }
    }

    private void completeQuest()
    {
        dbHelper.addUserExerciseQuest(locationPosition.getLocationId(), locationPosition.getPosition());
        addCompetitionLogMessage(
                String.format(
                        Locale.ROOT,
                        "Вы выполнили задание и получили %d %s опыта.",
                        locationPosition.getQuestExp(),
                        gameHelper.getCorrectPointWordRU(locationPosition.getQuestExp())
                ),
                false
        );
    }

    int getCharactersCount(int team_idx) {
        return teamsData.get(team_idx).size();
    }

    List<CharacterView> getCharacters(int team_idx) {
        List<CharacterView> character_views = new ArrayList<>();
        for (CharacterEntity character : teamsData.get(team_idx)) {
            character_views.add(character.getView());
        }

        return character_views;
    }

    public List<CharacterEntity> getActiveCharacters(int team_id, boolean useOppositeTeam) {
        int team_idx = team_id;
        if (useOppositeTeam) {
            team_idx = getOppositeTeamIdx(team_id);
        }
        List<CharacterEntity> character_views = new ArrayList<CharacterEntity>();
        for (CharacterEntity character : teamsData.get(team_idx)) {
            if (character.isActive()) {
                character_views.add(character);
            }
        }

        return character_views;
    }

    SparseArray<List<CharacterView>> getActionTargets(int team_idx, int idx_in_team)
    {
        SparseArray<List<CharacterView>> targets = new SparseArray<>();

        // TARGET_TYPE_SELF
        List<CharacterView> listSelf= new ArrayList<>();
        listSelf.add(teamsData.get(team_idx).get(idx_in_team).getView());
        targets.put(SkillView.TARGET_TYPE_SELF, listSelf);

        // TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM
        List<CharacterView> listSingleActiveFromMyTeam = new ArrayList<>();
        for (CharacterEntity character : teamsData.get(team_idx)) {
            if (character.isActive()) {
                listSingleActiveFromMyTeam.add(character.getView());
            }
        }
        targets.put(SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM, listSingleActiveFromMyTeam);

        // TARGET_TYPE_SINGLE_INACTIVE_FROM_MY_TEAM
        List<CharacterView> listSingleInactiveFromMyTeam = new ArrayList<>();
        for (CharacterEntity character : teamsData.get(team_idx)) {
            if (!character.isActive()) {
                listSingleInactiveFromMyTeam.add(character.getView());
            }
        }
        targets.put(SkillView.TARGET_TYPE_SINGLE_INACTIVE_FROM_MY_TEAM, listSingleInactiveFromMyTeam);

        // TARGET_TYPE_SINGLE_ACTIVE_FROM_TEAMMATES
        List<CharacterView> listSingleActiveFromTeammates = new ArrayList<>();
        for (CharacterEntity character : teamsData.get(team_idx)) {
            if (character.isActive() && character.getIdxInTeam() != idx_in_team) {
                listSingleActiveFromTeammates.add(character.getView());
            }
        }
        targets.put(SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_TEAMMATES, listSingleActiveFromTeammates);


        // TARGET_TYPE_ACTIVE_MY_TEAM
        targets.put(SkillView.TARGET_TYPE_ACTIVE_MY_TEAM, new ArrayList<CharacterView>());

        // TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM
        List<CharacterView> listSingleActiveFromOppositeTeam = new ArrayList<>();
        for (CharacterEntity character : teamsData.get(getOppositeTeamIdx(team_idx))) {
            if (character.isActive()) {
                listSingleActiveFromOppositeTeam.add(character.getView());
            }
        }
        targets.put(SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM, listSingleActiveFromOppositeTeam);

        // TARGET_TYPE_SINGLE_INACTIVE_FROM_OPPOSITE_TEAM
        List<CharacterView> listSingleInactiveFromOppositeTeam = new ArrayList<>();
        for (CharacterEntity character : teamsData.get(getOppositeTeamIdx(team_idx))) {
            if (!character.isActive()) {
                listSingleInactiveFromOppositeTeam.add(character.getView());
            }
        }
        targets.put(SkillView.TARGET_TYPE_SINGLE_INACTIVE_FROM_OPPOSITE_TEAM, listSingleInactiveFromOppositeTeam);

        // TARGET_TYPE_ACTIVE_OPPOSITE_TEAM
        targets.put(SkillView.TARGET_TYPE_ACTIVE_MY_TEAM, new ArrayList<CharacterView>());

        // TARGET_TYPE_ACTIVE_ALL
        targets.put(SkillView.TARGET_TYPE_ACTIVE_MY_TEAM, new ArrayList<CharacterView>());

        return targets;
    }

    List<SkillView> getPlayerPreActions(int team_idx, int idx_in_team)
    {
        List<SkillView> skill_views = new ArrayList<>();

        if (teamsData.get(team_idx).get(idx_in_team).isPlayer()) {
            PlayerEntity player_entity = (PlayerEntity) teamsData.get(team_idx).get(idx_in_team);
            skill_views.add(new SkillView("Нет", -1, 0, 0, 0, SkillView.TARGET_TYPE_SELF, player_entity.getName(), 0));
            ArrayList<Map<String, String>> data_pre_actions_db = dbHelper.getActiveLearntSkills(player_entity.getUserId(), player_entity.getExerciseId(), 1);
            for(Map<String, String> data : data_pre_actions_db) {
                if (player_entity.canReuseActiveSkill(Integer.parseInt(data.get("skill_group_id"))))
                {
                    skill_views.add(
                            new SkillView(
                                    data.get("skill_label"),
                                    Integer.parseInt(data.get("skill_group_id")),
                                    Integer.parseInt(data.get("skill_level")),
                                    Integer.parseInt(data.get("duration")),
                                    Integer.parseInt(data.get("reuse")),
                                    Integer.parseInt(data.get("target_type")),
                                    player_entity.getName(),
                                    Float.parseFloat(data.get("splash_multiplier"))
                            )
                    );
                }
            }
        }

        return skill_views;
    }

    List<SkillView> getPlayerActions(int team_idx, int idx_in_team)
    {
        List<SkillView> skill_views = new ArrayList<>();

        if (teamsData.get(team_idx).get(idx_in_team).isPlayer()) {
            PlayerEntity player_entity = (PlayerEntity) teamsData.get(team_idx).get(idx_in_team);
            skill_views.add(new SkillView("Обычный ход", -1, 0, 0, 0, SkillView.TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM, player_entity.getName(), 0));
            ArrayList<Map<String, String>> data_actions_db = dbHelper.getActiveLearntSkills(player_entity.getUserId(), player_entity.getExerciseId(), 2);
            for(Map<String, String> data : data_actions_db) {
                if (player_entity.canReuseActiveSkill(Integer.parseInt(data.get("skill_group_id"))))
                {
                    skill_views.add(
                            new SkillView(
                                    data.get("skill_label"),
                                    Integer.parseInt(data.get("skill_group_id")),
                                    Integer.parseInt(data.get("skill_level")),
                                    Integer.parseInt(data.get("duration")),
                                    Integer.parseInt(data.get("reuse")),
                                    Integer.parseInt(data.get("target_type")),
                                    player_entity.getName(),
                                    Float.parseFloat(data.get("splash_multiplier"))
                            )
                    );
                }
            }
        }

        return skill_views;
    }
}
