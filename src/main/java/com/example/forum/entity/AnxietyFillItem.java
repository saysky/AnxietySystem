package com.example.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.forum.common.base.BaseEntity;
import lombok.Data;

/**
 * 焦虑症问题填写详情
 *
 * @author 言曌 liuyanzhao.com
 * @since 2023/5/15 17:09
 */
@Data
@TableName("anxiety_fill_item")
public class AnxietyFillItem extends BaseEntity {

    /**
     * 填写id
     */
    private Long fillId;

    /**
     * 焦虑症测试问题id
     */
    private Long questionId;

    /**
     * 焦虑症测试问题标题
     */
    private String title;

    /**
     * 得分：完全不同意0分，基本不同意1分，中立2分，基本同意3分，完全同意4分
     */
    private Integer score;


    @TableField(exist = false)
    private User user;


}
