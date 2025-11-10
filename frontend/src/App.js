
import React, { useEffect , useState } from 'react';
import './App.css';
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";
import { format, set } from "date-fns";

function App() {
  
  const today = new Date();
  const [selectedDate, setSelectedDate] = useState(
    format(today, 'yyyy/MM/dd') // 今日の日付を初期値に設定
  );

  const handleChangeSelectedDate = (event) => {
    const formattedDate = format(event, 'yyyy/MM/dd');
    setSelectedDate(formattedDate); // 日付をフォーマットして設定
    setStartYear(event.getFullYear());
    if (event.getMonth() + 1 < 10) {
      setStartMonth(`0${event.getMonth() + 1}`); // 月は0から始まるため、1を足す
    }
    else {
      setStartMonth(event.getMonth() + 1);
    }
    if (event.getDate() < 10) {
      setStartDay(`0${event.getDate()}`); // 日も0から始まるため、1を足す
    }
    else {
      setStartDay(event.getDate());
    }
    setShowDatePicker(false); // 日付選択後にカレンダーを閉じる
  }; 

  const [startYear, setStartYear] = useState(selectedDate.split('/')[0]);
  const [startMonth, setStartMonth] = useState(selectedDate.split('/')[1]);
  const [startDay, setStartDay] = useState(selectedDate.split('/')[2]);
  const [startHour, setStartHour] = useState('');
  const [startMinute, setStartMinute] = useState('');
  const [endHour, setEndHour] = useState('');
  const [endMinute, setEndMinute] = useState('');
  const [title, setTitle] = useState('');
  const [category, setCategory] = useState('default');
  const [showModal, setShowModal] = useState(false);
  const [isRewriteMode, setIsRewriteMode] = useState(false);
  const [recordToRewrite, setRecordToRewrite] = useState(null);

  const handleChangeStartYear = (event) => {
    setStartYear(event.target.value);
  }

  const handleChangeStartMonth = (event) => {
    setStartMonth(event.target.value);
  }

  const handleChangeStartDay = (event) => {
    setStartDay(event.target.value);
  }

  const handleChangeStartHour = (event) => {
    setStartHour(event.target.value);
  }

  const handleChangeStartMinute = (event) => {
    setStartMinute(event.target.value);
  }

  const handleChangeEndHour = (event) => {
    setEndHour(event.target.value);
  }

  const handleChangeEndMinute = (event) => {
    setEndMinute(event.target.value);
  }

  const handleChangeTitle = (event) => {
    setTitle(event.target.value);
  }

  const handleChangeCategory = (event) => {
    setCategory(event.target.value);
  }

  const handleAddButtonClick = () => {
    setIsRewriteMode(false);
    setShowModal(true);
  };

  const [showDatePicker, setShowDatePicker] = useState(false);
  const handleDatePicker = () => {
    setShowDatePicker(!showDatePicker);
  };

  const [response, setResponse] = useState([]);

  const handleSubmitButtonClick = (e) => {
    e.preventDefault(); // ページリロードを防ぐ

    const errorMessage = checkInput();
    if (errorMessage !== '') {
      alert(errorMessage);
      return;
    }

    handleSubmitRecord();
    setShowModal(false);
  };

  const parsed = {
    startMonth: startMonth.length === 1 ? `0${startMonth}` : startMonth,
    startDay: startDay.length === 1 ? `0${startDay}` : startDay,
    startHour: startHour.length === 1 ? `0${startHour}` : startHour,
    startMinute: startMinute.length === 1 ? `0${startMinute}` : startMinute,
    endHour: endHour.length === 1 ? `0${endHour}` : endHour,
    endMinute: endMinute.length === 1 ? `0${endMinute}` : endMinute,
  };

  const handleSubmitRecord = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/add-message', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
            startDate: `${startYear}/${parsed.startMonth}/${parsed.startDay}`,
            startTime: `${parsed.startHour}:${parsed.startMinute}`,
            endTime: `${parsed.endHour}:${parsed.endMinute}`,
            title: title,
            category: category
          }), // 入力内容をJSONで送信
      });

      const data = await res.json(); // サーバーからのレスポンスを取得
      if(res.ok) {
        alert('送信成功');
        console.log('送信成功:', data);
        getMessage();
      } else {
        alert('送信失敗: ' + data.message);
        console.error('送信失敗:', data);
      }
    } catch (error) {
      console.error('送信エラー:', error);
    }
  };

  const getMessage = () => {
    fetch("http://localhost:8080/api/get-message?date=" + selectedDate)
      .then((response) => response.json())
      .then((json) => setResponse(json))
      .catch((error) => console.error("エラー:", error));
  }

  useEffect(() => {
    getMessage();
  }, [selectedDate]);

  const handleDeleteButtonClick = async (e) => {
    const rowIndex = e.target.closest('tr').rowIndex - 1; // 1行目はヘッダーなので-1
    const recordToDelete = response[rowIndex];
    if (!recordToDelete) {
      alert('削除するレコードが見つかりません。');
      return;
    }
    const confirmDelete = window.confirm(`本当に削除しますか？\n日付: ${recordToDelete.date}\n開始時間: ${recordToDelete.startTime}\n終了時間: ${recordToDelete.endTime}\nタイトル: ${recordToDelete.title}\nカテゴリ: ${recordToDelete.category}`);
    if (confirmDelete) {
      handleDeleteRecord(recordToDelete);
    }
  };

  const handleDeleteRecord = async (record) => {
      try {
        const res = await fetch('http://localhost:8080/api/delete-message', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
          },
          body: JSON.stringify({
            startDate: record.date,
            startTime: record.startTime,
            endTime: record.endTime,
            title: record.title,
            category: record.category
          }),
        });
        const data = await res.json();
        if (res.ok) {
          alert('削除成功');
          console.log('削除成功:', data);
          getMessage();
        } else {
          alert('削除失敗: ' + data.message);
          console.error('削除失敗:', data);
        }
      } catch (error) {
        console.error('削除エラー:', error);
        alert('削除中にエラーが発生しました。');
      }
  };

  const handleRewriteButtonClick = (e) => {
    const rowIndex = e.target.closest('tr').rowIndex - 1; // 1行目はヘッダーなので-1
    if (!response[rowIndex]) {
      alert('修正するレコードが見つかりません。');
      return;
    }

    setTitle(response[rowIndex].title);
    setCategory(response[rowIndex].category);
    const startDateParts = response[rowIndex].date.split('/');
    setStartYear(startDateParts[0]);
    setStartMonth(startDateParts[1]);
    setStartDay(startDateParts[2]);
    const startTimeParts = response[rowIndex].startTime.split(':');
    setStartHour(startTimeParts[0]);
    setStartMinute(startTimeParts[1]);
    const endTimeParts = response[rowIndex].endTime.split(':');
    setEndHour(endTimeParts[0]);
    setEndMinute(endTimeParts[1]);

    setRecordToRewrite(response[rowIndex]);
    setIsRewriteMode(true);
    setShowModal(true);
  };

  const handleSubmitRewriteClick = (e) => {
    e.preventDefault(); // ページリロードを防ぐ
    const errorMessage = checkInput();
    if (errorMessage !== '') {
      alert(errorMessage);
      return;
    }
    handleRewriteRecord();
    setShowModal(false);
  }

  const handleRewriteRecord = async () => {
    try {
      const res = await fetch('http://localhost:8080/api/update-message', {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
        },
        body: JSON.stringify({
          startDate: `${startYear}/${parsed.startMonth}/${parsed.startDay}`,
          startTime: `${parsed.startHour}:${parsed.startMinute}`,
          endTime: `${parsed.endHour}:${parsed.endMinute}`,
          title: title,
          category: category,
          oldStartDate: recordToRewrite.date,
          oldStartTime: recordToRewrite.startTime,
          oldEndTime: recordToRewrite.endTime,
          oldTitle: recordToRewrite.title,
          oldCategory: recordToRewrite.category
        }), // 入力内容をJSONで送信
      });
      const data = await res.json(); // サーバーからのレスポンスを取得
      if (res.ok) {
        alert('更新成功');
        console.log('更新成功:', data);
        getMessage();
      } else {
        alert('更新失敗: ' + data.message);
        console.error('更新失敗:', data);
      }
    } catch (error) {
      console.error('更新エラー:', error);
      alert('更新中にエラーが発生しました。');
    }
  };

  const [showUploadModal, setShowUploadModal] = useState(false);
  const handleShowUploadButtonClick = () => {
    setShowUploadModal(true);
  };

  const [uploadFile, setUploadFile] = useState(null);

  const handleChangeUploadFile = (event) => {
    setUploadFile(event.target.files[0]);
  };

  const handleUploadButtonClick = async (e) => {
    e.preventDefault(); // ページリロードを防ぐ
    if (!uploadFile) {
      alert('アップロードするファイルを選択してください。');
      return;
    } 
    const formData = new FormData();
    formData.append('file', uploadFile);
    try {
      const res = await fetch('http://localhost:8080/api/upload-csv', {
        method: 'POST',
        body: formData,
      });
      const data = await res.json(); // サーバーからのレスポンスを取得
      if (res.ok) {
        alert('アップロード成功');
        console.log('アップロード成功:', data);
        getMessage();
      } else {
        alert('アップロード失敗: ' + data.message);
        console.error('アップロード失敗:', data);
      }
    } catch (error) {
      console.error('アップロードエラー:', error);
      alert('アップロード中にエラーが発生しました。');
    }
    setShowUploadModal(false);
    setUploadFile(null); // アップロード後にファイルをリセット
  };

  const checkInput = () => {
    
    let message = '';

    if (!title) {
      message += 'タイトルを入力してください。\n';
    }
    if (category === 'default') {
      message += 'カテゴリを選択してください。\n';
    }
    if (!startYear) {
      message += '開始年を入力してください。\n';
    }
    if (!startMonth) {
      message += '開始月を入力してください。\n';
    }
    if (!startDay) { 
      message += '開始日を入力してください。\n';
    }
    if (!startHour) {
      message += '開始時間の時を入力してください。\n';
    }
    if (!startMinute) {
      message += '開始時間の分を入力してください。\n';
    }
    if (!endHour) {
      message += '終了時間の時を入力してください。\n';
    }
    if (!endMinute) { 
      message += '終了時間の分を入力してください。\n';
    }
    if (isNaN(startYear) || isNaN(startMonth) || isNaN(startDay) || isNaN(startHour) || isNaN(startMinute) || isNaN(endHour) || isNaN(endMinute)) {
      message += '年、月、日、時、分は数字でなければなりません。\n';
    }
    if (startYear > today.getFullYear()) {
      message += '開始年は現在の年以下でなければなりません。\n';
    }
    if (startYear === today.getFullYear() && startMonth > (today.getMonth() + 1)) {
      message += '開始月は現在の月以下でなければなりません。\n';
    }
    if (startYear === today.getFullYear() && startMonth === (today.getMonth() + 1) && startDay > today.getDate()) {
      message += '開始日は現在の日以下でなければなりません。\n';
    }
    if (parseInt(startYear) < 2000 ) {
      message += '開始年は2000年以降でなければなりません。\n';
    }
    if (parseInt(startYear) % 4 === 0) {
      if (parseInt(startMonth) === 2 && parseInt(startDay) > 29) {
        message += 'うるう年の2月は29日までしかありません。\n';
      }
    }
    else {
      if (parseInt(startMonth) === 2 && parseInt(startDay) > 28) {
        message += '2月は28日までしかありません。\n';
      }
    }
    if ((parseInt(startMonth) === 4 || parseInt(startMonth) === 6 || parseInt(startMonth) === 9 || parseInt(startMonth) === 11) && parseInt(startDay) > 30) {
      message += '4月、6月、9月、11月は30日までしかありません。\n';
    }
    if (startMonth < 1 || startMonth > 12) {
      message += '開始月は1から12の間でなければなりません。\n';
    }
    if (startDay < 1 || startDay > 31) {
      message += '開始日は1から31の間でなければなりません。\n';
    }
    if (startHour < 0 || startHour > 23) {
      message += '開始時間の時は0から23の間でなければなりません。\n';
    }
    if (startMinute < 0 || startMinute > 59) {
      message += '開始時間の分は0から59の間でなければなりません。\n';
    }
    if (endHour < 0 || endHour > 23) {
      message += '終了時間の時は0から23の間でなければなりません。\n';
    }
    if (endMinute < 0 || endMinute > 59) {
      message += '終了時間の分は0から59の間でなければなりません。\n';
    } 
    if (startHour && startMinute && endHour && endMinute) {
      if (parseInt(endHour) < parseInt(startHour) || (parseInt(endHour) === parseInt(startHour) && parseInt(endMinute) <= parseInt(startMinute))) {
        message += '終了時間は開始時間より後でなければなりません。\n';
      }
    }
    
    if (isRewriteMode && recordToRewrite) {
        if (recordToRewrite.date === `${startYear}/${parsed.startMonth}/${parsed.startDay}` &&
            recordToRewrite.startTime === `${parsed.startHour}:${parsed.startMinute}` &&
            recordToRewrite.endTime === `${parsed.endHour}:${parsed.endMinute}` &&
            recordToRewrite.title === title &&
            recordToRewrite.category === category) {
          message += '変更がありません。修正する内容を入力してください。\n';
        }
    }

    return message;

  };

  return (
    
  <div className='center'>
    <button
      onClick={handleDatePicker}
      style={{ padding: '8px 16px', fontSize: '16px' }}>
      <span>日付選択</span>
    </button>

    {showDatePicker && (
      <div className='modal-overlay' onClick={() => setShowDatePicker(false)}>
        <div className='modal-content center' onClick={(e) => e.stopPropagation()}> 
          <DatePicker
            selected={new Date(selectedDate)}
            onChange={handleChangeSelectedDate}
            inline
            dateFormat="yyyy/MM/dd"
            className="date-picker"
            maxDate={new Date()} 
          />
        </div>
      </div>
    )}

    <p>{selectedDate}</p>
    <table border="1">
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
              <button onClick={handleDeleteButtonClick}>削除</button>
            </td>
            <td>
              <button onClick={handleRewriteButtonClick}>修正</button>
              </td>
          </tr>
        ))}
      </tbody>
    </table>

    <br></br>

    <button
      onClick={handleAddButtonClick} style={{ padding: '8px 16px', fontSize: '16px' }}>
        <span>スケジュール入力</span>
    </button>

    <br></br>

    <button
      onClick={handleShowUploadButtonClick} style={{ padding: '8px 16px', fontSize: '16px' }}>
        <span>スケジュールアップロード(.csv)</span>
    </button>

    {showModal && (
      <div className='modal-overlay' onClick={() => setShowModal(false)}>
        <div className='modal-content' onClick={(e) => e.stopPropagation()}> 
          <form className='modal-form'>
            <div>
              <label className='form-label'>タイトル：</label>
              <input
                className='form-content'
                type="text"
                value={title}
                onChange={handleChangeTitle}
                placeholder="タイトル"
              />
            </div>
            <div>
              <label className='form-label'>カテゴリ：</label>
              <select
                className='form-select' value={category} onChange={handleChangeCategory}>
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
              <input
                className='form-date'
                type="text"
                value={startYear}
                onChange={handleChangeStartYear}
                placeholder="年"
              />
              <span> / </span>
              <input
                className='form-date'
                type="text"
                value={startMonth}
                onChange={handleChangeStartMonth}
                placeholder="月"
              />
              <span> / </span>
              <input
                className='form-date'
                type="text"
                value={startDay}
                onChange={handleChangeStartDay}
                placeholder="日"
              />
            </div>
            <div>
              <label className='form-label'>開始時間：</label>
              <input
                className='form-date'
                type="text"
                value={startHour}
                onChange={handleChangeStartHour}
                placeholder="時"
              />
              <span> : </span>
              <input
                className='form-date'
                type="text"
                value={startMinute}
                onChange={handleChangeStartMinute}
                placeholder="分"
              />
            </div>
            <div>
              <label className='form-label'>終了時間：</label>
              <input
                className='form-date'
                type="text"
                value={endHour}
                onChange={handleChangeEndHour}
                placeholder="時"
              />
              <span> : </span>
              <input
                className='form-date'
                type="text"
                value={endMinute}
                onChange={handleChangeEndMinute}
                placeholder="分"
              />
            </div>
            {!isRewriteMode && (
              <button
                className='form-button'
                onClick={handleSubmitButtonClick} 
                type="submit">
                  <span>送信</span>
              </button>
            )}
            {isRewriteMode && (
              <button
                className='form-button'
                onClick={handleSubmitRewriteClick} 
                type="rewrite">
                  <span>更新</span>
              </button>
            )}
          </form>
        </div>
      </div>
    )}

    {showUploadModal && (
      <div className='modal-overlay' onClick={() => setShowUploadModal(false)}>
        <div className='modal-content' onClick={(e) => e.stopPropagation()}> 
          <form className='modal-form'>
            <input
              type="file"
              accept=".csv"
              onChange={handleChangeUploadFile}
            />
            <button
              className='upload-form-button'
              onClick={handleUploadButtonClick} 
              type="submit">
                <span>アップロード</span>
            </button>
          </form>
        </div>
      </div>
    )}

  </div>

  );
}

export default App;
