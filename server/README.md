
# Invoice Processing System - Backend API Documentation

## InvoiceController

Spring REST controller managing invoice operations including retrieval, creation, adding items, and payments.

```java
@CrossOrigin(origins = "http://localhost")
@RestController
@RequiredArgsConstructor
public class InvoiceController {
    // ...
}
```

***

## **GET /invoice/{invoiceId}**

***

Retrieve invoice details by invoice ID.

- **Parameters:**  
  - `invoiceId` (UUID path variable)

- **Returns:**  
  - `InvoiceResponse` JSON with invoice data

***

## **GET /invoices?pageNumber=0**

***

Fetch paginated list of invoices.

- **Query Parameters:**  
  - `pageNumber` (int, optional, default = 0)

- **Returns:**  
  - `InvoicesResponse` JSON with list of invoices for the page

***

## **POST /invoice**

***

Create a new invoice.

- **Request Body:**  
  - `InvoiceRequest` JSON payload

- **Response:**  
  - HTTP 201 Created with Location header `/invoice/{invoiceId}`
  - `GenericApiResponse` with creation confirmation message

***

## **POST /invoice/{invoiceId}/item/{itemSkuCode}**

***

Add a line item to an existing invoice.

- **Parameters:**  
  - `invoiceId` (UUID path variable)  
  - `itemSkuCode` (string path variable)

- **Response:**  
  - HTTP 200 OK  
  - `GenericApiResponse` confirming the item addition

***

## **PATCH /invoice/{invoiceId}/pay**

***

Update payment information for an invoice.

- **Parameters:**  
  - `invoiceId` (UUID path variable)

- **Request Body:**  
  - `PaymentRequest` JSON payload with payment details

- **Response:**  
  - HTTP 200 OK  
  - `GenericApiResponse` confirming payment update

---

## ItemController

Spring REST controller managing invoice line items retrieval and creation.

```java
@CrossOrigin(origins = "http://localhost")
@RestController
@RequiredArgsConstructor
public class ItemController {
    // ...
}
```

***

## **GET /lineItems?pageNumber=0**

***

Fetch paginated list of invoice line items.

- **Query Parameters:**  
  - `pageNumber` (int, optional, default = 0)

- **Returns:**  
  - `LineItemsResponse` JSON with list of line items for the page

***

## **POST /lineItem**

***

Create a new line item.

- **Request Body:**  
  - `InvoiceLineItem` JSON payload

- **Response:**  
  - HTTP 201 Created with Location header `/lineItem/{sku}`
  - `GenericApiResponse` confirming line item creation

---

## CORS Configuration

***

- Allows cross-origin requests from `http://localhost` for frontend-backend integration during development.
