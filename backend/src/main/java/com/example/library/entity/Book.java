package com.example.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 图书信息
 */
@Data
@TableName("book")
public class Book {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "ISBN 不能为空")
    @Size(max = 20, message = "ISBN 长度不能超过 20")
    private String isbn;

    @NotBlank(message = "书名不能为空")
    @Size(max = 200, message = "书名长度不能超过 200")
    private String title;

    @NotBlank(message = "作者不能为空")
    @Size(max = 100, message = "作者长度不能超过 100")
    private String author;

    @Size(max = 100, message = "出版社长度不能超过 100")
    private String publisher;

    @Size(max = 50, message = "分类长度不能超过 50")
    private String category;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDate;

    @DecimalMin(value = "0", message = "定价不能为负数")
    private BigDecimal price;

    @NotNull(message = "馆藏总数不能为空")
    @Min(value = 0, message = "馆藏总数不能为负数")
    private Integer totalCopies;

    @NotNull(message = "可借数量不能为空")
    @Min(value = 0, message = "可借数量不能为负数")
    private Integer availableCopies;

    @Size(max = 50, message = "馆藏位置长度不能超过 50")
    private String location;

    /** 1-在架 0-下架 */
    private Integer status;

    @Size(max = 255, message = "备注长度不能超过 255")
    private String remark;

    /** 由数据库 DEFAULT CURRENT_TIMESTAMP 自动填充 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    /** 由数据库 ON UPDATE CURRENT_TIMESTAMP 自动维护 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;
}
