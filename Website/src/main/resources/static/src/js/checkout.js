function loadCheckout(){
    loadCheckoutPage();

    const provinceName = document.getElementById("provinceName").value;
    const districtName = document.getElementById("districtName").value;
    const wardName = document.getElementById("wardName").value;

    const host = "https://provinces.open-api.vn/api/";

    const callAPI = (api) => {
        return axios.get(api)
            .then((response) => response.data)
            .catch(error => {
                console.error("Error loading data:", error);
            });
    };

    const renderData = (array, select) => {
        let row = '<option value="">Chọn</option>';
        array.forEach(element => {
            row += `<option value="${element.code}" data-name="${element.name}">${element.name}</option>`;
        });
        document.querySelector("#" + select).innerHTML = row;
    };

    const selectOptionByName = (selectId, name) => {
        const options = document.querySelectorAll(`#${selectId} option`);
        options.forEach(option => {
            if (option.getAttribute('data-name') === name) {
                option.selected = true;
            }
        });
    };

    // Hàm xử lý khi tỉnh thay đổi
    const handleProvinceChange = () => {
        const provinceCode = document.querySelector("#province").value;
        if (provinceCode) {
            callAPI(host + "p/" + provinceCode + "?depth=2").then(provinceData => {
                renderData(provinceData.districts, "district");
                document.querySelector("#ward").innerHTML = '<option value="">Chọn xã</option>'; // Xóa xã khi chọn tỉnh mới
            });
        }
    };

    // Hàm xử lý khi huyện thay đổi
    const handleDistrictChange = () => {
        const districtCode = document.querySelector("#district").value;
        if (districtCode) {
            callAPI(host + "d/" + districtCode + "?depth=2").then(districtData => {
                renderData(districtData.wards, "ward");
            });
        }
    };

    // Gọi API để lấy tỉnh
    callAPI(host + "p").then(provinces => {
        renderData(provinces, "province");
        selectOptionByName("province", provinceName);

        let provinceCode = document.querySelector("#province").value;
        if (provinceCode) {
            // Gọi API để lấy danh sách huyện
            callAPI(host + "p/" + provinceCode + "?depth=2").then(provinceData => {
                renderData(provinceData.districts, "district");
                selectOptionByName("district", districtName);
                let districtCode = document.querySelector("#district").value;
                if (districtCode) {
                    // Gọi API để lấy danh sách xã
                    callAPI(host + "d/" + districtCode + "?depth=2").then(districtData => {
                        renderData(districtData.wards, "ward");
                        selectOptionByName("ward", wardName);
                    });
                }
            });
        }
    });

    // Thêm sự kiện khi thay đổi tỉnh
    document.querySelector("#province").addEventListener("change", handleProvinceChange);
    // Thêm sự kiện khi thay đổi huyện
    document.querySelector("#district").addEventListener("change", handleDistrictChange);


    const tongTien = document.getElementById("tongTien").innerText;
    const shipping = document.getElementById("shipping").innerText;
    const totalPrice = document.getElementById("totalPrice");
    const tongTienValue = parseInt(tongTien.replace(/\D/g, ''));
    const shippingValue = parseInt(shipping.replace(/\D/g, ''));
    const total = tongTienValue + shippingValue;
    totalPrice.innerText = total.toLocaleString() + ' VND';
}
const listGioHang = JSON.parse(sessionStorage.getItem('listGioHang'));

function loadCheckoutPage(){
    console.log(listGioHang)
    const tongTien = document.getElementById("tongTien");
    const listSP = document.getElementById("listSP");
    let total = 0;
    listGioHang.forEach(item => {
        listSP.innerHTML += `<div class="minicart-item d-flex">
                                        <div class="mini-img-wrapper">
                                            <img class="mini-img" src="${item.sanPhamChiTiet.anh_spct}" alt="img">
                                        </div>
                                        <div class="product-info">
                                            <h2 class="product-title">${item.sanPhamChiTiet.sanPham.tensanpham}</h2>
                                            <p class="product-vendor">${item.sanPhamChiTiet.size.ten_size} / ${item.sanPhamChiTiet.mauSac.tenmausac}</p>
                                            <p class="product-vendor">${item.sanPhamChiTiet.khuyenMaiChiTiet == null ? parseInt(item.sanPhamChiTiet.gia_ban, 10).toLocaleString() : parseInt(item.sanPhamChiTiet.khuyenMaiChiTiet.giaMoi, 10).toLocaleString()} <del>${item.sanPhamChiTiet.khuyenMaiChiTiet == null ? '' : parseInt(item.sanPhamChiTiet.gia_ban).toLocaleString()}</del> x ${item.soLuong}</p>
                                        </div>
                                    </div>`;
        total += item.tongTien
    })
    tongTien.innerText = total.toLocaleString() + ' VND';
}

