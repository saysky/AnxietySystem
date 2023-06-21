package com.example.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.forum.common.base.BaseEntity;
import lombok.Data;

/**
 * 焦虑症测试问题
 * @author 言曌 liuyanzhao.com
 * @since 2023/5/15 17:09
 */
@Data
@TableName("anxiety_question")
public class AnxietyQuestion extends BaseEntity {

    /**
     * 标题
     */
    private String title;


    @TableField(exist = false)
    private User user;

}
