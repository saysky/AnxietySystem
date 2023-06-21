package com.example.forum.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.forum.controller.common.BaseController;
import com.example.forum.dto.QueryCondition;
import com.example.forum.entity.Category;
import com.example.forum.dto.JsonResult;
import com.example.forum.service.CategoryService;
import com.example.forum.util.PageUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * <pre>
 *     后台分类管理控制器
 * </pre>
 */
@Slf4j
@Controller
@RequestMapping(value = "/admin/category")
public class CategoryController extends BaseController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 查询所有分类并渲染category页面
     *
     * @return 模板路径admin/admin_category
     */
    @GetMapping("/{type}")
    public String categories(@PathVariable("type") String cateType,
                             @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                             @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                             @RequestParam(value = "sort", defaultValue = "cateSort") String sort,
                             @RequestParam(value = "order", defaultValue = "desc") String order, Model model) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);
        QueryCondition queryCondition = new QueryCondition();
        Category condition = new Category();
        condition.setCateType(cateType);
        queryCondition.setData(condition);
        Page<Category> categoryPage = categoryService.findAll(page, queryCondition);
        model.addAttribute("categories", categoryPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));

        model.addAttribute("cateType", cateType);
        return "admin/admin_category";
    }

    /**
     * 新增/修改分类目录
     *
     * @param category category对象
     * @return 重定向到/admin/category
     */
    @PostMapping(value = "/save")
    @ResponseBody
    public JsonResult saveCategory(@ModelAttribute Category category) {
        categoryService.insertOrUpdate(category);
        return JsonResult.success("保存成功");
    }

    /**
     * 删除分类
     *
     * @param cateId 分类Id
     * @return JsonResult
     */
    @PostMapping(value = "/delete")
    @ResponseBody
    public JsonResult checkDelete(@RequestParam("id") Long cateId) {
        //1.判断这个分类有文章
        Integer count = categoryService.countPostByCateId(cateId);
        if (count != 0) {
            return JsonResult.error("该分类已经有了文章，无法删除");
        }
        categoryService.delete(cateId);
        return JsonResult.success("删除成功");
    }


    /**
     * 跳转到修改页面
     *
     * @param cateId cateId
     * @param model  model
     * @return 模板路径admin/admin_category
     */
    @GetMapping(value = "/edit")
    public String toEditCategory(Model model,
                                 @RequestParam(value = "page", defaultValue = "0") Integer pageNumber,
                                 @RequestParam(value = "size", defaultValue = "10") Integer pageSize,
                                 @RequestParam(value = "sort", defaultValue = "cateSort") String sort,
                                 @RequestParam(value = "order", defaultValue = "desc") String order,
                                 @RequestParam("id") Long cateId) {
        Page page = PageUtil.initMpPage(pageNumber, pageSize, sort, order);

        //更新的分类
        Category category = categoryService.get(cateId);
        if (category == null) {
            return "redirect:/admin/category/post";
        }
        model.addAttribute("updateCategory", category);

        // 所有分类
        Category condition = new Category();
        condition.setCateType(category.getCateType());
        QueryCondition queryCondition = new QueryCondition();
        queryCondition.setData(condition);
        Page<Category> categoryPage = categoryService.findAll(page, queryCondition);
        model.addAttribute("categories", categoryPage.getRecords());
        model.addAttribute("pageInfo", PageUtil.convertPageVo(page));
        return "admin/admin_category";
    }
}
