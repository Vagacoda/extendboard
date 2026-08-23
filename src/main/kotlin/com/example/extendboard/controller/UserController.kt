package com.example.extendboard.controller

import com.example.extendboard.domain.Role
import com.example.extendboard.domain.User
import com.example.extendboard.service.UserService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class UserController(
    private val userService: UserService
) {
    // 회원가입 화면
    @GetMapping("/join")
    fun joinForm(): String = "join"

    // 회원가입 처리
    @PostMapping("/join")
    fun join(@RequestParam loginId: String, @RequestParam password: String): String {
        return try {
            userService.join(loginId, password)
            "redirect:/login"
        } catch (e: IllegalArgumentException) {
            "redirect:/join?error"
        }
    }

    // 로그인 화면
    @GetMapping("/login")
    fun loginForm(): String = "login"

    // 로그인 처리
    @PostMapping("/login")
    fun login(
        @RequestParam loginId: String,
        @RequestParam password: String,
        session: HttpSession
    ): String {
        val user = userService.login(loginId, password)
        if (user != null) {
            session.setAttribute("loginUser", user)
            return "redirect:/"
        }
        return "redirect:/login?error"
    }

    // 로그아웃 처리
    @GetMapping("/logout")
    fun logout(session: HttpSession): String {
        session.invalidate()
        return "redirect:/"
    }

    // 마이페이지
    @GetMapping("/mypage")
    fun myPage(session: HttpSession, model: Model): String {
        val loginUser = session.getAttribute("loginUser") as User? ?: return "redirect:/login"
        model.addAttribute("user", loginUser)
        return "mypage"
    }

    // 비밀번호 변경 처리
    @PostMapping("/user/change-password")
    fun changePassword(@RequestParam newPassword: String, session: HttpSession): String {
        val loginUser = session.getAttribute("loginUser") as User? ?: return "redirect:/login"
        userService.changePassword(loginUser.id!!, newPassword)
        return "redirect:/mypage?success"
    }

    // 계정 탈퇴 (본인 삭제 처리)
    @PostMapping("/user/delete-self")
    fun deleteSelf(session: HttpSession): String {
        val loginUser = session.getAttribute("loginUser") as User? ?: return "redirect:/login"
        try {
            userService.deleteUser(loginUser.id!!, loginUser.id!!)
        } catch (e: Exception) {
            return "redirect:/mypage?error"
        }
        session.invalidate()
        return "redirect:/"
    }

    // [ADMIN 전용] 전체 회원 목록 조회
    @GetMapping("/admin/users")
    fun adminUserList(session: HttpSession, model: Model): String {
        val loginUser = session.getAttribute("loginUser") as User?
        if (loginUser == null || loginUser.role != Role.ADMIN) return "redirect:/"

        model.addAttribute("users", userService.findAllUsers())
        model.addAttribute("loginUser", loginUser)
        return "admin_users"
    }

    // [ADMIN 전용] 회원 강제 삭제
    @GetMapping("/admin/user/delete/{id}")
    fun adminDeleteUser(@PathVariable id: Long, session: HttpSession): String {
        val loginUser = session.getAttribute("loginUser") as User?
        if (loginUser == null || loginUser.role != Role.ADMIN) return "redirect:/"

        try {
            userService.deleteUser(id, loginUser.id!!)
        } catch (e: Exception) {
            return "redirect:/admin/users?error"
        }
        return "redirect:/admin/users"
    }
}