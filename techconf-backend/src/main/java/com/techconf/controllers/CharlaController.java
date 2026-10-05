package com.techconf.controllers;

import com.techconf.models.Charla;
import com.techconf.repositories.CharlaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/charlas")
@CrossOrigin(origins = "http://localhost:4200")
public class CharlaController {

    @Autowired
    private CharlaRepository repository;

    @GetMapping
    public List<Charla> obtenerTodas() {
        return repository.findAll();
    }

    @PostMapping
    public Charla registrarCharla(@RequestBody Charla nuevaCharla) {
        return repository.save(nuevaCharla);
    }
}
