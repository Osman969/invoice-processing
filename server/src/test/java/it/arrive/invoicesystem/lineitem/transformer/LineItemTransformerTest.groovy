package it.arrive.invoicesystem.lineitem.transformer

import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse
import it.arrive.invoicesystem.lineitem.model.LineItem
import org.modelmapper.ModelMapper
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import spock.lang.Specification

class LineItemTransformerTest extends Specification {

    def modelMapper = new ModelMapper()
    def transformer = new LineItemTransformer( modelMapper )

    def "toLineItemsResponse maps Page<LineItem> to LineItemsResponse correctly"() {
        given:
            def lineItem1 = new LineItem(
                    sku: "SKU001",
                    description: "Item 1",
                    quantity: 5,
                    price: new BigDecimal( "15.00" )
            )
            def lineItem2 = new LineItem(
                    sku: "SKU002",
                    description: "Item 2",
                    quantity: 3,
                    price: new BigDecimal( "10.00" )
            )
            def page = new PageImpl<>( [lineItem1, lineItem2], PageRequest.of( 0, 10 ), 2 )
        when:
            LineItemsResponse response = transformer.toLineItemsResponse( page )
        then:
            response != null
            response.items.size() == 2

            def dto1 = response.items[0]
            dto1.sku == "SKU001"
            dto1.description == "Item 1"
            dto1.quantity == 5
            dto1.price == new BigDecimal( "15.00" )

            def dto2 = response.items[1]
            dto2.sku == "SKU002"
            dto2.description == "Item 2"
            dto2.quantity == 3
            dto2.price == new BigDecimal( "10.00" )

            response.currentPage == 0
            response.totalPages == 1
            response.totalElements == 2
    }

    def "toLineItem maps InvoiceLineItem to LineItem correctly"() {
        given:
            def invoiceItem = new InvoiceLineItem(
                    sku: "SKU123",
                    description: "Transformed item",
                    quantity: 4,
                    price: new BigDecimal( "22.50" )
            )
        when:
            LineItem lineItem = transformer.toLineItem( invoiceItem )
        then:
            lineItem.sku == "SKU123"
            lineItem.description == "Transformed item"
            lineItem.quantity == 4
            lineItem.price == new BigDecimal( "22.50" )
    }
}