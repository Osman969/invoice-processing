package it.arrive.invoicesystem.lineitem.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.arrive.invoicesystem.invoice.dto.InvoiceLineItem;
import it.arrive.invoicesystem.lineitem.dto.LineItemsResponse;
import it.arrive.invoicesystem.lineitem.model.LineItem;
import it.arrive.invoicesystem.lineitem.services.LineItemService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.Collections;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LineItemService lineItemService;

    @Test
    public void shouldReturnLineItemsList() throws Exception {
        LineItemsResponse mockResponse = LineItemsResponse.builder()
                .items( Collections.emptyList() )
                .currentPage( 0 )
                .totalPages( 1 )
                .totalElements( 1L )
                .build();

        Mockito.when( lineItemService.getLineItems( 0 ) ).thenReturn( mockResponse );

        mockMvc.perform( get( "/lineItems?pageNumber=0" ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.items", hasSize( 0 ) ) )
                .andExpect( jsonPath( "$.currentPage" ).value( 0 ) );
    }

    @Test
    public void shouldCreateLineItem() throws Exception {
        InvoiceLineItem request = new InvoiceLineItem();
        request.setDescription( "Test Item" );
        request.setPrice( BigDecimal.ONE );

        LineItem savedItem = new LineItem();
        savedItem.setSku( "SKU123" );
        savedItem.setDescription( "Test Item" );
        savedItem.setPrice( BigDecimal.ONE );

        Mockito.when( lineItemService.createLineItem( Mockito.any( InvoiceLineItem.class ) ) )
                .thenReturn( savedItem );

        mockMvc.perform( post( "/lineItem" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( request ) ) )
                .andExpect( status().isCreated() )
                .andExpect( header().string( "Location", "/lineItem/SKU123" ) )
                .andExpect( jsonPath( "$.message", containsString( "SKU123" ) ) );
    }
}
