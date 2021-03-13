package beamoflight.sportintheforest;

/**
 * Created by beamoflight on 02.05.18.
 */
class LocationPositionEntity {
    protected int locationLevelPositionId;
    protected String name;
    protected int locationId;
    protected int position;
    protected int level;
    protected String info;
    protected int wins;
    protected int questCnt;
    protected int questExp;

    public String getName() {
        return name;
    }

    public LocationPositionEntity setName(String name) {
        this.name = name;
        return this;
    }

    public int getLocationLevelPositionId() {
        return locationLevelPositionId;
    }

    public LocationPositionEntity setLocationLevelPositionId(int locationLevelPositionId) {
        this.locationLevelPositionId = locationLevelPositionId;
        return this;
    }

    public int getLocationId() {
        return locationId;
    }

    public LocationPositionEntity setLocationId(int locationId) {
        this.locationId = locationId;
        return this;
    }

    public int getPosition() {
        return position;
    }

    public LocationPositionEntity setPosition(int position) {
        this.position = position;
        return this;
    }

    public int getLevel() {
        return level;
    }

    public LocationPositionEntity setLevel(int level) {
        this.level = level;
        return this;
    }

    public String getInfo() {
        return info;
    }

    public LocationPositionEntity setInfo(String info) {
        this.info = info;
        return this;
    }

    public int getWins() {
        return wins;
    }

    public LocationPositionEntity setWins(int wins) {
        this.wins = wins;
        return this;
    }

    public int getQuestCnt() {
        return questCnt;
    }

    public LocationPositionEntity setQuestCnt(int questCnt) {
        this.questCnt = questCnt;
        return this;
    }

    public int getQuestExp() {
        return questExp;
    }

    public LocationPositionEntity setQuestExp(int questExp) {
        this.questExp = questExp;
        return this;
    }

    LocationPositionEntity() {
        locationLevelPositionId = -1;
        name = "";
        locationId = -1;
        position = -1;
        level = 0;
        info = "";
        wins = 0;
        questCnt = 0;
        questExp = 0;
    }

    LocationPositionEntity(LocationPositionEntity location_position_entity) {
        locationLevelPositionId = location_position_entity.getLocationLevelPositionId();
        name = location_position_entity.getName();
        locationId = location_position_entity.getLocationId();
        position = location_position_entity.getPosition();
        level = location_position_entity.getLevel();
        info = location_position_entity.getInfo();
        wins = location_position_entity.getWins();
        questExp = location_position_entity.getQuestExp();
        questCnt = location_position_entity.getQuestCnt();
    }
}