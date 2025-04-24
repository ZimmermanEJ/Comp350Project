import React, { useState } from 'react';
import axios from 'axios';
import { useLocation } from 'react-router-dom';
import '../Search.css';

function SearchComponent() {
    const location = useLocation();
    const schedule = location.state?.schedule;

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
        startTime: '',
        endTime: '',
        professor: ''
    });

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
                    startTime: '',
                    endTime: '',
                    professor: ''
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
                alert(`Course ${course.department} ${course.courseNumber} added to schedule!`);
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

    const applyFilters = () => {
        const filtered = initialResults.filter((course) => {
            const selectedStartTime = parseFloat(selectedFilters.startTime) || 0;
            const selectedEndTime = parseFloat(selectedFilters.endTime) || 0;

            return (
                (!selectedFilters.credits || course.credits === parseInt(selectedFilters.credits)) &&
                (!selectedFilters.department || course.department.toLowerCase().includes(selectedFilters.department.toLowerCase())) &&
                (!selectedFilters.courseNumber || course.courseNumber === parseInt(selectedFilters.courseNumber)) &&
                (!selectedFilters.courseSection || course.sectionCode.toLowerCase().includes(selectedFilters.courseSection.toLowerCase())) &&
                (!selectedFilters.days || course.days.toLowerCase().includes(selectedFilters.days.toLowerCase())) &&
                (!selectedFilters.startTime || course.startTime === selectedStartTime) &&
                (!selectedFilters.endTime || course.endTime === selectedEndTime) &&
                (!selectedFilters.professor || course.professor.toLowerCase().includes(selectedFilters.professor.toLowerCase()))
            );
        });
        setFilteredResults(filtered);
        setShowFilters(false);
    };

    const clearFilters = () => {
        setSelectedFilters({
            credits: '',
            department: '',
            courseNumber: '',
            courseSection: '',
            days: '',
            startTime: '',
            endTime: '',
            professor: ''
        });
        setFilteredResults(initialResults);
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
                            <label>Start Time:</label>
                            <input
                                type="time"
                                name="startTime"
                                value={selectedFilters.startTime}
                                onChange={handleFilterChange}
                            />
                        </div>
                        <div className="filter-row">
                            <label>End Time:</label>
                            <input
                                type="time"
                                name="endTime"
                                value={selectedFilters.endTime}
                                onChange={handleFilterChange}
                            />
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
                        <li key={index}>
                            <div className="course-details">
                                {course.department} {course.courseNumber} {course.sectionCode}, Ref: {course.referenceNumber}
                            </div>
                            <div className="course-actions">
                                <button onClick={() => handleAddToSchedule(course)}>Add to Schedule</button>
                            </div>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No search results to display</p>
            )}
        </div>
    );
}

export default SearchComponent;