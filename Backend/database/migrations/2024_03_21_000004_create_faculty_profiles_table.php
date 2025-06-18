<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    public function up(): void
    {
        Schema::create('faculty_profiles', function (Blueprint $table) {
            $table->id('faculty_user_id');
            $table->string('faculty_name');
            $table->string('department_id');
            $table->foreign('department_id')->references('DepartmentId')->on('departments')->onDelete('cascade');
            $table->string('degree')->nullable();
            $table->string('phone_number')->nullable();
            $table->string('office_location', 255);
            $table->text('bio')->nullable();
            $table->string('avatar')->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('faculty_profiles');
    }
}; 