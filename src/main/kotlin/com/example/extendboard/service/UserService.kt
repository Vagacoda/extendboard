package com.example.extendboard.service

import com.example.extendboard.domain.Role
import com.example.extendboard.domain.User
import com.example.extendboard.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository
) {
    // 회원가입
    fun join(loginId: String, password: String): User {
        if (userRepository.findByLoginId(loginId) != null) {
            throw IllegalArgumentException("이미 존재하는 아이디입니다.")
        }
        val newUser = User(loginId = loginId, password = password, role = Role.COMMON)
        return userRepository.save(newUser)
    }

    // 로그인 검증
    fun login(loginId: String, password: String): User? {
        val user = userRepository.findByLoginId(loginId)
        if (user != null && user.password == password) {
            return user
        }
        return null
    }

    // 비밀번호 변경 (추가됨)
    fun changePassword(userId: Long, newPassword: String) {
        val user = userRepository.findById(userId).orElseThrow()
        user.password = newPassword
        userRepository.save(user)
    }

    // 회원 삭제 (추가됨 - 본인 삭제 불가 검증)
    fun deleteUser(targetUserId: Long, operatorId: Long) {
        if (targetUserId == operatorId) {
            throw IllegalStateException("자기 자신 계정은 삭제할 수 없습니다.")
        }
        userRepository.deleteById(targetUserId)
    }

    // 전체 회원 목록 조회 (추가됨)
    fun findAllUsers(): List<User> = userRepository.findAll()
}