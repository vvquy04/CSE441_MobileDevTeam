<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('faculty_profiles', function (Blueprint $table) {
            $table->id('FacultyUserId');
            $table->string('DepartmentId');
            $table->foreign('DepartmentId')->references('DepartmentId')->on('departments')->onDelete('cascade');
            $table->string('OfficeLocation', 255);
            $table->text('Bio')->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('faculty_profiles');
    }
}; 