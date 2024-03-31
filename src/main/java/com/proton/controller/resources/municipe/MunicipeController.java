package com.proton.controller.resources.municipe;

import java.net.URI;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.crossstore.ChangeSetPersister.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
// import org.springframework.web.bind.annotation.DeleteMapping;  >> NÃO VAI DELETAR USUÁRIO
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.proton.models.entities.municipe.Municipe;
import com.proton.models.entities.municipe.TokenReqRes;
import com.proton.services.municipe.JwtTokenService;
import com.proton.services.municipe.MunicipeService;

@RestController
@RequestMapping(value = "/municipes")
@CrossOrigin(origins = "http://localhost:3000")
public class MunicipeController {

    @Autowired // Para que o Spring faça essa injeção de Dependência do Service
    private MunicipeService service; // Dependência para o Service

    @Autowired
    private JwtTokenService jwtTokenService;

    // TODO Remover acesso a lista completa! E retirar retorno do atributo senha!

    // Método que responde á requisição do tipo GET do HTTP
    @GetMapping
    public ResponseEntity<List<Municipe>> findAll(@RequestHeader(value = "Authorization", required = false) String token) throws NotFoundException {
        if (service.checkToken(token)) { //Chega se o token é válido
            String email = jwtTokenService.getEmailFromToken(token.substring(7)); //Extrai o email do token
            System.out.println(email);
            String role = service.getRoleByEmail(email); //Pega o valor da role
            System.out.println(role);
            if ("MUNICIPE".equals(role)) { //Verifica se a role é de municipe, se for, o método tá liberado.
                List<Municipe> list = service.findAll();
                return ResponseEntity.ok().body(list);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }
    @GetMapping(value = "/{id}") // A requisição vai aceitar um ID dentro do URL
    public ResponseEntity<Municipe> findById(@PathVariable Integer id) {
        Municipe obj = service.findById(id);
        return ResponseEntity.ok().body(obj);
    }

    // Método que responde á requisição do tipo POST do HTTP
    @PostMapping
    public ResponseEntity<Municipe> insert(@RequestBody Municipe obj) {
        obj = service.insert(obj);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(obj.getId_municipe())
                .toUri();
        return ResponseEntity.created(uri).body(obj); // Código 201
    }

    // TODO NÃO TERÁ DELETE PARA O USUÁRIO >>>> REGRA DE NEGÓCIO
    // @DeleteMapping(value = "/{id}")
    // public ResponseEntity<Void> delete(@PathVariable Integer id){
    // service.delete(id);
    // return ResponseEntity.noContent().build(); //Resposta para sem conteúdo,
    // código 204
    // }

    @PutMapping(value = "{id}") // A requisição vai aceitar um ID dentro do URL
    public ResponseEntity<Municipe> update(@PathVariable Integer id, @RequestBody Municipe obj) {
        obj = service.update(id, obj);
        return ResponseEntity.ok(obj);
    }

    @PostMapping("/login") //Método que gera o token, praticamente é o método de login
    public ResponseEntity<Object> generateToken(@RequestBody TokenReqRes tokenReqRes){
        return ResponseEntity.ok(service.generateToken(tokenReqRes));
    }

    @PostMapping("/validar-token")
    public ResponseEntity<Object> validateToken(@RequestBody TokenReqRes tokenReqRes){
        return ResponseEntity.ok(service.validateToken(tokenReqRes));
    }

}
