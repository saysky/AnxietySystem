package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.Category;
import com.example.forum.entity.Post;
import com.example.forum.entity.Question;
import com.example.forum.enums.CategoryTypeEnum;
import com.example.forum.service.CategoryService;
import com.example.forum.service.PostService;
import com.example.forum.service.QuestionService;
import com.example.forum.service.UserService;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Objects;

/**
 * @author saysky
 * @date 2021/3/20
 */
@Controller
public class FrontCategoryController extends BaseController {

    @Autowired
    private PostService postService;

    @Autowired
    private QuestionService questionService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;


    /**
     * 分类对应的文章列表
     *
     * @param model
     * @return
     */
    @GetMapping("/category/{id}")
    public String postCategoryList(@PathVariable("id") Long cateId,
                                   @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                   @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                   @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                   @RequestParam(value = "order", defaultValue = "desc") String order,
                                   Model model) {

        Category category = categoryService.get(cateId);
        if (category == null) {
            return renderNotFound();
        }
        model.addAttribute("category", category);


        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        if (Objects.equals(category.getCateType(), CategoryTypeEnum.POST.getCode())) {
            QueryCondition queryCondition = new QueryCondition();
            Post condition = new Post();
            condition.setCateId(cateId);
            queryCondition.setData(condition);
            Page<Post> postPage = postService.findAll(page, queryCondition);
            for (Post post : postPage.getRecords()) {
                post.setUser(userService.get(post.getUserId()));
                post.setCategory(categoryService.get(post.getCateId()));
            }
            model.addAttribute("posts", postPage.getRecords());
            model.addAttribute("page", postPage);
            return "home/category_post";
        } else {
            Question condition = new Question();
            condition.setCateId(cateId);
            Page<Question> postPage = questionService.findAll(page, new QueryCondition<>(condition));
            for (Question question : postPage.getRecords()) {
                question.setUser(userService.get(question.getUserId()));
                question.setCategory(categoryService.get(question.getCateId()));
            }
            model.addAttribute("questions", postPage.getRecords());
            model.addAttribute("page", postPage);
            return "home/category_question";
        }

    }


}
