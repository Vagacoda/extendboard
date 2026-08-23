package com.example.extendboard.service

import com.example.extendboard.domain.Article
import com.example.extendboard.domain.Role
import com.example.extendboard.domain.User
import com.example.extendboard.repository.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class ArticleService(
    private val articleRepository: ArticleRepository
) {
    fun findAll(): List<Article> = articleRepository.findAll()

    fun findById(id: Long): Article = articleRepository.findById(id)
        .orElseThrow { IllegalArgumentException("해당 게시글이 없습니다. id=$id") }

    // 글 저장
    fun save(title: String, content: String, author: User): Article {
        val article = Article(title = title, content = content, author = author)
        return articleRepository.save(article)
    }

    // 글 수정 (본인 또는 ADMIN 검증)
    fun update(id: Long, title: String, content: String, loginUser: User) {
        val article = findById(id)
        if (article.author.id != loginUser.id && loginUser.role != Role.ADMIN) {
            throw IllegalStateException("수정 권한이 없습니다.")
        }
        article.update(title, content, article.imageUrl)
    }

    // 글 삭제 (본인 또는 ADMIN 검증)
    fun delete(id: Long, loginUser: User) {
        val article = findById(id)
        if (article.author.id != loginUser.id && loginUser.role != Role.ADMIN) {
            throw IllegalStateException("삭제 권한이 없습니다.")
        }
        articleRepository.delete(article)
    }

    // ArticleService.kt 내부에 파일 저장 로직 추가

    private fun saveFile(file: org.springframework.web.multipart.MultipartFile?): String? {
        if (file == null || file.isEmpty) return null
        val uploadDir = java.io.File("uploads")
        if (!uploadDir.exists()) uploadDir.mkdirs()

        val fileName = "${java.util.UUID.randomUUID()}_${file.originalFilename}"
        val saveFile = java.io.File(uploadDir, fileName)
        file.transferTo(saveFile)
        return "/images/$fileName" // 웹 접근 경로 반환
    }

    // save 및 update 함수에 file 파라미터 반영
    fun save(title: String, content: String, author: User, imageFile: org.springframework.web.multipart.MultipartFile?): Article {
        val imageUrl = saveFile(imageFile)
        val article = Article(title = title, content = content, author = author, imageUrl = imageUrl)
        return articleRepository.save(article)
    }
}