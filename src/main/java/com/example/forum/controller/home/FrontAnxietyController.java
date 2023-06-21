package com.example.forum.controller.home;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.AnxietyQuestion;
import com.example.forum.entity.Notice;
import com.example.forum.entity.User;
import com.example.forum.service.*;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


/**
 * @author saysky
 */
@Controller
public class FrontAnxietyController extends BaseController {
    @Autowired
    private AnxietyQuestionService anxietyQuestionService;

    @Autowired
    private AnxietyFillService anxietyFillService;

    @Autowired
    private AnxietyFillItemService anxietyFillItemService;


    @GetMapping("/anxiety")
    public String anxiety(Model model) {
        QueryWrapper<AnxietyQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        List<AnxietyQuestion> anxietyQuestionList = anxietyQuestionService.findAll(queryWrapper);
        model.addAttribute("anxietyQuestionList", anxietyQuestionList);
        return "home/anxiety";
    }






}
