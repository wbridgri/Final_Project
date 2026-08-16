// SIS.java
// Final Project Template – Student Information System (SIS)
// One file only. No GUI, no README, no JUnit.

import java.util.*;

public class SIS {

  // ======== Domain ========
    static class Student {
        String id;
        String name;
        String major;
        ArrayList<Double> grades = new ArrayList<>();

        Student(String id, String name, String major) {
            this.id = id; this.name = name; this.major = major;
        }

        // TODO: add a grade (0..100)
        void addGrade(double g) { 
            grades.add(g);
        }

        // TODO: return average or Double.NaN if no grades
        double average() { 
            
            if(grades.isEmpty()) 
                return Double.NaN; 

            double sum = 0.0;
            
            for(Double grade: grades)
                sum += grade;

            return sum / grades.size();
            }
            
            
            

        @Override public String toString() {  
            return "ID: " + id + ", " + "Name: " + name + ", Major: " + major; 
        }
    }

  // ======== BST keyed by Student.id (recursive ops expected) ========
    static class StudentBST {
        static class Node { Student s; Node left, right; Node(Student s){ this.s = s; } }
        private Node root;

        // TODO: public boolean insert(Student s)
        public boolean insert(Student s) { /* TODO (recursive) */ return false; }

        // TODO: public Student find(String id)
        public Student find(String id) { 
            if(id == null) 
                return null;
            return null;
    
        }

        // TODO: public boolean delete(String id)
        public boolean delete(String id) { /* TODO (recursive) */ return false; }

        // TODO: public void inOrder(Consumer<Student> f)
        public void inOrder(java.util.function.Consumer<Student> f) { /* TODO (recursive) */ }
    }

    // ======== Manager / Facade ========
    static class StudentManager {
        private final StudentBST bst = new StudentBST();

        // TODO: return true if added; false if invalid/duplicate
        boolean addStudent(String id, String name, String major) { /* TODO */ return false; }

        // TODO: edit if exists; return true/false
        boolean editStudent(String id, String newName, String newMajor) { /* TODO */ return false; }

        boolean deleteStudent(String id) { /* TODO */ return false; }

        Student getStudent(String id) { /* TODO */ return null; }

        boolean addGrade(String id, double grade) { /* TODO (0..100) */ return false; }

        ArrayList<Double> getGrades(String id) {
        /* TODO: return copy or null if not found */ return null;
        }

        double getStudentAverage(String id) { /* TODO */ return Double.NaN; }

        double getClassAverage() { /* TODO (iterate students; average of student averages with >=1 grade) */ return Double.NaN; }

        Student getHighestAverageStudent() { /* TODO (ignore students with no grades) */ return null; }

        void listAllStudents() { /* OPTIONAL: in-order traversal printing */ }
    }

    // ======== Console Menu ========
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        StudentManager sm = new StudentManager();

        while (true) {
        printMenu();
        int choice = readInt(sc, "Choose: ");
        switch (choice) {
            case 1 -> addStudentUI(sc, sm);
            case 2 -> editStudentUI(sc, sm);
            case 3 -> deleteStudentUI(sc, sm);
            case 4 -> viewStudentUI(sc, sm);
            case 5 -> addGradeUI(sc, sm);
            case 6 -> viewGradesUI(sc, sm);
            case 7 -> studentAverageUI(sc, sm);
            case 8 -> highestAverageUI(sm);
            case 9 -> classAverageUI(sm);
            case 10 -> { System.out.println("Exiting..."); return; }
            default -> System.out.println("Invalid choice.");
        }
        }
    }

    private static void printMenu() {
        System.out.println("\n--- Student Information System ---");
        System.out.println("1) Add Student");
        System.out.println("2) Edit Student");
        System.out.println("3) Delete Student");
        System.out.println("4) View Student");
        System.out.println("5) Add Grade");
        System.out.println("6) View Grades");
        System.out.println("7) Student Average");
        System.out.println("8) Highest Average Student");
        System.out.println("9) Class Average");
        System.out.println("10) Exit");
    }

    // ======== Minimal UI helpers (keep simple; students implement logic above) ========
    private static int readInt(Scanner sc, String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextInt()) { sc.next(); System.out.print("Enter a number: "); }
        return sc.nextInt();
    }
    private static String readLine(Scanner sc, String prompt) {
        System.out.print(prompt);
        String s = sc.nextLine();
        if (s.isEmpty()) s = sc.nextLine(); // handle pending newline after nextInt()
        return s.trim();
    }
    private static double readDouble(Scanner sc, String prompt) {
        System.out.print(prompt);
        while (!sc.hasNextDouble()) { sc.next(); System.out.print("Enter a number: "); }
        return sc.nextDouble();
    }

    private static void addStudentUI(Scanner sc, StudentManager sm) { /* TODO */ }
    private static void editStudentUI(Scanner sc, StudentManager sm) { /* TODO */ }
    private static void deleteStudentUI(Scanner sc, StudentManager sm) { /* TODO */ }
    private static void viewStudentUI(Scanner sc, StudentManager sm) { /* TODO */ }
    private static void addGradeUI(Scanner sc, StudentManager sm) { /* TODO */ }
    private static void viewGradesUI(Scanner sc, StudentManager sm) { /* TODO */ }
    private static void studentAverageUI(Scanner sc, StudentManager sm) { /* TODO */ }
    private static void highestAverageUI(StudentManager sm) { /* TODO */ }
    private static void classAverageUI(StudentManager sm) { /* TODO */ }
    }