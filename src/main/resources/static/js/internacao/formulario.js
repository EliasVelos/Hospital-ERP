// Espera que o documento esteja totalmente carregado
$(document).ready(function() {
    // Aplica a máscara de Data e Hora no formato dd/MM/yyyy HH:mm
    $('#dataEntrada').mask('00/00/0000 00:00');
    $('#dataAlta').mask('00/00/0000 00:00');
});
flatpickr(".datetimepicker", {
enableTime: true,
time_24hr: true,
dateFormat: "d/m/Y H:i",
locale: "pt"
});