package com.example.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.library.entity.Book;

/**
 * 图书 Mapper：CRUD 由 MyBatis-Plus 的 BaseMapper 提供，无需手写 SQL
 */
public interface BookMapper extends BaseMapper<Book> {
}
