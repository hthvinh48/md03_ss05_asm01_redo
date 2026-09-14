package com.example.asm01.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PageResponse<T> {
    private List<T> items;
    private int page;
    private int size;
    private int totalItems;
    private int totalPages;
    private boolean isLast;
}
