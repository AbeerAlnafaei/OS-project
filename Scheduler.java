/*import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
                    scheduler.runScheduler2();
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
        int trackQ1 =0;
        int trackQ2 =0;
        // Input process details
        for (int i = 0; i < numOfProcesses; i++) {
            
            System.out.println("Enter details for Process " + (i + 1) + ":");
            System.out.print("Priority (1 for high priority, 2 for low priority): ");
            int priority = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();

            // Q5
        if (priority == 1) {
            q1[trackQ1++] = new PCB("P" + (i + 1), priority, arrivalTime, burstTime);
        } else if (priority == 2) {
            q2[trackQ2++] = new PCB("P" + (i + 1), priority, arrivalTime, burstTime);
        } else {
            System.out.println("Invalid priority. Process not added.");
            i--;
        }
        
        }
        
        sortByArrivalTime(q1);
        sortByArrivalTime(q2);
        
    }

    public static PCB[] sortByArrivalTime(PCB[] array) {
        if (array == null || array.length == 0) {
            return array; // Return if array is null or empty
        }
        
        for (int i = 0; i < array.length - 1; i++) {
            for (int j = 0; j < array.length - i - 1; j++) {
                // Check for null elements
                if (array[j] == null || array[j + 1] == null) {
                    continue; // Skip if any element is null
                }
                // Compare arrival times
                if (array[j].arrivalTime > array[j + 1].arrivalTime) {
                    // Swap elements
                    PCB temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                }
            }
        }
        return array;
    }
    
    

    
   /*  private void runScheduler() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("Report.txt"));
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
            System.out.println(schedulingOrder); // Print scheduling order to console
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
    
                    // Print process details to console
                    printProcessDetails(process);
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
    
                    // Print process details to console
                    printProcessDetails(process);
                    // Write process details to the file
                    writeProcessDetails(writer, process);
                }
            }
    
            // Calculate averages
            double avgTurnaroundTime = calculateAverageTurnaroundTime();
            double avgWaitingTime = calculateAverageWaitingTime(q1);
            double avgResponseTime = calculateAverageResponseTime(q1);
    
            // Write averages to the file
            writer.println("Average Turnaround Time: " + avgTurnaroundTime);
            writer.println("Average Waiting Time: " + avgWaitingTime);
            writer.println("Average Response Time: " + avgResponseTime);
    
            writer.close(); // Close the file writer
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    int StartRun = 0 ;
    private void runScheduler2() {
        try {
            PrintWriter writer = new PrintWriter(new FileWriter("Report.txt"));
            StringBuilder schedulingOrder = new StringBuilder("Scheduling Order: [");
            String processInfo="" ;


        
            if(q2[0] != null && q1[0] != null && q2[0].arrivalTime < q1[0].arrivalTime){
                schedulingOrder.append(executeQ2(processInfo , writer ));
            }else{
                
            

                schedulingOrder.append(executeQ1(processInfo , writer ));
                schedulingOrder.append(executeQ2(processInfo , writer ));}
               

            
            schedulingOrder.append("]");
            System.out.println(schedulingOrder); // Print scheduling order to console
            writer.println(schedulingOrder);
    
 
            System.out.println(processInfo);
            writer.close(); // Close the file writer
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private StringBuilder executeQ2(String processesInfo, PrintWriter writer) {
        StringBuilder schedulingOrderQ2 = new StringBuilder(""); 
    
        // Track the number of processes remaining in q2
        int remainingProcesses = q2.length ;
        int i = 0;  
        while (remainingProcesses > 0 && !this.isEmpty(q2)) {
            if (q2[i] != null) {
                PCB shortestBurst = q2[i];
                boolean check = true;
                for (int j = i + 1; j < q2.length; j++) {
                    if (q2[j] != null &&q2[j].arrivalTime <= StartRun && q2[j].burstTime < shortestBurst.burstTime ) {
                        shortestBurst = q2[j];
                        check = false;
                    }
                }
                // now we decide process with the shortest burst time 
                schedulingOrderQ2.append(shortestBurst.processID);
                StartRun+=shortestBurst.burstTime;
                //
               if (remainingProcesses > 1 || !isEmpty(q1)) {
                    schedulingOrderQ2.append(" | CS | ");
                    StartRun += 2; // Assuming a context switch takes 2 units of time
                    }
    
                //before doing shift for process that will remove it, we will Print process details to console
                processesInfo = generateProcessDetails(shortestBurst, processesInfo);
    
                // Write process details to the file
                writeProcessDetails(writer, shortestBurst);
    
                q2 = removeProcess(q2, shortestBurst);
                remainingProcesses--;
    
                if (!isEmpty(q1)) {
                    //calling execute q1 method 
                    schedulingOrderQ2.append(executeQ1(processesInfo, writer)); // Append the result of executeQ1
                }
    
                if (check)
                    i++;
            }
            else {
                // Increment i if q2[i] is null
                i++;
            }
        }
    
        return schedulingOrderQ2;    
    }
    
        
         private StringBuilder executeQ1( String processesInfo , PrintWriter writer  ){
             
                StringBuilder schedulingOrder = new StringBuilder("");
                
     
                for (int i = 0; i < q1.length; i++) {
                    if (q1[i] != null) {
                        String processID = q1[i].processID;
                        schedulingOrder.append(processID).append(" | ");
                        //before doing shift for process that will remove it , we will Print process details to console
                        processesInfo = generateProcessDetails( q1[i] , processesInfo);
                        // Write process details to the file
                        writeProcessDetails(writer, q1[i]);
                        q1 = removeProcess(q1 , q1[i]);
                    }
                }
                return schedulingOrder;
             
         }
        
        // Helper method to check if an array of PCBs is empty
    private boolean isEmpty(PCB[] queue) {
        for (PCB process : queue) {
            if (process != null) {
                return false; // Queue is not empty
            }
        }
        return true; // Queue is empty
    }
    
    // Helper method to remove a process from an array of PCBs
    private PCB[] removeProcess(PCB[] queue, PCB processToRemove) {
        for (int i = 0; i < queue.length; i++) {
            if(queue[i] != null )
            if (queue[i].processID == processToRemove.processID) {
                queue[i] = null; // Remove process
                break;
            }
        }
        return queue;
    }

    // Method to generate process details as a string
private String generateProcessDetails(PCB process, String additionalDetails) {
    StringBuilder details = new StringBuilder();
    
    details.append(additionalDetails).append("\n\n");
    details.append("Process ID: ").append(process.processID).append("\n");
    details.append("Priority: ").append(process.priority).append("\n");
    details.append("Arrival Time: ").append(process.arrivalTime).append("\n");
    details.append("CPU Burst: ").append(process.burstTime).append("\n");
    details.append("Start Time: ").append(process.startTime).append("\n");
    details.append("Termination Time: ").append(process.terminationTime).append("\n");
    details.append("Turnaround Time: ").append(process.turnaroundTime).append("\n");
    details.append("Waiting Time: ").append(process.waitingTime).append("\n");
    details.append("Response Time: ").append(process.responseTime).append("\n");
    
    return details.toString();
}
    
    // Method to print process details to console
    private void printProcessDetails(PCB process) {
        System.out.println("Process ID: " + process.processID);
        System.out.println("Priority: " + process.priority);
        System.out.println("Arrival Time: " + process.arrivalTime);
        System.out.println("CPU Burst: " + process.burstTime);
        System.out.println("Start Time: " + process.startTime);
        System.out.println("Termination Time: " + process.terminationTime);
        System.out.println("Turnaround Time: " + process.turnaroundTime);
        System.out.println("Waiting Time: " + process.waitingTime);
        System.out.println("Response Time: " + process.responseTime);
        System.out.println();
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

    }*/
    /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */

