package it.arrive.invoicesystem.invoice.dto;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class InvoiceResponse {

    private InvoiceDto invoice;
}