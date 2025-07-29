import axios from 'axios';

const API_URL = "http://localhost:8080";

export const saveInvoice = async (payload) => {
    try {
        return await axios.post(`${API_URL}/invoice`, payload);
    } catch (error) {
        console.log("Error:", error.message);
        return error.response.data;
    }
}

export const saveItem = async (payload) => {
    try {
        return await axios.post(`${API_URL}/lineItem`, payload);
    } catch (error) {
        console.log("Error:", error.message);
        return error.response.data;
    }
}

export const payInvoice = async (invoiceId, paymentInfoDto) => {
    try {
        return await axios.patch(`${API_URL}/invoice/${invoiceId}/pay`, paymentInfoDto);
    } catch (error) {
        console.error("Error updating payment info for invoice:", error.response?.data || error.message);
        throw error;
    }
}

export const getAllItems = async (pageNumber = 0) => {
    try {
        return await axios.get(`${API_URL}/lineItems`, {
            params: { pageNumber }
        });
    } catch (error) {
        console.log("Error:", error.message);
        return error.response.data;
    }
}

export const getAllInvoices = async (pageNumber = 0) => {
    try {
        return await axios.get(`${API_URL}/invoices`, {
            params: { pageNumber }
        });
    } catch (error) {
        console.log("Error:", error.message);
        return error.response.data;
    }
}

export const getInvoice = async (invoiceId) => {
    try {
        return await axios.get(`${API_URL}/invoice/${invoiceId}`);
    } catch (error) {
        console.log("Error:", error.message);
        return error.response.data;
    }
}

export const addItemToInvoice = async (invoiceId, itemSkuCode) => {
  try {
    const response = await axios.post(`${API_URL}/invoice/${invoiceId}/item/${itemSkuCode}`);
    return { success: true, message: response.data.message };
  } catch (error) {
    const message = error.response?.data?.message || "Failed to add item to invoice.";
    return { success: false, message };
  }
}