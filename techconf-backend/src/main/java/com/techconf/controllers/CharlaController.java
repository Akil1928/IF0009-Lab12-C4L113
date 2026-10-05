package com.techconf.controllers;

import com.techconf.models.Asistente;
import com.techconf.models.Charla;
import com.techconf.repositories.AsistenteRepository;
import com.techconf.repositories.CharlaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/charlas")
@CrossOrigin(origins = "http://localhost:4200")
public class CharlaController {

    @Autowired
    private CharlaRepository repository;

    @Autowired
    private AsistenteRepository asistenteRepository;

    @GetMapping
    public List<Charla> obtenerTodas() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public Charla obtenerPorId(@PathVariable Long id) {
        return buscarCharla(id);
    }

    @PostMapping
    public Charla registrarCharla(@RequestBody Charla nuevaCharla) {
        nuevaCharla.setId(null);
        return repository.save(nuevaCharla);
    }

    // Vincula un asistente a la charla indicada
    @PostMapping("/{id}/asistentes")
    public ResponseEntity<Asistente> inscribirAsistente(@PathVariable Long id,
                                                        @RequestBody Asistente asistente) {
        Charla charla = buscarCharla(id);

        if (asistenteRepository.existsByCharla_IdAndCorreoIgnoreCase(id, asistente.getCorreo())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "Ya existe un asistente con ese correo inscrito en esta charla.");
        }

        asistente.setId(null);
        asistente.setCharla(charla);
        return ResponseEntity.status(HttpStatus.CREATED).body(asistenteRepository.save(asistente));
    }

    private Charla buscarCharla(Long id) {
        return repository.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "No existe la charla con id " + id));
    }
}
