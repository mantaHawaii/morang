package com.gusto.pikgoogoo.data

import android.os.Handler
import android.os.Looper
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject

class ArticleMapper
@Inject
constructor(): EntityMapper<ArticleEntity, Article> {
    override fun mapFromEntity(entity: ArticleEntity): Article {
        return Article(
            id = entity.id,
            content = entity.content,
            subjectId = entity.subjectId,
            imageUrl = entity.imageUrl,
            voteCount = entity.voteCount,
            commentCount = entity.commentCount,
            createdDatetime = entity.createdDatetime,
            voteCountFor = entity.voteCountFor,
            cropImage = entity.cropImage
        )
    }

    override fun mapToEntity(domainModel: Article): ArticleEntity {
        return ArticleEntity(
            id = domainModel.id,
            content = domainModel.content,
            subjectId = domainModel.subjectId,
            imageUrl = domainModel.imageUrl,
            voteCount = domainModel.voteCount,
            commentCount = domainModel.commentCount,
            createdDatetime = domainModel.createdDatetime,
            voteCountFor = domainModel.voteCountFor,
            cropImage = domainModel.cropImage
        )
    }

    fun mapFromEntityList(entities: List<ArticleEntity>): List<Article>{
        return entities.map { mapFromEntity(it) }
    }

}