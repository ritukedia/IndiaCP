package main.java.com.barclays.indiacp.client.model.req;

import com.fasterxml.jackson.annotation.JsonProperty;
import main.java.com.barclays.indiacp.client.model.Contract;

public class ContractCreateCommand {

    private String from;
    private String code;
    private Contract.CodeTypeEnum codeType;
    private Boolean optimize;
    private Object[] args;
    private String binary;

    public ContractCreateCommand() {
    }

    public ContractCreateCommand from(String from) {
        this.from = from;
        return this;
    }

    public ContractCreateCommand code(String code) {
        this.code = code;
        return this;
    }

    public ContractCreateCommand codeType(Contract.CodeTypeEnum codeType) {
        this.codeType = codeType;
        return this;
    }

    public ContractCreateCommand optimize(Boolean optimize) {
        this.optimize = optimize;
        return this;
    }

    public ContractCreateCommand args(Object[] args) {
        this.args = args;
        return this;
    }

    public ContractCreateCommand binary(String binary) {
        this.binary = binary;
        return this;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @JsonProperty("code_type")
    public Contract.CodeTypeEnum getCodeType() {
        return codeType;
    }

    public void setCodeType(Contract.CodeTypeEnum codeType) {
        this.codeType = codeType;
    }

    public Boolean getOptimize() {
        return optimize;
    }

    public void setOptimize(Boolean optimize) {
        this.optimize = optimize;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public Object[] getArgs() {
        return args;
    }

    public void setArgs(Object[] args) {
        this.args = args;
    }

    public String getBinary() {
        return binary;
    }

    public void setBinary(String binary) {
        this.binary = binary;
    }

}
