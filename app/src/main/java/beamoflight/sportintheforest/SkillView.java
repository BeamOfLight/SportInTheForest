package beamoflight.sportintheforest;

import java.util.Locale;

/**
 * Created by beamoflight on 09.05.18.
 */

class SkillView {
    public String name;
    int groupId;
    int level;
    int duration;
    int targetType;
    String ownerName;
    int result;
    float splashMultiplier;

    static final int TARGET_TYPE_SELF = 0;
    static final int TARGET_TYPE_SINGLE_ACTIVE_FROM_MY_TEAM = 1;
    static final int TARGET_TYPE_SINGLE_INACTIVE_FROM_MY_TEAM = 2;
    static final int TARGET_TYPE_SINGLE_ACTIVE_FROM_TEAMMATES = 3;
    static final int TARGET_TYPE_ACTIVE_MY_TEAM = 4;
    static final int TARGET_TYPE_SINGLE_ACTIVE_FROM_OPPOSITE_TEAM = 5;
    static final int TARGET_TYPE_SINGLE_INACTIVE_FROM_OPPOSITE_TEAM = 6;
    static final int TARGET_TYPE_ACTIVE_OPPOSITE_TEAM = 7;
    static final int TARGET_TYPE_ACTIVE_ALL = 8;

    SkillView(String name_, int group_id, int level_, int duration_, int target_type, String owner_name, float splash_multiplier)
    {
        name = name_;
        groupId = group_id;
        level = level_;
        duration = duration_;
        targetType = target_type;
        ownerName = owner_name;
        this.result = 0;
        this.splashMultiplier = splash_multiplier;
    }

    SkillView(SkillView skill_view)
    {
        name = skill_view.name;
        groupId = skill_view.groupId;
        level = skill_view.level;
        duration = skill_view.duration;
        targetType = skill_view.targetType;
        ownerName = skill_view.ownerName;
        result = skill_view.result;
        splashMultiplier = skill_view.splashMultiplier;
    }

    public String toString()
    {
        String title;
        if (level > 0) {
            title =  String.format(
                    Locale.ROOT,
                    "%s Ур. %d",
                    name,
                    level
            );
        } else {
            title = name;
        }
        return title;
    }
}
