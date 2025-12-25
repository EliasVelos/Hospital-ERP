package br.com.hospital.hospital.service;

import java.time.LocalDate;
import java.util.List; // 🚨 IMPORT NECESSÁRIO

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort; // 🚨 IMPORT NECESSÁRIO
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; 

import br.com.hospital.hospital.entity.LoteMedicamento;
import br.com.hospital.hospital.entity.Medicamento;
import br.com.hospital.hospital.repository.MedicamentoRepository;

@Service
public class MedicamentoService {

    @Autowired
    private MedicamentoRepository medicamentoRepository;

    @Autowired
    @Lazy
    private LoteMedicamentoService loteMedicamentoService;

    // -----------------------------------------------------------------
    // MÉTODOS DE LÓGICA DE LOTE (Para o Controller/View de Medicamentos)
    // -----------------------------------------------------------------
    
    // NOVO: Calcula o estoque total somando todos os lotes
    public Integer calcularEstoqueTotal(Integer idMedicamento) {
        return loteMedicamentoService.calcularEstoqueTotalPorMedicamento(idMedicamento);
    }

    // NOVO: Retorna a data de validade mais próxima a vencer (primeiro lote na ordem FEFO)
    public LocalDate encontrarProximaValidade(Integer idMedicamento) {
        // Reutiliza o método que busca lotes disponíveis ordenados
        List<LoteMedicamento> lotes = loteMedicamentoService.findLotesDisponiveisOrdenadosPorValidade(idMedicamento);
        
        // Retorna a validade do primeiro lote (o que vence primeiro), ou null se não houver estoque
        return lotes.isEmpty() ? null : lotes.get(0).getDataValidade();
    }
    
    // -----------------------------------------------------------------
    // MÉTODOS CRUD BÁSICOS E DE BUSCA
    // -----------------------------------------------------------------
    
    public Medicamento save(Medicamento medicamento) {
        return medicamentoRepository.save(medicamento);
    }

    // --- MÉTODO 1: Para a Lista Paginada (Usado em /medicamentos/listar) ---
    public Page<Medicamento> findAll(String termoBusca, Pageable pageable) {
    if (termoBusca != null && !termoBusca.trim().isEmpty()) {
        // 🚨 CORREÇÃO: Chamar o método sem o prefixo 'Medicamento' duplicado
        return medicamentoRepository.findByNomeMedicamentoContainingIgnoreCase(termoBusca, pageable);
    }
    return medicamentoRepository.findAll(pageable);
    }

    // --- 🚨 A CORREÇÃO: MÉTODO 2: Para o Dropdown (Usado em /movMedicamentos/criar) ---
    // Este método corrige o erro "not applicable for the arguments ()"
    public List<Medicamento> findAll() {
        // Retorna todos os medicamentos, ordenados por nome (bom para dropdowns)
        return medicamentoRepository.findAll(Sort.by(Sort.Direction.ASC, "nomeMedicamento"));
    }

    public Page<Medicamento> findAll(Pageable pageable) {
        return medicamentoRepository.findAll(pageable);
    }
    
    // 2. Método para buscar por nome (o que o controller agora chama)
    public Page<Medicamento> findByNomeMedicamentoContainingIgnoreCase(String termo, Pageable pageable) {
        // 🚨 Este método deve existir no Repository
        return medicamentoRepository.findByNomeMedicamentoContainingIgnoreCase(termo, pageable);
    }

    public Medicamento findById(Integer id) {
        // Retorna a entidade se existir, ou null se não for encontrada (simplificação para Controller)
        return medicamentoRepository.findById(id).orElse(null);
    }

    public void deleteById(Integer id) {
        // ATENÇÃO: Se um medicamento for excluído, todos os lotes relacionados (LoteMedicamento) 
        // devem ser excluídos via CASCADE ou lógica no Service.
        medicamentoRepository.deleteById(id);
    }
    public int contarAlertasEstoqueBaixo() {
    final int LIMITE_BAIXO = 10; // <-- Defina seu limite de alerta aqui
    int alertas = 0;

    // Pega TODOS os medicamentos (base)
    List<Medicamento> todosMedicamentos = medicamentoRepository.findAll();

    // Para cada medicamento, calcula o estoque
    for (Medicamento med : todosMedicamentos) {
        int estoqueAtual = calcularEstoqueTotal(med.getIdMedicamento()); // Usa seu método existente
        if (estoqueAtual < LIMITE_BAIXO) {
            alertas++;
        }
    }
    return alertas;
    }

        @Transactional
        public void desativar(Integer id) {
            Medicamento m = medicamentoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Medicamento não encontrado"));
            m.setAtivo(false);
            medicamentoRepository.save(m);
        }

        public List<Medicamento> listarAtivos() {
        return medicamentoRepository.findByAtivoTrue();
        }
}