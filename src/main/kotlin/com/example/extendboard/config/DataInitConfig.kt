package com.example.extendboard.config

import com.example.extendboard.domain.Role
import com.example.extendboard.domain.User
import com.example.extendboard.repository.UserRepository
import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DataInitConfig {

    // 서버가 켜질 때 딱 한 번 실행되는 코드
    @Bean
    fun initAdmin(userRepository: UserRepository) = CommandLineRunner {
        // root 아이디가 DB에 없는지 확인하고
        if (userRepository.findByLoginId("root") == null) {
            val admin = User(
                loginId = "root",
                password = "1q2w3e4r",
                role = Role.ADMIN
            )
            // 관리자 계정 생성 (보통 AUTO_INCREMENT 1번을 부여받게 됩니다)
            userRepository.save(admin)
        }
    }
}