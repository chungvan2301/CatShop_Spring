let selectedCard = 0;

function selectCard(cardNumber) {
        updateHiddenInputValue(cardNumber);
        updateCardStyles(cardNumber);
}

    function updateCardStyles(cardNumber) {
        const cards = document.querySelectorAll('.card');
        cards.forEach((card, index) => {
            if (index === cardNumber) {
                card.classList.add('selected');
            } else {
                card.classList.remove('selected');
            }
        });
    }

    function updateHiddenInputValue(cardNumber) {
        const hiddenInput = document.getElementById('selectedCardInput');
        hiddenInput.value = cardNumber;
    }

//Hide shipping button
document.addEventListener("DOMContentLoaded", function() {
    var textDayReceived = document.getElementById("dayReceivedSelected").innerText;
    document.getElementById("dayReceived").value = textDayReceived;

    var feeGoodsValue = document.getElementById("feeGoods").innerText;
    var feeShippingValue = document.getElementById("feeShipping").innerText;transportFeeSelected
    var totalFee = parseFloat(feeGoodsValue.replace(/\D/g, '')) + parseFloat(feeShippingValue.replace(/\D/g, ''));
    document.getElementById("feeTotal").innerText = totalFee.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

    var address = document.getElementById("addressDefaultId").value;
    var shippingButton = document.getElementById("shippingButton");
    if (address==0) {
            shippingButton.setAttribute('disabled', 'disabled');
            shippingButton.setAttribute('title', 'Vui lòng chọn địa chỉ');
    }

    var goodsFee = document.getElementById("goodsFee");
    var transportFee = document.getElementById("transportFee");
    var totalPrice = document.getElementById("totalPrice");
    goodsFee.value = parseFloat(feeGoodsValue.replace(/\D/g, ''));
    transportFee.value = parseFloat(feeShippingValue.replace(/\D/g, ''));
    totalPrice.value = totalFee;

    formatCurrency("feeGoods");
    formatCurrency("feeShipping");

    updateOrderButton();
})

//Select method payment


function selectPaymentMethod(selection) {
        updateBorderSelected(selection);
        updatePaymentMethodValue(selection);
}

function updateBorderSelected(selection) {
        const cards = document.querySelectorAll('.selection');
        const payment = document.getElementById("payment");
        const QRCode = document.getElementById("img-QRCode");
        if (selection===1) {
            payment.style.minHeight = "300px";
            QRCode.style.visibility = "visible";

        } else {
            payment.style.removeProperty("min-height");
            QRCode.style.visibility = "hidden";
        }

        cards.forEach((card, index) => {
            if (index === selection) {
                card.classList.add('border-primary');
            } else {
                card.classList.remove('border-primary');
            }
        });
    }
    function updatePaymentMethodValue(selection) {
        const paymentMethod = document.getElementById('paymentMethod');
        paymentMethod.value = selection;
    }

//Định dạng tiền tệ
function formatCurrency(elementId) {
        // Lấy giá trị từ thẻ có id elementId
        var elementValue = document.getElementById(elementId).innerText;

        // Chuyển đổi giá trị từ chuỗi sang số và định dạng tiền tệ
        var formattedValue = parseFloat(elementValue.replace(/\D/g, '')).toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });

        // Đặt nội dung của thẻ có id elementId bằng giá trị đã định dạng
        document.getElementById(elementId).innerText = formattedValue;
    }

function updateOrderButton () {
    var feeGoods = document.getElementById("feeGoods").innerText;
    var feeShipping = document.getElementById("feeShipping").innerText;
    var buttonOrder = document.getElementById("buttonOrder");

    if (feeGoods !== "0" && feeShipping !== "0") {
            buttonOrder.removeAttribute('disabled');
            buttonOrder.removeAttribute('title');
        } else {
            buttonOrder.setAttribute('disabled', 'disabled');
            buttonOrder.setAttribute('title', 'Vui lòng chọn đầy đủ thông tin');
        }
}



