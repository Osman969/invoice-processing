package it.arrive.invoicesystem.lineitem.services;

import it.arrive.invoicesystem.lineitem.dto.BasicLineItemWithDescAndPrice;
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse;
import it.arrive.invoicesystem.lineitem.model.LineItem;

import java.util.Optional;

/**
 * Interface for managing line items within the invoice system.
 * Provides methods for creating, retrieving, and modifying line items.
 */
public interface LineItemService {

    /**
     * Creates a new line item based on the provided input.
     * <ul>
     *     <li>Maps the input DTO to the LineItem entity.</li>
     *     <li>Persists the line item in the database.</li>
     * </ul>
     *
     * @param lineItem the {@link BasicLineItemWithDescAndPrice} to create
     * @return the saved {@link LineItem} entity
     */
    LineItem createLineItem( BasicLineItemWithDescAndPrice lineItem );

    /**
     * Retrieves a paginated list of line items.
     * <ul>
     *     <li>Page number must be valid and within bounds.</li>
     *     <li>Page size is configured via application properties.</li>
     * </ul>
     *
     * @param pageNumber the page number to fetch
     * @return {@link LineItemsResponse} containing paginated line items
     */
    LineItemsResponse getLineItems( int pageNumber );

    /**
     * Fetches a line item by SKU code.
     * <ul>
     *     <li>If not found, returns an empty Optional.</li>
     * </ul>
     *
     * @param sku the SKU code of the item
     * @return Optional containing the {@link LineItem} if found
     */
    Optional<LineItem> getLineItemBySkyCode( String sku );

    /**
     * Decrements the quantity of an existing line item by SKU code.
     *
     * @param sku       the SKU of the item
     * @param decrement the quantity to subtract
     * @return the number of affected rows (usually 1 or 0)
     */
    int decrementQuantityBySku( String sku, long decrement );
}
