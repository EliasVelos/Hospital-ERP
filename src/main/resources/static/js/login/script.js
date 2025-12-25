document.addEventListener('DOMContentLoaded', function () {
    const selectElement = document.querySelector('select[name="tipoUsuario"]');
    const cpfField = document.getElementById('cpfField');
    const cpfInput = document.getElementById('cpfInput');

    function toggleCpfField() {
        const selectedValue = selectElement.value;
        
        // Exibe o campo CPF apenas para Pacientes, Médicos, e Funcionários
        if (['PACIENTE', 'MEDICO', 'FUNCIONARIO'].includes(selectedValue)) {
            cpfField.style.display = 'block';
            cpfInput.setAttribute('required', 'required'); // Torna o CPF obrigatório
        } else {
            cpfField.style.display = 'none';
            cpfInput.removeAttribute('required'); // Remove a obrigatoriedade
        }
    }

    // Inicializa e monitora a mudança
    toggleCpfField(); 
    selectElement.addEventListener('change', toggleCpfField);
});

$(document).ready(function () {
            
            // 1. Aplica a máscara e preenche o valor existente (se estiver em edição)
            // CPF
            const $cpfField = $('input[name="cpfPaciente"]');
            $cpfField.mask('000.000.000-00');
            // Recarrega o valor para forçar a aplicação da máscara no valor existente
            if ($cpfField.val()) {
                $cpfField.val($cpfField.val()).trigger('input'); 
            }

            // 2. Aplica a máscara e preenche o valor existente (se estiver em edição)
            // Telefone
            const $telField = $('input[name="telefonePaciente"]');
            // Máscara flexível para celular (9º dígito)
            const phoneMask = function (val) {
                return val.replace(/\D/g, '').length === 11 ? '(00) 00000-0000' : '(00) 0000-00009';
            };
            $telField.mask(phoneMask, {
                onKeyPress: function (val, e, field, options) {
                    field.mask(phoneMask.apply({}, arguments), options);
                }
            });
            // Recarrega o valor para forçar a aplicação da máscara no valor existente
            if ($telField.val()) {
                $telField.val($telField.val()).trigger('input');
            }
        });