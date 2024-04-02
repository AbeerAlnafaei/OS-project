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
        
        //when created a process the remain attributes will be -1 because still not schedule in CPU
        this.startTime = -1; 
        this.terminationTime = -1; 
        this.turnaroundTime = -1; 
        this.waitingTime = -1; 
        this.responseTime = -1; 
    }
}

public class Scheduler {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Prompt user to enter the number of processes
        System.out.print("Enter the number of processes: ");
        int numOfProcesses = scanner.nextInt();
        
        // Call createProcess method to handle process creation
        createProcess(numOfProcesses);
    }
    
    private static void createProcess(int numOfProcesses) {
        Scanner scanner = new Scanner(System.in); // Create a new scanner object
        
        // Create arrays to represent Q1 and Q2
        PCB[] q1 = new PCB[numOfProcesses];
        PCB[] q2 = new PCB[numOfProcesses];

        // Input process details
        for (int i = 0; i < numOfProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");
            System.out.print("Priority (1 for high priority, 2 for low priority): ");
            int priority = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            // Create PCB objects for each process
            PCB process = new PCB("P" + (i + 1), priority, arrivalTime, burstTime);


            // Assign process to appropriate queue based on priority
            if (priority == 1) {
                q1[i] = process;
            } else if (priority == 2) {
                q2[i] = process;
            }
        }
    }
}
