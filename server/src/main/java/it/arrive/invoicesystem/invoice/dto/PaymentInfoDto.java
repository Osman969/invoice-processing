package it.arrive.invoicesystem.invoice.dto;

import it.arrive.invoicesystem.invoice.model.PaymentMethod;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PaymentInfoDto {

    private BigDecimal amount;
    private PaymentMethod paymentMethod;
    private LocalDateTime transactionDateTime;
}