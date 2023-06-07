package com.mucompany.muinmusic.payment;

import org.springframework.stereotype.Service;

@Service
public class PaymentService {

    public boolean paymentResult(String result) {
        if (result.equals("success")) {
            return true;
        }
        return false;
    }
}
