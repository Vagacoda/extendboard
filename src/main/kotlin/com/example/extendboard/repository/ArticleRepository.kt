package com.example.extendboard.repository

import com.example.extendboard.domain.Article
import org.springframework.data.jpa.repository.JpaRepository

interface ArticleRepository : JpaRepository<Article, Long>