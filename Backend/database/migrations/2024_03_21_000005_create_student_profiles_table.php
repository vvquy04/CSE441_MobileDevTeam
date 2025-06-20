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
            $table->string('StudentName', 255);
            $table->string('StudentCode', 50)->unique();
            $table->string('ClassName', 50)->nullable();
            $table->string('PhoneNumber', 20)->nullable();
            $table->string('avatar')->nullable();
            $table->timestamps();
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('student_profiles');
    }
}; 