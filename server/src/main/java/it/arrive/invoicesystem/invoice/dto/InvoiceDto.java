package it.arrive.invoicesystem.invoice.dto;

import it.arrive.invoicesystem.invoice.model.InvoicePaymentStatus;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class InvoiceDto {

    private String id;
    private String customerEmail;
    private LocalDateTime createdAt;
    private LocalDateTime lastUpdatedAt;
    private List<InvoiceLineItem> items;
    private PaymentInfoDto paymentInfo;
    private InvoicePaymentStatus invoicePaymentStatus;
    private BigDecimal totalInvoicePrice;
}
