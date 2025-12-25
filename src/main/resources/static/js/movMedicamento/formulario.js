const tipoSelect = document.getElementById('tipoMovimentacao');
        // 🚨 Adiciona o novo campo
        const campoNumeroLote = document.getElementById('campoNumeroLote');
        const numeroLoteInput = document.getElementById('numeroLote');
        
        const campoValidade = document.getElementById('campoValidade');
        const dataValidadeInput = document.getElementById('dataValidade');

        // Função para atualizar a visibilidade no carregamento e mudança
        function updateLoteAndValidadeVisibility() {
            // O campo deve ser obrigatório apenas se a opção for ENTRADA
            const isEntrada = tipoSelect.value === 'ENTRADA';
            
            if (isEntrada) {
                // EXIBE E TORNA OBRIGATÓRIO: Lote e Validade
                campoNumeroLote.style.display = 'block';
                numeroLoteInput.setAttribute('required', 'required');
                
                campoValidade.style.display = 'block';
                dataValidadeInput.setAttribute('required', 'required');
            } else {
                // ESCONDE E REMOVE OBRIGATORIEDADE
                campoNumeroLote.style.display = 'none';
                numeroLoteInput.removeAttribute('required');
                numeroLoteInput.value = ''; // Limpa valor
                
                campoValidade.style.display = 'none';
                dataValidadeInput.removeAttribute('required');
                dataValidadeInput.value = ''; // Limpa valor
            }
        }

        // 1. Inicializa o estado (útil para o modo de EDIÇÃO)
        updateLoteAndValidadeVisibility();
        
        // 2. Adiciona o listener para mudança
        tipoSelect.addEventListener('change', updateLoteAndValidadeVisibility);