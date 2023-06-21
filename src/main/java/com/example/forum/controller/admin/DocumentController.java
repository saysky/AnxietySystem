package com.example.forum.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.JsonResult;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.Document;
import com.example.forum.entity.Post;
import com.example.forum.entity.User;
import com.example.forum.enums.PostStatusEnum;
import com.example.forum.enums.ResultCodeEnum;
import com.example.forum.service.DocumentService;
import com.example.forum.util.FileUtil;
import com.example.forum.util.PageUtil;
import com.example.forum.util.SensUtils;
import com.example.forum.vo.SearchVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author saysky
 * @date 2021/3/20
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/document")
public class DocumentController extends BaseController {

    @Autowired
    private DocumentService documentService;

    /**
     * 处理后台获取文章列表的请求
     *
     * @param model model
     * @return 模板路径admin/admin_document
     */
    @GetMapping
    public String documents(Model model,
                            @RequestParam(value = "page", defaultValue = "1") Integer pageNumber,
                            @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                            @RequestParam(value = "sort", defaultValue = "createTime") String sort,
                            @RequestParam(value = "order", defaultValue = "desc") String order,
                            @ModelAttribute SearchVo searchVo) {

        Document condition = new Document();
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        Page<Document> documents = documentService.findAll(page, new QueryCondition<>(condition, searchVo));

        model.addAttribute("documents", documents.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_document";
    }

    /**
     * 上传文件
     *
     * @param file file
     * @return Map
     */
    @PostMapping(value = "/upload", produces = {"application/json;charset=UTF-8"})
    @ResponseBody
    public JsonResult uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, String> map = FileUtil.upload(file);
        Document document = new Document();
        document.setName(file.getOriginalFilename().substring(0, file.getOriginalFilename().lastIndexOf('.')).replaceAll(" ", "_").replaceAll(",", ""));
        document.setUrl(map.get("url"));
        document.setSize(SensUtils.parseSize(Long.valueOf(map.get("size"))));
        document.setPath(map.get("path"));
        document.setSuffix(map.get("suffix"));
        User user = getLoginUser();
        document.setUserId(user.getId());
        document.setUpdateBy(user.getUserName());
        document.setCreateBy(user.getUserName());
        documentService.insert(document);
        return JsonResult.success();
    }

    /**
     * 处理删除文章的请求
     *
     * @param postId 文章编号
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public JsonResult removePost(@RequestParam("id") Long postId) {
        documentService.delete(postId);
        return JsonResult.success("删除成功");
    }

    /**
     * 批量删除
     *
     * @param ids 资源ID列表
     * @return 重定向到/admin/post
     */
    @PostMapping(value = "/batchDelete")
    @ResponseBody
    public JsonResult batchDelete(@RequestParam("ids") List<Long> ids) {
        for (Long id : ids) {
            documentService.delete(id);
        }
        return JsonResult.success("删除成功");
    }
}
