package com.example.forum.controller.home;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.Notice;
import com.example.forum.entity.Post;
import com.example.forum.entity.Question;
import com.example.forum.service.*;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 首页
 *
 * @author 言曌 liuyanzhao.com
 */
@Controller
public class IndexController {

    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private QuestionService questionService;

    @GetMapping("/")
    public String index(Model model) {

        // 公告列表
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");
        queryWrapper.last("limit 6");
        model.addAttribute("noticeList", noticeService.findAll(queryWrapper));

        // 帖子列表
        QueryWrapper<Question> queryWrapper2 = new QueryWrapper<>();
        queryWrapper2.orderByDesc("create_time");
        queryWrapper2.last("limit 10");
        model.addAttribute("questionList", questionService.findAll(queryWrapper2));

        // 文章列表
        QueryWrapper<Post> queryWrapper3 = new QueryWrapper<>();
        queryWrapper3.orderByDesc("create_time");
        queryWrapper3.last("limit 10");
        model.addAttribute("postList", postService.findAll(queryWrapper3));


        return "home/index";
    }
}
