package com.example.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.entity.AnxietyQuestion;
import com.example.forum.entity.Notice;
import com.example.forum.mapper.AnxietyQuestionMapper;
import com.example.forum.mapper.NoticeMapper;
import com.example.forum.service.AnxietyQuestionService;
import com.example.forum.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <pre>
 *     焦虑症自测题服务实现类
 * </pre>
 */
@Service
public class AnxietyQuestionServiceImpl implements AnxietyQuestionService {

    @Autowired
    private AnxietyQuestionMapper anxietyQuestionMapper;


    @Override
    public BaseMapper<AnxietyQuestion> getRepository() {
        return anxietyQuestionMapper;
    }

    @Override
    public QueryWrapper<AnxietyQuestion> getQueryWrapper(AnxietyQuestion anxietyQuestion) {
        //对指定字段查询
        QueryWrapper<AnxietyQuestion> queryWrapper = new QueryWrapper<>();
        return queryWrapper;
    }

}
