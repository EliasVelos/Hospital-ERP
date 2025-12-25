package br.com.hospital.hospital.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional; // Import para findById

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.hospital.hospital.entity.LoteMedicamento;
import br.com.hospital.hospital.entity.Medicamento;
import br.com.hospital.hospital.repository.LoteMedicamentoRepository;

@Service
public class LoteMedicamentoService {

    @Autowired
    private LoteMedicamentoRepository loteMedicamentoRepository;
    
    @Autowired
    private MedicamentoService medicamentoService; 

    // ----------------------------------------------------
    // 1. GESTÃO DE ENTRADA (CRIAÇÃO DE NOVO LOTE)
    // ----------------------------------------------------
    @Transactional
    public LoteMedicamento registrarEntrada(
        Integer idMedicamento, 
        Integer quantidade, 
        LocalDate dataValidade, 
        String numeroLote
    ) {
        Medicamento medicamento = medicamentoService.findById(idMedicamento);
        if (medicamento == null) {
            throw new RuntimeException("Medicamento mestre não encontrado.");
        }

        // 1. Cria um novo Lote
        LoteMedicamento novoLote = new LoteMedicamento();
        novoLote.setMedicamento(medicamento);
        novoLote.setQuantidadeEmEstoque(quantidade);
        novoLote.setDataValidade(dataValidade);
        novoLote.setNumeroLote(numeroLote);
        
        // 2. Salva o novo Lote.
        return loteMedicamentoRepository.save(novoLote);
    }

    // ----------------------------------------------------
    // 2. GESTÃO DE SAÍDA (LÓGICA FEFO)
    // ----------------------------------------------------
    @Transactional
    public void registrarSaida(Integer idMedicamento, Integer quantidadeRequerida) {
        
        // 🚨 NOVO: Busca apenas lotes que NÃO ESTÃO VENCIDOS e com estoque > 0
        List<LoteMedicamento> lotesDisponiveis = findLotesDisponiveisOrdenadosPorValidade(idMedicamento);
        
        // Validação inicial do estoque
        if (lotesDisponiveis.isEmpty() || calcularEstoqueTotalPorMedicamento(idMedicamento) < quantidadeRequerida) {
             throw new IllegalArgumentException("Estoque total insuficiente ou inexistente para o medicamento ID: " + idMedicamento);
        }
        
        int quantidadeRestante = quantidadeRequerida;

        // Consome dos lotes que vencem primeiro (FEFO)
        for (LoteMedicamento lote : lotesDisponiveis) {
            
            if (quantidadeRestante <= 0) break; 
            
            Integer quantidadeNoLote = lote.getQuantidadeEmEstoque();
            
            if (quantidadeNoLote >= quantidadeRestante) {
                // Cobrir a saída e parar
                lote.setQuantidadeEmEstoque(quantidadeNoLote - quantidadeRestante);
                loteMedicamentoRepository.save(lote);
                quantidadeRestante = 0;
            } else {
                // Consome todo o lote e parte para o próximo
                quantidadeRestante -= quantidadeNoLote;
                lote.setQuantidadeEmEstoque(0); 
                loteMedicamentoRepository.save(lote);
            }
        }

        if (quantidadeRestante > 0) {
            // Garante que não houve erro no cálculo
            throw new IllegalStateException("Erro de cálculo de estoque: Estoque ficou negativo após a saída.");
        }
    }
    
    // ----------------------------------------------------
    // 3. MÉTODOS DE CONVENIÊNCIA (Para Controller e Service de Medicamento)
    // ----------------------------------------------------
    
    // 🚨 MÉTODO ATUALIZADO (Antes de ser usado no registrarSaida)
    // Busca lotes disponíveis e ordenados (Exclui vencidos e estoque zero)
    public List<LoteMedicamento> findLotesDisponiveisOrdenadosPorValidade(Integer idMedicamento) {
        // Usa a data atual como filtro para excluir os vencidos
        return loteMedicamentoRepository.findByMedicamento_IdMedicamentoAndQuantidadeEmEstoqueGreaterThanAndDataValidadeGreaterThanEqualOrderByDataValidadeAsc(
            idMedicamento, 0, LocalDate.now());
    }

    // Calcula o estoque total somando todos os lotes ativos
    public Integer calcularEstoqueTotalPorMedicamento(Integer idMedicamento) {
        return findLotesDisponiveisOrdenadosPorValidade(idMedicamento).stream()
            .mapToInt(LoteMedicamento::getQuantidadeEmEstoque)
            .sum();
    }
    
    // Retorna o Lote com a validade mais próxima (para lista de Medicamento)
    public LocalDate encontrarProximaValidade(Integer idMedicamento) {
        List<LoteMedicamento> lotes = findLotesDisponiveisOrdenadosPorValidade(idMedicamento);
        return lotes.isEmpty() ? null : lotes.get(0).getDataValidade();
    }
    
    // Lista todos os lotes de um medicamento (útil para relatórios/detalhes)
    public List<LoteMedicamento> findLotesPorMedicamento(Integer idMedicamento) {
        // Esta busca lista todos, incluindo vencidos, para fins de histórico
        return loteMedicamentoRepository.findByMedicamento_IdMedicamentoOrderByDataValidadeAsc(idMedicamento);
    }
    
    // ----------------------------------------------------
    // 4. MÉTODOS CRUD BÁSICOS DE LOTE
    // ----------------------------------------------------
    public LoteMedicamento findById(Long id) {
        return loteMedicamentoRepository.findById(id).orElse(null);
    }

    @Transactional
    public void deleteById(Long id) {
        loteMedicamentoRepository.deleteById(id);
    }
}