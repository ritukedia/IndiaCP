/*
 * IndiaCP API
 * This API will drive the UI
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package com.barclays.indiacp.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;

/**
 * CPIssue
 */
@javax.annotation.Generated(value = "class io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2016-12-22T06:22:33.818Z")
public class CPIssue   {
  @JsonProperty("cp_program_id")
  private String cpProgramId = null;

  @JsonProperty("cp_trade_id")
  private String cpTradeId = null;

  @JsonProperty("book_id")
  private String bookId = null;

  @JsonProperty("isin")
  private String isin = null;

  @JsonProperty("trader_id")
  private String traderId = null;

  @JsonProperty("issuer_id")
  private String issuerId = null;

  @JsonProperty("issuer_name")
  private String issuerName = null;

  @JsonProperty("investor_id")
  private String investorId = null;

  @JsonProperty("investor_name")
  private String investorName = null;

  @JsonProperty("ipa_id")
  private String ipaId = null;

  @JsonProperty("ipa_name")
  private String ipaName = null;

  @JsonProperty("depository_id")
  private String depositoryId = null;

  @JsonProperty("depository_name")
  private String depositoryName = null;

  @JsonProperty("trade_date")
  private Date tradeDate = null;

  @JsonProperty("value_date")
  private Date valueDate = null;

  @JsonProperty("maturity_date")
  private Date maturityDate = null;

  @JsonProperty("currency")
  private String currency = null;

  @JsonProperty("notional_amount")
  private Date notionalAmount = null;

  @JsonProperty("rate")
  private Float rate = null;

  @JsonProperty("issuer_settlement_details")
  private SettlementDetails issuerSettlementDetails = null;

  @JsonProperty("investor_settlement_details")
  private SettlementDetails investorSettlementDetails = null;

  @JsonProperty("deal_confirmation_doc_id")
  private String dealConfirmationDocId = null;

  @JsonProperty("status")
  private String status = null;

  @JsonProperty("version")
  private Integer version = null;

  @JsonProperty("last_modified")
  private Date lastModified = null;

  public CPIssue cpProgramId(String cpProgramId) {
    this.cpProgramId = cpProgramId;
    return this;
  }

   /**
   * Unique identifier representing a specific CP Program raised by an Issuer. This CP Issue is allotted under this umbrella program
   * @return cpProgramId
  **/
  //@ApiModelProperty(value = "Unique identifier representing a specific CP Program raised by an Issuer. This CP Issue is allotted under this umbrella program")
  public String getCpProgramId() {
    return cpProgramId;
  }

  public void setCpProgramId(String cpProgramId) {
    this.cpProgramId = cpProgramId;
  }

  public CPIssue cpTradeId(String cpTradeId) {
    this.cpTradeId = cpTradeId;
    return this;
  }

   /**
   * Unique identifier representing a specific CP Issue under the umbrella CP Program
   * @return cpTradeId
  **/
  //@ApiModelProperty(value = "Unique identifier representing a specific CP Issue under the umbrella CP Program")
  public String getCpTradeId() {
    return cpTradeId;
  }

  public void setCpTradeId(String cpTradeId) {
    this.cpTradeId = cpTradeId;
  }

  public CPIssue bookId(String bookId) {
    this.bookId = bookId;
    return this;
  }

   /**
   * Internal Book Id that this trade is booked under
   * @return bookId
  **/
  //@ApiModelProperty(value = "Internal Book Id that this trade is booked under")
  public String getBookId() {
    return bookId;
  }

  public void setBookId(String bookId) {
    this.bookId = bookId;
  }

  public CPIssue isin(String isin) {
    this.isin = isin;
    return this;
  }

   /**
   * Unique CP Security Identifier No. In India this is issued by NSDL for Commercial Paper type of securities.
   * @return isin
  **/
  //@ApiModelProperty(value = "Unique CP Security Identifier No. In India this is issued by NSDL for Commercial Paper type of securities.")
  public String getIsin() {
    return isin;
  }

  public void setIsin(String isin) {
    this.isin = isin;
  }

  public CPIssue traderId(String traderId) {
    this.traderId = traderId;
    return this;
  }

   /**
   * Unique identifier of the trader booking this trade
   * @return traderId
  **/
  //@ApiModelProperty(value = "Unique identifier of the trader booking this trade")
  public String getTraderId() {
    return traderId;
  }

  public void setTraderId(String traderId) {
    this.traderId = traderId;
  }

