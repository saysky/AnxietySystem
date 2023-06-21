package com.example.forum.controller.home;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.Notice;
import com.example.forum.entity.User;
import com.example.forum.service.NoticeService;
import com.example.forum.service.UserService;
import com.example.forum.util.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


/**
 * @author saysky
 */
@Controller
public class FrontNoticeController extends BaseController {
    @Autowired
    private NoticeService noticeService;

    @Autowired
    private UserService userService;


    @GetMapping("/notice")
    public String index(@RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                        @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                        @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                        @RequestParam(value = "order", defaultValue = "desc") String order,
                        Model model) {

        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Notice condition = new Notice();
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setData(condition);
        Page<Notice> noticePage = noticeService.findAll(page, queryCondition);
        for (Notice notice : noticePage.getRecords()) {
            notice.setUser(userService.get(notice.getUserId()));
        }
        model.addAttribute("noticeList", noticePage.getRecords());
        model.addAttribute("page", noticePage);
        return "home/notice_list";
    }


    /**
     * 公告详情
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/notice/{id}")
    public String noticeDetails(@PathVariable("id") Long id,
                                @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                                @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                                @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        // 公告
        Notice notice = noticeService.get(id);
        model.addAttribute("notice", notice);

        // 作者
        User user = userService.get(notice.getUserId());
        model.addAttribute("user", user);
        return "home/notice_detail";
    }


}
