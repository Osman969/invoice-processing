package it.arrive.invoicesystem.lineitem.services.impl

import it.arrive.invoicesystem.config.PaginationProperties
import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse
import it.arrive.invoicesystem.lineitem.model.LineItem
import it.arrive.invoicesystem.lineitem.repository.LineItemRepository
import it.arrive.invoicesystem.lineitem.transformer.LineItemTransformer
import it.arrive.invoicesystem.lineitem.validator.ItemValidator
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class LineItemServiceImplTest extends Specification {

    def service
    def validator
    def repository
    def transformer
    def paginationProperties

    def setup() {
        validator = Mock( ItemValidator )
        repository = Mock( LineItemRepository )
        transformer = Mock( LineItemTransformer )
        paginationProperties = new PaginationProperties( defaultPageSize: 10 )
        service = new LineItemServiceImpl(
                validator,
                repository,
                transformer,
                paginationProperties
        )
    }

    def "createLineItem saves transformed item to repository"() {
        given:
            def invoiceLineItem = new InvoiceLineItem( sku: "SKU1", price: new BigDecimal( "10.0" ), quantity: new BigDecimal( "5" ) )
            def lineItem = new LineItem( sku: "SKU1", price: new BigDecimal( "10.0" ), quantity: new BigDecimal( "5" ) )
        when:
            def result = service.createLineItem( invoiceLineItem )
        then:
            1 * transformer.toLineItem( invoiceLineItem ) >> lineItem
            1 * repository.save( lineItem ) >> lineItem
            result == lineItem
    }

    def "getLineItems returns paginated response"() {
        given:
            def pageNumber = 0
            def pageRequest = PageRequest.of( 0, 10 )
            def lineItems = [new LineItem( sku: "SKU1", quantity: new BigDecimal( "3" ) )]
            def page = new PageImpl<>( lineItems, pageRequest, 1 )
            def response = Mock( LineItemsResponse )
        when:
            def result = service.getLineItems( pageNumber )
        then:
            1 * validator.validatePageNumber( pageNumber )
            1 * repository.findAll( pageRequest ) >> page
            1 * transformer.toLineItemsResponse( page ) >> response
            result == response
    }

    def "getLineItemBySkyCode returns item from repository"() {
        given:
            def sku = "SKU1"
            def item = Optional.of( new LineItem( sku: sku ) )
        when:
            def result = service.getLineItemBySkyCode( sku )
        then:
            1 * repository.findBySku( sku ) >> item
            result == item
    }

    def "decrementQuantityBySku delegates to repository"() {
        given:
            def sku = "SKU1"
            def decrement = 2
        when:
            def result = service.decrementQuantityBySku( sku, decrement )
        then:
            1 * repository.decrementQuantityBySku( sku, new BigDecimal( "2" ) ) >> 1
            result == 1
    }
}
