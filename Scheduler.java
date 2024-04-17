import java.io.*;
import java.util.Scanner;

class PCB {
    String processID;
    int priority;
    int arrivalTime;
    int burstTime;
    int startTime;
    int terminationTime;
    int turnaroundTime;
    int waitingTime;
    int responseTime;

    public PCB(String processID, int priority, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.startTime = -1;
        this.terminationTime = -1;
        this.turnaroundTime = -1;
        this.waitingTime = -1;
        this.responseTime = -1;
    }
}

public class Scheduler {
    PCB[] q1;
    PCB[] q2;
    private static int timeQuantum = 3; // Time quantum for Round-Robin

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Scheduler scheduler = new Scheduler();

        System.out.println("Welcome to the Scheduler!");
        int choice;
        do {
            choice = MainMenu(scanner);
            switch (choice) {
                case 1:
                    // Add process information
                    scheduler.addProcess(scanner);
                    break;

                case 2:
                    // Report detailed information about each process and different scheduling criteria
                    scheduler.runScheduler();
                    break;

                case 3:
                //Exit Program
                    System.out.println("Goodbye! Program Exited");
                    break;

                default:
                    System.out.println("Invalid Choice, Please Try Again.");
            }
            System.out.println("\n\n");
        } while (choice != 3);
    }

    public static int MainMenu(Scanner scanner) {
        System.out.println("Please choose an option:");
        System.out.println("1. Enter process' information.");
        System.out.println("2. Report detailed information about each process and different scheduling criteria.");
        System.out.println("3. Exit the program");
        System.out.print("\nEnter your choice: ");
        int choice = scanner.nextInt();

        return choice;
    }

    private void addProcess(Scanner scanner) {
        // User to enter the number of processes
        System.out.print("Enter the number of processes: ");
        int numOfProcesses = scanner.nextInt();

        q1 = new PCB[numOfProcesses];
        q2 = new PCB[numOfProcesses];

        // Input process details
        for (int i = 0; i < numOfProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");
            System.out.print("Priority (1 for high priority, 2 for low priority): ");
            int priority = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            // Assign process to appropriate queue based on priority
            if (priority == 1) {
                q1[i] = new PCB("P" + (i + 1), priority, arrivalTime, burstTime);
            } else if (priority == 2) {
                q2[i] = new PCB("P" + (i + 1), priority, arrivalTime, burstTime);
            }
        }
    }

    private void runScheduler() {
        // running the scheduler
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("C:\\Users\\wejoud\\Desktop\\os\\process_report.txt"));
            StringBuilder schedulingOrder = new StringBuilder("Scheduling Order: [");

            // Append process IDs from q1
            for (int i = 0; i < q1.length; i++) {
                if (q1[i] != null) {
                    String processID = q1[i].processID;
                    schedulingOrder.append(processID).append(" | ");
                }
            }

            // Append process IDs from q2
            for (int i = 0; i < q2.length; i++) {
                if (q2[i] != null) {
                    String processID = q2[i].processID;
                    schedulingOrder.append(processID);
                    if (i < q2.length - 1 || q1.length > 0) {
                        schedulingOrder.append(" | ");
                    }
                }
            }
            schedulingOrder.append("]");
            writer.println(schedulingOrder);

            // Generate process report for q1
            for (int i = 0; i < q1.length; i++) {
                if (q1[i] != null) {
                    PCB process = q1[i];
                    process.startTime = process.arrivalTime;
                    process.terminationTime = Math.min(process.arrivalTime + process.burstTime, timeQuantum);
                    process.turnaroundTime = process.terminationTime - process.arrivalTime;
                    process.waitingTime = process.turnaroundTime - process.burstTime;
                    process.responseTime = process.startTime - process.arrivalTime;

                    // Write process details to the file
                    writeProcessDetails(writer, process);
                }
            }

            // Generate process report for q2
            for (int i = 0; i < q2.length; i++) {
                if (q2[i] != null) {
                    PCB process = q2[i];
                    process.startTime = process.arrivalTime;
                    process.terminationTime = process.arrivalTime + process.burstTime;
                    process.turnaroundTime = process.terminationTime - process.arrivalTime;
                    process.waitingTime = 0; // SJF is non-preemptive, waiting time is always 0
                    process.responseTime = process.startTime - process.arrivalTime;

                    // Write process details to the file
                    writeProcessDetails(writer, process);
                }
            }

            // Calculate averages
            double avgTurnaroundTime = calculateAverageTurnaroundTime();
            double avgWaitingTime = calculateAverageWaitingTime(q1);
            double avgResponseTime = calculateAverageResponseTime(q1);

            // Write averages
            writer.println("Average Turnaround Time: " + avgTurnaroundTime);
            writer.println("Average Waiting Time: " + avgWaitingTime);
            writer.println("Average Response Time: " + avgResponseTime);

            writer.close(); // Close the file writer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


private void writeProcessDetails(PrintWriter writer, PCB process) {
        writer.println("Process ID: " + process.processID);
        writer.println("Priority: " + process.priority);
        writer.println("Arrival Time: " + process.arrivalTime);
        writer.println("CPU Burst: " + process.burstTime);
        writer.println("Start Time: " + process.startTime);
        writer.println("Termination Time: " + process.terminationTime);
        writer.println("Turnaround Time: " + process.turnaroundTime);
        writer.println("Waiting Time: " + process.waitingTime);
        writer.println("Response Time: " + process.responseTime);
        writer.println();
    }

    private double calculateAverageTurnaroundTime() {
        double sum = 0;
        for (PCB process : q1) {
            if (process != null) {
                sum += process.turnaroundTime;
            }
        }
        for (PCB process : q2) {
            if (process != null) {
                sum += process.turnaroundTime;
            }
        }
        return sum / (q1.length + q2.length);
    }

    private double calculateAverageWaitingTime(PCB[] q1) {
        double sum = 0;
        for (PCB process : q1) {
            if (process != null) {
                sum += process.waitingTime;
            }
        }
        return sum / q1.length;
    }

    private double calculateAverageResponseTime(PCB[] q1) {
        double sum = 0;
        for (PCB process : q1) {
            if (process != null) {
                sum += process.responseTime;
            }
        }
        return sum / q1.length;
    }

    }

    
