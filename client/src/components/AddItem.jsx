import { Box, Button, TextField, Typography, styled } from "@mui/material";
import { useState } from "react";
import { saveItem } from "../services/api";
import PropTypes from 'prop-types';

const BoxComponent = styled(Box)({
    marginTop: 20,
    // To access child and typography is basically a <p> tag so its css is as follows
    '& > p': {
        fontSize: 26,
        marginBottom: 10
    },
    // for div inside div
    '& > div > div': {
        marginRight: 20,
        minWidth: 200,
    }
});

const defaultObj = {
    sku: '',
    description: '',
    quantity: '',
    price: '',
    amount: ''
};

const AddItem = ({ setAddItem }) => {
    const [item, setItem] = useState(defaultObj);

    // Generic handler for text fields (sku, description)
    const onChangeValue = (e) => {
        setItem({ ...item, [e.target.name]: e.target.value });
    };

    // Handler for integer-only quantity input
    const onChangeQuantity = (e) => {
        const value = e.target.value;
        // Allow empty string or only digits
        if (/^\d*$/.test(value)) {
            setItem(prevItem => ({
                ...prevItem,
                quantity: value
            }));
        }
    };

    // Handler for decimal price input (up to 2 decimal places)
    const onChangePrice = (e) => {
        const value = e.target.value;
        // Allow empty string, whole numbers, or decimals with up to 2 digits
        if (/^\d*\.?\d{0,2}$/.test(value)) {
            setItem(prevItem => ({
                ...prevItem,
                price: value
            }));
        }
    };

    const addNewItem = async () => {
        // Ensure quantity and price are converted to numbers before saving
        const itemToSave = {
            ...item,
            quantity: Number(item.quantity),
            price: Number(item.price),
            amount: Number(item.amount)
        };
        await saveItem(itemToSave);
        setAddItem(false);
    };

    return (
        <BoxComponent>
            <Typography>Add Item</Typography>
            <Box>
                <TextField
                    variant="standard"
                    placeholder="Enter Item Sku code"
                    onChange={onChangeValue}
                    name="sku"
                    value={item.sku} // Controlled component
                    autoComplete="off"
                />
                <TextField
                    variant="standard"
                    placeholder="Enter Item Description"
                    onChange={onChangeValue}
                    name="description"
                    value={item.description} // Controlled component
                    autoComplete="off"
                />
                <TextField
                    variant="standard"
                    placeholder="Enter Quantity"
                    name="quantity"
                    value={item.quantity}
                    onChange={onChangeQuantity} // Use specific handler for quantity
                    autoComplete="off"
                    type="text" // Use text to control input strictly with regex
                    inputMode="numeric" // Suggests numeric keyboard on mobile
                    pattern="[0-9]*" // HTML5 pattern for basic validation
                />
                <TextField
                    variant="standard"
                    placeholder="Enter Price"
                    name="price"
                    value={item.price}
                    onChange={onChangePrice} // Use specific handler for price
                    autoComplete="off"
                    type="text" // Use text to control input strictly with regex
                    inputMode="decimal" // Suggests decimal keyboard on mobile
                    pattern="[0-9]*[.,]?[0-9]{0,2}" // HTML5 pattern for basic validation
                />
                <Button
                    variant="contained"
                    onClick={addNewItem}
                >
                    Add Item
                </Button>
            </Box>
        </BoxComponent>
    );
};

AddItem.propTypes = {
    setAddItem: PropTypes.func.isRequired,
};

export default AddItem;
