package com.eldercare.user.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 老人档案实体。
 */
@Data
@TableName("elder")
public class Elder implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private String name;

    /** 性别：1 男 2 女 */
    private Integer gender;

    private Integer age;

    private String idCard;

    private String phone;

    private String address;

    private String community;

    private String emergencyContact;

    private String emergencyPhone;

    /** 绑定家属用户ID */
    private Long familyUserId;

    /** 与家属的关系（仅用于表单传参与绑定关系维护，不落库） */
    @TableField(exist = false)
    private String relation;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
