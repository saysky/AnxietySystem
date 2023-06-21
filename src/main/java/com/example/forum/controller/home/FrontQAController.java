package com.example.forum.controller.home;

import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.*;
import com.example.forum.enums.CommentTypeEnum;
import com.example.forum.service.*;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author saysky
 * @date 2021/3/20
 */
@Controller
public class FrontQAController extends BaseController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private AnswerService answerService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private QuestionMarkRefService questionMarkRefService;

    @Autowired
    private QuestionNoticeRefService questionNoticeRefService;

    @Autowired
    private FollowService followService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 帖子列表
     *
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @param order
     * @param model
     * @return
     */
    @GetMapping({"/question"})
    public String index(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        Model model) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Question condition = new Question();
        Page<Question> postPage = questionService.findAll(page, new QueryCondition<>(condition));
        for (Question question : postPage.getRecords()) {
            question.setUser(userService.get(question.getUserId()));
            question.setCategory(categoryService.get(question.getCateId()));
        }
        model.addAttribute("questions", postPage.getRecords());
        model.addAttribute("page", postPage);
        return "home/question_list";
    }

    /**
     * 帖子详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/question/{id}")
    public String postDetails(@PathVariable("id") Long id,
                              @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                              @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                              @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                              @RequestParam(value = "order", defaultValue = "desc") String order,
                              Model model) {
        // 帖子
        Question question = questionService.get(id);
        if (question == null) {
            return renderNotFound();
        }
        model.addAttribute("question", question);

        // 发帖人
        User user = userService.get(question.getUserId());
        model.addAttribute("user", user);

        // 回帖列表
        Answer condition = new Answer();
        condition.setQuestionId(id);
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Answer> answerPage = answerService.findAll(page, new QueryCondition<>(condition));
        for (Answer answer : answerPage.getRecords()) {
            answer.setUser(userService.get(answer.getUserId()));
            List<Comment> commentList = commentService.findByBusinessIdAndType(answer.getId(), CommentTypeEnum.ANSWER);
            answer.setCommentList(commentList);
        }

        model.addAttribute("answers", answerPage.getRecords());
        model.addAttribute("page", answerPage);

        // 添点赞数
        questionService.addView(id);

        return "home/question_detail";
    }


    /**
     * 帖子动态
     *
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @param order
     * @param model
     * @return
     */
    @GetMapping("/question/trend")
    public String trend(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        Model model) {
        Long userId = getLoginUserId();
        if (userId == null) {
            return this.renderLogin();
        }
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        QuestionNoticeRef condition = new QuestionNoticeRef();
        condition.setAcceptUserId(userId);
        Page<QuestionNoticeRef> postPage = questionNoticeRefService.findAll(page, new QueryCondition<>(condition));
        for (QuestionNoticeRef questionNoticeRef : postPage.getRecords()) {
            questionNoticeRef.setUser(userService.get(questionNoticeRef.getUserId()));
            questionNoticeRef.setQuestion(questionService.get(questionNoticeRef.getQuestionId()));
        }
        model.addAttribute("questionNoticeRefs", postPage.getRecords());
        model.addAttribute("page", postPage);
        return "home/question_trend";
    }

    /**
     * 帖子收藏列表
     *
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @param order
     * @param model
     * @return
     */
    @GetMapping("/question/mark")
    public String mark(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                       @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                       @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                       @RequestParam(value = "order", defaultValue = "desc") String order,
                       Model model) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        QuestionMarkRef condition = new QuestionMarkRef();
        Long userId = getLoginUserId();
        if (userId == null) {
            return renderLogin();
        }
        condition.setUserId(userId);
        Page<QuestionMarkRef> postPage = questionMarkRefService.findAll(page, new QueryCondition<>(condition));

        for (QuestionMarkRef questionMarkRef : postPage.getRecords()) {
            Question question = questionService.get(questionMarkRef.getQuestionId());
            if(question != null) {
                questionMarkRef.setQuestion(question);
                questionMarkRef.setUser(userService.get(question.getUserId()));
            }

        }
        model.addAttribute("marks", postPage.getRecords());
        model.addAttribute("page", postPage);
        return "home/question_mark";
    }

    /**
     * 我的关注
     *
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @param order
     * @param model
     * @return
     */
    @GetMapping("/question/user/follow")
    public String follow(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                         @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                         @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                         @RequestParam(value = "order", defaultValue = "desc") String order,
                         Model model) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Follow condition = new Follow();
        Long userId = getLoginUserId();
        if (userId == null) {
            return renderLogin();
        }
        condition.setUserId(userId);
        Page<Follow> postPage = followService.findAll(page, new QueryCondition<>(condition));
        for (Follow follow : postPage.getRecords()) {
            follow.setUser(userService.get(follow.getAcceptUserId()));
        }
        model.addAttribute("follows", postPage.getRecords());
        model.addAttribute("page", postPage);
        return "home/question_follow";
    }

    /**
     * 我的粉丝
     *
     * @param pageNumber
     * @param pageSize
     * @param sort
     * @param order
     * @param model
     * @return
     */
    @GetMapping("/question/user/fans")
    public String fans(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                       @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                       @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                       @RequestParam(value = "order", defaultValue = "desc") String order,
                       Model model) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Follow condition = new Follow();
        Long userId = getLoginUserId();
        if (userId == null) {
            return renderLogin();
        }
        condition.setAcceptUserId(userId);
        Page<Follow> postPage = followService.findAll(page, new QueryCondition<>(condition));
        for (Follow follow : postPage.getRecords()) {
            follow.setAcceptUser(userService.get(follow.getUserId()));
        }
        model.addAttribute("follows", postPage.getRecords());
        model.addAttribute("page", postPage);
        return "home/question_fans";
    }

    /**
     * 点赞帖子
     *
     * @param id
     * @return
     */
    @PostMapping("/question/like")
    @ResponseBody
    public JsonResult like(@RequestParam("id") Long id) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }

        questionService.addLike(id, user);
        return JsonResult.success();
    }

    /**
     * 收藏帖子
     *
     * @param id
     * @return
     */
    @PostMapping("/question/mark")
    @ResponseBody
    public JsonResult mark(@RequestParam("id") Long id) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }

        questionService.addMark(id, user);
        return JsonResult.success("收藏成功");
    }


    /**
     * 取消收藏帖子
     *
     * @param id
     * @return
     */
    @PostMapping("/question/unmark")
    @ResponseBody
    public JsonResult unmark(@RequestParam("id") Long id) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }
        questionService.deleteMark(id, user);
        return JsonResult.success();
    }


    /**
     * 关注用户
     *
     * @param acceptUserId
     * @return
     */
    @PostMapping("/question/user/follow")
    @ResponseBody
    public JsonResult follow(@RequestParam("acceptUserId") Long acceptUserId) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }
        if (Objects.equals(user.getId(), acceptUserId)) {
            return JsonResult.error("不能关注自己哦");
        }

        followService.follow(user, acceptUserId);
        return JsonResult.success();
    }

    /**
     * 取关用户
     *
     * @param acceptUserId
     * @return
     */
    @PostMapping("/question/user/unfollow")
    @ResponseBody
    public JsonResult unfollow(@RequestParam("acceptUserId") Long acceptUserId) {
        User user = getLoginUser();
        if (user == null) {
            return JsonResult.error("请先登录");
        }
        followService.unfollow(user, acceptUserId);
        return JsonResult.success();
    }


    /**
     * 点赞回帖
     *
     * @param answerId
     * @return
     */
    @PostMapping("/answer/like")
    @ResponseBody
    public JsonResult likeAnswer(@RequestParam("answerId") Long answerId) {
        Answer answer = answerService.get(answerId);
        if (answer == null) {
            return JsonResult.error("回帖不存在");
        }
        answer.setLikeCount(answer.getLikeCount() + 1);
        answerService.update(answer);
        return JsonResult.success();
    }

    /**
     * 点踩回帖
     *
     * @param answerId
     * @return
     */
    @PostMapping("/answer/dislike")
    @ResponseBody
    public JsonResult dislikeAnswer(@RequestParam("answerId") Long answerId) {
        Answer answer = answerService.get(answerId);
        if (answer == null) {
            return JsonResult.error("回帖不存在");
        }
        answer.setDislikeCount(answer.getDislikeCount() + 1);
        answerService.update(answer);
        return JsonResult.success();
    }

    /**
     * 回帖
     *
     * @param questionId 文章ID
     * @param content    回帖的内容
     * @return 重定向到/admin/comment
     */
    @PostMapping(value = "/answer")
    @ResponseBody
    public JsonResult newPostComment(@RequestParam(value = "questionId") Long questionId,
                                     @RequestParam("content") String content) {


        // 判断是否登录
        User loginUser = getLoginUser();
        if (loginUser == null) {
            return JsonResult.error("请先登录");
        }

        // 判断文章是否存在
        Question question = questionService.get(questionId);
        if (question == null) {
            return JsonResult.error("帖子不存在");
        }

        // 提取摘要
        int postSummaryLength = 100;
        String summaryText = HtmlUtil.cleanHtmlTag(content);
        if (summaryText.length() > postSummaryLength) {
            summaryText = summaryText.substring(0, postSummaryLength);
        }


        // 添加回帖
        Answer answer = new Answer();
        answer.setContent(content);
        answer.setSummary(summaryText);
        answer.setQuestionId(questionId);
        answer.setAcceptUserId(question.getUserId());
        answer.setUserId(loginUser.getId());
        answer.setLikeCount(0);
        answer.setDislikeCount(0);
        answer.setCommentCount(0);
        answer.setCreateTime(new Date());
        answer.setUpdateTime(new Date());
        answer.setCreateBy(loginUser.getUserName());
        answer.setUpdateBy(loginUser.getUserName());
        answerService.insert(answer);
        return JsonResult.success("回帖成功");
    }

}
