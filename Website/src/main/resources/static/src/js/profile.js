function loadProfile() {
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

function editProfile() {
    document.getElementById('editForm').style.display = 'block';
    document.getElementById('editBtn').style.display = 'none';
}

function saveChanges() {
    const id = document.getElementById("id").value;
    const name = document.getElementById("editName").value.trim();
    const email = document.getElementById("editEmail").value.trim();
    const phone = document.getElementById("editPhone").value.trim();
    const date = document.getElementById("editDate").value;
    const genderRdo = document.getElementById("inlineRadio1");
    const province = document.getElementById("province").value;
    const district = document.getElementById("district").value;
    const ward = document.getElementById("ward").value;
    const address = document.getElementById("editAddress").value.trim();
    let gender = 'Nữ';
    if(genderRdo.checked){
        gender = 'Nam';
    }
    if(validateForm(name, email, phone, date, province, district, ward, address)){
        const data = {
            hoTen : name,
            soDienThoai : phone,
            ngaySinh : date,
            gioiTinh : gender,
            email : email,
            xa : ward,
            huyen : district,
            thanhPho : province,
            diaChi : address
        }

        $.ajax({
            url : "/api/v1/web/editProfile/" + id,
            method: "POST",
            contentType: "application/json",
            data: JSON.stringify(data),
            success : function (data){
                document.getElementById('username').innerText = name
                document.getElementById('userEmail').textContent = email;
                document.getElementById('userPhone').textContent = phone;
                document.getElementById("userDate").textContent = date;
                document.getElementById("userGender").textContent = gender;
                document.getElementById('userAddress').textContent = data;
                cancelEdit();
                showToast("Thành công", "success");
            },
            error : function (error){
                console.log(error);
                showToast("Lỗi hệ thống", "warning");
            }
        })
    }

}

function validateForm(name, email, phone, date, province, district, ward, address) {
    if(name === ""){
        showToast("Vui lòng nhập họ tên.", "warning");
        return false;
    }
    if(date === ""){
        showToast("Vui lòng nhập ngày sinh.", "warning");
        return false;
    }

    const emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,6}$/;
    if (email === "") {
        showToast("Vui lòng nhập địa chỉ email.", "warning");
        return false;
    } else if (!emailPattern.test(email)) {
        showToast("Vui lòng nhập địa chỉ email hợp lệ.", "warning");
        return false;
    }

    const phonePattern = /^[0-9]{10}$/;
    if (phone === "") {
        showToast("Vui lòng nhập số điện thoại.", "warning");
        return false;
    } else if (!phonePattern.test(phone)) {
        showToast("Vui lòng nhập số điện thoại hợp lệ (10 chữ số).", "warning");
        return false;
    }

    if (province === "") {
        showToast("Vui lòng chọn Tỉnh/Thành phố.", "warning");
        return false;
    }

    if (district === "") {
        showToast("Vui lòng chọn Quận/Huyện.", "warning");
        return false;
    }

    if (ward === "") {
        showToast("Vui lòng chọn Xã.", "warning");
        return false;
    }

    if (address === "") {
        showToast("Vui lòng nhập Địa chỉ chi tiết.", "warning");
        return false;
    }

    return true;
}

function cancelEdit() {
    document.getElementById('editForm').style.display = 'none';
    document.getElementById('username').style.display = 'block';
    // document.getElementById('joinDate').style.display = 'block';
    document.getElementById('userEmail').style.display = 'block';
    document.getElementById('userPhone').style.display = 'block';
    document.getElementById('userAddress').style.display = 'block';
    document.getElementById('editBtn').style.display = 'inline';
}

function showChangePasswordForm() {
    document.getElementById('changePasswordForm').style.display = 'block';
    document.querySelector('.change-password-btn').style.display = 'none';
}

function changePassword() {
    const id = document.getElementById('id').value;
    const oldPassword = document.getElementById('oldPassword').value.trim();
    const newPassword = document.getElementById('newPassword').value.trim();
    const confirmPassword = document.getElementById('confirmPassword').value.trim();

    if (newPassword !== confirmPassword) {
        showToast("Mật khẩu mới và mật khẩu xác nhận không khớp!", "warning");
        return;
    }

    if (newPassword.length < 6) {
        showToast("Mật khẩu mới phải có ít nhất 6 ký tự!", "warning");
        return;
    }

    $.ajax({
        url: "/api/v1/web/resetPass/" + id + "?password=" + newPassword + "&oldPassword=" + oldPassword,
        method: "PUT",
        success: function (data) {
            if(data !== ""){
                showToast(data, 'warning');
                return;
            }
            showToast("Đổi mật khẩu thành công!", "success");
            [document.getElementById('oldPassword'), document.getElementById('newPassword'), document.getElementById('confirmPassword')]
                .forEach(item => item.value = '');
            cancelPasswordChange();
        },
        error: function (error) {
            console.log(error);
            showToast("Lỗi hệ thống", "warning");
        }
    })
}

function cancelPasswordChange() {
    document.getElementById('changePasswordForm').style.display = 'none';
    document.querySelector('.change-password-btn').style.display = 'block'; // Hiển thị lại nút Đổi mật khẩu
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