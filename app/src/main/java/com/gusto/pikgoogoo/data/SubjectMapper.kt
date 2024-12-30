package com.gusto.pikgoogoo.data

import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject

class SubjectMapper
@Inject
constructor(): EntityMapper<SubjectEntity, Subject> {

    override fun mapFromEntity(entity: SubjectEntity): Subject {
        return Subject(
            id = entity.id,
            title = entity.title,
            userId = entity.userId,
            userNicknmae = entity.nickname,
            userGrade = entity.grade,
            userGradeIcon = entity.gradeIcon,
            voteCount = entity.voteCount,
            commentCount = entity.commentCount,
            createdDatetime = entity.createdDatetime,
            categoryId = entity.categoryId,
            categoryName = entity.categoryName
        )
    }

    override fun mapToEntity(domainModel: Subject): SubjectEntity {
        return SubjectEntity(
            id = domainModel.id,
            title = domainModel.title,
            userId = domainModel.userId,
            nickname = domainModel.userNicknmae,
            grade = domainModel.userGrade,
            gradeIcon = domainModel.userGradeIcon,
            voteCount = domainModel.voteCount,
            commentCount = domainModel.commentCount,
            createdDatetime = domainModel.createdDatetime,
            categoryId = domainModel.categoryId,
            categoryName = domainModel.categoryName
        )
    }

    fun mapFromEntityList(entities: List<SubjectEntity>): List<Subject>{
        return entities.map { mapFromEntity(it) }
    }

}