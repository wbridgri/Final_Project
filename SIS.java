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

    // add a grade only when it is a  value in the range
    void addGrade(double g) {
      if (Double.isFinite(g) && g >= 0 && g <= 100) {
        grades.add(g);
      }
    }

    // return the average or Double.NaN when no grades exist
    double average() {
      if (grades.isEmpty()) {
        return Double.NaN;
      }

      double total = 0.0;
      for (double grade : grades) {
        total += grade;
      }
      return total / grades.size();
    }

    @Override public String toString() {
      return "ID: " + id + ", Name: " + name + ", Major: " + major;
    }
  }

  // ======== BST keyed by Student.id (recursive ops expected) ========
  static class StudentBST {
    static class Node { Student s; Node left, right; Node(Student s){ this.s = s; } }
    private Node root;

    public boolean insert(Student s) {
      if (s == null || s.id == null || s.id.isBlank() || find(s.id) != null) {
        return false;
      }

      root = insert(root, s);
      return true;
    }

    private Node insert(Node node, Student s) {
      if (node == null) {
        return new Node(s);
      }

      if (s.id.compareTo(node.s.id) < 0) {
        node.left = insert(node.left, s);
      } else {
        node.right = insert(node.right, s);
      }
      return node;
    }

    public Student find(String id) {
      if (id == null) {
        return null;
      }
      return find(root, id);
    }

    private Student find(Node node, String id) {
      if (node == null) {
        return null;
      }

      int comparison = id.compareTo(node.s.id);
      if (comparison == 0) {
        return node.s;
      }
      if (comparison < 0) {
        return find(node.left, id);
      }
      return find(node.right, id);
    }

    public boolean delete(String id) {
      if (id == null || find(id) == null) {
        return false;
      }

      root = delete(root, id);
      return true;
    }

    private Node delete(Node node, String id) {
      if (node == null) {
        return null;
      }

      int comparison = id.compareTo(node.s.id);
      if (comparison < 0) {
        node.left = delete(node.left, id);
      } else if (comparison > 0) {
        node.right = delete(node.right, id);
      } else {
        if (node.left == null) {
          return node.right;
        }
        if (node.right == null) {
          return node.left;
        }

        Node successor = smallest(node.right);
        node.s = successor.s;
        node.right = delete(node.right, successor.s.id);
      }
      return node;
    }

    private Node smallest(Node node) {
      if (node.left == null) {
        return node;
      }
      return smallest(node.left);
    }

    public void inOrder(java.util.function.Consumer<Student> f) {
      inOrder(root, f);
    }

    private void inOrder(Node node, java.util.function.Consumer<Student> f) {
      if (node == null) {
        return;
      }

      inOrder(node.left, f);
      f.accept(node.s);
      inOrder(node.right, f);
    }
  }

  // ======== Manager / Facade ========
  static class StudentManager {
    private final StudentBST bst = new StudentBST();

    // Return true if added; false if invalid or a duplicate.
    boolean addStudent(String id, String name, String major) {
      if (isMissing(id) || isMissing(name) || isMissing(major)) {
        return false;
      }

      Student student = new Student(id.trim(), name.trim(), major.trim());
      return bst.insert(student);
    }

    // Edit a student's name and major without changing the BST key.
    boolean editStudent(String id, String newName, String newMajor) {
      if (isMissing(id) || isMissing(newName) || isMissing(newMajor)) {
        return false;
      }

      Student student = bst.find(id.trim());
      if (student == null) {
        return false;
      }

      student.name = newName.trim();
      student.major = newMajor.trim();
      return true;
    }

    boolean deleteStudent(String id) {
      return !isMissing(id) && bst.delete(id.trim());
    }

    Student getStudent(String id) {
      if (isMissing(id)) {
        return null;
      }
      return bst.find(id.trim());
    }

    boolean addGrade(String id, double grade) {
      if (!Double.isFinite(grade) || grade < 0 || grade > 100) {
        return false;
      }

      Student student = getStudent(id);
      if (student == null) {
        return false;
      }

      student.addGrade(grade);
      return true;
    }

    ArrayList<Double> getGrades(String id) {
      Student student = getStudent(id);
      if (student == null) {
        return null;
      }
      return new ArrayList<>(student.grades);
    }

    double getStudentAverage(String id) {
      Student student = getStudent(id);
      return student == null ? Double.NaN : student.average();
    }

    double getClassAverage() {
      ArrayList<Student> students = allStudents();
      double total = 0.0;
      int gradedStudentCount = 0;

      for (Student student : students) {
        double average = student.average();
        if (!Double.isNaN(average)) {
          total += average;
          gradedStudentCount++;
        }
      }

      if (gradedStudentCount == 0) {
        return Double.NaN;
      }
      return total / gradedStudentCount;
    }

    Student getHighestAverageStudent() {
      Student highest = null;
      double highestAverage = Double.NEGATIVE_INFINITY;

      for (Student student : allStudents()) {
        double average = student.average();
        if (!Double.isNaN(average) && average > highestAverage) {
          highest = student;
          highestAverage = average;
        }
      }
      return highest;
    }

    void listAllStudents() {
      bst.inOrder(System.out::println);
    }

    private ArrayList<Student> allStudents() {
      ArrayList<Student> students = new ArrayList<>();
      bst.inOrder(students::add);
      return students;
    }

    private boolean isMissing(String value) {
      return value == null || value.isBlank();
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
      System.out.println("Unable to add student: fields must be non-empty and the ID must be unique.");
    }
  }

  private static void editStudentUI(Scanner sc, StudentManager sm) {
    String id = readLine(sc, "Student ID: ");
    String newName = readLine(sc, "New name: ");
    String newMajor = readLine(sc, "New major: ");

    if (sm.getStudent(id) == null) {
      System.out.println("Student not found.");
    } else if (sm.editStudent(id, newName, newMajor)) {
      System.out.println("Student updated.");
    } else {
      System.out.println("Name and major must be non-empty.");
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
    Student student = sm.getStudent(id);
    if (student == null) {
      System.out.println("Student not found.");
    } else {
      System.out.println(student);
    }
  }

  private static void addGradeUI(Scanner sc, StudentManager sm) {
    String id = readLine(sc, "Student ID: ");
    double grade = readDouble(sc, "Grade (0-100): ");

    if (sm.getStudent(id) == null) {
      System.out.println("Student not found.");
    } else if (sm.addGrade(id, grade)) {
      System.out.println("Grade added.");
    } else {
      System.out.println("Grade must be between 0 and 100.");
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
    if (sm.getStudent(id) == null) {
      System.out.println("Student not found.");
      return;
    }

    double average = sm.getStudentAverage(id);
    if (Double.isNaN(average)) {
      System.out.println("No grades yet.");
    } else {
      System.out.printf("Student average: %.2f%n", average);
    }
  }

  private static void highestAverageUI(StudentManager sm) {
    Student student = sm.getHighestAverageStudent();
    if (student == null) {
      System.out.println("No students with grades yet.");
    } else {
      System.out.printf("Highest average: %s, Average: %.2f%n", student, student.average());
    }
  }

  private static void classAverageUI(StudentManager sm) {
    double average = sm.getClassAverage();
    if (Double.isNaN(average)) {
      System.out.println("No grades recorded yet.");
    } else {
      System.out.printf("Class average: %.2f%n", average);
    }
  }
}
