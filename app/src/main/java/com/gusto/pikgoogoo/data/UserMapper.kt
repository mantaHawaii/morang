package com.gusto.pikgoogoo.data

import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject

class UserMapper
@Inject
constructor(): EntityMapper<UserEntity, User>{

    override fun mapFromEntity(entity: UserEntity): User {
        return User(
            id = entity.id,
            nickname = entity.nickname,
            grade = entity.grade,
            gradeIcon = entity.gradeIcon,
            isRestricted = entity.isRestricted
        )
    }

    override fun mapToEntity(domainModel: User): UserEntity {
        return UserEntity(
            id = domainModel.id,
            nickname = domainModel.nickname,
            grade = domainModel.grade,
            gradeIcon = domainModel.gradeIcon,
            isRestricted = domainModel.isRestricted
        )
    }


    fun mapFromEntityList(entities: List<UserEntity>): List<User>{
        return entities.map { mapFromEntity(it) }
    }

}