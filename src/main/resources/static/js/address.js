function renderCity() {
    var citis = document.getElementById("city");
    var districts = document.getElementById("district");
    var wards = document.getElementById("ward");

    // Thực hiện request để lấy dữ liệu từ file JSON
    var xhr = new XMLHttpRequest();
    xhr.onreadystatechange = function () {
        if (xhr.readyState === 4 && xhr.status === 200) {
            var data = JSON.parse(xhr.responseText);
            renderCityOptions(data);
        }
    };

    xhr.open("GET", "/json/address.json", true);
    xhr.send();

    function renderCityOptions(data) {
        if (!citis || !districts || !wards) {
            console.error("One or more elements not found.");
            return;
        }

        for (const province of data.province) {
            citis.options[citis.options.length] = new Option(province.name, province.idProvince);
        }

        citis.onchange = function () {
            districts.length = 1;
            wards.length = 1;
            if (this.value !== "") {
                const result = data.district.filter(n => n.idProvince === this.value);

                for (const district of result) {
                    districts.options[districts.options.length] = new Option(district.name, district.idDistrict);
                }
            }
        };

        districts.onchange = function () {
            wards.length = 1;
            const dataCity = data.district.filter((n) => n.idProvince === citis.value);
            if (this.value !== "") {
                const dataWards = data.commune.filter(n => n.idDistrict === this.value);

                for (const ward of dataWards) {
                    wards.options[wards.options.length] = new Option(ward.name, ward.idCommune);
                }
            }
        };
    }
}

function renderCity2(addressId) {
    var selectedValueFromServer3 = document.getElementById("wardValue"+addressId).value;
    var selectedValueFromServer1 = document.getElementById("provinceValue"+addressId).value;
    var selectedValueFromServer2 = document.getElementById("districtValue"+addressId).value;
    var citis = document.getElementById("city"+addressId);
    var districts = document.getElementById("district"+addressId);
    var wards = document.getElementById("ward"+addressId);

        // Thực hiện request để lấy dữ liệu từ file JSON
        var xhr = new XMLHttpRequest();
        xhr.onreadystatechange = function () {
            if (xhr.readyState === 4 && xhr.status === 200) {
                var data = JSON.parse(xhr.responseText);
                renderCityOptions(data);
            }
        };

        xhr.open("GET", "/json/address.json", true);
        xhr.send();

        async function renderCityOptions(data) {
                if (!citis || !districts || !wards) {
                    console.error("One or more elements not found.");
                    return;
                }

                //Lấy data province
                for (const province of data.province) {
                    citis.options[citis.options.length] = new Option(province.name, province.idProvince);
                }
                //Đặt select cho province = province ở server
                await selectedProvince(addressId);

                //Đặt select cho district = district ở server
                if (citis.value===selectedValueFromServer1) {
                const result = data.district.filter(n => n.idProvince === selectedValueFromServer1);
                for (const district of result) {
                    districts.options[districts.options.length] = new Option(district.name, district.idDistrict);
                }
                selectedDistrict(addressId);
                }

                citis.onchange = function () {
                    var defaultOption = districts.querySelector('option[value=""]');
                        if (defaultOption) {
                            defaultOption.selected = true;
                    }
                    districts.length = 1;
                    wards.length = 1;
                    if (this.value !== "selectedValueFromServer1") {
                        const result = data.district.filter(n => n.idProvince === this.value);
                        for (const district of result) {
                            districts.options[districts.options.length] = new Option(district.name, district.idDistrict);
                        }
                    }
                };

                //Đặt select cho ward = ward ở server
                if (districts.value===selectedValueFromServer2) {
                    const dataWards = data.commune.filter(n => n.idDistrict === selectedValueFromServer2);
                    for (const ward of dataWards) {
                         wards.options[wards.options.length] = new Option(ward.name, ward.idCommune);
                    }
                    selectedward(addressId);
                }

                districts.onchange = function () {
                    var defaultOption = wards.querySelector('option[value=""]');
                    if (defaultOption) {
                        defaultOption.selected = true;
                    }
                    wards.length = 1;
                    const dataCity = data.district.filter((n) => n.idProvince === citis.value);
                    if (this.value !== "selectedValueFromServer2") {
                        const dataWards = data.commune.filter(n => n.idDistrict === this.value);
                        for (const ward of dataWards) {
                            wards.options[wards.options.length] = new Option(ward.name, ward.idCommune);
                        }
                    }
                };
            }
}

