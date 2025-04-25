import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import '../Search.css';

function SearchComponent() {
    const location = useLocation();

    const initialSchedule = location.state?.schedule;
    const [schedule, setSchedule] = useState(initialSchedule);

    const initialCourses = location.state?.courses;
    const [courses, setCourses] = useState(initialCourses);

    const [searchTerm, setSearchTerm] = useState('');
    const [initialResults, setInitialResults] = useState([]);
    const [filteredResults, setFilteredResults] = useState([]);
    const [showFilters, setShowFilters] = useState(false);
    const [selectedFilters, setSelectedFilters] = useState({
        credits: '',
        department: '',
        courseNumber: '',
        courseSection: '',
        days: '',
        professor: '',
        timeRangeStart: '',
        timeRangeEnd: ''
    });
    const [showDescription, setShowDescription] = useState(false);
    const [selectedDescription, setSelectedDescription] = useState('');
    const [showPopup, setShowPopup] = useState(false);
    const [popupMessage, setPopupMessage] = useState('');





    const handleInputChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const handleSearch = async (event) => {
        if (event.key === 'Enter') {
            try {
                const response = await axios.get('http://localhost:4567/api/search', {
                    params: { searchString: searchTerm }
                });
                setInitialResults(response.data.searchResults);
                setFilteredResults(response.data.searchResults);

                setSelectedFilters({
                    credits: '',
                    department: '',
                    courseNumber: '',
                    courseSection: '',
                    days: '',
                    professor: '',
                    timeRangeStart: '',
                    timeRangeEnd: ''
                });
                setShowFilters(false);
            } catch (error) {
                console.error('Error fetching search results:', error);
            }
        }
    };



    const handleAddToSchedule = async (course) => {
        try {
            const response = await axios.put('http://localhost:4567/api/addToSchedule', null, {
                params: {
                    userID: schedule.userID,
                    scheduleID: schedule.scheduleID,
                    referenceNumber: course.referenceNumber
                }
            });
            if (response.data.status === 'success') {
                setSchedule(response.data.schedule);
                setCourses([...courses, course]);

            } else {
                alert(`Failed to add course: ${response.data.message}`);
            }
        } catch (error) {
            console.error('Error adding course to schedule:', error);
        }
    };

    const toggleFilters = () => {
        setShowFilters(!showFilters);
    };

    const handleFilterChange = (event) => {
        const { name, value } = event.target;
        setSelectedFilters((prevFilters) => ({
            ...prevFilters,
            [name]: value
        }));
    };

    const handleKeyDown = (event) => {
        if (event.key === 'Enter') {
            applyFilters();
        }
    };

    const applyFilters = async () => {
        if (initialResults.length === 0) {
            try {
                const response = await axios.get('http://localhost:4567/api/search', {
                    params: { searchString: searchTerm }
                });
                const searchResults = response.data.searchResults;
                setInitialResults(searchResults);
                setFilteredResults(searchResults);

                const filtered = searchResults.filter((course) => {
                    const timeRangeStart = parseFloat(selectedFilters.timeRangeStart) || 0;
                    const timeRangeEnd = parseFloat(selectedFilters.timeRangeEnd) || 24;

                    return (
                        (!selectedFilters.credits || course.credits === parseInt(selectedFilters.credits)) &&
                        (!selectedFilters.department || course.department.toLowerCase().includes(selectedFilters.department.toLowerCase())) &&
                        (!selectedFilters.courseNumber || course.courseNumber === parseInt(selectedFilters.courseNumber)) &&
                        (!selectedFilters.courseSection || course.sectionCode.toLowerCase().includes(selectedFilters.courseSection.toLowerCase())) &&
                        (!selectedFilters.days || course.days.toLowerCase().includes(selectedFilters.days.toLowerCase())) &&
                        (!selectedFilters.professor || course.professor.toLowerCase().includes(selectedFilters.professor.toLowerCase())) &&
                        (!selectedFilters.timeRangeStart || course.startTime >= timeRangeStart) &&
                        (!selectedFilters.timeRangeEnd || course.endTime <= timeRangeEnd) &&
                        (!selectedFilters.timeRangeStart || !selectedFilters.timeRangeEnd ||
                            (course.startTime >= timeRangeStart && course.endTime <= timeRangeEnd))
                    );
                });
                setFilteredResults(filtered);
            } catch (error) {
                console.error('Error fetching search results:', error);
            }
        } else {
            const filtered = initialResults.filter((course) => {
                const timeRangeStart = parseFloat(selectedFilters.timeRangeStart) || 0;
                const timeRangeEnd = parseFloat(selectedFilters.timeRangeEnd) || 24;

                return (
                    (!selectedFilters.credits || course.credits === parseInt(selectedFilters.credits)) &&
                    (!selectedFilters.department || course.department.toLowerCase().includes(selectedFilters.department.toLowerCase())) &&
                    (!selectedFilters.courseNumber || course.courseNumber === parseInt(selectedFilters.courseNumber)) &&
                    (!selectedFilters.courseSection || course.sectionCode.toLowerCase().includes(selectedFilters.courseSection.toLowerCase())) &&
                    (!selectedFilters.days || course.days.toLowerCase().includes(selectedFilters.days.toLowerCase())) &&
                    (!selectedFilters.professor || course.professor.toLowerCase().includes(selectedFilters.professor.toLowerCase())) &&
                    (!selectedFilters.timeRangeStart || course.startTime >= timeRangeStart) &&
                    (!selectedFilters.timeRangeEnd || course.endTime <= timeRangeEnd) &&
                    (!selectedFilters.timeRangeStart || !selectedFilters.timeRangeEnd ||
                        (course.startTime >= timeRangeStart && course.endTime <= timeRangeEnd))
                );
            });
            setFilteredResults(filtered);
        }
        setShowFilters(false);
    };

    const clearFilters = () => {
        setSelectedFilters({
            credits: '',
            department: '',
            courseNumber: '',
            courseSection: '',
            days: '',
            professor: '',
            timeRangeStart: '',
            timeRangeEnd: ''
        });
        setFilteredResults(initialResults);
    };

    const handleShowDescription = (course) => {

            const formatTime = (time) => {
                if (!time) return "N/A";
                const hours = Math.floor(time); // Extract the hour part
                const minutes = Math.round((time % 1) * 100); // Extract the exact minutes from the decimal
                const period = hours >= 12 ? "PM" : "AM"; // Determine AM/PM
                const formattedHours = hours % 12 || 12; // Convert 0 to 12 for 12-hour format
                const formattedMinutes = minutes.toString().padStart(2, "0"); // Ensure two-digit format for minutes
                return `${formattedHours}:${formattedMinutes} ${period}`;
            };


        const description = (
            <div>
                <p><strong>Professor:</strong> {course.professor || "Unknown"}</p>
                <p><strong>Time:</strong> {formatTime(course.startTime)} to {formatTime(course.endTime)}</p>
                <p><strong>Days:</strong> {course.days || "N/A"}</p>
                <p><strong>Description:</strong> {course.description || "No description available"}</p>
            </div>
        );
        setSelectedDescription(description);
        setShowDescription(true);
    };

    const handleCloseDescription = () => {
        setShowDescription(false);
        setSelectedDescription('');
    };

    return (
        <div>
            <input
                type="text"
                placeholder="Enter search term"
                value={searchTerm}
                onChange={handleInputChange}
                onKeyDown={handleSearch}
            />
            <button className="filter-button" onClick={toggleFilters}>Filters</button>
            {showFilters && (
                <div className="overlay">
                    <div className="filter-modal" onKeyDown={handleKeyDown} tabIndex={0}>
                        <h3>Filter Options</h3>
                        {/* Filter fields */}
                        <div className="filter-row">
                            <label>Credits:</label>
                            <input
                                type="number"
                                name="credits"
                                value={selectedFilters.credits}
                                onChange={handleFilterChange}
                            />
                        </div>
                        <div className="filter-row">
                            <label>Department:</label>
                            <input
                                type="text"
                                name="department"
                                value={selectedFilters.department}
                                onChange={handleFilterChange}
                            />
                        </div>
                        <div className="filter-row">
                            <label>Professor:</label>
                            <input
                                type="text"
                                name="professor"
                                value={selectedFilters.professor}
                                onChange={handleFilterChange}
                            />
                        </div>
                        <div className="filter-row">
                            <label>Course Number:</label>
                            <input
                                type="number"
                                name="courseNumber"
                                value={selectedFilters.courseNumber}
                                onChange={handleFilterChange}
                            />
                        </div>
                        <div className="filter-row">
                            <label>Course Section:</label>
                            <input
                                type="text"
                                name="courseSection"
                                value={selectedFilters.courseSection}
                                onChange={handleFilterChange}
                            />
                        </div>
                        <div className="filter-row">
                            <label>Days:</label>
                            <select
                                name="days"
                                value={selectedFilters.days}
                                onChange={handleFilterChange}
                            >
                                <option value="">Select Days</option>
                                <option value="MWF">MWF</option>
                                <option value="TR">TR</option>
                            </select>
                        </div>
                        <div className="filter-row">
                            <label>Time Range Start:</label>
                            <select
                                name="timeRangeStart"
                                value={selectedFilters.timeRangeStart}
                                onChange={handleFilterChange}
                            >
                                <option value="">Select Start Time</option>
                                <option value="8">8:00 AM</option>
                                <option value="8.3">8:30 AM</option>
                                <option value="9">9:00 AM</option>
                                <option value="9.3">9:30 AM</option>
                                <option value="10">10:00 AM</option>
                                <option value="10.3">10:30 AM</option>
                                <option value="11">11:00 AM</option>
                                <option value="11.3">11:30 AM</option>
                                <option value="12">12:00 PM</option>
                                <option value="12.3">12:30 PM</option>
                                <option value="13">1:00 PM</option>
                                <option value="13.3">1:30 PM</option>
                                <option value="14">2:00 PM</option>
                                <option value="14.3">2:30 PM</option>
                                <option value="15">3:00 PM</option>
                                <option value="15.3">3:30 PM</option>
                                <option value="16">4:00 PM</option>
                                <option value="16.3">4:30 PM</option>
                                <option value="17">5:00 PM</option>
                                <option value="17.3">5:30 PM</option>
                                <option value="18">6:00 PM</option>
                            </select>
                        </div>
                        <div className="filter-row">
                            <label>Time Range End:</label>
                            <select
                                name="timeRangeEnd"
                                value={selectedFilters.timeRangeEnd}
                                onChange={handleFilterChange}
                            >
                                <option value="">Select End Time</option>
                                <option value="8.3">8:30 AM</option>
                                <option value="9">9:00 AM</option>
                                <option value="9.3">9:30 AM</option>
                                <option value="10">10:00 AM</option>
                                <option value="10.3">10:30 AM</option>
                                <option value="11">11:00 AM</option>
                                <option value="11.3">11:30 AM</option>
                                <option value="12">12:00 PM</option>
                                <option value="12.3">12:30 PM</option>
                                <option value="13">1:00 PM</option>
                                <option value="13.3">1:30 PM</option>
                                <option value="14">2:00 PM</option>
                                <option value="14.3">2:30 PM</option>
                                <option value="15">3:00 PM</option>
                                <option value="15.3">3:30 PM</option>
                                <option value="16">4:00 PM</option>
                                <option value="16.3">4:30 PM</option>
                                <option value="17">5:00 PM</option>
                                <option value="17.3">5:30 PM</option>
                                <option value="18">6:00 PM</option>
                            </select>
                        </div>
                        <div className="button-container">
                            <div className="top-buttons">
                                <button onClick={applyFilters}>Apply Filters</button>
                                <button onClick={clearFilters}>Clear Filters</button>
                                <button className="close-button" onClick={toggleFilters}>Close</button>
                            </div>
                        </div>
                    </div>
                </div>
            )}
            <h1>Search Page</h1>
            {filteredResults.length > 0 ? (
                <ul className="content-section">
                    {filteredResults.map((course, index) => (
                        <li key={index} className="course-item">
                            <div
                                className="description-box"
                                onClick={() => handleShowDescription(course)}
                                title="Click to view description"
                            >
                                i
                            </div>
                            <div className="course-details">
                                {course.department} {course.courseNumber} {course.sectionCode}: {course.title}

                            </div>
                            <div className="course-actions">
                                <button
                                    onClick={() => handleAddToSchedule(course)}
                                    disabled={schedule?.courses?.includes(course.referenceNumber)}
                                    style={{
                                        backgroundColor: schedule?.courses?.includes(course.referenceNumber) ? 'darkblue' : '',
                                        color: schedule?.courses?.includes(course.referenceNumber) ? 'white' : ''
                                    }}
                                >
                                    {schedule?.courses?.includes(course.referenceNumber) ? 'Added' : 'Add to Schedule'}
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No search results to display</p>
            )}
            {showDescription && (
                <div className="overlay">
                    <div className="description-modal">
                        <h3>Course Description</h3>
                        <p>{selectedDescription}</p>
                        <button onClick={handleCloseDescription}>Close</button>
                    </div>
                </div>
            )}
            {showPopup && (
                <div className="popup-overlay">
                    <div className="popup-content">
                        <p>{popupMessage}</p>
                        <button onClick={() => setShowPopup(false)}>Close</button>
                    </div>
                </div>
            )}
        </div>
    );
}

export default SearchComponent;