package com.example.extendboard.domain

import jakarta.persistence.*

@Entity
class Article(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    var title: String,
    var content: String,

    var imageUrl: String? = null, // 사진은 없을 수도 있으므로 null 허용(?)

    // 중요: 글 하나(Many)는 한 명의 유저(One)가 작성함
    @ManyToOne
    @JoinColumn(name = "user_id")
    val author: User // 이 글의 주인(작성자) 정보
) {
    // 글 수정용 함수
    fun update(title: String, content: String, imageUrl: String?) {
        this.title = title
        this.content = content
        this.imageUrl = imageUrl
    }
}