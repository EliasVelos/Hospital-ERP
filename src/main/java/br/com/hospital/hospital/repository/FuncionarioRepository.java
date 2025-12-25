package br.com.hospital.hospital.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import br.com.hospital.hospital.entity.Funcionario;

@Repository
public interface FuncionarioRepository extends JpaRepository<Funcionario, Integer> {
    
    // Você pode adicionar métodos de consulta personalizados aqui, se precisar
    // Ex: List<Funcionario> findBySetor(String setor);
}