  public CPIssue issuerId(String issuerId) {
    this.issuerId = issuerId;
    return this;
  }

   /**
   * Unique identifier of the Issuer
   * @return issuerId
  **/
  //@ApiModelProperty(value = "Unique identifier of the Issuer")
  public String getIssuerId() {
    return issuerId;
  }

  public void setIssuerId(String issuerId) {
    this.issuerId = issuerId;
  }

  public CPIssue issuerName(String issuerName) {
    this.issuerName = issuerName;
    return this;
  }

   /**
   * Display name of the Issuer
   * @return issuerName
  **/
  //@ApiModelProperty(value = "Display name of the Issuer")
  public String getIssuerName() {
    return issuerName;
  }

  public void setIssuerName(String issuerName) {
    this.issuerName = issuerName;
  }

  public CPIssue investorId(String investorId) {
    this.investorId = investorId;
    return this;
  }

   /**
   * Unique identifier of the Investor. This also uniquely identifies the Investor DL Node
   * @return investorId
  **/
  //@ApiModelProperty(value = "Unique identifier of the Investor. This also uniquely identifies the Investor DL Node")
  public String getInvestorId() {
    return investorId;
  }

  public void setInvestorId(String investorId) {
    this.investorId = investorId;
  }

  public CPIssue investorName(String investorName) {
    this.investorName = investorName;
    return this;
  }

   /**
   * Display name of the Investor
   * @return investorName
  **/
  //@ApiModelProperty(value = "Display name of the Investor")
  public String getInvestorName() {
    return investorName;
  }

  public void setInvestorName(String investorName) {
    this.investorName = investorName;
  }

  public CPIssue ipaId(String ipaId) {
    this.ipaId = ipaId;
    return this;
  }

   /**
   * Unique identifier of the IPA
   * @return ipaId
  **/
  //@ApiModelProperty(value = "Unique identifier of the IPA")
  public String getIpaId() {
    return ipaId;
  }

  public void setIpaId(String ipaId) {
    this.ipaId = ipaId;
  }

  public CPIssue ipaName(String ipaName) {
    this.ipaName = ipaName;
    return this;
  }

   /**
   * Display name of the IPA
   * @return ipaName
  **/
  //@ApiModelProperty(value = "Display name of the IPA")
  public String getIpaName() {
    return ipaName;
  }

  public void setIpaName(String ipaName) {
    this.ipaName = ipaName;
  }

