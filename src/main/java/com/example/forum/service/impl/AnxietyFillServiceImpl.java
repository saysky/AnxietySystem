package com.example.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.entity.AnxietyFill;
import com.example.forum.mapper.AnxietyFillMapper;
import com.example.forum.service.AnxietyFillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <pre>
 *     服务实现类
 * </pre>
 */
@Service
public class AnxietyFillServiceImpl implements AnxietyFillService {

    @Autowired
    private AnxietyFillMapper anxietyFillMapper;


    @Override
    public BaseMapper<AnxietyFill> getRepository() {
        return anxietyFillMapper;
    }

    @Override
    public QueryWrapper<AnxietyFill> getQueryWrapper(AnxietyFill anxietyFill) {
        //对指定字段查询
        QueryWrapper<AnxietyFill> queryWrapper = new QueryWrapper<>();
        if (anxietyFill.getAnxietyFlag() != null && anxietyFill.getAnxietyFlag() != -1) {
            queryWrapper.eq("anxiety_flag", anxietyFill.getAnxietyFlag());
        }
        if (anxietyFill.getUserId() != null) {
            queryWrapper.eq("user_id", anxietyFill.getUserId());
        }
        return queryWrapper;
    }

}
