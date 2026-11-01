package com.soracel.library_system.payload.request;

import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookSearchRequest {

    private String searchTerm;
    private Long genreId;
    private Boolean availableOnly;
    private Integer page = 0;
    private Integer pageSize = 20;
    private String sortBy="createdAt";
    private String sortDirection="DESC";
}
