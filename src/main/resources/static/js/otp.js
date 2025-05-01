    // OTP 모달 열기 함수
    function openOtpModal(callback) {
        otpSuccessCallback = callback; // OTP 성공 시 실행할 함수 저장
        const otpModal = document.getElementById('otpModal');
        otpModal.style.display = 'block';
        startTimer();
        postData('/OTP/generate' ,{} , OTPCallback)
    }

    function OTPCallback(response){
        document.getElementById('otpNumber').textContent = response.otp;
    }
    // OTP 인증 타이머 시작 함수
    function startTimer() {
        let timeRemaining = 5 * 60; // 5분 (초 단위)

        countdown = setInterval(function() {
            timeRemaining--;

            const minutes = Math.floor(timeRemaining / 60);
            const seconds = timeRemaining % 60;

            // 타이머 업데이트
            timerDisplay.textContent = `남은 시간: ${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`;

            // 시간이 0이 되면 타이머 멈추고 팝업 닫기
            if (timeRemaining <= 0) {
                clearInterval(countdown);
                otpModal.style.display = 'none';
                alert("OTP 인증 시간이 만료되었습니다.");
            }
        }, 1000); // 1초마다 업데이트
    }
document.addEventListener('DOMContentLoaded', function() {
    // OTP 팝업 관련 요소들
    const otpModal = document.getElementById('otpModal');
    const closeOtpModalBtn = document.getElementById('closeOtpModal');
    const otpImage = document.getElementById('otpImage');
    const otpInput = document.getElementById('otpInput');
    const verifyOtpBtn = document.getElementById('verifyOtpBtn');
    const timerDisplay = document.getElementById('time');
    let countdown;

    // OTP 인증 팝업 닫기 함수
    if (closeOtpModalBtn) {
        closeOtpModalBtn.addEventListener('click', function() {
            otpModal.style.display = 'none';
            clearInterval(countdown); // 타이머 멈추기
        });
    }

    // OTP 인증 버튼 클릭 시 동작
    if (verifyOtpBtn) {
        verifyOtpBtn.addEventListener('click', function() {
            const otp = otpInput.value;
            console.log(otp);
            param = {};
            param.otp = otp;
            postData('/OTP/verify_otp' , param , function(response){
                if(response.status == "success"){
                    alert("OTP 인증이 완료되었습니다.");
                    otpModal.style.display = 'none'; // 팝업 닫기
                    clearInterval(countdown); // 타이머 멈추기

                    if (otpSuccessCallback) {
                        otpSuccessCallback(); // ✅ OTP 성공 후 콜백 실행
                        otpSuccessCallback = null; // 재사용 방지
                    }
                }else{
                    alert(response.message);
                }
            })
        });
    }

    // 이체 버튼 클릭 시 OTP 팝업 열기 예시
    const transferButton = document.getElementById('transferButton');
    if (transferButton) {
        transferButton.addEventListener('click', openOtpModal);
    }
});