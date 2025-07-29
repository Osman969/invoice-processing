import { Table, TableHead, TableBody, TableRow, TableCell, Button, styled, Typography } from '@mui/material';
import PropTypes from 'prop-types';

const StyledTable = styled(Table)({
    width: '80%',
    margin: 20,
    marginTop: 40,
    '& > thead > tr > th': {
        background: '#000',
        color: '#ffffff',
        fontSize: 18,
    },
    '& > tbody > tr > td': {
        fontSize: 16
    },
    '& > tbody > p': {
        fontSize: 18,
        marginTop: 15
    }
})

const Items = ({ items }) => {

    return (
        <StyledTable>
            <TableHead>
                <TableRow>
                    <TableCell>Item Sku Code</TableCell>
                    <TableCell>Item Description</TableCell>
                    <TableCell>Item Quantity</TableCell>
                    <TableCell>Item Price</TableCell>
                </TableRow>
            </TableHead>
            <TableBody>
                {
                    items && Array.isArray(items) && items.length > 0 ?
                        items.map(item => (
                            <TableRow key={item.id}>
                                <TableCell>{item.sku}</TableCell>
                                <TableCell>{item.description}</TableCell>
                                <TableCell>{item.quantity}</TableCell>
                                <TableCell>{item.price}</TableCell>
                            </TableRow>
                        ))
                        :
                        <Typography>No Items</Typography>
                }
            </TableBody>
        </StyledTable>
    )
}

Items.propTypes = {
    items: PropTypes.array.isRequired
}

export default Items;