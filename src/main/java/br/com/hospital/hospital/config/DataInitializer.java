package br.com.hospital.hospital.config;

import br.com.hospital.hospital.entity.Usuario;
import br.com.hospital.hospital.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Classe para inicializar dados de usuários de teste no banco de dados.
 * É executada uma única vez, após a inicialização do Spring Boot.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public void run(String... args) throws Exception {
        
        // Verifica se já existe algum usuário cadastrado para evitar duplicatas
        if (usuarioRepository.count() == 0) {
            
            System.out.println("-> Inicializando com o usuário Admin padrão");

            // --- 1. Usuário ADMIN ---
            Usuario admin = new Usuario();
            admin.setUsername("admin");
            admin.setPassword("123"); 
            admin.setRole("ADMIN");

            // Lista de usuários a serem salvos
            List<Usuario> usuariosIniciais = Arrays.asList(admin);

            // Salva todos de uma vez
            usuarioRepository.saveAll(usuariosIniciais);
            
            System.out.println("-> O usuário padrão Admin foi adicionado com sucesso:");
            System.out.println("   - admin / 123 (ADMIN)");
        } else {
             System.out.println("-> Usuário já existe. Inicialização de dados ignorada.");
        }
    }
}