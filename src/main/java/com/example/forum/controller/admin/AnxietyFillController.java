package com.example.forum.controller.admin;

import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.URLUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.AnxietyFill;
import com.example.forum.entity.AnxietyFillItem;
import com.example.forum.entity.AnxietyQuestion;
import com.example.forum.entity.User;
import com.example.forum.exception.MyBusinessException;
import com.example.forum.service.AnxietyFillItemService;
import com.example.forum.service.AnxietyFillService;
import com.example.forum.service.AnxietyQuestionService;
import com.example.forum.service.UserService;
import com.example.forum.util.PageUtil;
import com.example.forum.util.PdfUtil;
import com.example.forum.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.velocity.VelocityContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <pre>
 *     问题填写记录管理控制器
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/anxietyFill")
public class AnxietyFillController extends BaseController {

    @Autowired
    private AnxietyFillService anxietyFillService;

    @Autowired
    private AnxietyFillItemService anxietyFillItemService;

    @Autowired
    private AnxietyQuestionService anxietyQuestionService;


    @Autowired
    private UserService userService;

    /**
     * 处理后台获取焦虑症自测题列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_anxietyFill
     */
    @GetMapping
    public String anxietyFills(Model model,
                               @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                               @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                               @RequestParam(value = "sort", defaultValue = "id") String sort,
                               @RequestParam(value = "order", defaultValue = "asc") String order,
                               @RequestParam(value = "anxietyFlag", defaultValue = "-1") Integer anxietyFlag,
                               @ModelAttribute SearchVo searchVo) {


        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        QueryCondition queryCondition = new QueryCondition();
        AnxietyFill condition = new AnxietyFill();
        condition.setAnxietyFlag(anxietyFlag);
        if (!loginUserIsAdmin()) {
            condition.setUserId(getLoginUserId());
        }

        queryCondition.setData(condition);
        Page<AnxietyFill> anxietyFillPage = anxietyFillService.findAll(page, queryCondition);
        List<AnxietyFill> anxietyFillList = anxietyFillPage.getRecords();
        for (AnxietyFill anxietyFill : anxietyFillList) {
            User user = userService.get(anxietyFill.getUserId());
            anxietyFill.setUser(user != null ? user : new User());
        }


        model.addAttribute("anxietyFills", anxietyFillList);
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        model.addAttribute("order", order);
        model.addAttribute("sort", sort);
        model.addAttribute("anxietyFlag", anxietyFlag);

        return "admin/admin_anxietyFill";
    }



    //TODO 导出

