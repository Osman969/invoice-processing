package it.arrive.invoicesystem.lineitem.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class BasicLineItemWithDescAndPrice extends BasicLineItemDto {

    private BigDecimal price;
    private String description;
}
