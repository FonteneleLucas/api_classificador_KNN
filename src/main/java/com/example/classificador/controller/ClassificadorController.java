package com.example.classificador.controller;

import com.example.classificador.model.request.ClassificadorRequest;
import com.example.classificador.model.response.ClassificadorResponse;
import com.example.classificador.service.ClassificadorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileNotFoundException;

@RestController
@RequestMapping("/api")
public class ClassificadorController {

    @Autowired
    private ClassificadorService classificadorService;

    @PostMapping("/classificador")
    public ResponseEntity<ClassificadorResponse> classificador(@RequestBody ClassificadorRequest classificadorRequest) throws FileNotFoundException {
        ClassificadorResponse response = classificadorService.classificador(classificadorRequest);
        return ResponseEntity.ok(response);
    }
}