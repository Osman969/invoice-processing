package it.arrive.invoicesystem.invoice.dto;

import it.arrive.invoicesystem.common.model.PageableDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class InvoicesResponse extends PageableDto {

    private List<InvoiceDto> invoices;
}