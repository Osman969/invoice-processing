import React from 'react';
import { AppBar, Toolbar } from '@mui/material';
import logo from '../assets/logo.png';

const Header = () => {
  return (
    <AppBar position='static'>
      <Toolbar>
        <img src={logo} alt="logo" style={{ width: 200 }} />
      </Toolbar>
    </AppBar>
  );
};

export default Header;
