package it.arrive.invoicesystem.invoice.services.impl;

import it.arrive.invoicesystem.common.exceptions.ResourceNotFoundException;
import it.arrive.invoicesystem.config.PaginationProperties;
import it.arrive.invoicesystem.invoice.dto.InvoiceRequest;
import it.arrive.invoicesystem.invoice.dto.InvoiceResponse;
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse;
import it.arrive.invoicesystem.invoice.dto.PaymentRequest;
import it.arrive.invoicesystem.invoice.exceptions.InvoiceAlreadyPaidException;
import it.arrive.invoicesystem.invoice.exceptions.OutOfStockException;
import it.arrive.invoicesystem.invoice.model.Invoice;
import it.arrive.invoicesystem.invoice.model.InvoicePaymentStatus;
import it.arrive.invoicesystem.invoice.model.PaymentMethod;
import it.arrive.invoicesystem.invoice.repository.InvoiceRepository;
import it.arrive.invoicesystem.invoice.services.InvoiceService;
import it.arrive.invoicesystem.invoice.transformer.InvoiceTransformer;
import it.arrive.invoicesystem.invoice.validator.InvoiceValidator;
import it.arrive.invoicesystem.lineitem.model.LineItem;
import it.arrive.invoicesystem.lineitem.services.LineItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class InvoiceServiceImpl implements InvoiceService {

    private static final String ITEM_NOT_FOUND = "Item with SKU code '%s' not found";
    private static final String INVOICE_NOT_FOUND_ERROR = "Invoice with ID '%s' not found";
    private static final String ALREADY_PAID_ERROR = "Invoice with ID '%s' is already paid";
    private static final String OUT_OF_STOCK_ERROR = "Item with SKU code '%s' is out of stock";

    private final LineItemService itemService;
    private final InvoiceValidator validator;
    private final InvoiceRepository repository;
    private final InvoiceTransformer transformer;
    private final PaginationProperties paginationProperties;

    @Override
    @Transactional
    public Invoice createInvoice( InvoiceRequest invoiceRequest ) {
        validator.validateCustomerEmail( invoiceRequest.getCustomerEmail() );
        Invoice invoice = transformer.toInvoice( invoiceRequest );

        Map<String, Long> skuCountMap = invoice.getItems().stream()
                .collect( Collectors.groupingBy( LineItem::getSku, Collectors.counting() ) );

        Map<String, LineItem> dbItemMap = getSkuLineItemMap( skuCountMap );

        List<LineItem> resolvedItems = invoice.getItems().stream()
                .map( item -> dbItemMap.get( item.getSku() ) )
                .collect( Collectors.toList() );
        invoice.setItems( resolvedItems );
        Invoice saved = repository.save( invoice );
        log.debug( "Created invoice with ID '{}'.", saved.getId() );
        return saved;
    }

    @Override
    @Transactional
    public void addItemToInvoice( UUID invoiceId, String itemSkuCode ) {
        validator.validateItemSkuCode( itemSkuCode );
        Invoice invoice = getInvoice( invoiceId );
        LineItem item = getLineItem( itemSkuCode );
        invoice.addItem( item );
        repository.save( invoice );
        if ( itemService.decrementQuantityBySku( itemSkuCode, 1 ) == 0 ) {
            log.error( "Item with SKU '{}' is out of stock after decrement.", itemSkuCode );
            throw new OutOfStockException( OUT_OF_STOCK_ERROR.formatted( itemSkuCode ) );
        }
        log.debug( "Added item with SKU '{}' to invoice '{}'.", itemSkuCode, invoiceId );
    }

    @Override
    public void updatePaymentInfoForInvoice( UUID invoiceId, PaymentRequest paymentRequest ) {
        PaymentMethod paymentMethod = paymentRequest.getPaymentMethod();
        validator.validatePaymentMethod( paymentMethod );
        Invoice invoice = repository.findById( invoiceId )
                .orElseThrow( () -> new ResourceNotFoundException( INVOICE_NOT_FOUND_ERROR.formatted( invoiceId ) ) );
        throwExceptionIfAlreadyPaidInvoice( invoice );
        List<LineItem> items = invoice.getItems();
        validator.validateInvoiceItems( items );
        invoice.setPaymentInfo( transformer.toPaymentInfo( paymentMethod, items ) );
        invoice.setInvoicePaymentStatus( InvoicePaymentStatus.PAID );
        repository.save( invoice );
        log.debug( "Invoice '{}' had been paid successfully.", invoiceId );
    }

    @Override
    public InvoiceResponse getInvoiceResponse( UUID invoiceId ) {
        Invoice invoice = repository.findById( invoiceId )
                .orElseThrow( () -> new ResourceNotFoundException( INVOICE_NOT_FOUND_ERROR.formatted( invoiceId ) ) );
        return InvoiceResponse.builder()
                .invoice( transformer.toInvoiceDto( invoice ) )
                .build();
    }

    @Override
    public InvoicesResponse getInvoices( int pageNumber ) {
        validator.validatePageNumber( pageNumber );
        int pageSize = paginationProperties.getDefaultPageSize();
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize,
                Sort.by( Sort.Direction.DESC, "createdAt" )
        );
        Page<Invoice> invoicePage = repository.findAll( pageable );
        return transformer.toInvoicesResponse( invoicePage );
    }

    private Invoice getInvoice( UUID invoiceId ) {
        return repository.findById( invoiceId )
                .map( invoice -> {
                    throwExceptionIfAlreadyPaidInvoice( invoice );
                    return invoice;
                } )
                .orElseThrow( () -> new ResourceNotFoundException( INVOICE_NOT_FOUND_ERROR.formatted( invoiceId ) ) );
    }

    private LineItem getLineItem( String itemSkuCode ) {
        return itemService.getLineItemBySkyCode( itemSkuCode )
                .map( item -> {
                    if ( item.getQuantity() < 1 ) {
                        log.error( "Item with SKU '{}' is out of stock", itemSkuCode );
                        throw new OutOfStockException( OUT_OF_STOCK_ERROR.formatted( itemSkuCode ) );
                    }
                    return item;
                } )
                .orElseThrow( () -> new ResourceNotFoundException( ITEM_NOT_FOUND.formatted( itemSkuCode ) ) );
    }

    private void throwExceptionIfAlreadyPaidInvoice( Invoice invoice ) {
        if ( invoice.getInvoicePaymentStatus() == InvoicePaymentStatus.PAID ) {
            log.error( "Attempt to modify already paid invoice with ID '{}'", invoice.getId() );
            throw new InvoiceAlreadyPaidException( ALREADY_PAID_ERROR.formatted( invoice.getId() ) );
        }
    }

    private Map<String, LineItem> getSkuLineItemMap( Map<String, Long> skuCountMap ) {
        return skuCountMap.entrySet().stream()
                .collect( Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> {
                            String itemSkuCode = entry.getKey();
                            long count = entry.getValue();
                            LineItem lineItem = itemService.getLineItemBySkyCode( itemSkuCode )
                                    .orElseThrow( () -> new ResourceNotFoundException( ITEM_NOT_FOUND.formatted( itemSkuCode ) ) );
                            if ( lineItem.getQuantity() < count ) {
                                log.error( "Item with SKU '{}' is out of stock for requested quantity {}", itemSkuCode, count );
                                throw new OutOfStockException( OUT_OF_STOCK_ERROR.formatted( itemSkuCode ) );
                            }
                            if ( itemService.decrementQuantityBySku( itemSkuCode, count ) == 0 ) {
                                log.error( "Item with SKU '{}' is out of stock after decrement", itemSkuCode );
                                throw new OutOfStockException( OUT_OF_STOCK_ERROR.formatted( itemSkuCode ) );
                            }
                            return lineItem;
                        }
                ) );
    }
}
