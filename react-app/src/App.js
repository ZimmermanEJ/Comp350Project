import React from 'react';
import { BrowserRouter as Router, Route, Routes } from 'react-router-dom';
import LoginComponent from './components/LoginComponent';
import SignupComponent from './components/SignupComponent';
import HomeComponent from './components/HomeComponent';
import ScheduleComponent from './components/ScheduleComponent';
import './App.css';

function App() {
  return (
    <Router>
      <div className="App">

        <Routes>
          <Route path="/" element={<LoginComponent />} />
          <Route path="/signup/" element={<SignupComponent />} />
          <Route path="/home/:userID/" element={<HomeComponent />} />
          <Route path="/schedule/:userID/:scheduleID/*" element={<ScheduleComponent />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;