package com.example.forum.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.Answer;
import com.example.forum.entity.User;
import com.example.forum.enums.ResultCodeEnum;
import com.example.forum.exception.MyBusinessException;
import com.example.forum.service.AnswerService;
import com.example.forum.service.QuestionService;
import com.example.forum.service.UserService;
import com.example.forum.util.PageUtil;
import com.example.forum.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * <pre>
 *     后台回帖管理控制器
 * </pre>
 *
 * @author saysky
 * @date 2021/3/20
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/answer")
public class AnswerController extends BaseController {

    @Autowired
    private AnswerService answerService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuestionService questionService;

    /**
     * 处理后台获取回帖列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_answer
     */
    @GetMapping
    public String answers(Model model,
                          @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                          @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                          @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                          @RequestParam(value = "order", defaultValue = "desc") String order,
                          @ModelAttribute SearchVo searchVo) {

        Long loginUserId = getLoginUserId();
        Answer condition = new Answer();
        // 管理员可以查看所有用户的，非管理员只能看到自己的回帖
        if (!loginUserIsAdmin()) {
            condition.setUserId(loginUserId);
        }

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Answer> answers = answerService.findAll(
                page,
                new QueryCondition<>(condition, searchVo));

        for (Answer answer : answers.getRecords()) {
            answer.setQuestion(questionService.get(answer.getQuestionId()));
            answer.setUser(userService.get(answer.getUserId()));
        }

        //封装
        // 分类和标签
        model.addAttribute("answers", answers.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_answer";
    }

    /**
     * 处理后台获取回帖列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_answer
     */
    @GetMapping("/accept")
    public String acceptedAnswers(Model model,
                                  @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                  @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                  @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                  @RequestParam(value = "order", defaultValue = "desc") String order,
                                  @ModelAttribute SearchVo searchVo) {

        Long loginUserId = getLoginUserId();
        Answer condition = new Answer();
        condition.setAcceptUserId(loginUserId);

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Answer> answers = answerService.findAll(
                page,
                new QueryCondition<>(condition, searchVo));

        for (Answer answer : answers.getRecords()) {
            answer.setQuestion(questionService.get(answer.getQuestionId()));
            answer.setUser(userService.get(answer.getUserId()));
        }

        //封装
        // 分类和标签
        model.addAttribute("answers", answers.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_answer";
    }


    /**
     * 添加/更新回帖
     *
     * @param answer Answer实体
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushAnswer(@ModelAttribute Answer answer) {

        // 1.获得登录用户
        User user = getLoginUser();
        Boolean isAdmin = loginUserIsAdmin();
        answer.setUserId(getLoginUserId());

        //2、非管理员只能修改自己的回帖，管理员都可以修改
        Answer originAnswer = null;
        if (answer.getId() != null) {
            originAnswer = answerService.get(answer.getId());
            if (!Objects.equals(originAnswer.getUserId(), user.getId()) && !isAdmin) {
                return JsonResult.error("没有权限");
            }
            //以下属性不能修改
            answer.setUserId(originAnswer.getUserId());
            answer.setQuestionId(originAnswer.getQuestionId());
            answer.setDislikeCount(originAnswer.getDislikeCount());
            answer.setLikeCount(originAnswer.getLikeCount());
            answer.setDelFlag(originAnswer.getDelFlag());
        }


        // 3.添加/更新入库
        answerService.insertOrUpdate(answer);
        return JsonResult.success("发布成功");
    }


    /**
     * 处理删除回帖的请求
     *
     * @param answerId 回帖编号
     * @return 重定向到/admin/answer
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public JsonResult removeAnswer(@RequestParam("id") Long answerId) {
        Answer answer = answerService.get(answerId);
        basicCheck(answer);
        answerService.delete(answerId);
        return JsonResult.success("删除成功");
    }

    /**
     * 批量删除
     *
     * @param ids 回帖ID列表
     * @return 重定向到/admin/answer
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public JsonResult batchDelete(@RequestParam("ids") List<Long> ids) {
        //1、防止恶意操作
        if (ids == null || ids.size() == 0 || ids.size() >= 100) {
            return new JsonResult(ResultCodeEnum.FAIL.getCode(), "参数不合法!");
        }
        //2、检查用户权限
        List<Answer> answerList = answerService.findByBatchIds(ids);
        for (Answer answer : answerList) {
            basicCheck(answer);
        }
        //3、删除
        for (Answer answer : answerList) {
            answerService.delete(answer.getId());
        }
        return JsonResult.success("删除成功");
    }


    /**
     * 检查回帖是否存在和用户是否有权限控制
     *
     * @param answer
     */
    private void basicCheck(Answer answer) {
        if (answer == null) {
            throw new MyBusinessException("回帖不存在");
        }
        //只有创建者有权删除
        User user = getLoginUser();
        //管理员、回帖者、发帖人可以删除
        Boolean isAdmin = loginUserIsAdmin();
        if (!Objects.equals(answer.getUserId(), user.getId()) && !isAdmin && !Objects.equals(answer.getAcceptUserId(), user.getId())) {
            throw new MyBusinessException("没有权限");
        }
    }


}
