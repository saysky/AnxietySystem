package com.example.forum.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.forum.common.base.BaseEntity;
import com.example.forum.util.RelativeDateFormat;
import lombok.Data;


/**
 * <pre>
 *     公告
 * </pre>
 */
@Data
@TableName("notice")
public class Notice extends BaseEntity {

    /**
     * 标题
     */
    private String title;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 内容
     */
    private String content;

    /**
     * 摘要
     */
    private String summary;
    /**
     * 更新时间
     */
    @TableField(exist = false)
    private String createTimeStr;

    @TableField(exist = false)
    private User user;


    public String getCreateTimeStr() {
        return RelativeDateFormat.format(getCreateTime());
    }
}
