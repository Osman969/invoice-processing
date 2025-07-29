package it.arrive.invoicesystem.lineitem.transformer;

import it.arrive.invoicesystem.lineitem.dto.BasicLineItemWithDescAndPrice;
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse;
import it.arrive.invoicesystem.lineitem.model.LineItem;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class LineItemTransformer {

    private final ModelMapper modelMapper;

    public LineItemsResponse toLineItemsResponse( Page<LineItem> page ) {
        List<BasicLineItemWithDescAndPrice> lineItemDtos = page.getContent()
                .stream()
                .map( this::toLineItemDto )
                .toList();

        return LineItemsResponse.builder()
                .items( lineItemDtos )
                .currentPage( page.getNumber() )
                .totalPages( page.getTotalPages() )
                .totalElements( page.getTotalElements() )
                .build();
    }

    public LineItem toLineItem( BasicLineItemWithDescAndPrice lineItem ) {
        return modelMapper.map( lineItem, LineItem.class );
    }

    private BasicLineItemWithDescAndPrice toLineItemDto( LineItem lineItem ) {
        return modelMapper.map( lineItem, BasicLineItemWithDescAndPrice.class );
    }
}