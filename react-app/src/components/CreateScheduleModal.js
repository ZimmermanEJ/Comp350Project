import React, { useState, useEffect } from 'react';
import { useLocation } from 'react-router-dom';
import axios from 'axios';

function CreateScheduleModal({ isOpen, onClose, onCreate }) {
  const location = useLocation();
  const user = location.state?.user;
  const [scheduleName, setScheduleName] = useState('');
  const [useAI, setUseAI] = useState(false);
  const [major, setMajor] = useState('');
  const [year, setYear] = useState(0);
  const [showFields, setShowFields] = useState(false);

  useEffect(() => {
      if (user) {
          setMajor(user.major);
          setYear(user.year);
          setShowFields(user.year === 0 || user.major === "");
      }
  },[]);

  const handleCreate = () => {
    onCreate(scheduleName, useAI);
    setScheduleName('');
    onClose();
  };

  const handleCheckboxChange = (event) => {
      setUseAI(event.target.checked);
  };

  if (!isOpen) return null;

  return (
    <div className="modal-overlay">
      <div className="modal">
        <button className="close-button" onClick={onClose}>X</button>
        <h2>Create New Schedule</h2>
        <input
          type="text"
          value={scheduleName}
          onChange={(e) => setScheduleName(e.target.value)}
          placeholder="Enter schedule name"
        />
        <label className="checkbox-label">
            Use AI to generate a starting schedule:
            <input
              type="checkbox"
              checked={useAI}
              onChange={handleCheckboxChange}
            />
        </label>

        {useAI && showFields && (
          <div>
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
          </div>
        )}
        <button className="confirm-button" onClick={handleCreate}>Create</button>

      </div>
    </div>
  );
}

export default CreateScheduleModal;