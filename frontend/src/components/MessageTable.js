import React from 'react';

// Props:
// - response: array of messages
// - onDeleteClick(event)
// - onEditClick(event)
const MessageTable = ({ response, onDeleteClick, onEditClick }) => {
  return (
    <table className="messages-table">
      <thead>
        <tr>
          <th>日付</th>
          <th>開始時間</th>
          <th>終了時間</th>
          <th>タイトル</th>
          <th>カテゴリ</th>
          <th>削除</th>
          <th>修正</th>
        </tr>
      </thead>
      <tbody>
        {response.map((msg, index) => (
          <tr key={index}>
            <td>{msg.date}</td>
            <td>{msg.startTime}</td>
            <td>{msg.endTime}</td>
            <td>{msg.title}</td>
            <td>
              {msg.category === 'society' && '社会義務'}
              {msg.category === 'life' && '生理機能'}
              {msg.category === 'improvement' && '自己成長'}
              {msg.category === 'entertainment' && '娯楽'}
              {msg.category === 'other' && 'その他'}
            </td>
            <td>
              <button className="btn" onClick={onDeleteClick}>削除</button>
            </td>
            <td>
              <button className="btn" onClick={onEditClick}>修正</button>
            </td>
          </tr>
        ))}
      </tbody>
    </table>
  );
};

export default MessageTable;
