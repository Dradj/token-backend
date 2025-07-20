package com.zraj.tokenbackend.dto;

import java.math.BigDecimal;

public class RewardRequestDTO {
    private String userAddress;
    private String amount;  // меняем с BigDecimal на String

    public RewardRequestDTO() {}

    public RewardRequestDTO(String userAddress, BigDecimal amount) {
        this.userAddress = userAddress;
        this.amount = amount.toPlainString();  // конвертация в строку
    }

    public String getUserAddress() { return userAddress; }
    public void setUserAddress(String userAddress) { this.userAddress = userAddress; }

    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}


