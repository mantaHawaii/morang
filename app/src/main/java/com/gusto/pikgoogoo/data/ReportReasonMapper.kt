package com.gusto.pikgoogoo.data

import com.gusto.pikgoogoo.util.EntityMapper
import javax.inject.Inject

class ReportReasonMapper
@Inject
constructor() : EntityMapper<ReportReasonEntity, ReportReason> {

    override fun mapFromEntity(entity: ReportReasonEntity): ReportReason {
        return ReportReason(
            id = entity.id,
            reason = entity.reason
        )
    }

    override fun mapToEntity(domainModel: ReportReason): ReportReasonEntity {
        return ReportReasonEntity(
            id = domainModel.id,
            reason = domainModel.reason
        )
    }

    fun mapFromEntityList(entities: List<ReportReasonEntity>): List<ReportReason> {
        return entities.map { mapFromEntity(it) }
    }

}