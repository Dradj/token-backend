package com.zraj.tokenbackend.redis;

import java.io.Serializable;

public record WalletChangeRequestDTO(String newWallet, String code) implements Serializable {
}
