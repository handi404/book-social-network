package com.hd.book.handler;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 控制 JSON 输出的简洁性，避免不必要的 null 或空值字段
// JsonInclude.Include.NON_EMPTY: 仅当值不为 null 且不为空 (如空字符串、空集合/Map) 时包含
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ExceptionResponse {

    private Integer businessErrorCode; // 错误代码
    private String businessErrorDescription; // 错误描述
    private String error;
    private Set<String> validationErrors;
    private Map<String, String> errors;
}
