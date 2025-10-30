package com.inghubs.common.rest.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record PaginationRequest(
    @NotNull
    @Min(0)
    Integer page,
    @NotNull
    @Min(5)
    Integer size,
    String sortBy,
    String direction
) {

}