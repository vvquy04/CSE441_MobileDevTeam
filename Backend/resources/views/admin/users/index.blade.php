@extends('admin.layouts.app')

@section('title', 'Quản Lý Tài Khoản')

@section('content')
<div class="container-fluid user-management-container">
    <div class="d-flex justify-content-between align-items-center mb-4">
        <button type="button" class="btn btn-light border text-dark fw-bold d-flex align-items-center me-3">
            <i class="fas fa-filter me-2"></i> Lọc
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

    <div class="card shadow-sm user-table-card">
        <div class="card-body">
            <div class="table-responsive">
                <table class="table table-hover user-table mb-0">
                    <thead>
                        <tr>
                            <th style="width: 50px;"><input type="checkbox" class="form-check-input" id="selectAll"></th>
                            <th>Mã người dùng</th>
                            <th>Email</th>
                            <th>Vai trò</th>
                            <th class="text-center" style="width: 120px;">Thao tác</th>
                        </tr>
                    </thead>
                    <tbody>
                        @forelse ($users as $user)
                            <tr class="shadow-sm rounded mb-2 d-table-row">
                                <td><input type="checkbox" class="form-check-input" name="selected_users[]" value="{{ $user->UserId }}"></td>
                                <td>{{ $user->UserId }}</td>
                                <td>{{ $user->email }}</td>
                                <td>
                                    @foreach ($user->roles as $role)
                                        <span class="badge bg-secondary">{{ $role->RoleName }}</span>
                                    @endforeach
                                </td>
                                <td class="text-center">
                                    <button type="button" class="btn btn-sm btn-outline-secondary border-0 delete-user-btn" title="Xóa" data-bs-toggle="modal" data-bs-target="#deleteUserModal" data-user-id="{{ $user->UserId }}">
                                        <i class="fas fa-trash-alt"></i>
                                    </button>
                                </td>
                            </tr>
                        @empty
                            <tr>
                                <td colspan="5" class="text-center py-4">Không có tài khoản nào.</td>
                            </tr>
                        @endforelse
                    </tbody>
                </table>
            </div>

            <div class="d-flex justify-content-between align-items-center pt-3 mt-3 border-top">
                <button class="btn btn-outline-secondary pagination-btn {{ $users->onFirstPage() ? 'disabled' : '' }}" {{ $users->onFirstPage() ? 'disabled' : '' }} onclick="window.location='{{ $users->previousPageUrl() }}'">Trước</button>
                <span class="text-muted mx-3">Page {{ $users->currentPage() }} of {{ $users->lastPage() }}</span>
                <button class="btn btn-outline-secondary pagination-btn {{ $users->hasMorePages() ? '' : 'disabled' }}" {{ $users->hasMorePages() ? 'disabled' : '' }} onclick="window.location='{{ $users->nextPageUrl() }}'">Tiếp</button>
            </div>
        </div>
    </div>
</div>

<!-- Delete Confirmation Modal -->
<div class="modal fade" id="deleteUserModal" tabindex="-1" aria-labelledby="deleteUserModalLabel" aria-hidden="true">
    <div class="modal-dialog modal-dialog-centered">
        <div class="modal-content">
            <div class="modal-body text-center p-4">
                <h5 class="mb-4 fw-bold text-dark">Bạn có chắc muốn xóa tài khoản này không ?</h5>
                <form id="deleteUserForm" method="POST">
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
        var deleteUserModal = document.getElementById('deleteUserModal');
        deleteUserModal.addEventListener('show.bs.modal', function (event) {
            var button = event.relatedTarget; // Button that triggered the modal
            var userId = button.getAttribute('data-user-id');
            var deleteForm = deleteUserModal.querySelector('#deleteUserForm');
            deleteForm.action = '/admin/users/' + userId; // Set form action dynamically
        });
    });
</script>
@endsection 