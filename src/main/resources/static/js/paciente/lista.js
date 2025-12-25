$(document).ready(function() {
    var $input = $('#filtroInput');
    var $tabela = $('#tabelaPacientes');
    var $linhas = $tabela.find('tbody tr');
    
    // 1. Função principal de filtro (Sem alterações)
    $input.on('keyup', function() {
        var termo = $(this).val().toLowerCase().trim();

        $linhas.each(function() {
            var $linha = $(this);
            var textoLinha = $linha.text().toLowerCase();

            if (textoLinha.indexOf(termo) > -1) {
                $linha.show();
            } else {
                $linha.hide();
            }
        });
    });

    // 2. Função para limpar o filtro (Sem alterações)
    window.limparFiltro = function() {
        $input.val(''); 
        $linhas.show();  
        $input.focus(); 
    };
});