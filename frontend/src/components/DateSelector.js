import React, { useRef, useEffect } from 'react';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';

// Props:
// - selectedDate (string, yyyy/MM/dd)
// - onChange(date: Date)
// - showDatePicker (bool)
// - toggleDatePicker()
const DateSelector = ({ selectedDate, onChange, showDatePicker, toggleDatePicker }) => {
  const parsed = new Date(selectedDate);

  const popRef = useRef(null);

  useEffect(() => {
    function handleClickOutside(e) {
      if (showDatePicker && popRef.current && !popRef.current.contains(e.target)) {
        // toggle to close popover when clicking outside
        toggleDatePicker();
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, [showDatePicker, toggleDatePicker]);

  return (
    <>
      {showDatePicker && (
        <div className='datepicker-popover' ref={popRef} onClick={(e) => e.stopPropagation()}>
          <div className='popover-content'>
            <DatePicker
              selected={parsed}
              onChange={onChange}
              inline
              monthsShown={2}
              dateFormat="yyyy/MM/dd"
              className="date-picker"
              maxDate={new Date()}
            />
          </div>
        </div>
      )}
    </>
  );
};

export default DateSelector;
