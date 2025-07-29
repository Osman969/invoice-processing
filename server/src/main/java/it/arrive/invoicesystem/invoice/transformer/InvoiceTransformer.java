package it.arrive.invoicesystem.invoice.transformer;

import it.arrive.invoicesystem.invoice.dto.InvoiceDto;
import it.arrive.invoicesystem.lineitem.dto.BasicLineItemDto;
import it.arrive.invoicesystem.invoice.dto.InvoiceRequest;
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse;
import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem;
import it.arrive.invoicesystem.invoice.dto.PaymentInfoDto;
import it.arrive.invoicesystem.invoice.model.Invoice;
import it.arrive.invoicesystem.invoice.model.PaymentInfo;
import it.arrive.invoicesystem.invoice.model.PaymentMethod;
import it.arrive.invoicesystem.lineitem.model.LineItem;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component
@RequiredArgsConstructor
public class InvoiceTransformer {

    private final ModelMapper modelMapper;

    public InvoicesResponse toInvoicesResponse( Page<Invoice> page ) {
        List<InvoiceDto> invoiceDtos = page.getContent()
                .stream()
                .map( this::toInvoiceDto )
                .toList();

        return InvoicesResponse.builder()
                .invoices( invoiceDtos )
                .currentPage( page.getNumber() )
                .totalPages( page.getTotalPages() )
                .totalElements( page.getTotalElements() )
                .build();
    }

    public PaymentInfo toPaymentInfo( PaymentMethod paymentMethod, List<LineItem> items ) {
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setAmount( calculateTotalPrice( items ) );
        paymentInfo.setPaymentMethod( paymentMethod );
        paymentInfo.setTransactionDateTime( LocalDateTime.now() );
        return paymentInfo;
    }

    public InvoiceDto toInvoiceDto( Invoice invoice ) {
        List<InvoiceLineItem> groupedItems = groupLineItems( invoice.getItems() );
        BigDecimal totalInvoiceAmount = calculateTotalPriceSum( groupedItems );

        InvoiceDto dto = modelMapper.map( invoice, InvoiceDto.class );
        dto.setItems( groupedItems );
        dto.setTotalInvoicePrice( totalInvoiceAmount );

        mapPaymentInfo( invoice.getPaymentInfo(), totalInvoiceAmount )
                .ifPresent( dto::setPaymentInfo );

        return dto;
    }

    public Invoice toInvoice( InvoiceRequest invoiceRequest ) {
        Invoice invoice = new Invoice();
        invoice.setCustomerEmail( invoiceRequest.getCustomerEmail() );
        invoice.setItems( ungroupLineItemDtos( invoiceRequest.getItems() ) );
        return invoice;
    }

    private List<LineItem> ungroupLineItemDtos( List<BasicLineItemDto> dtos ) {
        return Optional.ofNullable( dtos )
                .orElseGet( List::of )
                .stream()
                .flatMap( dto -> {
                    int quantity = dto.getQuantity();
                    return IntStream.range( 0, quantity )
                            .mapToObj( i -> {
                                LineItem item = new LineItem();
                                item.setSku( dto.getSku() );
                                return item;
                            } );
                } )
                .toList();
    }

    private List<InvoiceLineItem> groupLineItems( List<LineItem> items ) {
        return Optional.ofNullable( items )
                .orElseGet( List::of )
                .stream()
                .collect( Collectors.groupingBy( LineItem::getSku ) )
                .values()
                .stream()
                .map( this::mapToLineItemDto )
                .toList();
    }

    private InvoiceLineItem mapToLineItemDto( List<LineItem> group ) {
        LineItem referenceItem = group.getFirst();
        int quantity = group.size();
        BigDecimal total = calculateTotalPrice( group );

        InvoiceLineItem dto = modelMapper.map( referenceItem, InvoiceLineItem.class );
        dto.setQuantity( quantity );
        dto.setTotalPrice( total );
        return dto;
    }

    private BigDecimal calculateTotalPrice( List<LineItem> items ) {
        return items.stream()
                .map( LineItem::getPrice )
                .reduce( BigDecimal.ZERO, BigDecimal::add );
    }

    private BigDecimal calculateTotalPriceSum( List<InvoiceLineItem> items ) {
        return items.stream()
                .map( InvoiceLineItem::getTotalPrice )
                .reduce( BigDecimal.ZERO, BigDecimal::add );
    }

    private Optional<PaymentInfoDto> mapPaymentInfo( PaymentInfo paymentInfo, BigDecimal overrideAmount ) {
        return Optional.ofNullable( paymentInfo )
                .map( pi -> {
                    PaymentInfoDto dto = modelMapper.map( pi, PaymentInfoDto.class );
                    dto.setAmount( overrideAmount );
                    return dto;
                } );
    }
}
