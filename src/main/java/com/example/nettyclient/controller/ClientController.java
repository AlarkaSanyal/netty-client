package com.example.nettyclient.controller;

import com.example.nettyclient.model.RentalRequest;
import com.example.nettyclient.model.RentalResponse;
import com.example.nettyclient.service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api/rental", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/v1")
    @ResponseStatus(HttpStatus.CREATED)
    public RentalResponse bookRental(@RequestBody RentalRequest rentalRequest) {
        return clientService.getStatus(rentalRequest);
    }
}
