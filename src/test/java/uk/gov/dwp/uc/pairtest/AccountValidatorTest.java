package uk.gov.dwp.uc.pairtest;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class AccountValidatorTest {

    private final AccountValidator underTest = new AccountValidator();

    @Test
    public void shouldReturnTrueWhenAccountIdIsGreaterThanZero() {
        assertThat(underTest.isValidAccount(1L), is(true));
    }

    @Test
    public void shouldReturnFalseWhenAccountIdIsNull() {
        assertThat(underTest.isValidAccount(null), is(false));
    }

    @Test
    public void shouldReturnFalseWhenAccountIdIsZero() {
        assertThat(underTest.isValidAccount(0L), is(false));
    }

    @Test
    public void shouldReturnFalseWhenAccountIdIsNegative() {
        assertThat(underTest.isValidAccount(-1L), is(false));
    }
}