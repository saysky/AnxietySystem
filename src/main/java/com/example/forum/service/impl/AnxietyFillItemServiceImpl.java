package com.example.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.entity.AnxietyFillItem;
import com.example.forum.mapper.AnxietyFillItemMapper;
import com.example.forum.service.AnxietyFillItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <pre>
 *     服务实现类
 * </pre>
 */
@Service
public class AnxietyFillItemServiceImpl implements AnxietyFillItemService {

    @Autowired
    private AnxietyFillItemMapper anxietyFillItemMapper;


    @Override
    public BaseMapper<AnxietyFillItem> getRepository() {
        return anxietyFillItemMapper;
    }

    @Override
    public QueryWrapper<AnxietyFillItem> getQueryWrapper(AnxietyFillItem anxietyFillItem) {
        //对指定字段查询
        QueryWrapper<AnxietyFillItem> queryWrapper = new QueryWrapper<>();
        return queryWrapper;
    }

}
