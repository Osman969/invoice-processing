package it.arrive.invoicesystem.invoice.services.impl

import it.arrive.invoicesystem.common.exceptions.ResourceNotFoundException
import it.arrive.invoicesystem.config.PaginationProperties
import it.arrive.invoicesystem.invoice.ModelStubs
import it.arrive.invoicesystem.invoice.dto.InvoiceRequest
import it.arrive.invoicesystem.invoice.dto.PaymentRequest
import it.arrive.invoicesystem.invoice.exceptions.InvoiceAlreadyPaidException
import it.arrive.invoicesystem.invoice.exceptions.OutOfStockException
import it.arrive.invoicesystem.invoice.model.InvoicePaymentStatus
import it.arrive.invoicesystem.invoice.model.PaymentMethod
import it.arrive.invoicesystem.invoice.repository.InvoiceRepository
import it.arrive.invoicesystem.invoice.transformer.InvoiceTransformer
import it.arrive.invoicesystem.invoice.validator.InvoiceValidator
import it.arrive.invoicesystem.lineitem.dto.BasicLineItemDto
import it.arrive.invoicesystem.lineitem.model.LineItem
import it.arrive.invoicesystem.lineitem.services.LineItemService
import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import spock.lang.Specification

class InvoiceServiceImplTest extends Specification {

    def stubs
    def service
    def validator
    def repository
    def itemService
    def transformer
    def paginationProperties

    def setup() {
        stubs = new ModelStubs()
        itemService = Mock( LineItemService )
        validator = new InvoiceValidator()
        repository = Mock( InvoiceRepository )
        transformer = new InvoiceTransformer( new ModelMapper() )
        paginationProperties = new PaginationProperties( defaultPageSize: 10 )
        service = new InvoiceServiceImpl(
                itemService,
                validator,
                repository,
                transformer,
                paginationProperties
        )
    }

    def "getInvoices returns paginated invoices transformed into response DTO"() {
        given:
            def pageRequest = PageRequest.of( 0, 10, Sort.by( Sort.Direction.DESC, "createdAt" ) )
        when:
            def result = service.getInvoices( 0 )
        then:
            1 * repository.findAll( pageRequest ) >> new PageImpl<>(
                    [stubs.aInvoiceWithItems()],
                    pageRequest,
                    1
            )
            result.invoices.size() == 1
            result.currentPage == 0
            result.totalPages == 1
            result.totalElements == 1
            def dto = result.invoices.get( 0 )
            dto.customerEmail == "customerEmail"
            dto.totalInvoicePrice == new BigDecimal( "40.0" )
            dto.invoicePaymentStatus == InvoicePaymentStatus.PENDING
            dto.items.size() == 2
            dto.items.get( 0 ).sku == "SKU1"
            dto.items.get( 0 ).price == new BigDecimal( "10.0" )
            dto.items.get( 0 ).totalPrice == new BigDecimal( "20.0" )
            dto.items.get( 1 ).sku == "SKU2"
            dto.items.get( 1 ).price == new BigDecimal( "20.0" )
            dto.items.get( 1 ).totalPrice == new BigDecimal( "20.0" )
    }

