package com.example.extendboard.controller

import com.example.extendboard.domain.User
import com.example.extendboard.service.ArticleService
import jakarta.servlet.http.HttpSession
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@Controller
class ArticleController(
    private val articleService: ArticleService
) {
    // 메인 게시판 화면
    @GetMapping("/")
    fun mainPage(session: HttpSession, model: Model): String {
        val loginUser = session.getAttribute("loginUser") as User?
        model.addAttribute("loginUser", loginUser)
        model.addAttribute("articles", articleService.findAll())
        return "board"
    }

    // 글 작성 (@RequestParam imageFile 추가)
    @PostMapping("/write")
    fun write(
        @RequestParam title: String,
        @RequestParam content: String,
        @RequestParam(required = false) imageFile: MultipartFile?, // 사진 파일 수신
        session: HttpSession
    ): String {
        val loginUser = session.getAttribute("loginUser") as User?
            ?: return "redirect:/login"

        // 서비스로 imageFile 함께 전달
        articleService.save(title, content, loginUser, imageFile)
        return "redirect:/"
    }

    // 글 수정 화면
    @GetMapping("/article/edit/{id}")
    fun editForm(@PathVariable id: Long, session: HttpSession, model: Model): String {
        val loginUser = session.getAttribute("loginUser") as User?
            ?: return "redirect:/login"

        val article = articleService.findById(id)
        model.addAttribute("article", article)
        return "edit"
    }

    // 글 수정 처리
    @PostMapping("/article/edit/{id}")
    fun edit(
        @PathVariable id: Long,
        @RequestParam title: String,
        @RequestParam content: String,
        session: HttpSession
    ): String {
        val loginUser = session.getAttribute("loginUser") as User?
            ?: return "redirect:/login"

        articleService.update(id, title, content, loginUser)
        return "redirect:/"
    }

    // 글 삭제 처리
    @GetMapping("/article/delete/{id}")
    fun delete(@PathVariable id: Long, session: HttpSession): String {
        val loginUser = session.getAttribute("loginUser") as User?
            ?: return "redirect:/login"

        articleService.delete(id, loginUser)
        return "redirect:/"
    }
}