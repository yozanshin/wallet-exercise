package com.playtomic.tests.wallet.model.repositories;

import com.playtomic.tests.wallet.model.entities.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WalletRepository  extends JpaRepository<Wallet, Long>{
}
