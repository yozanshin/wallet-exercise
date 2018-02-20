package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.exceptions.WalletInsufficientBalanceException;
import com.playtomic.tests.wallet.exceptions.WalletIntegrationException;
import com.playtomic.tests.wallet.exceptions.WalletNotFoundException;
import com.playtomic.tests.wallet.model.repositories.WalletRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles(profiles = "test")
@Transactional
public class WalletControllerIT {

    private static final long WALLET_NOT_FOUND_ID = -1L;

    @Autowired
    private WalletController walletController;

    @Autowired
    private WalletRepository walletRepository;

    @Test
    public void consultWalletBalance(){

        walletRepository.findAll().forEach(wallet -> {
            try {
                assertTrue(walletController.consultWalletBalance(wallet.getId())
                        .compareTo(wallet.getBalance()) == 0);
            }catch (WalletNotFoundException ex){
                fail();
            }
        });
    }

    @Test(expected = WalletNotFoundException.class)
    public void consultWalletNotFoundBalance() throws WalletNotFoundException{
        walletController.consultWalletBalance(WALLET_NOT_FOUND_ID);
    }

    @Test
    public void makePaymentFromWallet() {

        BigDecimal paymentAmount = BigDecimal.valueOf(100);

        walletRepository.findAll().forEach(wallet -> {
            BigDecimal initialBalance = wallet.getBalance();
            try {
                walletController.makePaymentFromWallet(wallet.getId(), paymentAmount);
                wallet = walletRepository.findOne(wallet.getId());
                assertTrue(wallet.getBalance().compareTo(initialBalance.subtract(paymentAmount)) == 0);
            }catch (WalletInsufficientBalanceException ex){
                assertTrue(paymentAmount.compareTo(initialBalance) > 0);
                assertTrue(walletRepository.findOne(wallet.getId()).getBalance().compareTo(initialBalance) == 0);
            }catch (Exception ex){
                fail();
            }
        });
    }

    @Test(expected = WalletNotFoundException.class)
    public void makePaymentFromWalletNotFound() throws WalletNotFoundException{

        BigDecimal paymentAmount = BigDecimal.valueOf(10);
        try {
            walletController.makePaymentFromWallet(WALLET_NOT_FOUND_ID, paymentAmount);
            fail();
        }catch (WalletInsufficientBalanceException ex){
            fail();
        }
    }

    @Test
    public void makeReturnToWallet() {

        BigDecimal returnAmount = BigDecimal.valueOf(100);

        walletRepository.findAll().forEach(wallet -> {
            BigDecimal initialBalance = wallet.getBalance();
            try {
                walletController.makeReturnToWallet(wallet.getId(), returnAmount);
                wallet = walletRepository.findOne(wallet.getId());
                assertTrue(wallet.getBalance().compareTo(initialBalance.add(returnAmount)) == 0);
            }catch (Exception ex){
                fail();
            }
        });
    }

    @Test(expected = WalletNotFoundException.class)
    public void makeReturnFromWalletNotFound() throws WalletNotFoundException{

        BigDecimal paymentAmount = BigDecimal.valueOf(10);
        walletController.makeReturnToWallet(WALLET_NOT_FOUND_ID, paymentAmount);
    }

    @Test
    public void makeChargeToWallet() {

        BigDecimal chargeAmount = BigDecimal.valueOf(100);

        walletRepository.findAll().forEach(wallet -> {
            BigDecimal initialBalance = wallet.getBalance();
            try {
                walletController.makeChargeToWallet(wallet.getId(), chargeAmount);
                wallet = walletRepository.findOne(wallet.getId());
                assertTrue(wallet.getBalance().compareTo(initialBalance.add(chargeAmount)) == 0);
            }catch (WalletIntegrationException ex){
                assertTrue(walletRepository.findOne(wallet.getId()).getBalance().compareTo(initialBalance) == 0);
            }catch (Exception ex){
                fail();
            }
        });

        BigDecimal badChargeAmount = BigDecimal.valueOf(3);

        walletRepository.findAll().forEach(wallet -> {
            BigDecimal initialBalance = wallet.getBalance();
            try {
                walletController.makeChargeToWallet(wallet.getId(), badChargeAmount);
                fail();
            }catch (WalletIntegrationException ex){
                assertTrue(walletRepository.findOne(wallet.getId()).getBalance().compareTo(initialBalance) == 0);
            }catch (Exception ex){
                fail();
            }
        });
    }

    @Test(expected = WalletNotFoundException.class)
    public void makeChargeFromWalletNotFound() throws WalletNotFoundException{

        BigDecimal paymentAmount = BigDecimal.valueOf(10);
        try{
            walletController.makeChargeToWallet(WALLET_NOT_FOUND_ID, paymentAmount);
        }catch (WalletIntegrationException ex){
            fail();
        }
    }
}