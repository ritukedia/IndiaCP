package com.barclays.indiacp.cordapp.protocol.issuer

import co.paralleluniverse.fibers.Suspendable
import com.barclays.indiacp.cordapp.api.IndiaCPApi
import com.barclays.indiacp.cordapp.contract.IndiaCommercialPaper
import com.barclays.indiacp.cordapp.contract.IndiaCommercialPaperProgram
import com.barclays.indiacp.cordapp.dto.IndiaCPProgramJSON
import com.barclays.indiacp.cordapp.utilities.CPUtils
import com.barclays.indiacp.cordapp.utilities.CP_PROGRAM_FLOW_STAGES
import net.corda.contracts.asset.DUMMY_CASH_ISSUER
import net.corda.core.contracts.*
import net.corda.core.crypto.Party
import net.corda.core.crypto.SecureHash
import net.corda.core.days
import net.corda.core.node.NodeInfo
import net.corda.core.flows.FlowLogic
import net.corda.core.node.services.linearHeadsOfType
import net.corda.core.seconds
import net.corda.core.transactions.SignedTransaction
import net.corda.core.transactions.TransactionBuilder
import net.corda.core.utilities.ProgressTracker
import net.corda.flows.NotaryFlow
import java.time.Instant
import java.util.*

/**
 * This whole class is really part of a demo just to initiate the agreement of a deal with a simple
 * API call from a single party without bi-directional access to the database of offers etc.
 *
 * In the "real world", we'd probably have the offers sitting in the platform prior to the agreement step
 * or the protocol would have to reach out to external systems (or users) to verify the deals.
 */
