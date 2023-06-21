package com.example.forum.controller.admin;

import com.example.forum.entity.*;
import com.example.forum.service.*;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * <pre>
 *     后台首页控制器
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin")
public class AdminController extends BaseController {

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    /**
     * 请求后台页面
     *
     * @param model model
     * @return 模板路径admin/admin_index
     */
    @GetMapping
    public String index(Model model) {
        User user = getLoginUser();
        model.addAttribute("user", user);

        Role role = roleService.findByUserId(user.getId());
        model.addAttribute("role", role);

        model.addAttribute("postTotal", postService.count(null));
        model.addAttribute("questionTotal", questionService.count(null));
        model.addAttribute("answerTotal", answerService.count(null));
        model.addAttribute("userTotal", userService.count(null));

        return "admin/admin_index";
    }


    /**
     * 获得当前用户的菜单
     *
     * @return
     */
    @GetMapping(value = "/currentMenus")
    @ResponseBody
    public JsonResult getMenu() {
        Long userId = getLoginUserId();
        List<Permission> permissions = permissionService.findPermissionTreeByUserIdAndResourceType(userId, "menu");
        return JsonResult.success("", permissions);
    }

    /**
     * 获得当前登录用户
     */
    @GetMapping(value = "/currentUser")
    @ResponseBody
    public JsonResult currentUser() {
        User user = getLoginUser();
        if (user != null) {
            return JsonResult.success("", user);
        }
        return JsonResult.error("用户未登录");
    }


}
