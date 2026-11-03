package com.example.library.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.library.common.PageResult;
import com.example.library.dto.BookQuery;
import com.example.library.entity.Book;

import java.util.List;

/**
 * 图书业务接口
 */
public interface BookService extends IService<Book> {

    /** 分页 + 条件查询 */
    PageResult<Book> pageBooks(BookQuery query);

    /** 查询单本图书，不存在时抛 404 业务异常 */
    Book getBook(Long id);

    /** 新增，返回新主键 */
    Long createBook(Book book);

    /** 修改 */
    void updateBook(Book book);

    /** 删除单本 */
    void deleteBook(Long id);

    /** 批量删除 */
    void deleteBatch(List<Long> ids);

    /** 全部分类（用于前端筛选下拉框） */
    List<String> listCategories();
}
