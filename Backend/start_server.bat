@echo off
echo ========================================
echo    TLU Office Hours - Laravel Server
echo ========================================
echo.

echo Checking PHP installation...
php --version
if %errorlevel% neq 0 (
    echo ERROR: PHP is not installed or not in PATH
    echo Please install PHP and add it to your PATH
    pause
    exit /b 1
)

echo.
echo Checking Composer installation...
composer --version
if %errorlevel% neq 0 (
    echo ERROR: Composer is not installed or not in PATH
    echo Please install Composer and add it to your PATH
    pause
    exit /b 1
)

echo.
echo Installing dependencies...
composer install

echo.
echo Checking database connection...
php artisan migrate:status
if %errorlevel% neq 0 (
    echo ERROR: Database connection failed
    echo Please check your .env file and database configuration
    pause
    exit /b 1
)

echo.
echo Running migrations...
php artisan migrate

echo.
echo Seeding database...
php artisan db:seed

echo.
echo Starting Laravel development server...
echo Server will be available at: http://localhost:8000
echo Press Ctrl+C to stop the server
echo.
php artisan serve

pause 