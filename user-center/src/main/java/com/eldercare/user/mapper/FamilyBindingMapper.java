package com.eldercare.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.eldercare.user.entity.FamilyBinding;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;

public interface FamilyBindingMapper extends BaseMapper<FamilyBinding> {

    /**
     * 幂等建立绑定关系：存在（含被逻辑删除的行）则恢复并更新关系，否则插入。
     * 依赖唯一键 uk_family_elder (family_user_id, elder_id)。
     */
    @Insert("INSERT INTO family_binding (family_user_id, elder_id, relation, deleted, create_time) " +
            "VALUES (#{familyUserId}, #{elderId}, #{relation}, 0, NOW()) " +
            "ON DUPLICATE KEY UPDATE deleted = 0, relation = #{relation}")
    int upsertBinding(@Param("familyUserId") Long familyUserId,
                      @Param("elderId") Long elderId,
                      @Param("relation") String relation);
}
