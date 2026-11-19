package com.soracel.library_system.common.pagination;

import com.soracel.library_system.payload.response.PageResponse;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.function.Function;

@NoArgsConstructor
public final class PaginationUtil {

    public static Pageable createPageable(
            int page,
            int size,
            String sortBy,
            String sortDirection) {

        size = Math.min(size, 20);
        size = Math.max(size, 1);

        Sort sort = sortDirection.equalsIgnoreCase("ASC")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();

        return PageRequest.of(page, size, sort);
    }

    public static <T, R> PageResponse<R> convertToPageResponse(
            Page<T> page,
            Function<T, R> mapper) {

        List<R> content = page.getContent()
                .stream()
                .map(mapper)
                .toList();

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast(),
                page.isFirst(),
                page.isEmpty()
        );
    }
}
