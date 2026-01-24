package com.example.demo.farm;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WalletTest {

    @Test
    void depositAndWithdraw() {
        Wallet w = new Wallet();
        w.depositCents(10_00);
        assertTrue(w.canAfford(5_00));
        assertTrue(w.withdrawCents(5_00));
        assertEquals(5_00, w.getCents());
    }

    @Test
    void withdrawFailsWhenNotEnoughMoney() {
        Wallet w = new Wallet();
        w.depositCents(2_00);
        assertFalse(w.withdrawCents(3_00));
        assertEquals(2_00, w.getCents());
    }
}
