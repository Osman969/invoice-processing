package it.arrive.invoicesystem.invoice.transformer

import it.arrive.invoicesystem.invoice.ModelStubs
import it.arrive.invoicesystem.invoice.dto.InvoiceDto
import it.arrive.invoicesystem.lineitem.dto.BasicLineItemDto
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse
import it.arrive.invoicesystem.invoice.model.PaymentMethod
import it.arrive.invoicesystem.lineitem.model.LineItem
import org.modelmapper.ModelMapper
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class InvoiceTransformerTest extends Specification {

    def stubs
    def transformer

    def setup() {
        stubs = new ModelStubs()
        transformer = new InvoiceTransformer( new ModelMapper() )
    }

    def "toInvoicesResponse maps page of invoices to InvoicesResponse DTO"() {
        given:
            def invoice = stubs.aInvoiceWithItemsAndPayment()
        and:
            Page page = new PageImpl( [invoice], PageRequest.of( 0, 10 ), 1 )
        when:
            InvoicesResponse response = transformer.toInvoicesResponse( page )
        then:
            response.invoices.size() == 1
            InvoiceDto dto = response.invoices[0]
            dto.items.size() == 2
            dto.totalInvoicePrice == new BigDecimal( "40.0" )
            dto.paymentInfo.amount == dto.totalInvoicePrice
            dto.paymentInfo.paymentMethod == invoice.paymentInfo.paymentMethod
            dto.paymentInfo.transactionDateTime == invoice.paymentInfo.transactionDateTime
            response.currentPage == 0
            response.totalPages == 1
            response.totalElements == 1
    }

    def "toInvoiceDto maps invoice with no items and no payment info"() {
        given:
            def invoice = stubs.anEmptyInvoice()
        when:
            def dto = transformer.toInvoiceDto( invoice )
        then:
            dto != null
            dto.items.isEmpty()
            dto.totalInvoicePrice == BigDecimal.ZERO
            dto.paymentInfo == null
    }

    def "should create PaymentInfo with correct values from line items and payment method"() {
        given:
            def lineItems = [
                    new LineItem( sku: "SKU1", price: new BigDecimal( "10.00" ), quantity: 2 ),
                    new LineItem( sku: "SKU1", price: new BigDecimal( "10.00" ), quantity: 2 ),
                    new LineItem( sku: "SKU2", price: new BigDecimal( "20.00" ), quantity: 1 )
            ]
        when:
            def result = transformer.toPaymentInfo( PaymentMethod.CASH, lineItems )
        then:
            result.amount == new BigDecimal( "40.00" )
            result.paymentMethod == PaymentMethod.CASH
            result.transactionDateTime != null
    }

    def "ungroupLineItemDtos should return a flat list of LineItems based on quantity"() {
        given:
            def dtos = [
                    new BasicLineItemDto( sku: "SKU1", quantity: 2 ),
                    new BasicLineItemDto( sku: "SKU2", quantity: 3 )
            ]
        when:
            List<LineItem> result = transformer.ungroupLineItemDtos( dtos )
        then:
            result.size() == 5
            result.count { it.sku == "SKU1" } == 2
            result.count { it.sku == "SKU2" } == 3
    }
}