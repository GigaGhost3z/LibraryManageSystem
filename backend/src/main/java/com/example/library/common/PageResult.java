package com.example.library.common;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

/**
 * 分页结果
 */
@Data
public class PageResult<T> {

    private long total;
    private long pageNum;
    private long pageSize;
    private long pages;
    private List<T> records;

    public static <T> PageResult<T> of(IPage<T> page) {
        PageResult<T> result = new PageResult<>();
        result.setTotal(page.getTotal());
        result.setPageNum(page.getCurrent());
        result.setPageSize(page.getSize());
        result.setPages(page.getPages());
        result.setRecords(page.getRecords());
        return result;
    }
}
