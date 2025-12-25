package br.com.hospital.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import br.com.hospital.hospital.entity.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer> {

    Usuario findByUsernameAndPassword(String username, String password);

    Usuario findByUsername(String username);
    
    // NOVO: Buscar usuário com todas as relações carregadas
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.paciente LEFT JOIN FETCH u.medico LEFT JOIN FETCH u.funcionario WHERE u.username = :username AND u.password = :password")
    Optional<Usuario> findByUsernameAndPasswordWithRelations(@Param("username") String username, @Param("password") String password);
    
    @Query("SELECT u FROM Usuario u LEFT JOIN FETCH u.paciente LEFT JOIN FETCH u.medico LEFT JOIN FETCH u.funcionario WHERE u.username = :username")
    Optional<Usuario> findByUsernameWithRelations(@Param("username") String username);

    
}