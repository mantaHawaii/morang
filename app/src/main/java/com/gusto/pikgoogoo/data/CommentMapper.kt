package com.gusto.pikgoogoo.data

import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject

class CommentMapper
@Inject
constructor(): EntityMapper<CommentEntity, Comment> {
    override fun mapFromEntity(entity: CommentEntity): Comment {
        return Comment(
            id = entity.id,
            comment = entity.comment,
            userId = entity.userId,
            nickname = entity.nickname,
            grade = entity.grade,
            gradeIcon = entity.gradeIcon,
            subjectId = entity.subjectId,
            articleId = entity.articleId,
            likeCount = entity.likeCount,
            createdDatetime = entity.createdDatetime,
            content = entity.content
        )
    }

    override fun mapToEntity(domainModel: Comment): CommentEntity {
        return CommentEntity(
            id = domainModel.id,
            comment = domainModel.comment,
            userId = domainModel.userId,
            nickname = domainModel.nickname,
            grade = domainModel.grade,
            gradeIcon = domainModel.gradeIcon,
            subjectId = domainModel.subjectId,
            articleId = domainModel.articleId,
            likeCount = domainModel.likeCount,
            createdDatetime = domainModel.createdDatetime,
            content = domainModel.content
        )
    }

    fun mapFromEntityList(entities: List<CommentEntity>): List<Comment>{
        return entities.map { mapFromEntity(it) }
    }

}