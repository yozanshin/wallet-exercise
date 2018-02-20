package com.playtomic.tests.wallet.api;

import com.playtomic.tests.wallet.exceptions.WalletInsufficientBalanceException;
import com.playtomic.tests.wallet.exceptions.WalletIntegrationException;
import com.playtomic.tests.wallet.exceptions.WalletNotFoundException;
import com.playtomic.tests.wallet.model.entities.Wallet;
import com.playtomic.tests.wallet.model.repositories.WalletRepository;
import com.playtomic.tests.wallet.service.payment.PaymentService;
import com.playtomic.tests.wallet.service.payment.exceptions.PaymentServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.MessageFormat;

@RestController
@RequestMapping("wallet/{id}")
@Slf4j
public class WalletController {

    private static final String WALLET_NOT_FOUND_TEMPLATE = "Wallet {0} not found";
    private static final String WALLET_INSUFFICIENT_BALANCE_TEMPLATE = "Wallet {0} have no balance enough for a payment of {1}";
    private static final String PAYMENT_SERVICE_INTEGRATION_ERROR_TEMPLATE = "Payment service integration error charging amount of {0}";

    private PaymentService paymentService;
    private WalletRepository walletRepository;

    @Autowired
    public WalletController(PaymentService paymentService, WalletRepository walletRepository) {
        this.paymentService = paymentService;
        this.walletRepository = walletRepository;
    }

    @RequestMapping(value = "balance", method = RequestMethod.GET)
    @Transactional(readOnly = true)
    BigDecimal consultWalletBalance(@PathVariable("id") long id) throws WalletNotFoundException{

        log.info("Getting balance of wallet {} ...", id);

        Wallet wallet = this.getWalletFromId(id);

        BigDecimal walletBalance = wallet.getBalance();

        log.info("Balance successfully got");

        return walletBalance;
    }

    // Se podría usar un método PUT, pero al ser éste idempotente no sería totalmente apropiado
    // Se pasa "amount" como query param para simplificar, se podría pasar también dentro del body
    @RequestMapping(value = "payment", method = RequestMethod.POST)
    void makePaymentFromWallet(@PathVariable("id") long id, @RequestParam("amount") BigDecimal amount) throws WalletNotFoundException, WalletInsufficientBalanceException{

        log.info("Making payment of amount {} from wallet {} ...", amount, id);

        Wallet wallet = this.getWalletFromId(id);

        log.info("Wallet {} current balance = {}", id, wallet.getBalance());

        BigDecimal newBalance = wallet.getBalance().subtract(amount);

        if(newBalance.compareTo(BigDecimal.ZERO) < 0){
            log.error("Wallet {} hasn't balance enough to subtract an amount of {}", id, amount);
            throw new WalletInsufficientBalanceException(MessageFormat.format(WALLET_INSUFFICIENT_BALANCE_TEMPLATE, id, amount));
        }

        wallet.setBalance(newBalance);

        walletRepository.save(wallet);

        log.info("Wallet {} new balance = {}", id, newBalance);

        log.info("Payment successfully made");
    }

    // Se podría usar un método PUT, pero al ser éste idempotente no sería totalmente apropiado
    // Se pasa "amount" como query param para simplificar, se podría pasar también dentro del body
    @RequestMapping(value = "return", method = RequestMethod.POST)
    void makeReturnToWallet(@PathVariable("id") long id, @RequestParam("amount") BigDecimal amount) throws WalletNotFoundException{

        /*
        Este caso de uso me hace pensar que, al tratarse de una devolución, habría un pago previo,
        y por lo tanto habría que enlazar esta operación con una transferencia de pago previa.
        En ese caso, habría que extender tanto el modelo como los endpoints para gestionar los correspondientes
        identificadores de transferencia.
        Puesto que en los requisitos no se hace mención a estas transferencias, se ha omitido esa implementación hasta tener más detalles
         */

        log.info("Making return of amount {} from wallet {}", amount, id);

        Wallet wallet = this.getWalletFromId(id);

        log.info("Wallet {} current balance = {}", id, wallet.getBalance());

        BigDecimal newBalance = wallet.getBalance().add(amount);

        wallet.setBalance(newBalance);

        walletRepository.save(wallet);

        log.info("Wallet {} new balance = {}", id, newBalance);

        log.info("Return successfully made");
    }

    // Se podría usar un método PUT, pero al ser éste idempotente no sería totalmente apropiado
    // Se pasa "amount" como query param para simplificar, se podría pasar también dentro del body
    @RequestMapping(value = "charge", method = RequestMethod.POST)
    void makeChargeToWallet(@PathVariable("id") long id, @RequestParam("amount") BigDecimal amount) throws WalletNotFoundException, WalletIntegrationException{

        /*
         No tengo muy claro el caso de uso:
         - ¿La pasarela de pagos invocaría el endpoint de añadir saldo al monedero tras validar la operación?
         - ¿Hay que añadir el saldo tras la integración con la pasarela de pagos?
         He optado por esta segunda opción a la espera de poder clarificar el caso de uso
          */

        log.info("Making charge of amount {} to wallet {} with payment service ...", amount, id);

        Wallet wallet = this.getWalletFromId(id);

        log.info("Wallet {} current balance = ", id, wallet.getBalance());

        this.chargeThroughPaymentService(amount);

        BigDecimal newBalance = wallet.getBalance().add(amount);

        log.info("Wallet {} new balance = ", id, newBalance);

        wallet.setBalance(newBalance);

        walletRepository.save(wallet);

        log.info("Charge successfully made");
    }

    private Wallet getWalletFromId(long walletId) throws WalletNotFoundException{

        log.info("Getting wallet {} info ...", walletId);

        Wallet wallet = walletRepository.findOne(walletId);

        if(wallet == null){
            log.error("Wallet {} not found", walletId);
            throw new WalletNotFoundException(MessageFormat.format(WALLET_NOT_FOUND_TEMPLATE, walletId));
        }

        return wallet;
    }

    private void chargeThroughPaymentService(BigDecimal amount) throws WalletIntegrationException{

        try{
            paymentService.charge(amount);
        }catch (PaymentServiceException ex){
            log.error("Error charging amount of {} through PaymentService", amount);
            throw new WalletIntegrationException(MessageFormat.format(PAYMENT_SERVICE_INTEGRATION_ERROR_TEMPLATE, amount));
        }
    }
}