    /**
     * 处理删除焦虑症自测题的请求
     *
     * @param anxietyFillId 焦虑症自测题编号
     * @return 重定向到/admin/anxietyFill
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public JsonResult removeAnxietyFill(@RequestParam("id") Long anxietyFillId) {
        anxietyFillService.delete(anxietyFillId);
        return JsonResult.success("删除成功");
    }

    @PostMapping("/save")
    @ResponseBody
    @Transactional(rollbackFor = Exception.class)
    public JsonResult save(HttpServletRequest request) {
        // 初始化参数
        QueryWrapper<AnxietyQuestion> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc("id");
        List<AnxietyQuestion> anxietyQuestionList = anxietyQuestionService.findAll(queryWrapper);

        List<Integer> scoreList = new ArrayList<>();
        for (int i = 0; i < anxietyQuestionList.size(); i++) {
            String[] parameterValues = request.getParameterValues("answer[" + i + "]");
            scoreList.add(parameterValues.length > 0 ? Integer.valueOf(parameterValues[0]) : 0);
        }

        // 存储数据
        Date date = new Date();

        AnxietyFill anxietyFill = new AnxietyFill();
        anxietyFill.setUserId(getLoginUserId());
        anxietyFill.setCreateTime(date);
        anxietyFillService.insert(anxietyFill);

        List<AnxietyFillItem> anxietyFillItems = new ArrayList<>();
        int i = 0;
        int inputScoreSum = 0;
        for (Integer score : scoreList) {
            AnxietyFillItem anxietyFillItem = new AnxietyFillItem();
            anxietyFillItem.setCreateTime(date);
            anxietyFillItem.setScore(score);
            anxietyFillItem.setQuestionId(anxietyQuestionList.get(i).getId());
            anxietyFillItem.setTitle(anxietyQuestionList.get(i).getTitle());
            anxietyFillItem.setFillId(anxietyFill.getId());
            anxietyFillItems.add(anxietyFillItem);
            i++;
            inputScoreSum += score;
        }
        anxietyFillItemService.batchInsert(anxietyFillItems);

        // 计算得分
        int avgScore = 2 * anxietyQuestionList.size();
        if (inputScoreSum < avgScore) {
            anxietyFill.setAnxietyFlag(0);
        } else {
            anxietyFill.setAnxietyFlag(1);
        }
        anxietyFill.setScore(inputScoreSum);
        anxietyFillService.update(anxietyFill);
        return JsonResult.success("", anxietyFill);
    }

    @GetMapping("/item")
    public String anxiety(@RequestParam Long id,  Model model) {
        QueryWrapper<AnxietyFillItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fill_id", id);
        queryWrapper.orderByAsc("id");
        List<AnxietyFillItem> anxietyFillItemList = anxietyFillItemService.findAll(queryWrapper);
        model.addAttribute("anxietyFillItemList", anxietyFillItemList);
        return "admin/admin_anxietyFill_item";
    }


    @GetMapping("/report")
    public String report(@RequestParam Long id, Model model) {
        AnxietyFill anxietyFill = anxietyFillService.get(id);
        model.addAttribute("anxietyFill", anxietyFill);

        QueryWrapper<AnxietyFillItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fill_id", id);
        queryWrapper.orderByAsc("id");
        List<AnxietyFillItem> anxietyFillItemList = anxietyFillItemService.findAll(queryWrapper);
        Integer inputScoreSum = 0;
        for (AnxietyFillItem anxietyFillItem : anxietyFillItemList) {
            inputScoreSum += anxietyFillItem.getScore();
        }

        int avgScore = 2 * anxietyFillItemList.size();
        int totalScore = 4 * anxietyFillItemList.size();
        double percent = 100.0 * (inputScoreSum - avgScore) / totalScore;
        DecimalFormat f = new DecimalFormat("#0.0");
        String percentStr = f.format(percent);
        model.addAttribute("percentStr", percentStr);
        return "admin/admin_anxietyFill_report";
    }

    @RequestMapping("/export")
    public void get(@RequestParam Long id, HttpServletResponse response){
        AnxietyFill anxietyFill = anxietyFillService.get(id);

        QueryWrapper<AnxietyFillItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("fill_id", id);
        queryWrapper.orderByAsc("id");
        List<AnxietyFillItem> anxietyFillItemList = anxietyFillItemService.findAll(queryWrapper);
        Integer inputScoreSum = 0;
        for (AnxietyFillItem anxietyFillItem : anxietyFillItemList) {
            inputScoreSum += anxietyFillItem.getScore();
        }

        int avgScore = 2 * anxietyFillItemList.size();
        int totalScore = 4 * anxietyFillItemList.size();
        double percent = 100.0 * (inputScoreSum - avgScore) / totalScore;
        DecimalFormat f = new DecimalFormat("#0.0");
        String percentStr = f.format(percent);

        response.reset();
        response.setContentType("application/pdf");
        String filename = System.currentTimeMillis()+".pdf";
        response.addHeader("Content-Disposition", "inline; filename=" + URLUtil.encode(filename, "UTF-8"));
        VelocityContext context = new VelocityContext();

        context.put("anxietyFlag", anxietyFill.getAnxietyFlag());
        context.put("percentStr", percentStr);

        try(ServletOutputStream outputStream = response.getOutputStream()){
            PdfUtil.pdfFile(context, "pdf_template.html", outputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
    }



}
