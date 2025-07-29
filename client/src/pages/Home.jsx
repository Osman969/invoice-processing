import { useEffect, useState } from "react";
import { Box, Typography, Button, Paper, Tabs, Tab } from "@mui/material";
import Header from "../components/Header";
import AddInvoice from "../components/AddInvoice";
import Invoices from "../components/Invoices";
import AddItem from "../components/AddItem";
import Items from "../components/Items";
import { getAllInvoices, getAllItems, addItemToInvoice, getInvoice } from "../services/api";

// Toast
import toast, { Toaster } from 'react-hot-toast';

const Home = () => {
  const [addInvoice, setAddInvoice] = useState(false);
  const [addItem, setAddItem] = useState(false);
  const [invoices, setInvoices] = useState([]);
  const [items, setItems] = useState([]);
  const [activeTab, setActiveTab] = useState('invoices');
  const [selectedInvoiceId, setSelectedInvoiceId] = useState(null);

  const [invoicePagination, setInvoicePagination] = useState({
    currentPage: 0,
    totalPages: 1
  });

  const [itemsPagination, setItemsPagination] = useState({
    currentPage: 0,
    totalPages: 1
  });

  // Search state
  const [searchInvoiceId, setSearchInvoiceId] = useState('');
  const [isSearchActive, setIsSearchActive] = useState(false);

  const handleInvoicePaid = async () => {
    await fetchInvoices();
  };

  const fetchInvoices = async (pageNo = 0) => {
    try {
      const response = await getAllInvoices(pageNo);
      setInvoices(response.data.invoices);
      setInvoicePagination({
        currentPage: response.data.currentPage,
        totalPages: response.data.totalPages
      });
      if (selectedInvoiceId && !response.data.invoices.some(inv => inv.id === selectedInvoiceId)) {
        setSelectedInvoiceId(null);
      }
    } catch (err) {
      toast.error("Failed to load invoices.");
    }
  };

  const fetchItems = async (pageNo = 0) => {
    try {
      const response = await getAllItems(pageNo);
      setItems(response.data.items || response.data);
      setItemsPagination({
        currentPage: response.data.currentPage || pageNo,
        totalPages: response.data.totalPages || 1
      });
    } catch (err) {
      toast.error("Failed to load items.");
    }
  };

  const handleInvoicePageChange = (newPageNo) => {
    fetchInvoices(newPageNo);
  };

  const handleItemsPageChange = (newPageNo) => {
    fetchItems(newPageNo);
  };

  useEffect(() => {
    if (activeTab === 'invoices') {
      fetchInvoices(0);
    }
  }, [addInvoice, activeTab]);

  useEffect(() => {
    if (activeTab === 'items') {
      fetchItems(0);
    }
  }, [addItem, activeTab]);

  const handleAddItemToInvoice = async (invoiceId, itemSkuCode) => {
    try {
      const result = await addItemToInvoice(invoiceId, itemSkuCode);
      if (result.success) {
        toast.success(result.message);
      } else {
        toast.error("Error: " + result.message);
      }
      if (activeTab === 'invoices') {
        fetchInvoices(invoicePagination.currentPage);
      } else {
        fetchItems(itemsPagination.currentPage);
      }
    } catch (err) {
      toast.error("Error assigning item to invoice.");
      console.error("Error assigning item to invoice:", err);
    }
  };

  const handleInvoiceClick = (invoiceId) => {
    setSelectedInvoiceId(prevId => (prevId === invoiceId ? null : invoiceId));
  };

  const getInvoicePageNumbers = () => {
    return Array.from({ length: invoicePagination.totalPages }, (_, i) => i);
  };

  const getItemsPageNumbers = () => {
    return Array.from({ length: itemsPagination.totalPages }, (_, i) => i);
  };

  const handleTabChange = (event, newValue) => {
    setActiveTab(newValue);
    setAddInvoice(false);
    setAddItem(false);
    setSelectedInvoiceId(null);
    setSearchInvoiceId('');
    setIsSearchActive(false);
  };

  // Search by invoice ID
  const handleSearchInvoice = async () => {
    if (!searchInvoiceId.trim()) return;

    try {
      const response = await getInvoice(searchInvoiceId.trim());
      if (response?.data) {
        setInvoices([response.data.invoice]);
        setIsSearchActive(true);
        setSelectedInvoiceId(null);
      } else {
        toast.error("Invoice not found.");
      }
    } catch (err) {
      toast.error("Failed to fetch invoice.");
    }
  };

  const clearSearch = () => {
    setSearchInvoiceId('');
    setIsSearchActive(false);
    fetchInvoices(0);
  };

  return (
    <>
      <Toaster
        position="top-center"
        toastOptions={{
          style: {
            fontSize: '1.2rem',
            padding: '16px 24px',
            minWidth: '300px',
            textAlign: 'center',
          },
          success: {
            style: {
              background: '#dff0d8',
              color: '#3c763d',
            },
          },
          error: {
            style: {
              background: '#f2dede',
              color: '#a94442',
            },
          },
        }}
      />

      <Header />

      <Box sx={{ width: '100%', borderBottom: 1, borderColor: 'divider', mt: 2 }}>
        <Tabs value={activeTab} onChange={handleTabChange} centered>
          <Tab label="Invoices" value="invoices" />
          <Tab label="Items" value="items" />
        </Tabs>
      </Box>

      {activeTab === 'invoices' && (
        <Box component={Paper} elevation={3} sx={{ m: 3, p: 3, borderRadius: 2 }}>
          <Typography variant="h4" gutterBottom>Invoices</Typography>

          {/* Search Bar */}
          <Box sx={{ display: 'flex', gap: 2, mb: 2 }}>
            <input
              type="text"
              placeholder="Search by Invoice ID"
              value={searchInvoiceId}
              onChange={(e) => setSearchInvoiceId(e.target.value)}
              style={{ padding: '8px', fontSize: '16px', flexGrow: 1 }}
            />
            <Button
              variant="contained"
              onClick={handleSearchInvoice}
              disabled={!searchInvoiceId.trim()}
            >
              Search
            </Button>
            <Button
              variant="outlined"
              onClick={clearSearch}
              disabled={!searchInvoiceId.trim() && !isSearchActive}
            >
              Clear
            </Button>
          </Box>

          {!addInvoice && (
            <Button variant="contained" sx={{ mt: 2, mb: 2 }} onClick={() => setAddInvoice(true)}>
              Add Invoice
            </Button>
          )}
          {addInvoice && <AddInvoice setAddInvoice={setAddInvoice} />}
          <Invoices
            invoices={invoices}
            onAddItemToInvoice={handleAddItemToInvoice}
            onInvoicePaid={handleInvoicePaid}
            onInvoiceClick={handleInvoiceClick}
            selectedInvoiceId={selectedInvoiceId}
          />

          {!isSearchActive && (
            <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 1, mt: 3 }}>
              <Button
                disabled={invoicePagination.currentPage === 0}
                onClick={() => handleInvoicePageChange(invoicePagination.currentPage - 1)}
                variant="outlined"
              >
                Prev
              </Button>

              {getInvoicePageNumbers().map((pageNumber) => (
                <Button
                  key={pageNumber}
                  variant={invoicePagination.currentPage === pageNumber ? "contained" : "outlined"}
                  onClick={() => handleInvoicePageChange(pageNumber)}
                  sx={{ minWidth: '40px' }}
                >
                  {pageNumber + 1}
                </Button>
              ))}

              <Button
                disabled={invoicePagination.currentPage >= invoicePagination.totalPages - 1}
                onClick={() => handleInvoicePageChange(invoicePagination.currentPage + 1)}
                variant="outlined"
              >
                Next
              </Button>
            </Box>
          )}
        </Box>
      )}

      {activeTab === 'items' && (
        <Box component={Paper} elevation={3} sx={{ m: 3, p: 3, borderRadius: 2 }}>
          <Typography variant="h4" gutterBottom>Items</Typography>
          {!addItem && (
            <Button variant="contained" sx={{ mt: 2, mb: 2 }} onClick={() => setAddItem(true)}>
              Add Item
            </Button>
          )}
          {addItem && <AddItem setAddItem={setAddItem} />}
          <Items items={items} />

          <Box sx={{ display: 'flex', justifyContent: 'center', alignItems: 'center', gap: 1, mt: 3 }}>
            <Button
              disabled={itemsPagination.currentPage === 0}
              onClick={() => handleItemsPageChange(itemsPagination.currentPage - 1)}
              variant="outlined"
            >
              Prev
            </Button>

            {getItemsPageNumbers().map((pageNumber) => (
              <Button
                key={pageNumber}
                variant={itemsPagination.currentPage === pageNumber ? "contained" : "outlined"}
                onClick={() => handleItemsPageChange(pageNumber)}
                sx={{ minWidth: '40px' }}
              >
                {pageNumber + 1}
              </Button>
            ))}

            <Button
              disabled={itemsPagination.currentPage >= itemsPagination.totalPages - 1}
              onClick={() => handleItemsPageChange(itemsPagination.currentPage + 1)}
              variant="outlined"
            >
              Next
            </Button>
          </Box>
        </Box>
      )}
    </>
  );
};

export default Home;
