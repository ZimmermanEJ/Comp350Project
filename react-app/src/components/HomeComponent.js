import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useLocation, useNavigate, useParams } from 'react-router-dom';
import CreateScheduleModal from './CreateScheduleModal';
import '../home.css';

function HomeComponent() {
  const { userID } = useParams();
  const location = useLocation();
  const navigate = useNavigate();
  const user = location.state?.user;
  const [schedules, setSchedules] = useState([]);
  const [isModalOpen, setIsModalOpen] = useState(false);

  useEffect(() => {
    const fetchSchedules = async () => {
      try {
        const response = await axios.get('http://localhost:4567/api/schedules', {
          params: { userID }
        });
        if (response.data.status === 'success') {
          setSchedules(response.data.schedules);
        } else {
            navigate(`/`);
        }
      } catch (error) {
        console.error(error.response?.data.message);
        navigate(`/`);
      }
    };

    fetchSchedules();
  }, [userID]);

  const handleScheduleClick = async (scheduleID) => {
    try {
      const response = await axios.get('http://localhost:4567/api/schedule', {
        params: { userID, scheduleID }
      });
      if (response.data.status === 'success') {
        navigate(`/schedule/${userID}/${scheduleID}/view/`, {
          state: {
            schedule: response.data.schedule,
            credits: response.data.credits,
            courses: response.data.courses,
            schedules
          }
        });
      }
    } catch (error) {
      console.error(error.response?.data.message);
    }
  };

  const handleCreateSchedule = async (name) => {
    try {
      const response = await axios.post('http://localhost:4567/api/schedule', null, {
        params: { userID, name }
      });
      if (response.data.status === 'success') {
        setSchedules([...schedules, response.data.schedule]);
        setIsModalOpen(false);
        navigate(`/schedule/${userID}/${response.data.schedule.scheduleID}/view/`, {
          state: {
            schedule: response.data.schedule,
            credits: 0,
            courses: response.data.courses,
            schedules
          }
        });
      }
    } catch (error) {
      console.error(error.response?.data.message);
    }
  };

  const handleDeleteSchedule = async (scheduleID) => {
    try {
      const response = await axios.delete('http://localhost:4567/api/schedule', {
        params: { userID, scheduleID }
      });
      if (response.data.status === 'success') {
        setSchedules(schedules.filter(schedule => schedule.scheduleID !== scheduleID));
      }
    } catch (error) {
      console.error(error.response?.data.message);
    }
  };

  return (
    <div className="container">
      <h1>Welcome {user ? user.name : 'User'}!</h1>
      <div className="schedules-container">
        <div className="schedules-list">
          <ul>
            {schedules.length > 0 ? schedules.map((schedule, index) => (
              <li key={index} className="card">
                <span onClick={() => handleScheduleClick(schedule.scheduleID)}>
                  {schedule.name}
                </span>
                <button onClick={() => handleDeleteSchedule(schedule.scheduleID)}>X</button>
              </li>
            )) : <li>No schedules available</li>}
          </ul>
          <button onClick={() => setIsModalOpen(true)} className="create-button">+ New</button>
        </div>
      </div>
      <CreateScheduleModal
        isOpen={isModalOpen}
        onClose={() => setIsModalOpen(false)}
        onCreate={handleCreateSchedule}
      />
    </div>
  );
}

export default HomeComponent;