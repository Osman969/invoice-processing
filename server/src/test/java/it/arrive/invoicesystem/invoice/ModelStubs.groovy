package it.arrive.invoicesystem.invoice

import it.arrive.invoicesystem.invoice.dto.InvoiceDto
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse
import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem
import it.arrive.invoicesystem.invoice.dto.PaymentInfoDto
import it.arrive.invoicesystem.invoice.model.Invoice
import it.arrive.invoicesystem.invoice.model.InvoicePaymentStatus
import it.arrive.invoicesystem.invoice.model.PaymentInfo
import it.arrive.invoicesystem.invoice.model.PaymentMethod
import it.arrive.invoicesystem.lineitem.model.LineItem

import java.time.LocalDateTime

class ModelStubs {

    def randomUUID = UUID.randomUUID()

    InvoicesResponse aInvoicesResponse() {

        def item1 = new InvoiceLineItem( sku: "SKU1", price: new BigDecimal( "10.0" ), quantity: 2, totalPrice: new BigDecimal( "20.0" ) )
        def item2 = new InvoiceLineItem( sku: "SKU2", price: new BigDecimal( "20.0" ), quantity: 1, totalPrice: new BigDecimal( "20.0" ) )

        return InvoicesResponse.builder()
                .invoices( [
                        new InvoiceDto(
                                id: randomUUID,
                                customerEmail: "customerEmail",
                                items: [item1, item2],
                                totalInvoicePrice: new BigDecimal( "40.0" ),
                                invoicePaymentStatus: InvoicePaymentStatus.PENDING
                        )
                ] )
                .currentPage( 0 )
                .totalPages( 1 )
                .totalElements( 1 )
                .build()
    }

    Invoice aInvoiceWithItems() {
        def item1 = new LineItem( sku: "SKU1", price: new BigDecimal( "10.0" ) )
        def item2 = new LineItem( sku: "SKU1", price: new BigDecimal( "10.0" ) )
        def item3 = new LineItem( sku: "SKU2", price: new BigDecimal( "20.0" ) )

        return new Invoice(
                id: randomUUID,
                customerEmail: "customerEmail",
                items: [item1, item2, item3]
        )
    }

    Invoice aInvoiceWithItemsAndPayment() {
        def item1 = new LineItem( sku: "SKU1", price: new BigDecimal( "10.0" ) )
        def item2 = new LineItem( sku: "SKU1", price: new BigDecimal( "10.0" ) )
        def item3 = new LineItem( sku: "SKU2", price: new BigDecimal( "20.0" ) )

        return new Invoice(
                id: randomUUID,
                customerEmail: "customerEmail",
                items: [item1, item2, item3],
                paymentInfo: aPaymentInfo()
        )
    }

    Invoice anEmptyInvoice() {
        return new Invoice(
                id: randomUUID,
                items: [],
                paymentInfo: null
        )
    }

    PaymentInfo aPaymentInfo() {
        return new PaymentInfo(
                transactionDateTime: LocalDateTime.now(),
                paymentMethod: PaymentMethod.CASH
        )
    }

    PaymentInfoDto aPaymentInfoDto() {
        return new PaymentInfoDto(
                amount: new BigDecimal( "100.0" ),
                paymentMethod: PaymentMethod.CASH
        )
    }
}