let userData = {}

function validateForm() {
    // Lấy các giá trị từ form
    const id = document.getElementById("idKh").value;
    const hoTen = document.getElementsByName("hoTen")[0].value.trim();
    const email = document.getElementsByName("email")[0].value.trim();
    const soDienThoai = document.getElementsByName("soDienThoai")[0].value.trim();
    const diaChi = document.getElementsByName("diaChi")[0].value.trim();
    const thanhPho = document.getElementsByName("thanhPho")[0].value;
    const huyen = document.getElementsByName("huyen")[0].value;
    const xa = document.getElementsByName("xa")[0].value;

    // Kiểm tra từng trường và hiển thị lỗi đầu tiên gặp phải
    if (hoTen === "") {
        showToast("Họ tên không được để trống.", "info");
        return false;
    }

    // Kiểm tra định dạng email
    const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    if (email === "") {
        showToast("Email không được để trống.", "info");
        return false;
    } else if (!emailPattern.test(email)) {
        showToast("Email không hợp lệ.", "info");
        return false;
    }

    // Kiểm tra số điện thoại (chỉ gồm 10 chữ số)
    const phonePattern = /^\d{10}$/;
    if (soDienThoai === "") {
        showToast("Số điện thoại không được để trống.", "info");
        return false;
    } else if (!phonePattern.test(soDienThoai)) {
        showToast("Số điện thoại phải gồm 10 chữ số.", "info");
        return false;
    }

    // Kiểm tra địa chỉ chi tiết
    if (diaChi === "") {
        showToast("Địa chỉ chi tiết không được để trống.", "info");
        return false;
    }

    // Kiểm tra các trường Tỉnh/Thành phố, Quận/Huyện, Phường/Xã
    if (thanhPho === "") {
        showToast("Vui lòng chọn Tỉnh/Thành phố.", "info");
        return false;
    }
    if (huyen === "") {
        showToast("Vui lòng chọn Quận/Huyện.", "info");
        return false;
    }
    if (xa === "") {
        showToast("Vui lòng chọn Phường/Xã.", "info");
        return false;
    }

    // Tạo mảng hoặc đối tượng khachHang với các thông tin từ form
    userData = {
        id: id,
        hoTen: hoTen,
        email: email,
        soDienThoai: soDienThoai,
        diaChi: diaChi,
        thanhPho: thanhPho,
        huyen: huyen,
        xa: xa
    };
    return true;
}


function checkout(e){
    e.preventDefault();
    if (validateForm()){
        const formSubmit = document.getElementById("formCheckOut");
        const inputs = document.querySelectorAll('.integers');
        const integers = Array.from(inputs).map(input => parseInt(input.value, 10));
        let data = {
            gioHangs : listGioHang,
            khachHang : userData
        }
        $.ajax({
            url : "/checkout/successCheckout?payment=" + 1,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            success : function (message){
                if(message !== ""){
                    sessionStorage.setItem("message", message);
                    window.location.href = "/home/cart";
                }else {
                    formSubmit.submit();
                }
            }
        })
    }

}

function showToast(message, status) {
    const Toast = Swal.mixin({
        toast: true,
        position: "top-end",
        showConfirmButton: false,
        timer: 2500,
        timerProgressBar: true,
        didOpen: (toast) => {
            toast.onmouseenter = Swal.stopTimer;
            toast.onmouseleave = Swal.resumeTimer;
        }
    });
    Toast.fire({
        icon: status,
        title: message
    });
}
