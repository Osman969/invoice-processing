package it.arrive.invoicesystem.invoice.dto;

import it.arrive.invoicesystem.lineitem.dto.BasicLineItemDto;
import lombok.Data;

import java.util.List;

@Data
public class InvoiceRequest {

    private String customerEmail;
    private List<BasicLineItemDto> items;
}