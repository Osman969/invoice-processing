package it.arrive.invoicesystem.invoice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table ( name = "payments" )
@Getter
@Setter
public class PaymentInfo {

    @Id
    @GeneratedValue ( strategy = GenerationType.IDENTITY )
    private Long id;

    @Column ( nullable = false )
    private BigDecimal amount;

    @CreationTimestamp
    @Column ( nullable = false, updatable = false )
    private LocalDateTime transactionDateTime;

    @Column ( nullable = false )
    private PaymentMethod paymentMethod;
}