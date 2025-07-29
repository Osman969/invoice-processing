package it.arrive.invoicesystem.lineitem.controller;

import it.arrive.invoicesystem.common.dto.GenericApiResponse;
import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem;
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse;
import it.arrive.invoicesystem.lineitem.services.LineItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@CrossOrigin ( origins = "http://localhost" )
public class ItemController {

    private final LineItemService service;

    @GetMapping ( "/lineItems" )
    public ResponseEntity<LineItemsResponse> getLineItems( @RequestParam ( defaultValue = "0" ) int pageNumber ) {
        return ResponseEntity.ok( service.getLineItems( pageNumber ) );
    }

    @PostMapping ( "/lineItem" )
    public ResponseEntity<GenericApiResponse> createLineItem( @RequestBody InvoiceLineItem lineItem ) {
        String sku = service.createLineItem( lineItem ).getSku();
        URI location = URI.create( "/lineItem/" + sku );
        GenericApiResponse response = new GenericApiResponse(
                HttpStatus.CREATED.value(),
                "Line Item with SKU '%s' had been created successfully".formatted( sku ),
                LocalDateTime.now()
        );
        return ResponseEntity.created( location ).body( response );
    }
}
