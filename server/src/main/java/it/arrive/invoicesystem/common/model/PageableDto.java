package it.arrive.invoicesystem.common.model;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class PageableDto {

    private int currentPage;
    private int totalPages;
    private long totalElements;
}
