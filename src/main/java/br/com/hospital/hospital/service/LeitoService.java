package br.com.hospital.hospital.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import br.com.hospital.hospital.entity.Leito;
import br.com.hospital.hospital.repository.LeitoRepository;
import br.com.hospital.hospital.repository.InternacaoRepository;

@Service
public class LeitoService {

    @Autowired
    private LeitoRepository leitoRepository;
    
    @Autowired
    private InternacaoRepository internacaoRepository;

    // --- CRUD BÁSICO ---
    public Leito save(Leito leito) {
        return leitoRepository.save(leito);
    }
    
    // Retorna todos os leitos
    public List<Leito> findAll() {
        return leitoRepository.findAll();
    }
    
    public Leito findById(Integer id) {
        return leitoRepository.findById(id).orElse(null);
    }
    
    public void deleteById(Integer id) {
        leitoRepository.deleteById(id);
    }

    // --- LÓGICA DE NEGÓCIO E RELATÓRIOS ---

    /**
     * Retorna todos os leitos, excluindo aqueles cujos IDs estão na lista.
     * Usado para evitar a seleção de leitos ocupados em um formulário de internação.
     */
    public List<Leito> findAllExcludingIds(List<Integer> leitoIdsExcluir) {
        if (leitoIdsExcluir == null || leitoIdsExcluir.isEmpty()) {
            return leitoRepository.findAll();
        }
        
        return leitoRepository.findByIdQuartoNotIn(leitoIdsExcluir);
    }
    
    /**
     * Conta o total de leitos cadastrados.
     */
    public long countAllLeitos() {
        return leitoRepository.count();
    }
    
    /**
     * Conta o total de leitos ocupados com base nas internações ATIVAS.
     */
    public long countLeitosOcupados() {
        // Assume-se que 'Ativa' é o status da Internação que indica a ocupação
        // Este método depende do InternacaoRepository retornar uma lista de IDs de leitos ocupados
        return internacaoRepository.findIdQuartoByStatus("Ativa").size();
    }

    /**
     * Calcula a taxa de ocupação hospitalar atual.
     */
    public double calcularTaxaOcupacaoAtual() {
        long totalLeitos = countAllLeitos();
        long leitosOcupados = countLeitosOcupados();

        if (totalLeitos == 0) {
            return 0.0;
        }

        // Cálculo: (Ocupados / Total) * 100
        return ((double) leitosOcupados / totalLeitos) * 100.0;
    }
    
    /**
     * 🟢 NOVO MÉTODO PRINCIPAL: VERIFICA O STATUS DINÂMICO
     * Verifica se existe uma Internação ATIVA associada ao Leito consultando o repositório.
     * * @param leitoId ID do Leito (idQuarto).
     * @return true se o leito estiver ocupado por uma internação ATIVA, false caso contrário.
     */
    public boolean isLeitoOcupado(Integer leitoId) {
        // Usa o método da LeitoRepository que verifica Internacao Ativa por ID do Leito
        return leitoRepository.findInternacaoAtivaByLeitoId(leitoId).isPresent();
    }
    
    /**
     * 🟢 NOVO MÉTODO: Retorna o número de leitos disponíveis (calculado)
     */
    public long countLeitosDisponiveis() {
        long total = countAllLeitos();
        long ocupados = countLeitosOcupados();
        
        // Retorna o total menos os ocupados.
        return total - ocupados;
    }

    // ❌ REMOVIDO: O método 'contarLeitosDisponiveis()' baseado em campo estático foi removido.
    // O método findAvailable() que lançava UnsupportedOperationException foi removido.
}