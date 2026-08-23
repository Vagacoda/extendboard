package com.example.extendboard.service

import com.example.extendboard.domain.Article
import com.example.extendboard.domain.Role
import com.example.extendboard.domain.User
import com.example.extendboard.repository.ArticleRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.UUID

@Service
@Transactional
class ArticleService(
    private val articleRepository: ArticleRepository
) {
    fun findAll(): List<Article> = articleRepository.findAll()

    fun findById(id: Long): Article = articleRepository.findById(id)
        .orElseThrow { IllegalArgumentException("해당 게시글이 없습니다. id=$id") }

    // 파일 저장 처리 함수
    private fun saveFile(file: MultipartFile?): String? {
        if (file == null || file.isEmpty) return null

        // 프로젝트 루트 폴더 아래 uploads 폴더 생성
        val uploadDir = File("uploads")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        val fileName = "${UUID.randomUUID()}_${file.originalFilename}"
        val saveFile = File(uploadDir, fileName)
        file.transferTo(saveFile)

        return "/images/$fileName" // 웹에서 접근할 경로 리턴
    }

    // 글 저장 (사진 파일 포함)
    fun save(title: String, content: String, author: User, imageFile: MultipartFile?): Article {
        val imageUrl = saveFile(imageFile)
        val article = Article(title = title, content = content, author = author, imageUrl = imageUrl)
        return articleRepository.save(article)
    }

    // 글 수정
    fun update(id: Long, title: String, content: String, loginUser: User) {
        val article = findById(id)
        if (article.author.id != loginUser.id && loginUser.role != Role.ADMIN) {
            throw IllegalStateException("수정 권한이 없습니다.")
        }
        article.update(title, content, article.imageUrl)
    }

    // 글 삭제
    fun delete(id: Long, loginUser: User) {
        val article = findById(id)
        if (article.author.id != loginUser.id && loginUser.role != Role.ADMIN) {
            throw IllegalStateException("삭제 권한이 없습니다.")
        }
        articleRepository.delete(article)
    }
}