import React, { useState, useEffect, useCallback } from "react";
import {
  Dialog,
  DialogTitle,
  DialogContent,
  DialogActions,
  Button,
  MenuItem,
  Select,
  Typography,
  Box,
  Divider,
  CircularProgress,
  Alert,
  TextField,
} from "@mui/material";
import { getAllItems, saveInvoice, payInvoice } from "../services/api";
import PropTypes from "prop-types";

const PLACEHOLDER_SKU_VALUE = "";
const PLACEHOLDER_PAYMENT_METHOD = "";

const PAYMENT_METHODS = [
  { value: "CASH", label: "Cash" },
  { value: "CREDIT_CARD", label: "Credit Card" },
  { value: "DEBIT_CARD", label: "Debit Card" },
  { value: "BANK_TRANSFER", label: "Bank Transfer" },
  { value: "PAYPAL", label: "PayPal" },
  { value: "CHECK", label: "Check" },
  { value: "OTHER", label: "Other" }
];

const Invoices = ({ invoices = [], onAddItemToInvoice, onInvoicePaid }) => {
  const [openAddDialog, setOpenAddDialog] = useState(false);
  const [openItemsDialog, setOpenItemsDialog] = useState(false);
  const [openPayDialog, setOpenPayDialog] = useState(false);

  const [selectedInvoiceId, setSelectedInvoiceId] = useState(null);
  const [items, setItems] = useState([]);
  const [selectedItemSku, setSelectedItemSku] = useState(PLACEHOLDER_SKU_VALUE);
  const [selectedInvoiceItems, setSelectedInvoiceItems] = useState([]);
  const [currentInvoiceTotalPrice, setCurrentInvoiceTotalPrice] = useState(null);

  // paymentAmount will now be pre-filled, so no longer an empty string placeholder
  const [paymentAmount, setPaymentAmount] = useState("");
  const [paymentMethod, setPaymentMethod] = useState(PLACEHOLDER_PAYMENT_METHOD);
  const [payingInvoice, setPayingInvoice] = useState(false);
  const [selectedInvoiceForPayment, setSelectedInvoiceForPayment] = useState(null);

  const [loadingItems, setLoadingItems] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    if (openAddDialog) {
      fetchItems();
      setSelectedItemSku(PLACEHOLDER_SKU_VALUE);
    }
  }, [openAddDialog]);

  const fetchItems = useCallback(async () => {
    setLoadingItems(true);
    setError(null);
    try {
      const response = await getAllItems();
      const validItems = response.data.items?.filter(item => item.sku && typeof item.sku === 'string') || [];
      setItems(validItems);
    } catch (err) {
      console.error("Failed to fetch items:", err);
      setError("Failed to load items. Please try again.");
    } finally {
      setLoadingItems(false);
    }
  }, []);

  const handleOpenAddDialog = (invoiceId) => {
    setSelectedInvoiceId(invoiceId);
    setSelectedItemSku(PLACEHOLDER_SKU_VALUE);
    setOpenAddDialog(true);
  };

  const handleCloseAddDialog = () => {
    setOpenAddDialog(false);
    setSelectedInvoiceId(null);
    setSelectedItemSku(PLACEHOLDER_SKU_VALUE);
    setError(null);
  };

  const handleAdd = async () => {
    if (!selectedInvoiceId || selectedItemSku === PLACEHOLDER_SKU_VALUE) {
      setError("Please select an item to add.");
      return;
    }

    try {
      await onAddItemToInvoice(selectedInvoiceId, selectedItemSku);
      handleCloseAddDialog();
    } catch (err) {
      console.error("Failed to add item:", err);
      setError("Failed to add item. Please try again.");
    }
  };

  const handleViewItems = (invoice) => {
    setSelectedInvoiceItems(invoice.items || []);
    setCurrentInvoiceTotalPrice(invoice.totalInvoicePrice);
    setOpenItemsDialog(true);
  };

  const handleCloseItemsDialog = () => {
    setOpenItemsDialog(false);
    setSelectedInvoiceItems([]);
    setCurrentInvoiceTotalPrice(null);
  };

  const handleOpenPayDialog = (invoice) => {
    setSelectedInvoiceForPayment(invoice);
    // Automatically set paymentAmount to totalInvoicePrice
    setPaymentAmount(invoice.totalInvoicePrice?.toFixed(2) || '0.00');
    setPaymentMethod(PLACEHOLDER_PAYMENT_METHOD);
    setError(null);
    setOpenPayDialog(true);
  };

  const handleClosePayDialog = () => {
    setOpenPayDialog(false);
    setSelectedInvoiceForPayment(null);
    setPaymentAmount(""); // Reset to empty when closing
    setPaymentMethod(PLACEHOLDER_PAYMENT_METHOD);
    setError(null);
    setPayingInvoice(false);
  };

  const handlePayInvoice = async () => {
    if (!selectedInvoiceForPayment || paymentMethod === PLACEHOLDER_PAYMENT_METHOD) {
      setError("Please select a payment method.");
      return;
    }

    const amountNum = parseFloat(selectedInvoiceForPayment.totalInvoicePrice); // Use totalInvoicePrice
    if (isNaN(amountNum) || amountNum <= 0) {
      setError("Invoice total amount is invalid. Cannot process payment.");
      return;
    }

    setPayingInvoice(true);
    setError(null);

    try {
      const paymentInfoPayload = {
        paymentMethod: paymentMethod
      };

      // Corrected: Use selectedInvoiceForPayment.id directly
      const response = await payInvoice(selectedInvoiceForPayment.id, paymentInfoPayload);

      const updatedInvoiceForFrontend = {
        ...selectedInvoiceForPayment,
        invoicePaymentStatus: "PAID",
        paymentInfo: {
            ...paymentInfoPayload,
            transactionDateTime: new Date().toISOString(), // Client-side timestamp for immediate UI update
        },
      };

      if (onInvoicePaid) {
        onInvoicePaid(updatedInvoiceForFrontend);
      }

      handleClosePayDialog();
    } catch (err) {
      console.error("Failed to process payment:", err);
      // Check if err has a response data for more specific error from backend
      const errorMessage = err.response?.data?.message || "Failed to process payment. Please try again.";
      setError(errorMessage);
    } finally {
      setPayingInvoice(false);
    }
  };

  const formatDateTime = (dateTimeString) => {
    if (!dateTimeString) return "N/A";
    try {
      const date = new Date(dateTimeString);
      return date.toLocaleString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric',
        hour: '2-digit',
        minute: '2-digit',
        hour12: true
      });
    } catch (e) {
      console.error("Error parsing date:", dateTimeString, e);
      return "Invalid Date";
    }
  };

  return (
    <Box>
      {invoices.map((invoice) => (
        <Box
          key={invoice.id}
          sx={{
            border: "1px solid #ccc",
            borderRadius: 2,
            p: 2,
            my: 2,
            backgroundColor: "#fafafa",
          }}
        >
          <Typography variant="h6">Invoice ID: {invoice.id}</Typography>
          <Typography variant="h6">Customer Email: {invoice.customerEmail}</Typography>
          <Typography sx={{ color: invoice.invoicePaymentStatus === "PAID" ? 'success.main' : 'text.primary' }}>
            Status: {invoice.invoicePaymentStatus}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Created At: {formatDateTime(invoice.createdAt)}
          </Typography>
          <Typography variant="body2" color="text.secondary">
            Last Updated At: {formatDateTime(invoice.lastUpdatedAt)}
          </Typography>

          <Box display="flex" gap={2} mt={2}>
            <Button
              variant="outlined"
              onClick={() => handleViewItems(invoice)}
            >
              View Items
            </Button>
            <Button variant="outlined" onClick={() => handleOpenAddDialog(invoice.id)}>
              Add Item
            </Button>
            {invoice.invoicePaymentStatus === "PENDING" && (
              <Button
                variant="contained"
                color="success"
                onClick={() => handleOpenPayDialog(invoice)}
              >
                Pay Invoice
              </Button>
            )}
          </Box>
        </Box>
      ))}

      {/* --- Add Item Dialog --- */}
      <Dialog open={openAddDialog} onClose={handleCloseAddDialog} fullWidth>
        <DialogTitle>Select an Item to Add</DialogTitle>
        <DialogContent>
          {loadingItems ? (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', p: 2 }}>
              <CircularProgress size={24} />
              <Typography sx={{ ml: 2 }}>Loading items...</Typography>
            </Box>
          ) : (
            <>
              {error && (
                <Alert severity="error" sx={{ mb: 2 }}>
                  {error}
                </Alert>
              )}
              <Select
                fullWidth
                value={selectedItemSku}
                onChange={(e) => setSelectedItemSku(e.target.value)}
                displayEmpty
                disabled={items.length === 0 && !error}
              >
                <MenuItem value={PLACEHOLDER_SKU_VALUE} disabled>
                  {items.length === 0 && !error ? "No items available" : "Select an item"}
                </MenuItem>
                {items.map((item) => (
                  <MenuItem key={item.id} value={String(item.sku)}>
                    {item.description} — {item.sku}
                  </MenuItem>
                ))}
              </Select>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseAddDialog}>Cancel</Button>
          <Button
            variant="contained"
            onClick={handleAdd}
            disabled={selectedItemSku === PLACEHOLDER_SKU_VALUE || loadingItems}
          >
            Add
          </Button>
        </DialogActions>
      </Dialog>

      {/* --- View Items Dialog --- */}
      <Dialog open={openItemsDialog} onClose={handleCloseItemsDialog} fullWidth maxWidth="sm">
        <DialogTitle>Invoice Items</DialogTitle>
        <DialogContent dividers>
          {selectedInvoiceItems.length === 0 ? (
            <Typography variant="body1" color="text.secondary">
              No items associated with this invoice yet.
            </Typography>
          ) : (
            <>
              {selectedInvoiceItems.map((item, index) => (
                <Box key={item.id || index} sx={{ mb: 2 }}>
                  <Typography variant="body2"><strong>SKU:</strong> {item.sku}</Typography>
                  <Typography variant="body2"><strong>Description:</strong> {item.description}</Typography>
                  <Typography variant="body2"><strong>Quantity:</strong> {item.quantity}</Typography>
                  <Typography variant="body2"><strong>Price:</strong> ${item.price?.toFixed(2) || '0.00'}</Typography>
                  <Typography variant="body2"><strong>Total Price:</strong> ${item.totalPrice?.toFixed(2) || '0.00'}</Typography>
                  {index !== selectedInvoiceItems.length - 1 && <Divider sx={{ mt: 2, mb: 2 }} />}
                </Box>
              ))}
              <Divider sx={{ mt: 2, mb: 2 }} />
              <Typography variant="h6" sx={{ textAlign: 'right', mt: 2 }}>
                Invoice Total: ${currentInvoiceTotalPrice?.toFixed(2) || '0.00'}
              </Typography>
            </>
          )}
        </DialogContent>
        <DialogActions>
          <Button onClick={handleCloseItemsDialog}>Close</Button>
        </DialogActions>
      </Dialog>

      {/* --- Pay Invoice Dialog --- */}
      <Dialog open={openPayDialog} onClose={handleClosePayDialog} fullWidth maxWidth="xs">
        <DialogTitle>Pay Invoice: {selectedInvoiceForPayment?.customerEmail}</DialogTitle>
        <DialogContent dividers>
          {error && (
            <Alert severity="error" sx={{ mb: 2 }}>
              {error}
            </Alert>
          )}
          <TextField
            label="Amount to Pay"
            type="text"
            fullWidth
            value={paymentAmount}
            // The amount is now read-only as it's the invoice total
            InputProps={{
              readOnly: true,
            }}
            margin="normal"
            disabled={payingInvoice}
            helperText="Invoice total price"
          />
          <Select
            fullWidth
            value={paymentMethod}
            onChange={(e) => setPaymentMethod(e.target.value)}
            displayEmpty
            margin="normal"
            disabled={payingInvoice}
          >
            <MenuItem value={PLACEHOLDER_PAYMENT_METHOD} disabled>
              Select Payment Method
            </MenuItem>
            {PAYMENT_METHODS.map((method) => (
              <MenuItem key={method.value} value={method.value}>
                {method.label}
              </MenuItem>
            ))}
          </Select>
          <Typography variant="body2" color="text.secondary" sx={{ mt: 2 }}>
            Current Invoice Status: {selectedInvoiceForPayment?.invoicePaymentStatus}
          </Typography>
        </DialogContent>
        <DialogActions>
          <Button onClick={handleClosePayDialog} disabled={payingInvoice}>Cancel</Button>
          <Button
            variant="contained"
            color="primary"
            onClick={handlePayInvoice}
            disabled={paymentMethod === PLACEHOLDER_PAYMENT_METHOD || payingInvoice} // Only validate method now
          >
            {payingInvoice ? <CircularProgress size={24} /> : "Process Payment"}
          </Button>
        </DialogActions>
      </Dialog>
    </Box>
  );
};

Invoices.propTypes = {
  invoices: PropTypes.array,
  onAddItemToInvoice: PropTypes.func.isRequired,
  onInvoicePaid: PropTypes.func,
};

export default Invoices;