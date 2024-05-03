package com.proton.controller.resources.funcionario;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proton.models.entities.Funcionario;
import com.proton.models.entities.Secretaria;
import com.proton.services.exceptions.ResourceNotFoundException;
import com.proton.services.funcionario.FuncionarioService;
import com.proton.services.seretaria.SecretariaService;
import com.proton.services.user.AuthenticationService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(value = "protoon/funcionarios")
@CrossOrigin(origins = "http://localhost:3000")
public class FuncionarioController { // Definição dos endpoints RESTful

    @Autowired
    private FuncionarioService service;

    @Autowired
    private SecretariaService secretariaService;
    @Autowired
    private AuthenticationService authenticationService;

    @GetMapping()
    public ResponseEntity<List<Funcionario>> findAll(){
        List<Funcionario> list = service.findAll();
        return ResponseEntity.ok().body(list);
    }

    @GetMapping(value = "/bytoken")
    public ResponseEntity<Funcionario> findByToken(HttpServletRequest request){
        Integer id = authenticationService.getUserIdFromToken(request);
        Funcionario obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    @GetMapping(value = "/{id}")
    public ResponseEntity<Funcionario> findById(@PathVariable Integer id){
        try {
            Funcionario obj = service.findById(id);
            return ResponseEntity.ok().body(obj);
        } catch (EntityNotFoundException e) {
            e.printStackTrace();
			throw new ResourceNotFoundException(id);
        }
   
    }

    @PostMapping
    public ResponseEntity<Funcionario> insert(@RequestBody Funcionario obj){
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getIdFuncionario()).toUri();
        return ResponseEntity.created(uri).body(obj);
    }

    // @PutMapping(value = "/bytoken")
    // public ResponseEntity<Funcionario> updateByToken(HttpServletRequest request, @RequestBody Funcionario obj){
    //     Integer id = authenticationService.getUserIdFromToken(request);
    //     obj = service.update(id, obj);
    //     return ResponseEntity.ok(obj);
    // }
    @PutMapping(value = "/{id}/{idSec}")
    public ResponseEntity<Funcionario> updateById(@PathVariable Integer id, @PathVariable Long idSec, @RequestBody Funcionario obj){
        Secretaria secretaria = secretariaService.findById(idSec);
        obj = service.update(id, secretaria, obj);
        return ResponseEntity.ok(obj);
    }

    @PutMapping(value = "/bytoken/{idSec}")
    public ResponseEntity<Funcionario> updateById(HttpServletRequest request, @PathVariable Long idSec, @RequestBody Funcionario obj){
        Integer id = authenticationService.getUserIdFromToken(request);
        Secretaria secretaria = secretariaService.findById(idSec);
        obj = service.update(id, secretaria, obj);
        return ResponseEntity.ok(obj);
    }
    

    @PutMapping(value = "/bytoken")
    public ResponseEntity<Funcionario> updateByToken(HttpServletRequest request, @RequestBody Funcionario obj){
        Integer id = authenticationService.getUserIdFromToken(request);
        obj = service.updateByToken(id, obj);
        return ResponseEntity.ok(obj);
    }
    
}
