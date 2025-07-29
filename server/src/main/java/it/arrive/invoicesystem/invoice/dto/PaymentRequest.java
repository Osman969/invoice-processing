package it.arrive.invoicesystem.invoice.dto;

import it.arrive.invoicesystem.invoice.model.PaymentMethod;
import lombok.Data;

@Data
public class PaymentRequest {

    private PaymentMethod paymentMethod;
}
