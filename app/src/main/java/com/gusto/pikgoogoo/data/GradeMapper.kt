package com.gusto.pikgoogoo.data

import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject

class GradeMapper
@Inject
constructor() : EntityMapper<GradeEntity, Grade> {

    override fun mapFromEntity(entity: GradeEntity): Grade {
        return Grade(
            id = entity.id,
            minValue = entity.minValue
        )
    }

    override fun mapToEntity(domainModel: Grade): GradeEntity {
        return GradeEntity(
            id = domainModel.id,
            minValue = domainModel.minValue
        )
    }

    fun mapFromEntityList(entities: List<GradeEntity>): List<Grade> {
        return entities.map { mapFromEntity(it) }
    }

}