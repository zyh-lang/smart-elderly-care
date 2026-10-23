package com.eldercare.health.entity;

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
 * 健康指标实体（血压/血糖/心率等时序数据）。
 */
@Data
@TableName("health_metric")
public class HealthMetric implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long elderId;

    /** 指标类型：BLOOD_PRESSURE / BLOOD_SUGAR / HEART_RATE / TEMPERATURE / WEIGHT */
    private String metricType;

    /** 指标值，如 120/80 或 5.6 */
    private String metricValue;

    private String remark;

    private LocalDateTime measureTime;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