class CPProgramFlows(val newCPProgram: IndiaCPProgramJSON,
                     val trig_stage : CP_PROGRAM_FLOW_STAGES
                        ) : FlowLogic<SignedTransaction>() {

    companion object {
        val PROSPECTUS_HASH = SecureHash.parse("decd098666b9657314870e192ced0c3519c2c9d395507a238338f8d003929de9")

        object CP_PROGRAM_ISSUING : ProgressTracker.Step("Issuing and timestamping IndiaCP Program")
        object ADD_ISIN_GEN_DOC : ProgressTracker.Step("Adding ISIN GEN DOC and timestamping IndiaCP Program")
        object CP_PROGRAM_ADD_ISIN : ProgressTracker.Step("Adding ISIN and timestamping IndiaCP Program")
        object ADD_IPA_VERI_DOC : ProgressTracker.Step("Adding IPA Verification Document and timestamping IndiaCP Program")
        object ADD_IPA_CERT_DOC : ProgressTracker.Step("Adding IPA Certification Document and timestamping IndiaCP Program")
        object ADD_CORP_ACT_FORM_DOC : ProgressTracker.Step("Adding Corporate Action Document and timestamping IndiaCP Program")
        object ADD_ALLOT_LETTER_DOC : ProgressTracker.Step("Adding Allotment Letter Document and timestamping IndiaCP Program")

        object CP_PROGRAM_ISSUE_CP : ProgressTracker.Step("Issuing a IndiaCP within a IndiaCP Program")


        object OBTAINING_NOTARY_SIGNATURE : ProgressTracker.Step("Obtaining Notary Signature")
        object NOTARY_SIGNATURE_OBTAINED : ProgressTracker.Step("Notary Signature Obtained")
        object RECORDING_TRANSACTION : ProgressTracker.Step("Recording Transaction in Local Storage")
        object TRANSACTION_RECORDED : ProgressTracker.Step("Transaction Recorded in Local Storage")
        //For exception case, we will not have any state recorded inside the transaction.
        object TRANSACTION_ERROR : ProgressTracker.Step("Transaction Failed. Not recorded in Local Storage.")

        // We vend a progress tracker that already knows there's going to be a TwoPartyTradingProtocol involved at some
        // point: by setting up the tracker in advance, the user can see what's coming in more detail, instead of being
        // surprised when it appears as a new set of tasks below the current one.
        fun tracker() = ProgressTracker(CP_PROGRAM_ISSUING, ADD_ISIN_GEN_DOC, CP_PROGRAM_ADD_ISIN,
                CP_PROGRAM_ISSUE_CP, ADD_IPA_VERI_DOC, ADD_IPA_CERT_DOC, ADD_CORP_ACT_FORM_DOC, ADD_ALLOT_LETTER_DOC,
                OBTAINING_NOTARY_SIGNATURE, NOTARY_SIGNATURE_OBTAINED, RECORDING_TRANSACTION, TRANSACTION_RECORDED,
                TRANSACTION_ERROR)
    }

    override val progressTracker = tracker()

    @Suspendable
    override fun call(): SignedTransaction
    {

        try {


            val notary: NodeInfo = serviceHub.networkMapCache.notaryNodes[0]

            var tx: TransactionBuilder = TransactionType.General.Builder(notary.notaryIdentity)


            when (trig_stage) {

                CP_PROGRAM_FLOW_STAGES.ISSUE_CP_PROGRAM -> {
                    progressTracker.currentStep = CP_PROGRAM_ISSUING;
                    val issuer = getPartyByName(newCPProgram.issuer)
                    val ipa = getPartyByName(newCPProgram.ipa)
                    val depository = getPartyByName(newCPProgram.depository)
                    tx = generateCPProgram(newCPProgram, issuer, ipa, depository, notary);
                }

                CP_PROGRAM_FLOW_STAGES.ADDISIN -> {
                    progressTracker.currentStep = CP_PROGRAM_ADD_ISIN;
                    tx = addIsinToCPProgram(newCPProgram, notary);
                }

                CP_PROGRAM_FLOW_STAGES.ADD_ISIN_GEN_DOC -> {
                    progressTracker.currentStep = ADD_ISIN_GEN_DOC;
                    tx = addIsinGenDocToCPProgram(newCPProgram, notary);
                }

                CP_PROGRAM_FLOW_STAGES.ADD_IPA_VERI_DOC -> {
                    progressTracker.currentStep = ADD_IPA_VERI_DOC;
                    tx = addIPAVerificationDocToCPProgram(newCPProgram, notary);
                }

                CP_PROGRAM_FLOW_STAGES.ADD_IPA_CERT_DOC -> {
                    progressTracker.currentStep = ADD_IPA_CERT_DOC;
                    tx = addIPACertifcateDocToCPProgram(newCPProgram, notary);
                }

                CP_PROGRAM_FLOW_STAGES.ADD_CORP_ACT_FORM_DOC -> {
                    progressTracker.currentStep = ADD_CORP_ACT_FORM_DOC;
                    tx = addCorpActionFormDocToCPProgram(newCPProgram, notary);
                }

                CP_PROGRAM_FLOW_STAGES.ADD_ALLOT_LETTER_DOC -> {
                    progressTracker.currentStep = ADD_ALLOT_LETTER_DOC;
                    tx = addAllotmentLetterDocToCPProgram(newCPProgram, notary);
                }
                CP_PROGRAM_FLOW_STAGES.ISSUE_CP ->
                {


                }
            }


            // Requesting timestamping, all CP must be timestamped.
            tx.setTime(Instant.now(), 30.seconds)

            // Sign it as Issuer.
            tx.signWith(serviceHub.legalIdentityKey)

            // Get the notary to sign the timestamp
            progressTracker.currentStep = OBTAINING_NOTARY_SIGNATURE
            val notarySig = subFlow(NotaryFlow.Client(tx.toSignedTransaction(false)))
            progressTracker.currentStep = NOTARY_SIGNATURE_OBTAINED
            tx.addSignatureUnchecked(notarySig)

            // Commit it to local storage.
            val stx = tx.toSignedTransaction(true)
            progressTracker.currentStep = RECORDING_TRANSACTION
            serviceHub.recordTransactions(listOf(stx))
            progressTracker.currentStep = TRANSACTION_RECORDED

            return stx

        }catch(e : Exception)
        {

            progressTracker.currentStep = TRANSACTION_ERROR

            //ToDo: Need to handle this better with logger sometime later.
            e.printStackTrace()

            throw e;
        }

    }

    private fun getPartyByName(partyName: String) : Party {
        return serviceHub.networkMapCache.getNodeByLegalName(partyName)!!.legalIdentity
    }


    /*
    Method for creating a CP Program.
     */
    private fun generateCPProgram(newCPProgram: IndiaCPProgramJSON, issuer : Party, ipa : Party, depository: Party, notary: NodeInfo) : TransactionBuilder
    {
        val tx = IndiaCommercialPaperProgram().generateIssue(
                IndiaCommercialPaperProgram.State(issuer, ipa, depository,
                        newCPProgram.program_id, newCPProgram.name, newCPProgram.type, newCPProgram.purpose,
                        newCPProgram.issuer_id, newCPProgram.issuer_name,
                        newCPProgram.issue_commencement_date.toInstant(),
                        newCPProgram.program_size.DOLLARS `issued by` DUMMY_CASH_ISSUER,
                        newCPProgram.program_allocated_value.DOLLARS `issued by` DUMMY_CASH_ISSUER,
                        Currency.getInstance("INR"), //TODO fix the hardcoding to INR and DOLLAR
                        newCPProgram.maturity_days.toInstant(), newCPProgram.ipa_id, newCPProgram.ipa_name,
                        newCPProgram.depository_id, newCPProgram.depository_name,
                        newCPProgram.isin, newCPProgram.isin_generation_request_doc_id,
                        newCPProgram.ipa_verification_request_doc_id,
                        newCPProgram.ipa_certificate_doc_id, newCPProgram.corporate_action_form_doc_id,
                        newCPProgram.allotment_letter_doc_id,
                        CP_PROGRAM_FLOW_STAGES.ISSUE_CP_PROGRAM.endStatus, //TODO: Add Status Enum
                        Instant.now(),
                        Integer(0)),
                notary = notary.notaryIdentity)

        return tx
    }


    /*
    Method for adding ISIN & supporting document into CP Program.
     */
    private fun addIsinToCPProgram(indiaCPProgramJSON: IndiaCPProgramJSON, notary: NodeInfo) : TransactionBuilder
    {
        val indiaCPProgramSF: StateAndRef<IndiaCommercialPaperProgram.State> = getCPProgramStateandRef(newCPProgram.program_id)

        val tx = IndiaCommercialPaperProgram().addIsinToCPProgram(indiaCPProgramSF, notary.notaryIdentity, indiaCPProgramJSON.isin, indiaCPProgramJSON.status)
        return tx
    }


    /*
Method for adding ISIN & supporting document into CP Program.
 */
    private fun addIsinGenDocToCPProgram(indiaCPProgramJSON: IndiaCPProgramJSON, notary: NodeInfo) : TransactionBuilder
    {
        val indiaCPProgramSF: StateAndRef<IndiaCommercialPaperProgram.State> = getCPProgramStateandRef(newCPProgram.program_id)

        val tx = IndiaCommercialPaperProgram().addIsinGenDocToCPProgram(indiaCPProgramSF, notary.notaryIdentity, indiaCPProgramJSON.isin_generation_request_doc_id, indiaCPProgramJSON.status)
        return tx
    }

     /*
            Method for uploading IPA Verification document hash a CP Program.
     */
    private fun addIPAVerificationDocToCPProgram(indiaCPProgramJSON: IndiaCPProgramJSON, notary: NodeInfo) : TransactionBuilder
    {
        val indiaCPProgramSF: StateAndRef<IndiaCommercialPaperProgram.State> = getCPProgramStateandRef(newCPProgram.program_id)

        val tx = IndiaCommercialPaperProgram().addIPAVerificationDocToCPProgram(indiaCPProgramSF, notary.notaryIdentity, indiaCPProgramJSON.ipa_verification_request_doc_id, indiaCPProgramJSON.status)
        return tx
    }


    /*
       Method for uploading IPA Certificate document hash a CP Program.
*/
    private fun addIPACertifcateDocToCPProgram(indiaCPProgramJSON: IndiaCPProgramJSON, notary: NodeInfo) : TransactionBuilder
    {
        val indiaCPProgramSF: StateAndRef<IndiaCommercialPaperProgram.State> = getCPProgramStateandRef(newCPProgram.program_id)

        val tx = IndiaCommercialPaperProgram().addIPACertifcateDocToCPProgram(indiaCPProgramSF, notary.notaryIdentity, indiaCPProgramJSON.ipa_certificate_doc_id, indiaCPProgramJSON.status)
        return tx
    }


    /*
       Method for uploading Corporate Action Verification document hash a CP Program.
*/
    private fun addCorpActionFormDocToCPProgram(indiaCPProgramJSON: IndiaCPProgramJSON, notary: NodeInfo) : TransactionBuilder
    {
        val indiaCPProgramSF: StateAndRef<IndiaCommercialPaperProgram.State> = getCPProgramStateandRef(newCPProgram.program_id)

        val tx = IndiaCommercialPaperProgram().addCorpActionFormDocToCPProgram(indiaCPProgramSF, notary.notaryIdentity, indiaCPProgramJSON.corporate_action_form_doc_id, indiaCPProgramJSON.status)
        return tx
    }


    /*
       Method for uploading Corporate Action Verification document hash a CP Program.
*/
    private fun addAllotmentLetterDocToCPProgram(indiaCPProgramJSON: IndiaCPProgramJSON, notary: NodeInfo) : TransactionBuilder
    {
        val indiaCPProgramSF: StateAndRef<IndiaCommercialPaperProgram.State> = getCPProgramStateandRef(newCPProgram.program_id)

        val tx = IndiaCommercialPaperProgram().addAllotmentLetterDocToCPProgram(indiaCPProgramSF, notary.notaryIdentity, indiaCPProgramJSON.allotment_letter_doc_id, indiaCPProgramJSON.status)
        return tx
    }




//    addIPAVerificationDocs
//    addIPACertifcateDoc
//    addCorpActionFormDoc
//    addAllotmentLetterDoc


    private fun getCPProgramStateandRef(ref: String): StateAndRef<IndiaCommercialPaperProgram.State>
    {

        val states = this.serviceHub.vaultService.linearHeadsOfType<IndiaCommercialPaperProgram.State>().filterValues { it.state.data.programId == ref }
//        return if (states.isEmpty()) null else
//        {
//            val deals = states.values.map { it }
//            return if (deals.isEmpty()) null else deals[0]
//        }

        //For now, assumption is that you will always find a deal for updating the details.
        //If a deal is not found then we have a big problem :(
        val deals = states.values.map { it }
        return deals[0]
    }


    /*
   Method for creatign a new CP of some value from within a CP Program.
*/
    private fun createCPInCPProgram(indiaCPProgramJSON: IndiaCPProgramJSON, notary: NodeInfo) : TransactionBuilder
    {
        val indiaCPProgramSF: StateAndRef<IndiaCommercialPaperProgram.State> = getCPProgramStateandRef(newCPProgram.program_id)

        val tx = IndiaCommercialPaperProgram().addAllotmentLetterDocToCPProgram(indiaCPProgramSF, notary.notaryIdentity, indiaCPProgramJSON.allotment_letter_doc_id, indiaCPProgramJSON.status)
        return tx
    }


}