/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */


import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Scanner;

class PCB {
    String processID;
    int priority;
    int arrivalTime;
    int burstTime;
    int remainingBurst;
    int startTime = -1;
    int terminationTime = -1;
    int turnaroundTime = -1;
    int waitingTime = -1;
    int responseTime = -1;

    public PCB(String processID, int priority, int arrivalTime, int burstTime) {
        this.processID = processID;
        this.priority = priority;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.remainingBurst = burstTime;
    }
}

public class Scheduler {
    PCB[] q1;
    PCB[] q2;
    private static final int timeQuantum = 3;
    private static int currentTime = 0;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Scheduler scheduler = new Scheduler();
        System.out.println("Welcome to the Scheduler!");
        int choice;
        do {
            choice = MainMenu(scanner);
            switch (choice) {
                case 1:
                    scheduler.addProcess(scanner);
                    break;
                case 2:
                    scheduler.runScheduler();
                    break;
                case 3:
                    System.out.println("Exiting the Scheduler.");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
            System.out.println();
        } while (choice != 3);
    }

    public static int MainMenu(Scanner scanner) {
        System.out.println("1. Enter process' information.");
        System.out.println("2. Report detailed information about each process and different scheduling criteria.");
        System.out.println("3. Exit the program");
        System.out.print("Enter your choice: ");
        return scanner.nextInt();
    }

