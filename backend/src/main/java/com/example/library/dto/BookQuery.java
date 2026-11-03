package com.example.library.dto;

import lombok.Data;

/**
 * 图书列表查询条件
 */
@Data
public class BookQuery {

    private Long pageNum = 1L;

    private Long pageSize = 10L;

    /** 关键字：模糊匹配 书名 / 作者 / ISBN */
    private String keyword;

    private String category;

    /** 1-在架 0-下架，null 表示不限 */
    private Integer status;
}
