package it.arrive.invoicesystem.invoice.repository;

import it.arrive.invoicesystem.invoice.model.Invoice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InvoiceRepository extends JpaRepository<Invoice, UUID> {
}
