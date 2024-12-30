package com.gusto.pikgoogoo.data

import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject

class CategoryMapper
@Inject
constructor() : EntityMapper<CategoryEntity, Category> {

    override fun mapFromEntity(entity: CategoryEntity): Category {
        return Category(
            id = entity.id,
            name = entity.name
        )
    }

    override fun mapToEntity(domainModel: Category): CategoryEntity {
        return CategoryEntity(
            id = domainModel.id,
            name = domainModel.name
        )
    }

    fun mapFromEntityList(entities: List<CategoryEntity>): List<Category> {
        return entities.map { mapFromEntity(it) }
    }

}