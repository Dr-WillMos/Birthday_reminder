package com.Birthday_reminder.vo;

import lombok.Data;

import java.util.List;

/**
 * 分页响应VO
 */
@Data
public class PageVO<T> {
    /**
     * 数据列表
     */
    private List<T> content;
    
    /**
     * 总页数
     */
    private Integer totalPages;
    
    /**
     * 当前页码
     */
    private Integer currentPage;
    
    /**
     * 每页大小
     */
    private Integer pageSize;
    
    /**
     * 总记录数
     */
    private Long total;
    
    public PageVO(List<T> content, Integer totalPages, Integer currentPage, Integer pageSize, Long total) {
        this.content = content;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.total = total;
    }
}
