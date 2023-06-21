package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.PostQueryCondition;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.Follow;
import com.example.forum.entity.Post;
import com.example.forum.entity.Question;
import com.example.forum.entity.User;
import com.example.forum.service.*;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author saysky
 * @date 2021/3/20
 */
@Controller
public class FrontUserController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private FollowService followService;

    /**
     * 用户列表
     *
     * @param model
     * @return
     */
    @GetMapping("/user")
    public String hotUser(Model model) {
        List<User> users = userService.getHotUsers(100);
        model.addAttribute("users", users);
        return "home/user";
    }



    /**
     * 用户文章列表
     *
     * @param model
     * @return
     */
    @GetMapping("/user/{id}/post")
    public String userPostList(@PathVariable("id") Long userId,
                               @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                               @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                               @RequestParam(value = "order", defaultValue = "desc") String order,
                               Model model) {

        User user = userService.get(userId);
        if (user == null) {
            return renderNotFound();
        }

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        QueryCondition queryCondition = new QueryCondition();
        Post condition = new Post();
        condition.setUserId(userId);
        queryCondition.setData(condition);
        Page<Post> postPage = postService.findAll(page, queryCondition);
        for (Post post : postPage.getRecords()) {
            post.setUser(userService.get(post.getUserId()));
            post.setCategory(categoryService.get(post.getCateId()));
        }
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("page", postPage);
        model.addAttribute("user", user);

        // 判断当前用户是否关注
        Long loginUserId = getLoginUserId();
        if (loginUserId != null) {
            try {
                Follow follow = followService.findByUserIdAndAcceptUserId(loginUserId, userId);
                if (follow != null) {
                    model.addAttribute("followFlag", "Y");
                } else {
                    model.addAttribute("followFlag", "N");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return "home/user_post_list";
    }


    /**
     * 用户帖子列表
     *
     * @param model
     * @return
     */
    @GetMapping({"/user/{id}/question", "/user/{id}"})
    public String userQuestionList(@PathVariable("id") Long userId,
                                   @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                   @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                   @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                   @RequestParam(value = "order", defaultValue = "desc") String order,
                                   Model model) {

        User user = userService.get(userId);
        if (user == null) {
            return renderNotFound();
        }

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Question condition = new Question();
        condition.setUserId(userId);
        Page<Question> postPage = questionService.findAll(page, new QueryCondition<>(condition));
        for (Question question : postPage.getRecords()) {
            question.setUser(user);
            question.setCategory(categoryService.get(question.getCateId()));
        }
        model.addAttribute("questions", postPage.getRecords());
        model.addAttribute("page", postPage);
        model.addAttribute("user", user);
        model.addAttribute("followFlag", "N");

        // 判断当前用户是否关注
        Long loginUserId = getLoginUserId();
        if (loginUserId != null) {
            try {
                Follow follow = followService.findByUserIdAndAcceptUserId(loginUserId, userId);
                if (follow != null) {
                    model.addAttribute("followFlag", "Y");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "home/user_question_list";
    }


}
