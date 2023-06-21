package com.example.forum.enums;

/**
 * <pre>
 *     文章类型enum
 * </pre>
 *
 * @author saysky
 * @date 2021/3/20
 */
public enum PostTypeEnum {

    /**
     * 文章
     */
    POST_TYPE_POST("post"),

    /**
     * 页面
     */
    POST_TYPE_PAGE("page"),

    ;

    private String value;

    PostTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
