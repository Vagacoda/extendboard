package com.example.extendboard.domain

import jakarta.persistence.*

@Entity
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null, // 고유 번호 (1번, 2번...)

    @Column(unique = true)
    val loginId: String,  // 로그인 아이디 (예: root) - 중복 불가

    var password: String, // 비밀번호 (수정 가능해야 하므로 var)

    @Enumerated(EnumType.STRING)
    var role: Role = Role.COMMON // 기본값은 일반 회원
)