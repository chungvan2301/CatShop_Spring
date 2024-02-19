 document.addEventListener('DOMContentLoaded', function() {
    toastr.options = {
        closeButton: true,
        debug: false,
        newestOnTop: false,
        progressBar: true,
        positionClass: "toast-top-right",
        preventDuplicates: false,
        showDuration: "300",
        hideDuration: "1000",
        timeOut: "2000",
        extendedTimeOut: "1000",
        showEasing: "swing",
        hideEasing: "linear",
        showMethod: "fadeIn",
        hideMethod: "fadeOut"
    };

    var msg = document.getElementById('sucessfullPaid').value;

    if (msg === "sucessfullPaid") {
        toastr.success("Thanh toán thành công");
    }

    //Định dạng tiền tệ
    var elements = document.querySelectorAll(".currency");
    elements.forEach(function(element) {
        var elementValue = element.innerText;
        var formattedValue = parseFloat(elementValue.replace(/\D/g, '')).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
        element.innerText = formattedValue;
    });
});