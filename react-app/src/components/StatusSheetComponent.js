import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import axios from 'axios';
import '../StatusSheet.css'; // Ensure you have styles for the modal

function StatusSheetComponent() {
  const { userID, scheduleID } = useParams();
  const [user, setUser] = useState(null);
  const [image, setImage] = useState("");
  const [isModalOpen, setIsModalOpen] = useState(false); // State for modal visibility
  const [major, setMajor] = useState(""); // State for selected major
  const [year, setYear] = useState(0); // State for selected year

  useEffect(() => {
    const fetchMajorYear = async () => {
      try {
        const response = await axios.get('http://localhost:4567/api/getmajoryear', {
          params: { userID }
        });
        if (response.data.status === 'success') {
          setMajor(response.data.major);
          setYear(response.data.year);
        } else {
          console.error('Failed to fetch user:', response.data.message);
        }
      } catch (error) {
        console.error('Error fetching user:', error.message);
      }
    };

    if (userID) {
      fetchMajorYear();
    } else {
      console.error('Invalid userID');
    }
  }, [userID]);

  useEffect(() => {
    if (year !== 0 && major !== "") {
      setIsModalOpen(false);
      setImage(`/images/${major}${year}.png`);
    } else {
      setIsModalOpen(true);
      setImage("/images/gcc.jpg");
    }
  }, [major, year]);

  const handleSave = async () => {
    if (major && year) {
      try {
          const response = await axios.put('http://localhost:4567/api/setmajoryear', null, {
            params: { userID, major, year }
          });
      } catch (error) {
        console.error('Error saving major and year:', error.response?.data.message);
      }
      setImage(`/images/${major}${year}.png`);
      setIsModalOpen(false); // Close the modal

    } else {
      alert("Please select both Major and Year.");
    }
  };

  return (
    <div className="StatusSheetComponent">
      {isModalOpen && (
        <div className="statussheet-modal">
          <div className="statussheet-modal-content">
            <h2>Select Major and Year</h2>
            <div>
              <label htmlFor="major">Major: </label>
              <select
                className="dropdown"
                id="major"
                value={major}
                onChange={(e) => setMajor(e.target.value)}
              >
                <option value="">-- Select Major --</option>
                <option value="ComputerScience">Computer Science</option>
              </select>
            </div>
            <div>
              <label htmlFor="year">Year: </label>
              <select
                className="dropdown"
                id="year"
                value={year}
                onChange={(e) => setYear(e.target.value)}
              >
                <option value="">--Select Graduation Year--</option>
                <option value="2026">2026</option>
                <option value="2027">2027</option>
                <option value="2028">2028</option>
                <option value="2029">2029</option>
              </select>
            </div>
            <button className='create-button' onClick={handleSave}>Save</button>
          </div>
        </div>
      )}

      <img
        src={image || null}
        width="60%"
        height="60%"
        style={{ border: '2px solid black' }}
        alt="Status Sheet"
      />
    </div>
  );
}

export default StatusSheetComponent;