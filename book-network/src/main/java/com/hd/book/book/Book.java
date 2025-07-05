package com.hd.book.book;

import com.hd.book.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Book extends BaseEntity {

    private String title;
    private String authorName;
    private String isbn; // 编号
    private String synopsis; // 简介
    private String bookCover; // 封面
    private boolean archived; // 是否存档
    private boolean shareable; // 是否共享

}
