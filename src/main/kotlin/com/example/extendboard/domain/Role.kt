package com.example.extendboard.domain

// 유저의 권한을 정의하는 클래스
enum class Role {
    GUEST,  // 비회원 (DB에는 저장 안 됨, 로그인 안 한 상태를 의미)
    COMMON, // 일반 회원 (글쓰기, 내 글 수정/삭제 가능)
    ADMIN   // 최고 관리자 (모든 권한)
}