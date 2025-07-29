package it.arrive.invoicesystem.invoice.model;

import it.arrive.invoicesystem.lineitem.model.LineItem;
import it.arrive.invoicesystem.common.model.BaseEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table ( name = "invoices" )
@Getter
@Setter
public class Invoice extends BaseEntity {

    @Column ( nullable = false )
    private String customerEmail;

    @Enumerated ( EnumType.STRING )
    private InvoicePaymentStatus invoicePaymentStatus = InvoicePaymentStatus.PENDING;

    @ManyToMany
    private List<LineItem> items = new ArrayList<>();

    @OneToOne ( cascade = CascadeType.ALL, orphanRemoval = true )
    @JoinColumn ( name = "payment_info_id" )
    private PaymentInfo paymentInfo;

    public void addItem( LineItem item ) {
        items.add( item );
    }
}