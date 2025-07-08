package com.hd.book.common;

import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageResponse<T> {
    private List<T> content; // 当前页数据
    private int number; // 当前页码 (0-indexed)
    private int size; // 每页大小
    private long totalElements; // 总记录数
    private int totalPages; // 总页数
    private boolean first; // 是否是第一页
    private boolean last; // 是否是最后一页
}
