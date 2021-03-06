package beamoflight.sportintheforest;

import android.content.Context;

/**
 * Created by beamoflight on 02.05.18.
 */

public class NonPlayerCharacterEntity extends CharacterEntity {
    public int npcId;
    public int locationId;
    public int position;
    public String info;
    public String type;
    public int exp;
    public int maxRes;
    public boolean questOwner;
    protected String actions;
    protected String preActions;

    public String getActions() {
        return actions;
    }

    public NonPlayerCharacterEntity setActions(String actions) {
        this.actions = actions;
        return this;
    }

    public String getPreActions() {
        return preActions;
    }

    public NonPlayerCharacterEntity setPreActions(String preActions) {
        this.preActions = preActions;
        return this;
    }

    public boolean isPlayer()
    {
        return false;
    }

    NonPlayerCharacterEntity(Context current)
    {
        super(current);
    }

    public int getExp()
    {
        return exp;
    }

    public boolean getQuestOwner()
    {
        return questOwner;
    }

    public NonPlayerCharacterEntity setQuestOwner(boolean quest_owner)
    {
        questOwner = quest_owner;
        return this;
    }

    public NonPlayerCharacterEntity setExp(int exp_)
    {
        exp = exp_;
        return this;
    }

    public NonPlayerCharacterEntity setMaxResult(int max_res)
    {
        maxRes = max_res;
        return this;
    }

    public NonPlayerCharacterEntity setLocationId(int location_id)
    {
        locationId = location_id;
        return this;
    }

    public NonPlayerCharacterEntity setPosition(int position_)
    {
        position = position_;
        return this;
    }

    public NonPlayerCharacterEntity setId(int npc_id)
    {
        npcId = npc_id;
        return this;
    }

    public NonPlayerCharacterEntity setType(String type_)
    {
        type = type_;
        return this;
    }

    public NonPlayerCharacterEntity setInfo(String info_)
    {
        info = info_;
        return this;
    }

    float getAvgResult()
    {
        return ((float) getMaxResult()) * (float) 0.8;
    }


    public int getLocationId()
    {
        return locationId;
    }
    public int getMaxResult()
    {
        return maxRes;
    }
    public int getPosition()
    {
        return position;
    }
    public int getId()
    {
        return npcId;
    }

    public String getType()
    {
        return type;
    }
}
