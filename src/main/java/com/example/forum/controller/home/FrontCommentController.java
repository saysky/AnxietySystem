package com.example.forum.controller.home;

import cn.hutool.http.HtmlUtil;
import com.example.forum.controller.common.BaseController;
import com.example.forum.entity.*;
import com.example.forum.dto.JsonResult;
import com.example.forum.enums.CommentTypeEnum;
import com.example.forum.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author saysky
 * @date 2021/3/20
 */
@Controller
public class FrontCommentController extends BaseController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    /**
     * 发布文章评论
     *
     * @param postId         文章ID
     * @param commentId      上级回帖ID
     * @param commentContent 回帖的内容
     * @return 重定向到/admin/comment
     */
    @PostMapping(value = "/comment/post")
    @ResponseBody
    public JsonResult newPostComment(@RequestParam(value = "postId") Long postId,
                                     @RequestParam(value = "commentId", required = false) Long commentId,
                                     @RequestParam("commentContent") String commentContent) {


        // 判断是否登录
        User loginUser = getLoginUser();
        if (loginUser == null) {
            return JsonResult.error("请先登录");
        }

        // 判断文章是否存在
        Post post = postService.get(postId);
        if (post == null) {
            return JsonResult.error("文章不存在");
        }


        // 如果是回帖
        Comment comment = new Comment();
        if (commentId != null) {
            //回帖回帖
            Comment parentComment = commentService.get(commentId);
            if (parentComment == null || !Objects.equals(parentComment.getBusinessId(), postId)) {
                return JsonResult.error("回帖不存在");
            }
            User parentUser = userService.get(parentComment.getUserId());
            if (parentUser != null) {
                String lastContent = "<a href='#comment" + parentComment.getId() + "'>@" + parentUser.getUserDisplayName() + "</a> ";
                comment.setCommentContent(lastContent + parentUser.getUserDisplayName() + ": " + HtmlUtil.escape(commentContent));
                comment.setCommentParent(parentComment.getId());
                comment.setAcceptUserId(parentComment.getUserId());
                comment.setPathTrace(parentComment.getPathTrace() + parentComment.getId() + "/");
            }
        } else {
            // 回帖文章
            comment.setCommentContent(HtmlUtil.escape(commentContent));
            comment.setCommentParent(0L);
            comment.setAcceptUserId(post.getUserId());
            comment.setPathTrace("/");
        }
        comment.setUserId(loginUser.getId());
        comment.setType(CommentTypeEnum.POST.getCode());
        comment.setBusinessId(postId);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        commentService.insert(comment);

        // 修改评论数
        postService.resetCommentSize(postId);
        return JsonResult.success("评论成功", comment.getId());
    }

    /**
     * 发布回帖评论
     *
     * @param answerId 回帖ID
     * @param content  评论的内容
     * @return 重定向到/admin/comment
     */
    @PostMapping(value = "/comment/answer")
    @ResponseBody
    public JsonResult newAnswerComment(@RequestParam(value = "answerId") Long answerId,
                                       @RequestParam("content") String content) {


        // 判断是否登录
        User loginUser = getLoginUser();
        if (loginUser == null) {
            return JsonResult.error("请先登录");
        }

        // 判断回帖是否存在
        Answer answer = answerService.get(answerId);
        if (answer == null) {
            return JsonResult.error("回帖不存在");
        }


        // 如果是回帖
        Comment comment = new Comment();
        User parentUser = userService.get(answer.getUserId());
        if (parentUser == null) {
            return JsonResult.error("该回帖作者不存在");
        }
        String lastContent = "<a href='#answer" + answer.getId() + "'>@" + parentUser.getUserDisplayName() + "</a> ";
        comment.setCommentContent(lastContent + parentUser.getUserDisplayName() + ": " + HtmlUtil.escape(content));
        comment.setCommentParent(0L);
        comment.setAcceptUserId(answer.getUserId());
        comment.setPathTrace("/");
        comment.setType(CommentTypeEnum.ANSWER.getCode());
        comment.setUserId(loginUser.getId());
        comment.setBusinessId(answerId);
        comment.setCreateTime(new Date());
        comment.setUpdateTime(new Date());
        commentService.insert(comment);

        // 修改回帖评论数
        answerService.resetCommentSize(answerId);
        return JsonResult.success("评论成功", comment.getId());
    }

    /**
     * 点赞评论
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comment/like")
    @ResponseBody
    public JsonResult likeComment(@RequestParam("commentId") Long commentId) {
        Comment comment = commentService.get(commentId);
        if (comment == null) {
            return JsonResult.error("回帖不存在");
        }
        comment.setLikeCount(comment.getLikeCount() + 1);
        commentService.update(comment);
        return JsonResult.success();
    }

    /**
     * 点赞评论
     *
     * @param commentId
     * @return
     */
    @PostMapping("/comment/dislike")
    @ResponseBody
    public JsonResult dislikeComment(@RequestParam("commentId") Long commentId) {
        Comment comment = commentService.get(commentId);
        if (comment == null) {
            return JsonResult.error("回帖不存在");
        }
        comment.setDislikeCount(comment.getDislikeCount() + 1);
        commentService.update(comment);
        return JsonResult.success();
    }
}
