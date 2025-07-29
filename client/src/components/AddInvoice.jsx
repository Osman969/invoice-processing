import { Box, Button, TextField, Typography, styled } from "@mui/material";
import { useState } from "react";
import { saveInvoice } from "../services/api";
import PropTypes from 'prop-types'

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
})

const defaultObj = {
}

const AddInvoice = ({ setAddInvoice }) => {
    const [invoice, setInvoice] = useState(defaultObj);

    const onChangeValue = (e) => {
        setInvoice({ ...invoice, [e.target.name] : e.target.value });
    }

    const addNewInvoice = async () => {
        await saveInvoice({ ...invoice, amount: Number(invoice['amount']) });
        setAddInvoice(false);
    }

    return (
        <BoxComponent>
            <Typography>Add Invoice</Typography>
            <Box>
                <TextField
                    variant="standard"
                    placeholder="Enter Customer Email"
                    onChange={(e) =>  onChangeValue(e)}
                    name="customerEmail"
                    autoComplete="off"
                />
                <Button
                    variant="contained"
                    onClick={() => addNewInvoice()}
                >
                    Add Invoice
                </Button>
            </Box>
        </BoxComponent>
    );
}

AddInvoice.propTypes = {
    setAddInvoice: PropTypes.func.isRequired,
}

export default AddInvoice;