    def "addItemToInvoice successfully adds item and decrements stock"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def sku = "SKU123"
        and:
            def invoice = stubs.aInvoiceWithItems()
        and:
            def item = new LineItem( sku: sku, quantity: new BigDecimal( "10.0" ) )
        when:
            service.addItemToInvoice( invoiceId, sku )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( invoice )
            1 * itemService.getLineItemBySkyCode( sku ) >> Optional.of( item )
            1 * repository.save( invoice )
            1 * itemService.decrementQuantityBySku( sku, 1 ) >> 1
    }

    def "addItemToInvoice throws ResourceNotFoundException if invoice not found"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def sku = "SKU123"
        when:
            service.addItemToInvoice( invoiceId, sku )
        then:
            1 * repository.findById( invoiceId ) >> Optional.empty()
            def e = thrown( ResourceNotFoundException )
            e.message == "Invoice with ID '%s' not found".formatted( invoiceId )
    }

    def "addItemToInvoice throws InvoiceAlreadyPaidException if invoice is paid"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def sku = "SKU123"
        and:
            def paidInvoice = stubs.aInvoiceWithItems()
            paidInvoice.invoicePaymentStatus = InvoicePaymentStatus.PAID
            paidInvoice.id = invoiceId
        when:
            service.addItemToInvoice( invoiceId, sku )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( paidInvoice )
            def e = thrown( InvoiceAlreadyPaidException )
            e.message == "Invoice with ID '%s' is already paid".formatted( invoiceId )
    }

    def "addItemToInvoice throws ResourceNotFoundException if item not found"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def sku = "SKU123"
        when:
            service.addItemToInvoice( invoiceId, sku )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( stubs.aInvoiceWithItems() )
            1 * itemService.getLineItemBySkyCode( sku ) >> Optional.empty()
            def e = thrown( ResourceNotFoundException )
            e.message == "Item with SKU code '%s' not found".formatted( sku )
    }

    def "addItemToInvoice throws OutOfStockException if item quantity less than 1"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def sku = "SKU123"
        and:
            def item = new LineItem( sku: sku, quantity: new BigDecimal( "0" ) )
        when:
            service.addItemToInvoice( invoiceId, sku )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( stubs.aInvoiceWithItems() )
            1 * itemService.getLineItemBySkyCode( sku ) >> Optional.of( item )
            def e = thrown( OutOfStockException )
            e.message == "Item with SKU code '%s' is out of stock".formatted( sku )
    }

    def "addItemToInvoice throws OutOfStockException if decrementQuantityBySku returns 0"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def sku = "SKU123"
        and:
            def item = new LineItem( sku: sku, quantity: new BigDecimal( "1" ) )
        when:
            service.addItemToInvoice( invoiceId, sku )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( stubs.aInvoiceWithItems() )
            1 * itemService.getLineItemBySkyCode( sku ) >> Optional.of( item )
            1 * itemService.decrementQuantityBySku( sku, 1 ) >> 0
            def e = thrown( OutOfStockException )
            e.message == "Item with SKU code '%s' is out of stock".formatted( sku )
    }

    def "updatePaymentInfoForInvoice successfully updates invoice"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def paymentRequest = new PaymentRequest(
                    paymentMethod: PaymentMethod.CREDIT_CARD
            )
        when:
            service.updatePaymentInfoForInvoice( invoiceId, paymentRequest )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( stubs.aInvoiceWithItems() )
            1 * repository.save( {
                it.invoicePaymentStatus == InvoicePaymentStatus.PAID
            } )
    }

    def "updatePaymentInfoForInvoice throws ResourceNotFoundException if invoice not found"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def paymentRequest = new PaymentRequest(
                    paymentMethod: PaymentMethod.CREDIT_CARD
            )
        when:
            service.updatePaymentInfoForInvoice( invoiceId, paymentRequest )
        then:
            1 * repository.findById( invoiceId ) >> Optional.empty()
            def e = thrown( ResourceNotFoundException )
            e.message == "Invoice with ID '%s' not found".formatted( invoiceId )
    }

    def "updatePaymentInfoForInvoice throws InvoiceAlreadyPaidException if invoice is paid"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def paymentRequest = new PaymentRequest(
                    paymentMethod: PaymentMethod.CREDIT_CARD
            )
        and:
            def paidInvoice = stubs.aInvoiceWithItems()
            paidInvoice.invoicePaymentStatus = InvoicePaymentStatus.PAID
            paidInvoice.id = invoiceId
        when:
            service.updatePaymentInfoForInvoice( invoiceId, paymentRequest )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( paidInvoice )
            def e = thrown( InvoiceAlreadyPaidException )
            e.message == "Invoice with ID '%s' is already paid".formatted( invoiceId )
    }

    def "updatePaymentInfoForInvoice throws IllegalArgumentException if invoice has no associated items"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def paymentRequest = new PaymentRequest(
                    paymentMethod: PaymentMethod.CREDIT_CARD
            )
        and:
            def invoice = stubs.aInvoiceWithItems()
            invoice.items = []
        when:
            service.updatePaymentInfoForInvoice( invoiceId, paymentRequest )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( invoice )
            def e = thrown( IllegalArgumentException )
            e.message == "Invoice must have at least one item"
    }

    def "getInvoiceResponse returns response with invoice DTO"() {
        given:
            def invoiceId = UUID.randomUUID()
        and:
            def invoice = stubs.aInvoiceWithItems()
            invoice.id = invoiceId
        when:
            def response = service.getInvoiceResponse( invoiceId )
        then:
            1 * repository.findById( invoiceId ) >> Optional.of( invoice )
            response.invoice.customerEmail == "customerEmail"
            response.invoice.totalInvoicePrice == new BigDecimal( "40.0" )
            response.invoice.items.size() == 2
            response.invoice.invoicePaymentStatus == InvoicePaymentStatus.PENDING
    }

    def "getInvoiceResponse throws ResourceNotFoundException if invoice not found"() {
        given:
            def invoiceId = UUID.randomUUID()
        when:
            service.getInvoiceResponse( invoiceId )
        then:
            1 * repository.findById( invoiceId ) >> Optional.empty()
            def e = thrown( ResourceNotFoundException )
            e.message == "Invoice with ID '%s' not found".formatted( invoiceId )
    }

    def "addInvoice saves and returns the invoice"() {
        given:
            def request = new InvoiceRequest(
                    customerEmail: "test@gmail.com"
            )
        when:
            def result = service.createInvoice( request )
        then:
            1 * repository.save( {
                it -> it.customerEmail == "test@gmail.com"
            } ) >> stubs.aInvoiceWithItems()
            result.id == stubs.aInvoiceWithItems().id
    }

    def "createInvoice fetches items, checks stock, and saves invoice"() {
        given:
            def sku1 = "SKU1"
        and:
            def sku2 = "SKU2"
        and:
            def request = new InvoiceRequest(
                    customerEmail: "customer@example.com",
                    items: [
                            new BasicLineItemDto( sku: sku1, quantity: 2 ),
                            new BasicLineItemDto( sku: sku2, quantity: 3 )
                    ]
            )
        and:
            def fetchedItem1 = new LineItem( sku: sku1, price: new BigDecimal( "10.0" ), quantity: 2 )
        and:
            def fetchedItem2 = new LineItem( sku: sku2, price: new BigDecimal( "20.0" ), quantity: 3 )
        and:
            def invoice = stubs.aInvoiceWithItems()
        when:
            def result = service.createInvoice( request )
        then:
            1 * itemService.getLineItemBySkyCode( sku1 ) >> Optional.of( fetchedItem1 )
            1 * itemService.getLineItemBySkyCode( sku2 ) >> Optional.of( fetchedItem2 )
            1 * itemService.decrementQuantityBySku( sku1, 2 ) >> 1
            1 * itemService.decrementQuantityBySku( sku2, 3 ) >> 1
            1 * repository.save( {
                it.customerEmail == "customer@example.com" &&
                        it.items*.sku.containsAll( [sku1, sku2] )
            } ) >> invoice
            result == invoice
    }

    def "createInvoice throws ResourceNotFoundException when item SKU not found"() {
        given:
            def sku = "MISSING_SKU"
        and:
            def request = new InvoiceRequest(
                    customerEmail: "customer@example.com",
                    items: [new BasicLineItemDto( sku: sku, quantity: 1 )]
            )
        when:
            service.createInvoice( request )
        then:
            1 * itemService.getLineItemBySkyCode( sku ) >> Optional.empty()
            def e = thrown( ResourceNotFoundException )
            e.message == "Item with SKU code '${sku}' not found"
    }

    def "createInvoice throws OutOfStockException when item quantity is insufficient"() {
        given:
            def sku = "LOW_STOCK_SKU"
        and:
            def request = new InvoiceRequest(
                    customerEmail: "customer@example.com",
                    items: [new BasicLineItemDto( sku: sku, quantity: 5 )]
            )
        and:
            def fetchedItem = new LineItem( sku: sku, price: new BigDecimal( "10.0" ), quantity: 3 )
        when:
            service.createInvoice( request )
        then:
            1 * itemService.getLineItemBySkyCode( sku ) >> Optional.of( fetchedItem )
            def e = thrown( OutOfStockException )
            e.message == "Item with SKU code '${sku}' is out of stock"
    }

    def "createInvoice throws OutOfStockException when decrementQuantityBySku returns 0"() {
        given:
            def sku = "SKU_FAIL_DECREMENT"
        and:
            def request = new InvoiceRequest(
                    customerEmail: "customer@example.com",
                    items: [new BasicLineItemDto( sku: sku, quantity: 2 )]
            )
        and:
            def fetchedItem = new LineItem( sku: sku, price: new BigDecimal( "10.0" ), quantity: 5 )
        when:
            service.createInvoice( request )
        then:
            1 * itemService.getLineItemBySkyCode( sku ) >> Optional.of( fetchedItem )
            1 * itemService.decrementQuantityBySku( sku, 2 ) >> 0
            def e = thrown( OutOfStockException )
            e.message == "Item with SKU code '${sku}' is out of stock"
    }
}