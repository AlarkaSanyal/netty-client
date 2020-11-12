package com.example.nettyclient.service;

import com.example.nettyclient.model.RentalRequest;
import com.example.nettyclient.model.RentalResponse;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ClientService {

    @Autowired
    private ObjectFactory<ClientGateway> clientGatewayObjectFactory;

    public RentalResponse getStatus(RentalRequest rentalRequest) {
        ClientGatewayInterface clientGatewayInterface = clientGatewayObjectFactory.getObject();
        return clientGatewayInterface.status(rentalRequest);
    }
}
