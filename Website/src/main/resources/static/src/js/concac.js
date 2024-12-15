    function loadingg() {
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
}

    function checkValidate(e) {
    e.preventDefault();

    const form = document.getElementById('myForm');

    const formData = new FormData(form);
    let dataCheck = {};

    formData.forEach((value, key) => {
    dataCheck[key] = value.trim(); // Trim để loại bỏ khoảng trắng
});

    if (dataCheck.hoTen === "") {
    showToast("Không được bỏ trống tên", "error")
    return;
}

    if (dataCheck.soDienThoai === "" || dataCheck.soDienThoai.length !== 10 || !/^\d{10}$/.test(dataCheck.soDienThoai)) {
    console.log("Số điện thoại phải là 10 chữ số")
    return;
}

    if (dataCheck.ngaySinh === "") {
    console.log("Không được bỏ trống ngày sinh")
    return;
}

    const birthYear = new Date(dataCheck.ngaySinh).getFullYear();
    const currentYear = new Date().getFullYear();
    if (birthYear > currentYear - 16) {
    console.log("Người dùng phải trên 16 tuổi")
    return;
}

    if (dataCheck.email === "") {
    // error.textContent = "Không được bỏ trống email";
    return;
}

    if (!isValidEmail(dataCheck.email)) {
    // error.textContent = "Email không hợp lệ";
    return;
}
    if (dataCheck.thanhPho === "") {
    // let error;
    // error.textContent = "Hãy chọn thành phố";
    return;
}

    if (dataCheck.huyen === "") {
    // error.textContent = "Hãy chọn huyện";
    return;
}

    if (dataCheck.xa === "") {
    // error.textContent = "Hãy chọn xã";
    return;
}

    if (dataCheck.diaChi === "") {
    console.log("Không được bỏ trống địa chỉ")
    return;
}
    const id = document.getElementById("id").value;
    $.ajax({
    url: "/api/khachHang/data?id=" + id,
    method: "GET",
    success: function (data) {
    const isPhoneDuplicate = data.some(item => item.soDienThoai === dataCheck.soDienThoai);
    const isEmailDuplicate = data.some(item => item.email === dataCheck.email);

    if (isPhoneDuplicate) {
    // error.textContent = "Số điện thoại đã được sử dụng";
    return;
}

    if (isEmailDuplicate) {
    // error.textContent = "Email đã được sử dụng";
    return;
}
    Swal.fire({
    title: 'Xác nhận thêm chất liệu',
    html: 'Bạn có chắc chắn muốn tạo chất liệu không?',
    icon: 'warning',
    showCancelButton: true,       // Hiển thị nút "Hủy"
    confirmButtonText: 'Xác nhận', // Nút xác nhận đăng xuất
    cancelButtonText: 'Hủy'        // Nút hủy
}).then((result) => {
    if (result.isConfirmed) {
    form.submit();
}
});
},
    error: function (error) {
    console.log(error);
}
});
}

    function isValidEmail(email) {
    const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
    return emailRegex.test(email);
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
