package com.example.extendboard.service

import com.example.extendboard.domain.Article
import com.example.extendboard.domain.Role
import com.example.extendboard.domain.User
import com.example.extendboard.repository.ArticleRepository
import com.example.extendboard.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.util.UUID

@Service
@Transactional
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val userRepository: UserRepository // DB 재조회용 추가
) {
    fun findAll(): List<Article> = articleRepository.findAll()

    fun findById(id: Long): Article = articleRepository.findById(id)
        .orElseThrow { IllegalArgumentException("해당 게시글이 없습니다. id=$id") }

    // 파일 저장 (절대 경로 지정으로 500 에러 방지)
    private fun saveFile(file: MultipartFile?): String? {
        if (file == null || file.isEmpty) return null

        // 현재 프로젝트 디렉토리 안의 uploads 폴더 경로 생성
        val uploadDir = File(System.getProperty("user.dir"), "uploads")
        if (!uploadDir.exists()) {
            uploadDir.mkdirs()
        }

        val fileName = "${UUID.randomUUID()}_${file.originalFilename}"
        val saveFile = File(uploadDir, fileName)

        // absoluteFile로 절대 경로를 전달해야 톰캣 에러가 나지 않음
        file.transferTo(saveFile.absoluteFile)

        return "/images/$fileName"
    }

    // 글 저장 (세션의 유저 대신 DB의 싱싱한 유저 객체 연결)
    fun save(title: String, content: String, loginUser: User, imageFile: MultipartFile?): Article {
        val imageUrl = saveFile(imageFile)

        // 세션 유저의 id로 DB에서 진짜 유저 엔티티를 조회해 옴
        val author = userRepository.findById(loginUser.id!!)
            .orElseThrow { IllegalArgumentException("존재하지 않는 유저입니다.") }

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