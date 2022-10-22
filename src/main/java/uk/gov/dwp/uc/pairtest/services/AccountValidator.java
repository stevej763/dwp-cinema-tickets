package uk.gov.dwp.uc.pairtest.services;

public class AccountValidator {

    public boolean isValidAccount(Long accountId) {
        return accountId != null && accountId > 0;
    }
}
