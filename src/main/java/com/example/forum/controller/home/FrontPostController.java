package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.PostQueryCondition;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.*;
import com.example.forum.enums.CommentTypeEnum;
import com.example.forum.enums.PostStatusEnum;
import com.example.forum.service.*;
import com.example.forum.util.CommentUtil;
import com.example.forum.util.PageUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.locks.Condition;

/**
 * @author saysky
 * @date 2021/3/20
 */
@Controller
public class FrontPostController extends BaseController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;


    public static final String NEW = "new";
    public static final String PUBLISH = "publish";
    public static final String HOT = "hot";
    public static final String USER = "user";
    public static final String CATEGORY = "category";


    @GetMapping("/post")
    public String index(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        @RequestParam(value = "keywords", required = false) String keywords,
                        Model model) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Post condition = new Post();
        condition.setPostTitle(keywords);
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setData(condition);
        Page<Post> postPage = postService.findAll(page, queryCondition);
        for (Post post : postPage.getRecords()) {
            post.setUser(userService.get(post.getUserId()));
            post.setCategory(categoryService.get(post.getCateId()));
        }
        model.addAttribute("posts", postPage.getRecords());
        model.addAttribute("page", postPage);
        return "home/post_list";
    }



    /**
     * 点赞文章
     *
     * @param postId
     * @return
     */
    @PostMapping("/post/like")
    @ResponseBody
    public JsonResult likePost(@RequestParam("postId") Long postId) {
        Post post = postService.get(postId);
        if (post == null) {
            return JsonResult.error("文章不存在");
        }
        post.setPostLikes(post.getPostLikes() + 1);
        postService.update(post);
        return JsonResult.success();
    }


    /**
     * 文章详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/post/{id}")
    public String postDetails(@PathVariable("id") Long id,
                              @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                              @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                              @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        // 文章
        Post post = postService.get(id);
        if (post == null || !PostStatusEnum.PUBLISHED.getCode().equals(post.getPostStatus())) {
            return renderNotFound();
        }
        model.addAttribute("post", post);

        // 作者
        User user = userService.get(post.getUserId());
        model.addAttribute("user", user);

        // 分类
        Category category = categoryService.get(post.getCateId());
        model.addAttribute("category", category);

        // 评论列表
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Comment condition = new Comment();
        condition.setBusinessId(id);
        condition.setType(CommentTypeEnum.POST.getCode());
        condition.setCommentParent(0L);
        Page<Comment> commentPage = commentService.findAll(page, new QueryCondition<>(condition));
        for (Comment comment : commentPage.getRecords()) {
            List<Comment> commentList = commentService.findByCommentParent(comment.getId());
            comment.setChildComments(commentList);
            comment.setUser(userService.get(comment.getUserId()));
        }

        model.addAttribute("comments", commentPage.getRecords());
        model.addAttribute("page", commentPage);
        // 点击数加1
        postService.updatePostView(id);
        return "home/post_detail";
    }


}
