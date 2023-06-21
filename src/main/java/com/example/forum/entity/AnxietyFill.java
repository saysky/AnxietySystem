package com.example.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.forum.common.base.BaseEntity;
import lombok.Data;

/**
 * 焦虑症问题填写
 *
 * @author 言曌 liuyanzhao.com
 * @since 2023/5/15 17:09
 */
@Data
@TableName("anxiety_fill")
public class AnxietyFill extends BaseEntity {

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 总得分
     */
    private Integer score;

    /**
     * 备注
     */
    private String remark;

    /**
     * 是否异常:1异常，0正常
     */
    private Integer anxietyFlag;

    @TableField(exist = false)
    private User user;


}
