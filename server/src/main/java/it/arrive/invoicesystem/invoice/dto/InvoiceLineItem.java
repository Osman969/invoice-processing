package it.arrive.invoicesystem.invoice.dto;

import it.arrive.invoicesystem.lineitem.dto.BasicLineItemWithDescAndPrice;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class InvoiceLineItem extends BasicLineItemWithDescAndPrice {

    private BigDecimal totalPrice;
}
