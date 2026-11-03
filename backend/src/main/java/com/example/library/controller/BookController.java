package com.example.library.controller;

import com.example.library.common.PageResult;
import com.example.library.common.Result;
import com.example.library.dto.BookQuery;
import com.example.library.entity.Book;
import com.example.library.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 图书管理接口
 */
@RestController
@RequestMapping("/api/book")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;

    /** 分页查询：/api/book/page?pageNum=1&pageSize=10&keyword=三体&category=科幻小说&status=1 */
    @GetMapping("/page")
    public Result<PageResult<Book>> page(BookQuery query) {
        return Result.ok(bookService.pageBooks(query));
    }

    /** 分类列表（下拉框用） */
    @GetMapping("/categories")
    public Result<List<String>> categories() {
        return Result.ok(bookService.listCategories());
    }

    /** 详情 */
    @GetMapping("/{id}")
    public Result<Book> detail(@PathVariable Long id) {
        return Result.ok(bookService.getBook(id));
    }

    /** 新增 */
    @PostMapping
    public Result<Long> create(@Valid @RequestBody Book book) {
        return Result.ok("新增成功", bookService.createBook(book));
    }

    /** 修改 */
    @PutMapping
    public Result<Void> update(@Valid @RequestBody Book book) {
        bookService.updateBook(book);
        return Result.ok("修改成功", null);
    }

    /** 批量删除（放在 /{id} 之前，避免被路径变量抢先匹配） */
    @DeleteMapping("/batch")
    public Result<Void> deleteBatch(@RequestBody List<Long> ids) {
        bookService.deleteBatch(ids);
        return Result.ok("批量删除成功", null);
    }

    /** 删除 */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        bookService.deleteBook(id);
        return Result.ok("删除成功", null);
    }
}
