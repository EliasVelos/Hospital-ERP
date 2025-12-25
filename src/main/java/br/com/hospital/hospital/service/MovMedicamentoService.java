package br.com.hospital.hospital.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.hospital.hospital.entity.Medicamento;
import br.com.hospital.hospital.entity.MovMedicamento;
import br.com.hospital.hospital.entity.LoteMedicamento; // Import necessário
import br.com.hospital.hospital.repository.MovMedicamentoRepository;

@Service
public class MovMedicamentoService {

    @Autowired
    private MovMedicamentoRepository movMedicamentoRepository;

    @Autowired 
    private MedicamentoService medicamentoService;

    @Autowired 
    private LoteMedicamentoService loteMedicamentoService; 

    // ----------------------------------------------------
    // 1. LÓGICA DE NEGÓCIO (Processamento de Movimentação)
    // ----------------------------------------------------
    @Transactional
    public void processarMovimentacao(MovMedicamento movMedicamento, LocalDate dataValidade, String numeroLote) throws IllegalArgumentException {
        
        // Validação inicial do medicamento (se necessário, buscar o medicamento mestre)
        if (movMedicamento.getMedicamento() == null || movMedicamento.getMedicamento().getIdMedicamento() == null) {
             throw new IllegalArgumentException("O medicamento deve ser selecionado.");
        }
        
        // 1. Extrai dados
        String tipo = movMedicamento.getTipoMovimentacao();
        Integer idMedicamento = movMedicamento.getMedicamento().getIdMedicamento();
        Integer quantidade = movMedicamento.getQuantidadeMovimentada();
        
        // 2. Processa Entrada ou Saída
        if ("ENTRADA".equalsIgnoreCase(tipo)) {
            // Validações obrigatórias para ENTRADA
            if (dataValidade == null) {
                throw new IllegalArgumentException("A data de validade é obrigatória para a entrada de novos lotes.");
            }
            if (numeroLote == null || numeroLote.trim().isEmpty()) {
                throw new IllegalArgumentException("O número de lote é obrigatório para a entrada.");
            }
            
            // 🚨 CORREÇÃO PRINCIPAL (CRIAÇÃO E ASSOCIAÇÃO DO LOTE)
            // Chama o Service para criar um NOVO Lote no banco
            LoteMedicamento novoLote = loteMedicamentoService.registrarEntrada(idMedicamento, quantidade, dataValidade, numeroLote);
            
            // Associa o Lote RECÉM-CRIADO ao registro de Movimentação
            // Isso é o que faltava para parar a sobrescrita e vincular a movimentação ao lote
            movMedicamento.setLote(novoLote);

        } else if ("SAIDA".equalsIgnoreCase(tipo)) {
            
            // A Saída no Service usa a lógica FEFO (First Expired, First Out)
            loteMedicamentoService.registrarSaida(idMedicamento, quantidade);
            
            // 🚨 MELHORIA: Para Saída, o campo Lote deve ser nulo (se não for selecionado)
            // Se você tiver um campo de seleção de lote para Saída, a lógica aqui deve ser ajustada
            movMedicamento.setLote(null); // Garantindo que não carregamos lixo

        } else {
            throw new IllegalArgumentException("Tipo de movimentação inválido: " + tipo);
        }
        
        // 3. Salva o registro da movimentação (Histórico)
        movMedicamento.setDataMovimentacao(LocalDateTime.now());
        // Garante que o ID é nulo se for uma nova movimentação (previne sobrescrita)
        if (movMedicamento.getIdMovimentacao() != null && movMedicamento.getIdMovimentacao() == 0) {
            movMedicamento.setIdMovimentacao(null); 
        }
        movMedicamentoRepository.save(movMedicamento);
    }
    
    // ----------------------------------------------------
    // 2. MÉTODOS CRUD BÁSICOS E PAGINAÇÃO
    // ----------------------------------------------------
    
    // Método FindAll UNIFICADO (para o Controller)
    public Page<MovMedicamento> findAll(String termoBusca, Pageable pageable) {
        if (termoBusca != null && !termoBusca.trim().isEmpty()) {
            // Busca pelo nome do medicamento
            return movMedicamentoRepository.findByMedicamentoNomeMedicamentoContainingIgnoreCase(termoBusca, pageable); 
        }
        // Retorna todos os registros com paginação e ordenação
        return movMedicamentoRepository.findAll(pageable);
    }
    
    public MovMedicamento save(MovMedicamento movMedicamento) {
        return movMedicamentoRepository.save(movMedicamento);
    }

    public MovMedicamento findById(Integer id) {
        return movMedicamentoRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        // 🚨 Cuidado: Idealmente, a exclusão deveria reverter a movimentação de estoque
        movMedicamentoRepository.deleteById(id);
    }

    public void salvar(MovMedicamento mov1) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'salvar'");
    }
}