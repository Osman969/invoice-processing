package it.arrive.invoicesystem.lineitem.services;

import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem;
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse;
import it.arrive.invoicesystem.lineitem.model.LineItem;

import java.util.Optional;

public interface LineItemService {

    LineItem createLineItem( InvoiceLineItem lineItem );

    LineItemsResponse getLineItems( int pageNumber );

    Optional<LineItem> getLineItemBySkyCode( String sku );

    int decrementQuantityBySku( String sku, long decrement );
}
