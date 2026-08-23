package com.example.extendboard.repository

import com.example.extendboard.domain.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    // 아이디(loginId)로 회원을 찾는 기능 추가 (findByName처럼 JPA가 자동으로 SQL 만들어줌)
    fun findByLoginId(loginId: String): User?
}