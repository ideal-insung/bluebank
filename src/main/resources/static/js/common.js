// common.js
function showAlert(message) {
    alert(message);
}

function setElementText(selector, text) {
    const element = document.querySelector(selector);
    if (element) {
        element.textContent = text;
    }
}

// GET 요청 처리 함수
function getData(url, params, callback) {
    let queryString = new URLSearchParams(params).toString();
    let fullUrl = url + (queryString ? '?' + queryString : '');

    fetch(fullUrl)
        .then(response => response.json())
        .then(data => callback(data))
        .catch(error => console.error('Error:', error));
}

// POST 요청 처리 함수
function postData(url, data, callback) {
    fetch(url, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: JSON.stringify(data)
    })
        .then(response => response.json())
        .then(data => callback(data))
        .catch(error => console.error('Error:', error));
}

// 1. DOM 요소 가져오기
function $(selector) {
    return document.querySelector(selector);
}

// 2. 이벤트 리스너 추가
function addEvent(selector, eventType, callback) {
    const element = $(selector);
    if (element) {
        element.addEventListener(eventType, callback);
    }
}
// 5. 쿠키 설정
function setCookie(name, value, days) {
    const expires = new Date();
    expires.setTime(expires.getTime() + (days * 24 * 60 * 60 * 1000));
    document.cookie = `${name}=${value}; expires=${expires.toUTCString()}; path=/`;
}

// 6. 쿠키 가져오기
function getCookie(name) {
    const nameEQ = name + "=";
    const ca = document.cookie.split(';');
    for (let i = 0; i < ca.length; i++) {
        let c = ca[i].trim();
        if (c.indexOf(nameEQ) === 0) {
            return c.substring(nameEQ.length, c.length);
        }
    }
    return null;
}

// 7. 쿠키 삭제
function deleteCookie(name) {
    document.cookie = `${name}=; expires=Thu, 01 Jan 1970 00:00:00 UTC; path=/`;
}

// 8. 숫자 포맷 (천 단위 구분기호 추가)
function formatNumber(num) {
    return num.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
}

// 9. 이메일 유효성 검사
function validateEmail(email) {
    const regex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return regex.test(email);
}

// 10. 전화번호 유효성 검사 (국내 전화번호)
function validatePhoneNumber(phoneNumber) {
    const regex = /^(010\d{4}\d{4})$/;
    return regex.test(phoneNumber);
}

// 11. 문자열 비어있는지 확인
function isEmpty(str) {
    return !str || str.trim() === "";
}

// 12. 날짜 형식 변환 (YYYY-MM-DD)
function formatDate(date) {
    const d = new Date(date);
    const year = d.getFullYear();
    const month = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${year}-${month}-${day}`;
}

// 13. 페이지 리디렉션
function redirect(url) {
    window.location.href = url;
}

// 14. 로컬 스토리지에 데이터 저장
function setLocalStorage(key, value) {
    localStorage.setItem(key, JSON.stringify(value));
}

// 15. 로컬 스토리지에서 데이터 가져오기
function getLocalStorage(key) {
    const value = localStorage.getItem(key);
    return value ? JSON.parse(value) : null;
}

// 16. 로컬 스토리지에서 데이터 삭제
function removeLocalStorage(key) {
    localStorage.removeItem(key);
}

// 모달 열기
function openModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = "block";
    resetModalInputs(modalId);
}

// 모달 닫기
function closeModal(modalId) {
    document.getElementById(modalId).style.display = "none";
}

// 모달 외부 클릭 시 닫기
window.onclick = function(event) {
    if (event.target.classList.contains('modal')) {
        closeModal(event.target.id);
    }
}

function formatAccountNumber(accountNumber) {
    // 접두어를 제외한 숫자 부분 추출 (접두어는 3자리로 가정)
    const accountDigits = accountNumber.substring(3);  // "1169682776" 부분

    // 4자리마다 '-' 추가
    let formattedAccountNumber = '';
    for (let i = 0; i < accountDigits.length; i++) {
        if (i > 0 && i % 4 === 0) {
            formattedAccountNumber += '-';  // 4자리마다 '-' 추가
        }
        formattedAccountNumber += accountDigits.charAt(i);
    }

    return formattedAccountNumber;
}

// 입력 필드 초기화 공통 함수
function resetModalInputs(modalId) {
    const modal = document.getElementById(modalId);
    const inputs = modal.querySelectorAll("input, select, textarea");
    inputs.forEach(input => {
        input.removeAttribute("readonly");
        input.removeAttribute("disabled");
        input.value = "";  // 입력값 초기화
        if (input.type === "checkbox" || input.type === "radio") {
            input.checked = false;  // 체크박스/라디오 버튼 초기화
        }
    });

    // 버튼들 활성화
    const buttons = modal.querySelectorAll("button");
    buttons.forEach(button => {
        button.removeAttribute("disabled");
    });
}