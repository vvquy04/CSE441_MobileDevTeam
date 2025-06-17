@extends('admin.layouts.app')

@section('title', 'Quản Lý Bộ Môn')

@section('content')
<div class="container-fluid department-management-container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <button type="button" class="btn btn-light border text-dark fw-bold d-flex align-items-center me-3" data-bs-toggle="modal" data-bs-target="#addDepartmentModal">
            <i class="fas fa-plus-circle me-2"></i> Thêm mới
        </button>
        <div class="search-bar rounded-pill overflow-hidden shadow-sm ms-auto d-flex align-items-center">
            <i class="fas fa-search text-muted ms-3 me-2"></i>
            <input type="text" class="form-control border-0 ps-0 py-2" placeholder="Tìm Kiếm">
        </div>
    </div>

    @if(session('success'))
        <div class="alert alert-success alert-dismissible fade show custom-success-message" role="alert">
            {{ session('success') }}
            <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
        </div>
    @endif

    <div class="card shadow-sm department-table-card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover department-table mb-0">
                    <thead>
                        <tr>
                            <th style="width: 50px;"><input type="checkbox" class="form-check-input" id="selectAll"></th>
                            <th>Mã bộ môn</th>
                            <th>Tên bộ môn</th>
                            <th class="text-center" style="width: 120px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse ($departments as $department)
                            <tr class="shadow-sm rounded mb-2 d-table-row">
                                <td><input type="checkbox" class="form-check-input" name="selected_departments[]" value="{{ $department->DepartmentId }}"></td>
                                <td>{{ $department->DepartmentId }}</td>
                                <td>{{ $department->DepartmentName }}</td>
                                <td>
                                    <button type="button" class="btn btn-sm btn-outline-secondary border-0 me-2 edit-department-btn" title="Sửa" data-bs-toggle="modal" data-bs-target="#editDepartmentModal" data-department-id="{{ $department->DepartmentId }}" data-department-name="{{ $department->DepartmentName }}">
                                        <i class="fas fa-pencil-alt"></i>
                                    </button>
                                    <button type="button" class="btn btn-sm btn-outline-secondary border-0 delete-department-btn" title="Xóa" data-bs-toggle="modal" data-bs-target="#deleteDepartmentModal" data-department-id="{{ $department->DepartmentId }}">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="4" class="text-center py-4">Không có bộ môn nào.</td>
                            </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>

            <div class="d-flex justify-content-between align-items-center pt-3 mt-3 border-top">
                <button class="btn btn-outline-secondary pagination-btn {{ $departments->onFirstPage() ? 'disabled' : '' }}" {{ $departments->onFirstPage() ? 'disabled' : '' }} onclick="window.location='{{ $departments->previousPageUrl() }}'">Trước</button>
                <span class="text-muted mx-3">Page {{ $departments->currentPage() }} of {{ $departments->lastPage() }}</span>
                <button class="btn btn-outline-secondary pagination-btn {{ $departments->hasMorePages() ? '' : 'disabled' }}" {{ $departments->hasMorePages() ? 'disabled' : '' }} onclick="window.location='{{ $departments->nextPageUrl() }}'">Tiếp</button>
            </div>
        </div>
    </div>
</div>

<!-- Add Department Modal -->
<div class="modal fade" id="addDepartmentModal" tabindex="-1" aria-labelledby="addDepartmentModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header text-center">
                <h5 class="modal-title w-100 fw-bold text-dark" id="addDepartmentModalLabel">Thêm mới bộ môn</h5>
                <button type="button" class="btn-close position-absolute top-0 end-0 mt-3 me-3" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                @if ($errors->any())
                    <div class="alert alert-danger pb-0">
                        <ul class="mb-0">
                            @foreach ($errors->all() as $error)
                                <li>{{ $error }}</li>
                            @endforeach
                        </ul>
                    </div>
                @endif
                <form id="addDepartmentForm" action="{{ route('departments.store') }}" method="POST">
                    @csrf
                    <div class="mb-3">
                        <label for="DepartmentId" class="form-label fw-bold">Mã bộ môn <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="DepartmentId" name="DepartmentId" value="{{ old('DepartmentId') }}" required>
                    </div>
                    <div class="mb-3">
                        <label for="DepartmentName" class="form-label fw-bold">Tên bộ môn <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="DepartmentName" name="DepartmentName" value="{{ old('DepartmentName') }}" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer justify-content-end">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" form="addDepartmentForm" class="btn btn-primary">Thêm mới</button>
            </div>
        </div>
    </div>
</div>

<!-- Edit Department Modal -->
<div class="modal fade" id="editDepartmentModal" tabindex="-1" aria-labelledby="editDepartmentModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-header text-center">
                <h5 class="modal-title w-100 fw-bold text-dark" id="editDepartmentModalLabel">Chỉnh sửa thông tin bộ môn</h5>
                <button type="button" class="btn-close position-absolute top-0 end-0 mt-3 me-3" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <div class="modal-body">
                <form id="editDepartmentForm" method="POST">
                    @csrf
                    @method('PUT')
                    <div class="mb-3">
                        <label for="editDepartmentId" class="form-label fw-bold">Mã bộ môn</label>
                        <input type="text" class="form-control" id="editDepartmentId" name="DepartmentId" readonly>
                    </div>
                    <div class="mb-3">
                        <label for="editDepartmentName" class="form-label fw-bold">Tên bộ môn <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="editDepartmentName" name="DepartmentName" required>
                    </div>
                </form>
            </div>
            <div class="modal-footer justify-content-end">
                <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Hủy</button>
                <button type="submit" form="editDepartmentForm" class="btn btn-primary">Cập nhật</button>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteDepartmentModal" tabindex="-1" aria-labelledby="deleteDepartmentModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-body text-center p-4">
                <h5 class="mb-4 fw-bold text-dark">Bạn có chắc muốn xóa môn này không ?</h5>
                <form id="deleteDepartmentForm" method="POST">
                    @csrf
                    @method('DELETE')
                    <button type="button" class="btn btn-light me-3" data-bs-dismiss="modal">Hủy</button>
                    <button type="submit" class="btn btn-dark-blue">Xác Nhận</button>
                </form>
            </div>
        </div>
    </div>
</div>

<script>
    document.addEventListener('DOMContentLoaded', function () {
        var editDepartmentModal = document.getElementById('editDepartmentModal');
        editDepartmentModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget; // Button that triggered the modal
            var departmentId = button.getAttribute('data-department-id');
            var departmentName = button.getAttribute('data-department-name');

            var modalTitle = editDepartmentModal.querySelector('.modal-title');
            var modalBodyInputId = editDepartmentModal.querySelector('#editDepartmentId');
            var modalBodyInputName = editDepartmentModal.querySelector('#editDepartmentName');
            var editForm = editDepartmentModal.querySelector('#editDepartmentForm');

            modalBodyInputId.value = departmentId;
            modalBodyInputName.value = departmentName;
            editForm.action = '/admin/departments/' + departmentId; // Set form action dynamically
        });

        var deleteDepartmentModal = document.getElementById('deleteDepartmentModal');
        deleteDepartmentModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget; // Button that triggered the modal
            var departmentId = button.getAttribute('data-department-id');
            var deleteForm = deleteDepartmentModal.querySelector('#deleteDepartmentForm');
            deleteForm.action = '/admin/departments/' + departmentId; // Set form action dynamically
        });
    });
</script>
@endsection 