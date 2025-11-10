import React from 'react';

// Props (many form values and handlers):
// visible, onClose, title, category, startYear, startMonth, startDay, startHour, startMinute,
// endHour, endMinute, handlers: onChangeX, onSubmit, isRewriteMode, onSubmitRewrite
const ScheduleModal = ({
  visible,
  onClose,
  title,
  category,
  startYear,
  startMonth,
  startDay,
  startHour,
  startMinute,
  endHour,
  endMinute,
  onChangeTitle,
  onChangeCategory,
  onChangeStartYear,
  onChangeStartMonth,
  onChangeStartDay,
  onChangeStartHour,
  onChangeStartMinute,
  onChangeEndHour,
  onChangeEndMinute,
  onSubmit,
  isRewriteMode,
  onSubmitRewrite,
}) => {
  if (!visible) return null;

  return (
    <div className='modal-overlay' onClick={onClose}>
      <div className='modal-content' onClick={(e) => e.stopPropagation()}>
        <form className='modal-form'>
          <div>
            <label className='form-label'>タイトル：</label>
            <input className='form-content' type="text" value={title} onChange={onChangeTitle} placeholder="タイトル" />
          </div>

          <div>
            <label className='form-label'>カテゴリ：</label>
            <select className='form-select' value={category} onChange={onChangeCategory}>
              <option value="default">選んでください</option>
              <option value="society">社会義務</option>
              <option value="life">生理機能</option>
              <option value="improvement">自己成長</option>
              <option value="entertainment">娯楽</option>
              <option value="other">その他</option>
            </select>
          </div>

          <div>
            <label className='form-label'>開始日付：</label>
            <input className='form-date' type="text" value={startYear} onChange={onChangeStartYear} placeholder="年" />
            <span> / </span>
            <input className='form-date' type="text" value={startMonth} onChange={onChangeStartMonth} placeholder="月" />
            <span> / </span>
            <input className='form-date' type="text" value={startDay} onChange={onChangeStartDay} placeholder="日" />
          </div>

          <div>
            <label className='form-label'>開始時間：</label>
            <input className='form-date' type="text" value={startHour} onChange={onChangeStartHour} placeholder="時" />
            <span> : </span>
            <input className='form-date' type="text" value={startMinute} onChange={onChangeStartMinute} placeholder="分" />
          </div>

          <div>
            <label className='form-label'>終了時間：</label>
            <input className='form-date' type="text" value={endHour} onChange={onChangeEndHour} placeholder="時" />
            <span> : </span>
            <input className='form-date' type="text" value={endMinute} onChange={onChangeEndMinute} placeholder="分" />
          </div>

          {!isRewriteMode ? (
            <button className='form-button' onClick={onSubmit} type="submit">送信</button>
          ) : (
            <button className='form-button' onClick={onSubmitRewrite} type="button">更新</button>
          )}
        </form>
      </div>
    </div>
  );
};

export default ScheduleModal;
