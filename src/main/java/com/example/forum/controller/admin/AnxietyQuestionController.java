package com.example.forum.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.entity.AnxietyQuestion;
import com.example.forum.exception.MyBusinessException;
import com.example.forum.service.AnxietyQuestionService;
import com.example.forum.util.PageUtil;
import com.example.forum.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <pre>
 *     后台自测题管理控制器
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/anxietyQuestion")
public class AnxietyQuestionController extends BaseController {

    @Autowired
    private AnxietyQuestionService anxietyQuestionService;

    /**
     * 处理后台获取焦虑症自测题列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_anxietyQuestion
     */
    @GetMapping
    public String anxietyQuestions(Model model,
                          @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                          @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                          @RequestParam(value = "sort", defaultValue = "id") String sort,
                          @RequestParam(value = "order", defaultValue = "asc") String order,
                          @ModelAttribute SearchVo searchVo) {


        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<AnxietyQuestion> anxietyQuestionPage = anxietyQuestionService.findAll(page);
        List<AnxietyQuestion> anxietyQuestionList = anxietyQuestionPage.getRecords();

        model.addAttribute("anxietyQuestions", anxietyQuestionList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);

        return "admin/admin_anxietyQuestion";
    }


    /**
     * 处理跳转到新建焦虑症自测题页面
     *
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/new")
    public String newAnxietyQuestion(Model model) {
        return "admin/admin_anxietyQuestion_new";
    }


    /**
     * 添加/更新焦虑症自测题
     *
     * @param anxietyQuestion AnxietyQuestion实体
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult pushAnxietyQuestion(@ModelAttribute AnxietyQuestion anxietyQuestion) {
        anxietyQuestionService.insertOrUpdate(anxietyQuestion);
        return JsonResult.success("发布成功");
    }


    /**
     * 处理删除焦虑症自测题的请求
     *
     * @param anxietyQuestionId 焦虑症自测题编号
     * @return 重定向到/admin/anxietyQuestion
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public JsonResult removeAnxietyQuestion(@RequestParam("id") Long anxietyQuestionId) {
        anxietyQuestionService.delete(anxietyQuestionId);
        return JsonResult.success("删除成功");
    }


    /**
     * 跳转到编辑焦虑症自测题页面
     *
     * @param anxietyQuestionId 焦虑症自测题编号
     * @param model    model
     * @return 模板路径admin/admin_editor
     */
    @GetMapping(value = "/edit")
    public String editAnxietyQuestion(@RequestParam("id") Long anxietyQuestionId, Model model) {
        AnxietyQuestion anxietyQuestion = anxietyQuestionService.get(anxietyQuestionId);
        if (anxietyQuestion == null) {
            throw new MyBusinessException("焦虑症自测题不存在");
        }

        model.addAttribute("anxietyQuestion", anxietyQuestion);

        return "admin/admin_anxietyQuestion_edit";
    }


}
