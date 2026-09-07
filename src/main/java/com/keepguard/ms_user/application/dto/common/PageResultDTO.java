package com.keepguard.ms_user.application.dto.common;

import java.util.List;

public record PageResultDTO<T>(
    List<T> content,
    long totalElements,
    int page,
    int size
) {
    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / size);
    }

    public boolean hasNext() {
        return page < getTotalPages() - 1;
    }

    public boolean hasPrevious() {
        return page > 0;
    }

    public boolean isEmpty() {
        return content.isEmpty();
    }
}
