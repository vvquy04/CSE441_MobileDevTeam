<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('student_profiles', function (Blueprint $table) {
            $table->id('StudentUserId');
            $table->string('StudentCode', 20);
            $table->string('FullName', 100);
            $table->string('DepartmentId');
            $table->foreign('DepartmentId')->references('DepartmentId')->on('departments')->onDelete('cascade');
            $table->string('ClassName', 50);
            $table->integer('EnrollmentYear');
            $table->string('EmailContact', 255);
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('student_profiles');
    }
}; 