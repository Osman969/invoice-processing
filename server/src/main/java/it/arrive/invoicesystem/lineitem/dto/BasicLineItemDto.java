package it.arrive.invoicesystem.lineitem.dto;

import lombok.Data;

@Data
public class BasicLineItemDto {

    private String sku;
    private Integer quantity;
}
