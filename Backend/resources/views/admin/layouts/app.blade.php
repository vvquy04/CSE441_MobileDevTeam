<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>@yield('title', 'TLU Office Hours - Admin')</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <link href="{{ asset('css/admin_layout.css') }}" rel="stylesheet">
</head>
<body>
    <div class="d-flex flex-row flex-grow-1 min-vh-100">
        <!-- Sidebar -->
        <aside class="main-sidebar d-flex flex-column bg-light shadow-sm">
            <ul class="nav flex-column sidebar-menu flex-grow-1 pt-3 pb-3">
                <li class="nav-item"><a class="nav-link {{ Request::routeIs('admin.dashboard') ? 'active' : '' }}" href="{{ route('admin.dashboard') }}"><i class="fas fa-home me-2"></i> Trang chủ</a></li>
                <li class="nav-item"><a class="nav-link {{ Request::routeIs('users.index') ? 'active' : '' }}" href="{{ route('users.index') }}"><i class="fas fa-users me-2"></i> Quản Lý Tài Khoản</a></li>
                <li class="nav-item"><a class="nav-link {{ Request::routeIs('departments.index') ? 'active' : '' }}" href="{{ route('departments.index') }}"><i class="fas fa-building me-2"></i> Quản Lý Bộ Môn</a></li>
            </ul>
            <div class="sidebar-footer border-top p-3 text-center">
                <form action="{{ route('admin.logout') }}" method="POST">
                    @csrf
                    <button type="submit" class="btn btn-link text-decoration-none w-100"><i class="fas fa-sign-out-alt me-2"></i> Đăng Xuất</button>
                </form>
            </div>
        </aside>

        <!-- Content Wrapper -->
        <div class="content-wrapper flex-grow-1">
            <!-- Header -->
            <header class="main-header navbar navbar-expand-lg navbar-light bg-white shadow-sm px-4 py-2 fixed-top">
                <div class="container-fluid justify-content-between align-items-center">
                    <a class="navbar-brand d-flex align-items-center" href="#">
                        <img src="{{ asset('img/logosp.png') }}" alt="TLU Logo" class="header-logo me-2">
                    </a>
                    <div class="d-flex align-items-center">
                        <img src="{{ asset('img/user.jpg') }}" alt="User Avatar" class="user-avatar">
                    </div>
                </div>
            </header>

            <main class="container-fluid py-4">
                @if(session('success'))
                    <div class="alert alert-success alert-dismissible fade show custom-success-message" role="alert">
                        {{ session('success') }}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                @endif

                @if(session('error'))
                    <div class="alert alert-danger alert-dismissible fade show" role="alert">
                        {{ session('error') }}
                        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
                    </div>
                @endif

                @yield('content')
            </main>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html> 