    private void addProcess(Scanner scanner) {
        System.out.print("Enter the number of processes: ");
        int numOfProcesses = scanner.nextInt();
        q1 = new PCB[numOfProcesses];
        q2 = new PCB[numOfProcesses];
        int q1Index = 0, q2Index = 0;
        for (int i = 0; i < numOfProcesses; i++) {
            System.out.println("Enter details for Process " + (i + 1) + ":");
            System.out.print("Priority (1=High, 2=Low): ");
            int priority = scanner.nextInt();
            System.out.print("Arrival Time: ");
            int arrivalTime = scanner.nextInt();
            System.out.print("Burst Time: ");
            int burstTime = scanner.nextInt();
            PCB process = new PCB("P" + (i + 1), priority, arrivalTime, burstTime);
            if (priority == 1) {
                q1[q1Index++] = process;
            } else {
                q2[q2Index++] = process;
            }
        }
        q1 = Arrays.copyOf(q1, q1Index); // Resize to actual number of high priority processes
        q2 = Arrays.copyOf(q2, q2Index); // Resize to actual number of low priority processes
        sortByArrivalTime(q1);
        sortByArrivalTime(q2);
    }

    private void runScheduler() {
        if ((q1 == null || q1.length == 0) && (q2 == null || q2.length == 0)) {
            System.out.println("No processes to schedule.");
            return;
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter("Report.txt"))) {
            StringBuilder schedulingOrder = new StringBuilder();
            schedulingOrder.append(executeRoundRobin(q1, writer));
            schedulingOrder.append(executeSJF(q2, writer));
            writer.println("Scheduling Order: " + schedulingOrder);
            System.out.println("Scheduling Order: " + schedulingOrder);

            // Calculate and display averages
            double avgTATQ1 = calculateAverage(q1, 'T');
            double avgWTQ1 = calculateAverage(q1, 'W');
            double avgRTQ1 = calculateAverage(q1, 'R');
            double avgTATQ2 = calculateAverage(q2, 'T');
            double avgWTQ2 = calculateAverage(q2, 'W');
            double avgRTQ2 = calculateAverage(q2, 'R');

            writer.printf("Average Turnaround Time Q1: %.2f\n", avgTATQ1);
            writer.printf("Average Waiting Time Q1: %.2f\n", avgWTQ1);
            writer.printf("Average Response Time Q1: %.2f\n", avgRTQ1);
            writer.printf("Average Turnaround Time Q2: %.2f\n", avgTATQ2);
            writer.printf("Average Waiting Time Q2: %.2f\n", avgWTQ2);
            writer.printf("Average Response Time Q2: %.2f\n", avgRTQ2);

            System.out.printf("Average Turnaround Time Q1: %.2f\n", avgTATQ1);
            System.out.printf("Average Waiting Time Q1: %.2f\n", avgWTQ1);
            System.out.printf("Average Response Time Q1: %.2f\n", avgRTQ1);
            System.out.printf("Average Turnaround Time Q2: %.2f\n", avgTATQ2);
            System.out.printf("Average Waiting Time Q2: %.2f\n", avgWTQ2);
            System.out.printf("Average Response Time Q2: %.2f\n", avgRTQ2);
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private double calculateAverage(PCB[] queue, char type) {
        double sum = 0;
        int count = 0;
        for (PCB pcb : queue) {
            if (pcb != null && pcb.startTime != -1) { // Ensure process started
                switch (type) {
                    case 'T':
                        sum += pcb.turnaroundTime;
                        break;
                    case 'W':
                        sum += pcb.waitingTime;
                        break;
                    case 'R':
                        sum += pcb.responseTime;
                        break;
                }
                count++;
            }
        }
        return (count > 0) ? sum / count : 0;
    }

    public static void sortByArrivalTime(PCB[] array) {
        Arrays.sort(array, Comparator.comparingInt(p -> p.arrivalTime));
    }

    private String executeRoundRobin(PCB[] queue, PrintWriter writer) {
        StringBuilder schedulingOrder = new StringBuilder();
        boolean processesRemaining;
        do {
            processesRemaining = false;
            for (PCB process : queue) {
                if (process != null && process.remainingBurst > 0 && process.arrivalTime <= currentTime) {
                    if (process.startTime == -1) {
                        process.startTime = Math.max(currentTime, process.arrivalTime);
                        process.responseTime = process.startTime - process.arrivalTime;
                    }
                    int timeSlice = Math.min(process.remainingBurst, timeQuantum);
                    schedulingOrder.append(process.processID).append(" | ");
                    currentTime += timeSlice;
                    process.remainingBurst -= timeSlice;

                    if (process.remainingBurst <= 0) {
                        process.terminationTime = currentTime;
                        process.turnaroundTime = process.terminationTime - process.arrivalTime;
                        process.waitingTime = process.turnaroundTime - process.burstTime;
                        writer.printf("Process ID: %s, Start Time: %d, Termination Time: %d, Turnaround Time: %d, Waiting Time: %d, Response Time: %d%n",
                                      process.processID, process.startTime, process.terminationTime, process.turnaroundTime, process.waitingTime, process.responseTime);
                    } else {
                        processesRemaining = true;
                    }
                }
            }
        } while (processesRemaining);
        return schedulingOrder.toString();
    }
    private String executeSJF(PCB[] queue, PrintWriter writer) {
    StringBuilder schedulingOrder = new StringBuilder();
    int totalProcesses = queue.length;
    int completedProcesses = 0;
    boolean processExecuted;

    // Sort the queue initially by burst time to prepare for SJF
    Arrays.sort(queue, Comparator.comparingInt(p -> p.burstTime));

    while (completedProcesses < totalProcesses) {
        processExecuted = false;

        // Find the shortest job that can start given the current time
        for (int i = 0; i < queue.length; i++) {
            PCB process = queue[i];
            if (process != null && process.remainingBurst > 0 && process.arrivalTime <= currentTime) {
                if (process.startTime == -1) {
                    process.startTime = currentTime;  // Set start time the first time it runs
                    process.responseTime = process.startTime - process.arrivalTime;
                }

                // Execute the process
                schedulingOrder.append(process.processID).append(" | ");
                currentTime += process.burstTime;
                process.terminationTime = currentTime;
                process.turnaroundTime = process.terminationTime - process.arrivalTime;
                process.waitingTime = process.turnaroundTime - process.burstTime;
                process.remainingBurst = 0; // Mark as completed

                writer.printf("Process ID: %s, Start Time: %d, Termination Time: %d, Turnaround Time: %d, Waiting Time: %d, Response Time: %d%n",
                              process.processID, process.startTime, process.terminationTime, process.turnaroundTime, process.waitingTime, process.responseTime);

                completedProcesses++; // Increment the count of completed processes
                processExecuted = true;
                if (q1.length != 0 ) {
                    //calling execute q1 method 
                    schedulingOrder.append(executeRoundRobin(q1, writer)); // Append the result of executeQ1
                }
                break;  // Break after scheduling one process per iteration
            }


        }

        if (!processExecuted) { // If no process was executed, increment current time
            currentTime++;
        }
    }

    if (schedulingOrder.length() > 3) {
        schedulingOrder.setLength(schedulingOrder.length() - 3); // Remove the last " | "
    }

    return schedulingOrder.toString();
}
}


