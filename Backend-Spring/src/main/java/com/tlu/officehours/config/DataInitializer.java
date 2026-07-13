package com.tlu.officehours.config;

import com.tlu.officehours.entity.*;
import com.tlu.officehours.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private DepartmentRepository departmentRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FacultyProfileRepository facultyProfileRepository;

    @Autowired
    private StudentProfileRepository studentProfileRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        // 1. Initialize Roles
        Role adminRole = initRole("admin");
        Role facultyRole = initRole("faculty");
        Role studentRole = initRole("student");

        // 2. Initialize Departments
        initDepartment("CNPM", "Công nghệ phần mềm");
        initDepartment("HTTT", "Hệ thống thông tin");
        initDepartment("ANM", "Mạng và an toàn bảo mật");
        initDepartment("TTNT", "Trí tuệ nhân tạo");

        // 3. Initialize Admin User
        if (!userRepository.existsByEmail("admin@tlu.edu.vn")) {
            User admin = new User();
            admin.setEmail("admin@tlu.edu.vn");
            admin.setPassword(passwordEncoder.encode("password123"));
            admin.setEmailVerifiedAt(LocalDateTime.now());
            admin.getRoles().add(adminRole);
            userRepository.save(admin);
            System.out.println("Seeded admin account (admin@tlu.edu.vn / password123)");
        }

        // 4. Initialize Faculty Users
        initFaculty("thay.nguyen@tlu.edu.vn", "123456", "ThS. Nguyễn Văn A", "CNPM", "Thạc sĩ", "0987654321", "Phòng 101", facultyRole);
        initFaculty("thay.tran@tlu.edu.vn", "123456", "TS. Trần Thị B", "HTTT", "Tiến sĩ", "0987654322", "Phòng 102", facultyRole);
        initFaculty("thay.le@tlu.edu.vn", "123456", "ThS. Lê Văn C", "ANM", "Thạc sĩ", "0987654323", "Phòng 103", facultyRole);
        initFaculty("thay.pham@tlu.edu.vn", "123456", "TS. Phạm Thị D", "TTNT", "Tiến sĩ", "0987654324", "Phòng 104", facultyRole);

        // 5. Initialize Student User
        initStudent("sinhvien@e.tlu.edu.vn", "123456", "Nguyễn Văn Sinh Viên", "SV2251170001", "63CNPM1", "0123456789", studentRole);
    }

    private Role initRole(String roleName) {
        return roleRepository.findByRoleName(roleName).orElseGet(() -> {
            Role role = new Role();
            role.setRoleName(roleName);
            Role saved = roleRepository.save(role);
            System.out.println("Seeded role: " + roleName);
            return saved;
        });
    }

    private void initDepartment(String id, String name) {
        if (!departmentRepository.existsById(id)) {
            Department dept = new Department();
            dept.setDepartmentId(id);
            dept.setDepartmentName(name);
            departmentRepository.save(dept);
            System.out.println("Seeded department: " + name);
        }
    }

    private void initFaculty(String email, String rawPassword, String facultyName, String deptId,
                             String degree, String phone, String office, Role facultyRole) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEmailVerifiedAt(LocalDateTime.now());
            user.getRoles().add(facultyRole);
            User savedUser = userRepository.save(user);

            FacultyProfile profile = new FacultyProfile();
            profile.setFacultyUserId(savedUser.getUserId());
            profile.setFacultyName(facultyName);
            profile.setDepartmentId(deptId);
            profile.setDegree(degree);
            profile.setPhoneNumber(phone);
            profile.setOfficeLocation(office);
            facultyProfileRepository.save(profile);
            System.out.println("Seeded faculty account: " + email + " / " + rawPassword);
        }
    }

    private void initStudent(String email, String rawPassword, String studentName, String studentCode,
                             String className, String phone, Role studentRole) {
        if (!userRepository.existsByEmail(email)) {
            User user = new User();
            user.setEmail(email);
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setEmailVerifiedAt(LocalDateTime.now());
            user.getRoles().add(studentRole);
            User savedUser = userRepository.save(user);

            StudentProfile profile = new StudentProfile();
            profile.setStudentUserId(savedUser.getUserId());
            profile.setStudentName(studentName);
            profile.setStudentCode(studentCode);
            profile.setClassName(className);
            profile.setPhoneNumber(phone);
            studentProfileRepository.save(profile);
            System.out.println("Seeded student account: " + email + " / " + rawPassword);
        }
    }
}