function selectedOption(addressId){
var selectedValueFromServer = document.getElementById("type"+addressId).value;
    // Sử dụng JavaScript để tìm và thêm thuộc tính selected
    var selectElement = document.getElementById("typeOption"+addressId);
    var options = selectElement.options;

    for (var i = 0; i < options.length; i++) {
        if (options[i].value === selectedValueFromServer) {
            options[i].setAttribute("selected", "selected");
            break; // Dừng khi đã tìm thấy và thêm selected
        }
    }
}

function selectedward(addressId){
var selectedValueFromServer = document.getElementById("wardValue"+addressId).value;
    // Sử dụng JavaScript để tìm và thêm thuộc tính selected
    var selectElement = document.getElementById("ward"+addressId);
    var options = selectElement.options;

    for (var i = 0; i < options.length; i++) {
        if (options[i].value === selectedValueFromServer) {
            options[i].setAttribute("selected", "selected");
            break; // Dừng khi đã tìm thấy và thêm selected
        }
    }
}

function selectedProvince(addressId){
var selectedValueFromServer = document.getElementById("provinceValue"+addressId).value;
    // Sử dụng JavaScript để tìm và thêm thuộc tính selected
    var selectElement = document.getElementById("city"+addressId);
    var options = selectElement.options;

    for (var i = 0; i < options.length; i++) {
        if (options[i].value === selectedValueFromServer) {
            options[i].setAttribute("selected", "selected");
            break; // Dừng khi đã tìm thấy và thêm selected
        }
    }
}

function selectedDistrict(addressId){
var selectedValueFromServer = document.getElementById("districtValue"+addressId).value;
    // Sử dụng JavaScript để tìm và thêm thuộc tính selected
    var selectElement = document.getElementById("district"+addressId);
    var options = selectElement.options;

    for (var i = 0; i < options.length; i++) {
        if (options[i].value === selectedValueFromServer) {
            options[i].setAttribute("selected", "selected");
            break; // Dừng khi đã tìm thấy và thêm selected
        }
    }
}


async function runFunctions(addressId) {
    await renderCity2(addressId);
    selectedOption(addressId);
}

window.onload = function () {
    let addressDefaultId = document.getElementById("addressDefaultId").value || 0;
    let addressDefault = document.getElementById("addressId"+addressDefaultId) || 0;
    if (addressDefault!=0) {
        addressDefault.classList.add("border-primary");
    }
}


function selectAddressDeafault(addressId) {
        let addressDefaultId = document.getElementById("addressDefaultId").value;
        let addressDefaultPrevious = document.getElementById("addressId"+addressDefaultId);
        addressDefaultPrevious.classList.remove("border-primary");

        let addressDefaultNow = document.getElementById("addressId"+addressId);
        addressDefaultNow.classList.add("border-primary");
        addressDefaultNow.classList.remove("border-success");
        document.getElementById("addressDefaultId").value = addressId;
}

function handleAddressDeafaultEnter(addressId){
    let addressDefaultId = document.getElementById("addressDefaultId").value;
    let addressDefaultNow = document.getElementById("addressId"+addressId);
    if (addressId!==addressDefaultId) {
        addressDefaultNow.classList.add("border-success");
    }
    addressDefaultNow.style.cursor = 'pointer';
}

function handleAddressDeafaultLeave(addressId){
    let addressDefaultNow = document.getElementById("addressId"+addressId);
    addressDefaultNow.classList.remove("border-success");
}

