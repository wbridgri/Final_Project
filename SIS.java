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

        // add a grade (0..100)
        void addGrade(double g) {
            grades.add(g);
        }

        // return average or Double.NaN if no grades
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

        public boolean insert(Student s) {
            if (s == null || s.id == null) return false;
            if (find(s.id) != null) return false; // no duplicate ids
            root = insertNode(root, s);
            return true;
        }

        private Node insertNode(Node node, Student s) {
            if (node == null) return new Node(s); // empty spot found: attach new leaf here
            if (s.id.compareTo(node.s.id) < 0) {
                node.left = insertNode(node.left, s);
            } else {
                node.right = insertNode(node.right, s);
            }
            return node;
        }

        public Student find(String id) {
            return find(root, id);

        }
        private Student find(Node node, String id) {
            if(node == null) {
                return null;
            }else if(node.s.id.equals(id)) {
                return node.s;
            }else {
                if(node.s.id.compareTo(id) > 0)
                    return find(node.left, id);
                else
                    return find(node.right, id);
            }

        }

        public boolean delete(String id) {
            if (id == null || find(id) == null) return false; // nothing to delete
            root = deleteNode(root, id);
            return true;
        }

        private Node deleteNode(Node node, String id) {
            if (node == null) return null;

            int cmp = id.compareTo(node.s.id);
            if (cmp < 0) {
                node.left = deleteNode(node.left, id);
            } else if (cmp > 0) {
                node.right = deleteNode(node.right, id);
            } else {
                // this is the node to remove
                if (node.left == null) return node.right;   // 0 or 1 child (right)
                if (node.right == null) return node.left;   // 1 child (left)

                // 2 children: replace this node's data with the in-order successor
                // (the leftmost node of the right subtree), then delete that successor
                // from the right subtree instead.
                Node successor = node.right;
                while (successor.left != null) successor = successor.left;
                node.s = successor.s;
                node.right = deleteNode(node.right, successor.s.id);
            }
            return node;
        }

        public void inOrder(java.util.function.Consumer<Student> f) {
            inOrderHelper(root, f);
        }

        private void inOrderHelper(Node node, java.util.function.Consumer<Student> f) {
            if (node == null) return;
            inOrderHelper(node.left, f);
            f.accept(node.s);
            inOrderHelper(node.right, f);
        }
    }

    // ======== Manager / Facade ========
    static class StudentManager {
        private final StudentBST bst = new StudentBST();

        // return true if added; false if invalid/duplicate
        boolean addStudent(String id, String name, String major) {
            if (id == null || id.trim().isEmpty()) return false;
            if (name == null || name.trim().isEmpty()) return false;
            if (major == null || major.trim().isEmpty()) return false;
            return bst.insert(new Student(id.trim(), name.trim(), major.trim()));
        }

        // edit if exists; return true/false
        boolean editStudent(String id, String newName, String newMajor) {
            if (newName == null || newName.trim().isEmpty()) return false;
            if (newMajor == null || newMajor.trim().isEmpty()) return false;
            Student s = bst.find(id);
            if (s == null) return false;
            s.name = newName.trim();
            s.major = newMajor.trim();
            return true;
        }

        boolean deleteStudent(String id) {
            return bst.delete(id);
        }

        Student getStudent(String id) {
            return bst.find(id);
        }

        boolean addGrade(String id, double grade) {
            if (grade < 0 || grade > 100) return false;
            Student s = bst.find(id);
            if (s == null) return false;
            s.addGrade(grade);
            return true;
        }

        ArrayList<Double> getGrades(String id) {
            Student s = bst.find(id);
            if (s == null) return null;
            return new ArrayList<>(s.grades); // copy so callers can't mutate internal state
        }

        double getStudentAverage(String id) {
            Student s = bst.find(id);
            if (s == null) return Double.NaN;
            return s.average();
        }

        double getClassAverage() {
            // collect all students via the BST's in-order traversal, then
            // work through them with a plain loop (ordinary ArrayList usage)
            ArrayList<Student> all = new ArrayList<>();
            bst.inOrder(s -> all.add(s));

            double sum = 0.0;
            int count = 0;
            for (Student s : all) {
                if (!s.grades.isEmpty()) {
                    sum += s.average();
                    count++;
                }
            }
            return count == 0 ? Double.NaN : sum / count;
        }

        Student getHighestAverageStudent() {
            ArrayList<Student> all = new ArrayList<>();
            bst.inOrder(s -> all.add(s));

            Student best = null;
            double bestAvg = -1.0;
            for (Student s : all) {
                if (s.grades.isEmpty()) continue; // skip students with no grades yet
                double avg = s.average(); // compute once, reuse below
                if (avg > bestAvg) {
                    best = s;
                    bestAvg = avg;
                }
            }
            return best;
        }

        void listAllStudents() {
            bst.inOrder(s -> System.out.println(s));
        }
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

    private static void addStudentUI(Scanner sc, StudentManager sm) {
        String id = readLine(sc, "Student ID: ");
        String name = readLine(sc, "Name: ");
        String major = readLine(sc, "Major: ");
        if (sm.addStudent(id, name, major)) {
            System.out.println("Student added.");
        } else {
            System.out.println("Failed to add student (duplicate ID or invalid input).");
        }
    }

    private static void editStudentUI(Scanner sc, StudentManager sm) {
        String id = readLine(sc, "Student ID: ");
        String name = readLine(sc, "New Name: ");
        String major = readLine(sc, "New Major: ");
        if (sm.editStudent(id, name, major)) {
            System.out.println("Student updated.");
        } else {
            System.out.println("Student not found or invalid input.");
        }
    }

    private static void deleteStudentUI(Scanner sc, StudentManager sm) {
        String id = readLine(sc, "Student ID: ");
        if (sm.deleteStudent(id)) {
            System.out.println("Student deleted.");
        } else {
            System.out.println("Student not found.");
        }
    }

    private static void viewStudentUI(Scanner sc, StudentManager sm) {
        String id = readLine(sc, "Student ID: ");
        Student s = sm.getStudent(id);
        if (s == null) {
            System.out.println("Student not found.");
        } else {
            System.out.println(s);
        }
    }

    private static void addGradeUI(Scanner sc, StudentManager sm) {
        String id = readLine(sc, "Student ID: ");
        double grade = readDouble(sc, "Grade (0-100): ");
        if (sm.addGrade(id, grade)) {
            System.out.println("Grade added.");
        } else {
            System.out.println("Failed to add grade (student not found or grade out of range).");
        }
    }

    private static void viewGradesUI(Scanner sc, StudentManager sm) {
        String id = readLine(sc, "Student ID: ");
        ArrayList<Double> grades = sm.getGrades(id);
        if (grades == null) {
            System.out.println("Student not found.");
        } else if (grades.isEmpty()) {
            System.out.println("No grades yet.");
        } else {
            System.out.println("Grades: " + grades);
        }
    }

    private static void studentAverageUI(Scanner sc, StudentManager sm) {
        String id = readLine(sc, "Student ID: ");
        Student s = sm.getStudent(id);
        if (s == null) {
            System.out.println("Student not found.");
            return;
        }
        double avg = sm.getStudentAverage(id);
        if (Double.isNaN(avg)) {
            System.out.println("No grades yet.");
        } else {
            System.out.printf("Average: %.2f%n", avg);
        }
    }

    private static void highestAverageUI(StudentManager sm) {
        Student s = sm.getHighestAverageStudent();
        if (s == null) {
            System.out.println("No students with grades yet.");
        } else {
            System.out.printf("%s, Average: %.2f%n", s, s.average());
        }
    }

    private static void classAverageUI(StudentManager sm) {
        double avg = sm.getClassAverage();
        if (Double.isNaN(avg)) {
            System.out.println("No grades recorded yet.");
        } else {
            System.out.printf("Class Average: %.2f%n", avg);
        }
    }
    }
