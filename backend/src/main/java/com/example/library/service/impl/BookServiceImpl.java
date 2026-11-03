package com.example.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.library.common.BusinessException;
import com.example.library.common.PageResult;
import com.example.library.dto.BookQuery;
import com.example.library.entity.Book;
import com.example.library.mapper.BookMapper;
import com.example.library.service.BookService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;

/**
 * 图书业务实现
 */
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private static final long DEFAULT_PAGE_SIZE = 10L;
    private static final long MAX_PAGE_SIZE = 100L;

    @Override
    public PageResult<Book> pageBooks(BookQuery query) {
        long pageNum = (query.getPageNum() == null || query.getPageNum() < 1) ? 1L : query.getPageNum();
        long pageSize = (query.getPageSize() == null || query.getPageSize() < 1)
                ? DEFAULT_PAGE_SIZE : Math.min(query.getPageSize(), MAX_PAGE_SIZE);

        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(query.getKeyword())) {
            String keyword = query.getKeyword().trim();
            wrapper.and(w -> w.like(Book::getTitle, keyword)
                    .or().like(Book::getAuthor, keyword)
                    .or().like(Book::getIsbn, keyword));
        }
        if (StringUtils.hasText(query.getCategory())) {
            wrapper.eq(Book::getCategory, query.getCategory().trim());
        }
        if (query.getStatus() != null) {
            wrapper.eq(Book::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Book::getId);

        Page<Book> page = baseMapper.selectPage(new Page<>(pageNum, pageSize), wrapper);
        return PageResult.of(page);
    }

    @Override
    public Book getBook(Long id) {
        Book book = getById(id);
        if (book == null) {
            throw new BusinessException(404, "图书不存在：id=" + id);
        }
        return book;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createBook(Book book) {
        validateCopies(book);
        if (exists(new LambdaQueryWrapper<Book>().eq(Book::getIsbn, book.getIsbn()))) {
            throw new BusinessException(409, "ISBN 已存在：" + book.getIsbn());
        }
        book.setId(null);
        if (book.getStatus() == null) {
            book.setStatus(1);
        }
        save(book);
        return book.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateBook(Book book) {
        if (book.getId() == null) {
            throw new BusinessException(400, "缺少图书 id，无法修改");
        }
        if (getById(book.getId()) == null) {
            throw new BusinessException(404, "图书不存在：id=" + book.getId());
        }
        validateCopies(book);
        Long duplicated = baseMapper.selectCount(new LambdaQueryWrapper<Book>()
                .eq(Book::getIsbn, book.getIsbn())
                .ne(Book::getId, book.getId()));
        if (duplicated != null && duplicated > 0) {
            throw new BusinessException(409, "ISBN 已被其它图书占用：" + book.getIsbn());
        }
        updateById(book);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBook(Long id) {
        if (getById(id) == null) {
            throw new BusinessException(404, "图书不存在：id=" + id);
        }
        removeById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteBatch(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException(400, "请先选择要删除的图书");
        }
        removeByIds(ids);
    }

    @Override
    public List<String> listCategories() {
        List<Object> values = baseMapper.selectObjs(new QueryWrapper<Book>()
                .select("DISTINCT category")
                .isNotNull("category")
                .orderByAsc("category"));
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .toList();
    }

    /** 可借数量不能超过馆藏总数 */
    private void validateCopies(Book book) {
        if (book.getAvailableCopies() != null && book.getTotalCopies() != null
                && book.getAvailableCopies() > book.getTotalCopies()) {
            throw new BusinessException(400, "可借数量不能大于馆藏总数");
        }
    }
}
