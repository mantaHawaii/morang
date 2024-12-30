package com.gusto.pikgoogoo.data

import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject
import kotlin.math.floor

class VoteHistoryMapper
@Inject
constructor() : EntityMapper<VoteHistoryEntity, VoteHistory> {
    override fun mapFromEntity(entity: VoteHistoryEntity): VoteHistory {
        return VoteHistory(
            id = entity.id,
            voteCount = entity.voteCount,
            dateMilliSeconds = entity.dateSeconds*1000
        )
    }

    override fun mapToEntity(domainModel: VoteHistory): VoteHistoryEntity {
        return VoteHistoryEntity(
            id = domainModel.id,
            voteCount = domainModel.voteCount,
            dateSeconds = floor((domainModel.dateMilliSeconds/1000L).toDouble()).toLong()
        )
    }

    fun mapFromEntityList(entities: List<VoteHistoryEntity>): List<VoteHistory>{
        return entities.map { mapFromEntity(it) }
    }

}