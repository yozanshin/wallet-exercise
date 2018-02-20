package com.playtomic.tests.wallet.model.repositories;


import com.playtomic.tests.wallet.model.entities.Wallet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static junit.framework.TestCase.*;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
public class WalletRepositoryIT {

    @Autowired
    private WalletRepository walletRepository;

    @Test
    public void crudTest(){

        // CREATE
        List<Wallet> wallets = walletRepository.findAll();
        assertNotNull(wallets);
        assertTrue(wallets.size() == 3);
        assertNull(walletRepository.findOne(4L));

        Wallet newWallet = new Wallet(4L, BigDecimal.valueOf(50));
        walletRepository.save(newWallet);

        wallets = walletRepository.findAll();
        assertTrue(wallets.size() == 4);
        assertNotNull(walletRepository.findOne(4L));

        // READ
        Wallet wallet4 = walletRepository.findOne(4L);
        assertNotNull(wallet4);
        assertTrue(wallet4.getBalance().compareTo(BigDecimal.valueOf(50)) == 0);

        // UPDATE
        wallet4.setBalance(BigDecimal.valueOf(200));
        walletRepository.save(wallet4);
        wallet4 = walletRepository.findOne(4L);
        assertTrue(wallet4.getBalance().compareTo(BigDecimal.valueOf(200)) == 0);

        // DELETE
        assertNotNull(walletRepository.findOne(4L));
        walletRepository.delete(4L);
        assertNull(walletRepository.findOne(4L));

    }
}