  public CPIssue depositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
    return this;
  }

   /**
   * Unique identifier of the Depository (NSDL)
   * @return depositoryId
  **/
  //@ApiModelProperty(value = "Unique identifier of the Depository (NSDL)")
  public String getDepositoryId() {
    return depositoryId;
  }

  public void setDepositoryId(String depositoryId) {
    this.depositoryId = depositoryId;
  }

  public CPIssue depositoryName(String depositoryName) {
    this.depositoryName = depositoryName;
    return this;
  }

   /**
   * Display name of the Depository
   * @return depositoryName
  **/
  //@ApiModelProperty(value = "Display name of the Depository")
  public String getDepositoryName() {
    return depositoryName;
  }

  public void setDepositoryName(String depositoryName) {
    this.depositoryName = depositoryName;
  }

  public CPIssue tradeDate(Date tradeDate) {
    this.tradeDate = tradeDate;
    return this;
  }

   /**
   * Date on which the trade was captured
   * @return tradeDate
  **/
  //@ApiModelProperty(value = "Date on which the trade was captured")
  public Date getTradeDate() {
    return tradeDate;
  }

  public void setTradeDate(Date tradeDate) {
    this.tradeDate = tradeDate;
  }

  public CPIssue valueDate(Date valueDate) {
    this.valueDate = valueDate;
    return this;
  }

   /**
   * Date on which the trade was settled and the Cash and CP securities were swapped between the Issuer and the Investor
   * @return valueDate
  **/
  //@ApiModelProperty(value = "Date on which the trade was settled and the Cash and CP securities were swapped between the Issuer and the Investor")
  public Date getValueDate() {
    return valueDate;
  }

  public void setValueDate(Date valueDate) {
    this.valueDate = valueDate;
  }

  public CPIssue maturityDate(Date maturityDate) {
    this.maturityDate = maturityDate;
    return this;
  }

   /**
   * Date on which the CP will be matured and redeemed
   * @return maturityDate
  **/
  //@ApiModelProperty(value = "Date on which the CP will be matured and redeemed")
  public Date getMaturityDate() {
    return maturityDate;
  }

  public void setMaturityDate(Date maturityDate) {
    this.maturityDate = maturityDate;
  }

  public CPIssue currency(String currency) {
    this.currency = currency;
    return this;
  }

   /**
   * Currency of the issued CP Notes
   * @return currency
  **/
  //@ApiModelProperty(value = "Currency of the issued CP Notes")
  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public CPIssue notionalAmount(Date notionalAmount) {
    this.notionalAmount = notionalAmount;
    return this;
  }

   /**
   * This is the amount that will be paid by the Issuer to the Investor on redemption
   * @return notionalAmount
  **/
  //@ApiModelProperty(value = "This is the amount that will be paid by the Issuer to the Investor on redemption")
  public Date getNotionalAmount() {
    return notionalAmount;
  }

  public void setNotionalAmount(Date notionalAmount) {
    this.notionalAmount = notionalAmount;
  }

  public CPIssue rate(Float rate) {
    this.rate = rate;
    return this;
  }

   /**
   * Rate at which the yield is calculated
   * @return rate
  **/
  //@ApiModelProperty(value = "Rate at which the yield is calculated")
  public Float getRate() {
    return rate;
  }

  public void setRate(Float rate) {
    this.rate = rate;
  }

  public CPIssue issuerSettlementDetails(SettlementDetails issuerSettlementDetails) {
    this.issuerSettlementDetails = issuerSettlementDetails;
    return this;
  }

   /**
   * Get issuerSettlementDetails
   * @return issuerSettlementDetails
  **/
  //@ApiModelProperty(value = "")
  public SettlementDetails getIssuerSettlementDetails() {
    return issuerSettlementDetails;
  }

  public void setIssuerSettlementDetails(SettlementDetails issuerSettlementDetails) {
    this.issuerSettlementDetails = issuerSettlementDetails;
  }

  public CPIssue investorSettlementDetails(SettlementDetails investorSettlementDetails) {
    this.investorSettlementDetails = investorSettlementDetails;
    return this;
  }

   /**
   * Get investorSettlementDetails
   * @return investorSettlementDetails
  **/
  //@ApiModelProperty(value = "")
  public SettlementDetails getInvestorSettlementDetails() {
    return investorSettlementDetails;
  }

  public void setInvestorSettlementDetails(SettlementDetails investorSettlementDetails) {
    this.investorSettlementDetails = investorSettlementDetails;
  }

  public CPIssue dealConfirmationDocId(String dealConfirmationDocId) {
    this.dealConfirmationDocId = dealConfirmationDocId;
    return this;
  }

   /**
   * Unique identifier of the deal confirmation document signed by both the Issuer and the Investor
   * @return dealConfirmationDocId
  **/
  //@ApiModelProperty(value = "Unique identifier of the deal confirmation document signed by both the Issuer and the Investor")
  public String getDealConfirmationDocId() {
    return dealConfirmationDocId;
  }

  public void setDealConfirmationDocId(String dealConfirmationDocId) {
    this.dealConfirmationDocId = dealConfirmationDocId;
  }

  public CPIssue status(String status) {
    this.status = status;
    return this;
  }

   /**
   * Current status of the CP Issue
   * @return status
  **/
  //@ApiModelProperty(value = "Current status of the CP Issue")
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public CPIssue version(Integer version) {
    this.version = version;
    return this;
  }

   /**
   * Current version of the CP Issue
   * @return version
  **/
  //@ApiModelProperty(value = "Current version of the CP Issue")
  public Integer getVersion() {
    return version;
  }

  public void setVersion(Integer version) {
    this.version = version;
  }

  public CPIssue lastModified(Date lastModified) {
    this.lastModified = lastModified;
    return this;
  }

   /**
   * Unique identifier of the Allotment Letter generated by IPA for CP transfer to Investor DP account
   * @return lastModified
  **/
  //@ApiModelProperty(value = "Unique identifier of the Allotment Letter generated by IPA for CP transfer to Investor DP account")
  public Date getLastModified() {
    return lastModified;
  }

  public void setLastModified(Date lastModified) {
    this.lastModified = lastModified;
  }


  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    CPIssue cpIssue = (CPIssue) o;
    return Objects.equals(this.cpProgramId, cpIssue.cpProgramId) &&
        Objects.equals(this.cpTradeId, cpIssue.cpTradeId) &&
        Objects.equals(this.bookId, cpIssue.bookId) &&
        Objects.equals(this.isin, cpIssue.isin) &&
        Objects.equals(this.traderId, cpIssue.traderId) &&
        Objects.equals(this.issuerId, cpIssue.issuerId) &&
        Objects.equals(this.issuerName, cpIssue.issuerName) &&
        Objects.equals(this.investorId, cpIssue.investorId) &&
        Objects.equals(this.investorName, cpIssue.investorName) &&
        Objects.equals(this.ipaId, cpIssue.ipaId) &&
        Objects.equals(this.ipaName, cpIssue.ipaName) &&
        Objects.equals(this.depositoryId, cpIssue.depositoryId) &&
        Objects.equals(this.depositoryName, cpIssue.depositoryName) &&
        Objects.equals(this.tradeDate, cpIssue.tradeDate) &&
        Objects.equals(this.valueDate, cpIssue.valueDate) &&
        Objects.equals(this.maturityDate, cpIssue.maturityDate) &&
        Objects.equals(this.currency, cpIssue.currency) &&
        Objects.equals(this.notionalAmount, cpIssue.notionalAmount) &&
        Objects.equals(this.rate, cpIssue.rate) &&
        Objects.equals(this.issuerSettlementDetails, cpIssue.issuerSettlementDetails) &&
        Objects.equals(this.investorSettlementDetails, cpIssue.investorSettlementDetails) &&
        Objects.equals(this.dealConfirmationDocId, cpIssue.dealConfirmationDocId) &&
        Objects.equals(this.status, cpIssue.status) &&
        Objects.equals(this.version, cpIssue.version) &&
        Objects.equals(this.lastModified, cpIssue.lastModified);
  }

  @Override
  public int hashCode() {
    return Objects.hash(cpProgramId, cpTradeId, bookId, isin, traderId, issuerId, issuerName, investorId, investorName, ipaId, ipaName, depositoryId, depositoryName, tradeDate, valueDate, maturityDate, currency, notionalAmount, rate, issuerSettlementDetails, investorSettlementDetails, dealConfirmationDocId, status, version, lastModified);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class CPIssue {\n");
    
    sb.append("    cpProgramId: ").append(toIndentedString(cpProgramId)).append("\n");
    sb.append("    cpTradeId: ").append(toIndentedString(cpTradeId)).append("\n");
    sb.append("    bookId: ").append(toIndentedString(bookId)).append("\n");
    sb.append("    isin: ").append(toIndentedString(isin)).append("\n");
    sb.append("    traderId: ").append(toIndentedString(traderId)).append("\n");
    sb.append("    issuerId: ").append(toIndentedString(issuerId)).append("\n");
    sb.append("    issuerName: ").append(toIndentedString(issuerName)).append("\n");
    sb.append("    investorId: ").append(toIndentedString(investorId)).append("\n");
    sb.append("    investorName: ").append(toIndentedString(investorName)).append("\n");
    sb.append("    ipaId: ").append(toIndentedString(ipaId)).append("\n");
    sb.append("    ipaName: ").append(toIndentedString(ipaName)).append("\n");
    sb.append("    depositoryId: ").append(toIndentedString(depositoryId)).append("\n");
    sb.append("    depositoryName: ").append(toIndentedString(depositoryName)).append("\n");
    sb.append("    tradeDate: ").append(toIndentedString(tradeDate)).append("\n");
    sb.append("    valueDate: ").append(toIndentedString(valueDate)).append("\n");
    sb.append("    maturityDate: ").append(toIndentedString(maturityDate)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    notionalAmount: ").append(toIndentedString(notionalAmount)).append("\n");
    sb.append("    rate: ").append(toIndentedString(rate)).append("\n");
    sb.append("    issuerSettlementDetails: ").append(toIndentedString(issuerSettlementDetails)).append("\n");
    sb.append("    investorSettlementDetails: ").append(toIndentedString(investorSettlementDetails)).append("\n");
    sb.append("    dealConfirmationDocId: ").append(toIndentedString(dealConfirmationDocId)).append("\n");
    sb.append("    status: ").append(toIndentedString(status)).append("\n");
    sb.append("    version: ").append(toIndentedString(version)).append("\n");
    sb.append("    lastModified: ").append(toIndentedString(lastModified)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

