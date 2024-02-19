function updateQuantity(input) {
    var cartId = input.dataset.cartId;
    console.log(input.value);
    console.log(cartId);
    var xhr = new XMLHttpRequest();
    var csrfToken = document.querySelector('meta[name="_csrf"]').content;
    var csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;
    xhr.open('POST', '/cart/update/' + cartId, true);
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.setRequestHeader(csrfHeader, csrfToken);
    xhr.send('quantity=' + input.value);

    xhr.onload = function() {
        if (xhr.status == 200) {
            // Yêu cầu POST thành công, thực hiện yêu cầu GET để cập nhật dữ liệu trên trang
            window.location.reload();
        }
    };
}

function updateCartStatus(checkbox) {
    var cartId = checkbox.value;
    var url = checkbox.checked ? '/cart/sold/' : '/cart/not-sold/';

    fetch(url + cartId, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
            'X-CSRF-TOKEN': document.querySelector('meta[name="_csrf"]').content
        }
    })
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .catch(error => {
        console.error('There has been a problem with your fetch operation:', error);
    });
    if (checkbox.checked) {updateSumAdd(cartId);} else {updateSumRemove(cartId);}
}


window.onload = function() {
    var inputs = document.getElementsByTagName('input');
    for(var i = 0; i < inputs.length; i++) {
        if(inputs[i].type.toLowerCase() == 'checkbox') {
            inputs[i].checked = false;
        }
    }
}

function updateSumAdd(cartId) {
    var cart = document.getElementById("cartPrice"+cartId).innerText;
    var sum = document.getElementById("sum").innerText;

    var totalFee = parseFloat(sum.replace(/\D/g, '')) + parseFloat(cart.replace(/\D/g, ''));
    document.getElementById("sum").innerText = totalFee.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    updateCheckoutButton ();
}

function updateSumRemove(cartId) {
    var cart = document.getElementById("cartPrice"+cartId).innerText;
    var sum = document.getElementById("sum").innerText;

    var totalFee = parseFloat(sum.replace(/\D/g, '')) - parseFloat(cart.replace(/\D/g, ''));
    document.getElementById("sum").innerText = totalFee.toLocaleString('vi-VN', { style: 'currency', currency: 'VND' });
    updateCheckoutButton ();
}

function updateCheckoutButton () {
var sum = document.getElementById("sum").innerText;
var sumFloat = parseFloat(sum.replace(/\D/g, ''));
var buttonCheckout = document.getElementById("buttonCheckout");
    if (sumFloat == 0) {
        buttonCheckout.setAttribute('disabled', 'disabled');
        buttonCheckout.setAttribute('title', 'Vui lòng chọn sản phẩm');

    } else {
        buttonCheckout.removeAttribute('disabled');
        buttonCheckout.removeAttribute('title');
    }
}
