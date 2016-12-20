package com.barclays.indiacp.cordapp.schemas

import net.corda.core.schemas.MappedSchema
import net.corda.core.schemas.PersistentState
import java.time.Instant
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.Table

/**
 * An object used to fully qualify the [StandardSettlementSchema] family name (i.e. independent of version).
 */
object IssuerSettlementSchema


/**
 * First version of a India commercial paper contract ORM schema that maps all fields of the [StandardSettlementSchema] contract state
 * as it stood at the time of writing.
 */
object IssuerSettlementSchemaV1 : MappedSchema(schemaFamily = IssuerSettlementSchema.javaClass, version = 1, mappedTypes = listOf(PersistentIssuerSettlementSchemaState::class.java)) {
    @Entity
    @Table(name = "issuer_settlement_states")
    class PersistentIssuerSettlementSchemaState(

            @Column(name = "settlement_key")
            var settlement_key: String,

            @Column(name = "cp_program_id")
            var cpProgramID: String,

            @Column(name = "cp_trade_id")
            var cpTradeID: String,

            @Column(name = "creditorName")
            var creditorName: String,

            @Column(name = "bankAccountDetails")
            var bankAccountDetails: String,

            @Column(name = "bankName")
            var bankName: String,

            @Column(name = "rtgsCode")
            var rtgsCode: String,

            @Column(name = "dpName")
            var dpName: String,

            @Column(name = "clientId")
            var clientId: String,

            @Column(name = "dpID")
            var dpID: String

    ) : PersistentState()
}