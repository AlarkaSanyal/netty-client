package com.example.nettyclient.service;

import com.example.nettyclient.model.RentalRequest;
import com.example.nettyclient.model.RentalResponse;

public interface ClientGatewayInterface {
    public RentalResponse status(RentalRequest rentalRequest);
}
