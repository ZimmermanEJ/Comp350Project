import React, { useState } from 'react';

function CreateScheduleModal({ isOpen, onClose, onCreate }) {
  const [scheduleName, setScheduleName] = useState('');

  const handleCreate = () => {
    onCreate(scheduleName);
    setScheduleName('');
    onClose();
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
        <button className="confirm-button" onClick={handleCreate}>Create</button>
      </div>
    </div>
  );
}

export default CreateScheduleModal;