import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';
import '../signup.css'; // Import the CSS file

function SignupComponent() {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [name, setName] = useState('');
  const [message, setMessage] = useState('');
  const navigate = useNavigate(); // useNavigate hook for navigation

  const handleSignup = async (e) => {
    e.preventDefault();
    try {
      const response = await axios.post('http://localhost:4567/api/signup', null, {
        params: {
          email: email,
          password: password,
          name: name
        }
      });
      if (response.data.status === 'success') {
        navigate(`/home/${response.data.user.userID}`, { state: { user: response.data.user, schedules: response.data.schedules } });
      }
    } catch (error) {
      setMessage(error.response.data.message);
    }
  };

  return (
    <div className="signup-container">
      <form className="signup-form" onSubmit={handleSignup}>
        <h2>Signup</h2>
        <input
          type="text"
          placeholder="Name"
          value={name}
          onChange={(e) => setName(e.target.value)}
          required
        />
        <input
          type="email"
          placeholder="Email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          required
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
          required
        />
        <button type="submit">Signup</button>
        {message && <p>{message}</p>}
        <p>Already have an account? <Link to="/">Login</Link></p>
      </form>
    </div>
  );
}

export default SignupComponent;