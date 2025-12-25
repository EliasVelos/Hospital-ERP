package br.com.hospital.hospital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hospital.hospital.entity.Internacao;
import br.com.hospital.hospital.repository.InternacaoRepository;

@Service
public class InternacaoService {

    @Autowired
    private InternacaoRepository internacaoRepository;

    // Salvar ou Atualizar (Internacao)
    public Internacao save(Internacao internacao) {
        return internacaoRepository.save(internacao);
    }

    // Listar todas as Internações
    public List<Internacao> findAll(){
        return internacaoRepository.findAll();
    }

    // Buscar Internação por ID
    public Internacao findById(Integer id){ 
        // Retorna a internação ou 'null' se não encontrar
        return internacaoRepository.findById(id).orElse(null);
    }

    // Excluir Internação por ID
    public void deleteById(Integer id){
        internacaoRepository.deleteById(id);
    }

    /**
     * Busca os IDs dos leitos ocupados por internações que ainda estão ativas.
     * Esta lista será usada para excluir esses leitos da seleção no formulário.
     * @return Lista de IDs (Integer) dos leitos que estão em uso.
     */
    public List<Integer> findLeitoIdsOcupados() {
        // ✅ CORREÇÃO: Chamando o método do Repository com o status "Ativa"
        return internacaoRepository.findIdQuartoByStatus("Ativa");
    }

    public boolean existeInternacaoAtivaParaPaciente(Internacao internacao) {
    // 1. Busca no repositório por uma internação ATIVA para o paciente.
    Optional<Internacao> internacaoAtiva = internacaoRepository.findByPacienteAndStatus(
        internacao.getPaciente(), "Ativa"
    );

    // 2. Se for uma nova internação (ID nulo) e já existir uma ativa, retorna true.
    if (internacao.getIdInternacao() == null && internacaoAtiva.isPresent()) {
        return true;
    }
    
    // 3. Se for uma edição (ID não nulo), verifica se a internação ativa encontrada
    // não é a internação que estamos tentando salvar/editar.
    if (internacao.getIdInternacao() != null && internacaoAtiva.isPresent()) {
        // Se o ID da internação ativa for diferente do ID que estamos editando,
        // significa que estamos tentando manter ou criar uma nova ativa enquanto outra já existe.
        return !internacaoAtiva.get().getIdInternacao().equals(internacao.getIdInternacao());
    }
    
    // Nenhuma restrição ativa encontrada.
    return false;
}
public int contarInternacoesAtivas() {
    return internacaoRepository.countInternacoesAtivas();
}

        

}