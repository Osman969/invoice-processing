package it.arrive.invoicesystem.invoice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.arrive.invoicesystem.invoice.dto.InvoiceDto;
import it.arrive.invoicesystem.lineitem.dto.BasicLineItemDto;
import it.arrive.invoicesystem.invoice.dto.InvoiceRequest;
import it.arrive.invoicesystem.invoice.dto.InvoiceResponse;
import it.arrive.invoicesystem.invoice.dto.InvoicesResponse;
import it.arrive.invoicesystem.invoice.dto.PaymentRequest;
import it.arrive.invoicesystem.invoice.model.Invoice;
import it.arrive.invoicesystem.invoice.model.PaymentMethod;
import it.arrive.invoicesystem.invoice.services.InvoiceService;
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

import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith ( SpringRunner.class )
@SpringBootTest
@AutoConfigureMockMvc
public class InvoiceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private InvoiceService service;

    @Test
    public void shouldReturnInvoiceResponse() throws Exception {
        // Given
        UUID invoiceId = UUID.randomUUID();
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId( invoiceId.toString() );
        InvoiceResponse invoiceResponse = InvoiceResponse.builder().invoice( invoiceDto ).build();

        // When / Then
        Mockito.when( service.getInvoiceResponse( invoiceId ) ).thenReturn( invoiceResponse );

        mockMvc.perform( get( "/invoice/{invoiceId}", invoiceId ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.invoice.id" ).value( invoiceId.toString() ) );
    }

    @Test
    public void shouldReturnInvoicesList() throws Exception {
        // Given
        InvoiceDto invoiceDto = new InvoiceDto();
        invoiceDto.setId( UUID.randomUUID().toString() );

        InvoicesResponse response = InvoicesResponse.builder()
                .invoices( List.of( invoiceDto ) )
                .currentPage( 0 )
                .totalPages( 1 )
                .totalElements( 1L )
                .build();

        Mockito.when( service.getInvoices( 0 ) ).thenReturn( response );

        // When / Then
        mockMvc.perform( get( "/invoices?pageNumber=0" ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.invoices", hasSize( 1 ) ) )
                .andExpect( jsonPath( "$.currentPage" ).value( 0 ) );
    }

    @Test
    public void shouldCreateInvoice() throws Exception {
        // Given
        UUID invoiceId = UUID.randomUUID();

        InvoiceRequest request = new InvoiceRequest();
        request.setCustomerEmail( "user@test.com" );
        request.setItems( List.of( new BasicLineItemDto() ) );

        Invoice createdInvoice = new Invoice();
        createdInvoice.setId( invoiceId );

        Mockito.when( service.createInvoice( any( InvoiceRequest.class ) ) ).thenReturn( createdInvoice );

        // When / Then
        mockMvc.perform( post( "/invoice" )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( request ) ) )
                .andExpect( status().isCreated() )
                .andExpect( header().string( "Location", "/invoice/" + invoiceId ) )
                .andExpect( jsonPath( "$.message", containsString( invoiceId.toString() ) ) );
    }

    @Test
    public void shouldAddItemToInvoice() throws Exception {
        UUID invoiceId = UUID.randomUUID();
        String sku = "SKU123";

        // No return value to mock, just verify 200 OK
        mockMvc.perform( post( "/invoice/{invoiceId}/item/{sku}", invoiceId, sku ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( "Item added to invoice successfully" ) );
    }

    @Test
    public void shouldPayInvoice() throws Exception {
        UUID invoiceId = UUID.randomUUID();
        PaymentRequest paymentRequest = new PaymentRequest();
        paymentRequest.setPaymentMethod( PaymentMethod.CASH );

        // No return value to mock
        mockMvc.perform( patch( "/invoice/{invoiceId}/pay", invoiceId )
                        .contentType( MediaType.APPLICATION_JSON )
                        .content( objectMapper.writeValueAsString( paymentRequest ) ) )
                .andExpect( status().isOk() )
                .andExpect( jsonPath( "$.message" ).value( "Payment information updated successfully" ) );
    }
}
