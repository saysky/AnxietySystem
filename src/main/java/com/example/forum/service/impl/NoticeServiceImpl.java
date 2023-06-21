package com.example.forum.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.forum.entity.Notice;
import com.example.forum.mapper.NoticeMapper;
import com.example.forum.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * <pre>
 *     公告实现类
 * </pre>
 */
@Service
public class NoticeServiceImpl implements NoticeService {

    @Autowired
    private NoticeMapper noticeMapper;


    @Override
    public BaseMapper<Notice> getRepository() {
        return noticeMapper;
    }

    @Override
    public QueryWrapper<Notice> getQueryWrapper(Notice notice) {
        //对指定字段查询
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        return queryWrapper;
    }

}
