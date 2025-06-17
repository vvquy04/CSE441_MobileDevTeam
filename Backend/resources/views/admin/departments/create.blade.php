@extends('admin.layouts.app')

@section('title', 'Thêm Bộ Môn Mới')

@section('content')
<div class="department-form-container">
    <h1 class="dashboard-title">Thêm Bộ Môn Mới</h1>

    @if ($errors->any())
        <div class="alert alert-danger">
            <ul>
                @foreach ($errors->all() as $error)
                    <li>{{ $error }}</li>
                @endforeach
            </ul>
        </div>
    @endif

    <div class="card form-card">
        <div class="card-body">
            <form action="{{ route('departments.store') }}" method="POST">
                @csrf
                <div class="mb-3">
                    <label for="DepartmentName" class="form-label">Tên Bộ Môn</label>
                    <input type="text" class="form-control" id="DepartmentName" name="DepartmentName" value="{{ old('DepartmentName') }}" required>
                </div>
                <button type="submit" class="btn btn-success">Thêm Bộ Môn</button>
                <a href="{{ route('departments.index') }}" class="btn btn-secondary">Hủy</a>
            </form>
        </div>
    </div>
</div>
@endsection 