package it.arrive.invoicesystem.invoice.controller;

import it.arrive.invoicesystem.common.dto.GenericApiResponse;
import it.arrive.invoicesystem.invoice.dto.InvoiceRequest;
import it.arrive.invoicesystem.invoice.dto.InvoiceResponse;
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse;
import it.arrive.invoicesystem.invoice.dto.PaymentRequest;
import it.arrive.invoicesystem.invoice.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@CrossOrigin ( origins = "http://localhost" )
public class InvoiceController {

    private final InvoiceService service;

    @GetMapping ( "/invoice/{invoiceId}" )
    public ResponseEntity<InvoiceResponse> getInvoice( @PathVariable UUID invoiceId ) {
        return ResponseEntity.ok( service.getInvoiceResponse( invoiceId ) );
    }

    @GetMapping ( "/invoices" )
    public ResponseEntity<InvoicesResponse> getInvoices( @RequestParam ( defaultValue = "0" ) int pageNumber ) {
        return ResponseEntity.ok( service.getInvoices( pageNumber ) );
    }

    @PostMapping ( "/invoice" )
    public ResponseEntity<GenericApiResponse> createInvoice( @RequestBody InvoiceRequest invoiceRequest ) {
        UUID invoiceId = service.createInvoice( invoiceRequest ).getId();
        URI location = URI.create( "/invoice/" + invoiceId );
        GenericApiResponse response = new GenericApiResponse(
                HttpStatus.CREATED.value(),
                "Invoice '%s' had been created successfully".formatted( invoiceId ),
                LocalDateTime.now()
        );
        return ResponseEntity.created( location ).body( response );
    }

    @PostMapping ( "/invoice/{invoiceId}/item/{itemSkuCode}" )
    public ResponseEntity<GenericApiResponse> addItemToInvoice( @PathVariable UUID invoiceId, @PathVariable String itemSkuCode ) {
        service.addItemToInvoice( invoiceId, itemSkuCode );
        return ResponseEntity.ok(
                new GenericApiResponse(
                        HttpStatus.OK.value(),
                        "Item added to invoice successfully",
                        LocalDateTime.now()
                )
        );
    }

    @PatchMapping ( "/invoice/{invoiceId}/pay" )
    public ResponseEntity<GenericApiResponse> payInvoice( @PathVariable UUID invoiceId, @RequestBody PaymentRequest paymentRequest ) {
        service.updatePaymentInfoForInvoice( invoiceId, paymentRequest );
        GenericApiResponse response = new GenericApiResponse(
                HttpStatus.OK.value(),
                "Payment information updated successfully",
                LocalDateTime.now()
        );
        return ResponseEntity.ok( response );
    }
}
