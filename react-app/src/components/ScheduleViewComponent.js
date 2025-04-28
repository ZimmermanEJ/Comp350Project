import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import ExportScheduleModal from './ExportScheduleModal';
import '../ScheduleView.css';

function ScheduleViewComponent() {
  const location = useLocation();
  const [schedule, setSchedule] = useState(location.state?.schedule);
  const [credits, setCredits] = useState(location.state?.credits);
  const schedules = location.state?.schedules;
  const initialCourses = location.state?.courses;
  const [courses, setCourses] = useState(initialCourses);
  const [exportModalOpen, setExportModalOpen] = useState(false);
  const [canUndo, setCanUndo] = useState(false);

  const fetchSchedule = async (scheduleID) => {
      try {
        const response = await axios.get('http://localhost:4567/api/schedule', {
          params: { userID: schedule.userID, scheduleID: schedule.scheduleID }
        });
        if (response.data.status === 'success') {
          setCredits(response.data.credits)
          setCourses(response.data.courses);
          setSchedule(response.data.schedule);
        }
      } catch (error) {
        console.error(error.response?.data.message);
      }
  };

  useEffect(() => {
      fetchSchedule();
  }, [schedule.scheduleID]);

  const handleDropButton = async (referenceNumber) => {
    try {
      const response = await axios.delete('http://localhost:4567/api/course', {
        params: {
          userID: schedule.userID,
          scheduleID: schedule.scheduleID,
          referenceNumber: referenceNumber
        }
      });
      if (response.data.status === 'success') {
        const course = courses.find(course => course.referenceNumber === referenceNumber);
        setCourses(courses.filter(course => course.referenceNumber !== referenceNumber));
        setCredits(credits - course.credits);
        setSchedule(response.data.schedule);
        setCanUndo(true);
      }
    } catch (error) {
      if (error.response != null) {
        console.error(error.response.data.message);
      } else {
        console.error('Error:', error);
      }
    }
  };

  const handleUndo = async () => {
      try {
        const response = await axios.put('http://localhost:4567/api/undo', null, {
          params: { userID: schedule.userID, scheduleID: schedule.scheduleID }
        });
        if (response.data.status === 'success') {

            // uncomment this if allow multiple undos
//            if (response.data.isLast === true) {
                setCanUndo(false);
//            }
            fetchSchedule(schedule.scheduleID);
        } else if (response.data.status === 'error') {
        }
      } catch (error) {
        console.error(error.response?.data.message);
      }
  }

  const handleExportSchedule = async (name) => {
      try {
        const response = await axios.post('http://localhost:4567/api/exportSchedule', null, {
            params: {
              userID: schedule.userID,
              scheduleID: schedule.scheduleID,
              fileName: name
            }
        });
        if (response.data.status === 'success') {
            window.open("https://calendar.google.com/calendar/u/0/r/settings/export", "_blank");
            window.open("https://outlook.office.com/calendar/addcalendar", "_blank");
        }
      } catch (error) {
        console.error('Error exporting schedule:', error);
      }
  };

  const renderScheduleView = (schedule) => {
    if (!schedule) return null;

    const days = ["Monday", "Tuesday", "Wednesday", "Thursday", "Friday"];
    const hours = [];
    for (let hour = 8; hour < 21; hour++) {
      for (let minute = 0; minute <= 45; minute += 15) {
        hours.push(hour + minute / 100);
      }
    }

    return (
      <>
        <div>
          <div>
            {canUndo && (
                <button className="undo-button" onClick={handleUndo}>Undo</button>
            )}
            <h1>{schedule.scheduleName} - {credits} credits</h1>
            <button onClick={() => setExportModalOpen(true)} className="floating-button">Export</button>
          </div>
          <table className='schedule-table'>
            <thead>
              <tr>
                <th></th>
                {days.map(day => (
                  <th key={day}>{day}</th>
                ))}
              </tr>
            </thead>
            <tbody>
              {hours.map(hour => (
                <tr key={hour}>
                  <td>{(((hour - 1) % 12) + 1).toFixed(2)}{hour <= 11 ? "AM" : "PM"}</td>
                  {days.map((day, dayIndex) => (
                    <td key={dayIndex}>
                      {courses && courses.map((course, courseIndex) => {
                        const timeslot = course.timeSlot[dayIndex + 1];
                        if (timeslot && timeslot.length === 2 && hour >= timeslot[0] && hour < timeslot[1]) {
                          if (hour - 0.149 < timeslot[0]) {
                            return (
                              <div key={courseIndex} className="course-cell">
                                {course.title}
                                <br />
                                {(((timeslot[0] - 1) % 12) + 1).toFixed(2)} - {(((timeslot[1] - 1) % 12) + 1).toFixed(2)}
                                <button className="drop-button" onClick={() => handleDropButton(course.referenceNumber)}>X</button>
                              </div>
                            );
                          } else {
                            return (
                              <div key={courseIndex} className="course-cell">
                                <br />
                                <br />
                                <br />
                              </div>
                            );
                          }
                        }
                        return null;
                      })}
                    </td>
                  ))}
                </tr>
              ))}
            </tbody>
          </table>
          <div>
            {schedule.events.length > 0 ? (
              <>
                <h3>Events:</h3>
                <ul>
                  {schedule.events.map((event, index) => (
                    <li key={index}>{event}</li>
                  ))}
                </ul>
              </>
            ) : (
              <p>No events scheduled</p>
            )}
          </div>
          <ExportScheduleModal
              isOpen={exportModalOpen}
              onClose={() => setExportModalOpen(false)}
              onExport={handleExportSchedule}
          />
        </div>
      </>
    );
  };

  return (
    <div>
      {schedule ? (
        <>
          {renderScheduleView(schedule)}
        </>
      ) : (
        <p>Schedule not found</p>
      )}
    </div>
  );
}

export default ScheduleViewComponent;