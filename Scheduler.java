


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
             if (q1.length > 0 && q2.length > 0 && q2[0].arrivalTime < q1[0].arrivalTime) {
                schedulingOrder.append(executeSJF(q2, writer));
             }else{
             schedulingOrder.append(executeRoundRobin(q1, writer));
             schedulingOrder.append(executeSJF(q2, writer));}
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
            
            for (int i = 0; i < queue.length; i++) {
                PCB process = queue[i];
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
                                      System.out.printf("Process ID: %s, Start Time: %d, Termination Time: %d, Turnaround Time: %d, Waiting Time: %d, Response Time: %d%n",
                                      process.processID, process.startTime, process.terminationTime, process.turnaroundTime, process.waitingTime, process.responseTime);
                                     
                        // Remove process if burst time is 0
                        for (int j = i; j < queue.length - 1; j++) {
                            queue[j] = queue[j + 1];
                        }
                        queue[queue.length - 1] = null;
                        i--; // Decrement i as we shifted the array elements
                    } else {
                        processesRemaining = true;

                        int j;
                        
                        for (j = i; j < queue.length - 1 && queue[j + 1] != null; j++) {
                            if (queue[j + 1].arrivalTime <= currentTime ) {
                                queue[j] = queue[j + 1];
                            } else {
                                break;
                            }
                        }
                        queue[j] = process;
                        i--; // Decrement i as we shifted the array elements
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
 
                               System.out.printf("Process ID: %s, Start Time: %d, Termination Time: %d, Turnaround Time: %d, Waiting Time: %d, Response Time: %d%n",
                               process.processID, process.startTime, process.terminationTime, process.turnaroundTime, process.waitingTime, process.responseTime);
 
                 completedProcesses++; // Increment the count of completed processes
                 processExecuted = true;
                 if (q1.length != 0 ) {
                     //calling execute q1 method 
                     schedulingOrder.append(executeRoundRobin(q1, writer)); 
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