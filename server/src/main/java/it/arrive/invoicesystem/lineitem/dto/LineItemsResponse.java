package it.arrive.invoicesystem.lineitem.dto;

import it.arrive.invoicesystem.common.model.PageableDto;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

import java.util.List;

@Getter
@SuperBuilder
public class LineItemsResponse extends PageableDto {

    private List<BasicLineItemWithDescAndPrice> items;
}