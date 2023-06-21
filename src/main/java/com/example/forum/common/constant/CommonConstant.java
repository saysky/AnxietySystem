package com.example.forum.common.constant;

import java.util.HashMap;
import java.util.Map;

/**
 * 常量
 *
 * @author saysky
 */
public class CommonConstant {

    /**
     * 正常状态
     */
    public static Integer STATUS_NORMAL = 0;

    /**
     * 用户密码加盐的盐
     */
    public static String PASSWORD_SALT = "sens";

    /**
     * none
     */
    public static String NONE = "none";


    /**
     * 错误码
     */
    public static Map<String, String> ERROR_CODE_MAP = new HashMap<>();

    static {
        ERROR_CODE_MAP.put("900001", "文件不存在，待管理员重新上传");
    }
}
