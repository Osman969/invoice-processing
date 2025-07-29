package it.arrive.invoicesystem.lineitem.services.impl;

import it.arrive.invoicesystem.config.PaginationProperties;
import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem;
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse;
import it.arrive.invoicesystem.lineitem.model.LineItem;
import it.arrive.invoicesystem.lineitem.repository.LineItemRepository;
import it.arrive.invoicesystem.lineitem.services.LineItemService;
import it.arrive.invoicesystem.lineitem.transformer.LineItemTransformer;
import it.arrive.invoicesystem.lineitem.validator.ItemValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LineItemServiceImpl implements LineItemService {

    private final ItemValidator validator;
    private final LineItemRepository repository;
    private final LineItemTransformer transformer;
    private final PaginationProperties paginationProperties;

    @Override
    public LineItem createLineItem( InvoiceLineItem lineItem ) {
        return repository.save( transformer.toLineItem( lineItem ) );
    }

    @Override
    public LineItemsResponse getLineItems( int pageNumber ) {
        validator.validatePageNumber( pageNumber );
        int pageSize = paginationProperties.getDefaultPageSize();
        Pageable pageable = PageRequest.of(
                pageNumber,
                pageSize
        );
        Page<LineItem> itemPage = repository.findAll( pageable );
        return transformer.toLineItemsResponse( itemPage );
    }

    @Override
    public Optional<LineItem> getLineItemBySkyCode( String sku ) {
        return repository.findBySku( sku );
    }

    @Override
    public int decrementQuantityBySku( String sku, long decrement ) {
        return repository.decrementQuantityBySku( sku, BigDecimal.valueOf( decrement ) );
    }
}
