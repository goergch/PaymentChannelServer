/*
 * PaymentChannelServer.
 * No description provided (generated by Swagger Codegen https://github.com/swagger-api/swagger-codegen)
 *
 * OpenAPI spec version: 1.0.0
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Date;
import java.util.UUID;
import javax.validation.constraints.*;

/**
 * Invoice
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen", date = "2017-06-09T08:42:18.129Z")
public class Invoice   {
  @JsonProperty("totalAmount")
  private Long totalAmount = null;

  @JsonProperty("expiration")
  private Date expiration = null;

  @JsonProperty("invoiceId")
  private UUID invoiceId = null;

  @JsonProperty("referenceId")
  private String referenceId = null;

  public Invoice totalAmount(Long totalAmount) {
    this.totalAmount = totalAmount;
    return this;
  }

   /**
   * total amount of Satoshis that are requested to be paid
   * @return totalAmount
  **/
  @JsonProperty("totalAmount")
  @ApiModelProperty(value = "total amount of Satoshis that are requested to be paid")
  public Long getTotalAmount() {
    return totalAmount;
  }

  public void setTotalAmount(Long totalAmount) {
    this.totalAmount = totalAmount;
  }

  public Invoice expiration(Date expiration) {
    this.expiration = expiration;
    return this;
  }

   /**
   * date after which this invoice is no longer valid'
   * @return expiration
  **/
  @JsonProperty("expiration")
  @ApiModelProperty(value = "date after which this invoice is no longer valid'")
  public Date getExpiration() {
    return expiration;
  }

  public void setExpiration(Date expiration) {
    this.expiration = expiration;
  }

  public Invoice invoiceId(UUID invoiceId) {
    this.invoiceId = invoiceId;
    return this;
  }

   /**
   * unique id of the invoice assigned by payment service
   * @return invoiceId
  **/
  @JsonProperty("invoiceId")
  @ApiModelProperty(value = "unique id of the invoice assigned by payment service")
  public UUID getInvoiceId() {
    return invoiceId;
  }

  public void setInvoiceId(UUID invoiceId) {
    this.invoiceId = invoiceId;
  }

  public Invoice referenceId(String referenceId) {
    this.referenceId = referenceId;
    return this;
  }

   /**
   * id defined by the client for its internal reference, which is just stored and passed but not processed by the payment service
   * @return referenceId
  **/
  @JsonProperty("referenceId")
  @ApiModelProperty(value = "id defined by the client for its internal reference, which is just stored and passed but not processed by the payment service")
  public String getReferenceId() {
    return referenceId;
  }

  public void setReferenceId(String referenceId) {
    this.referenceId = referenceId;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Invoice invoice = (Invoice) o;
    return Objects.equals(this.totalAmount, invoice.totalAmount) &&
        Objects.equals(this.expiration, invoice.expiration) &&
        Objects.equals(this.invoiceId, invoice.invoiceId) &&
        Objects.equals(this.referenceId, invoice.referenceId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(totalAmount, expiration, invoiceId, referenceId);
  }


  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Invoice {\n");
    
    sb.append("    totalAmount: ").append(toIndentedString(totalAmount)).append("\n");
    sb.append("    expiration: ").append(toIndentedString(expiration)).append("\n");
    sb.append("    invoiceId: ").append(toIndentedString(invoiceId)).append("\n");
    sb.append("    referenceId: ").append(toIndentedString(referenceId)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

