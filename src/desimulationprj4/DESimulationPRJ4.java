/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desimulationprj4;

import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

class MyInputException extends Exception {

    public MyInputException() {
        System.out.println("Invalid Input");
    }

    public MyInputException(String s) {
        super(s);
    }
}

class Link {

    public double linkTime; // data time
    public String type;
    public Link next; // next link in list
    public Link previous; // previous link in list

    public Link(String eventName, double time) {
        this.linkTime = time;
        this.type = eventName;
    }

    public void displayLink() {
        System.out.print(type + ": " + linkTime + " , ");
    }
}

class DoublyLinkedList {

    private Link first;
    private Link last;

    public DoublyLinkedList() {
        first = null;
        last = null;
    }

    public Link showFirst() {
        return first;
    }

    public Link showSecond() {
        return first.next;
    }

    public Link deleteFirst() {
        Link temp = first;
        if (first.next == null) {
            last = null;
        } else {
            first.next.previous = null;
        }
        first = first.next;
        return temp;
    }

    public void insertSorted(String type, double data) {

        Link NewNode = new Link(type, data);
        if (this.first == null) {
            this.first = NewNode;
            this.last = NewNode;
            return;
        }

        Link cur = first;
        while (cur.next != null && cur.linkTime < NewNode.linkTime) {
            cur = cur.next;
        }

        if (cur.next == null && NewNode.linkTime > cur.linkTime) {
            cur.next = NewNode;
            NewNode.previous = cur;
            last = NewNode;
            return;
        }

        if (cur.previous != null) {
            cur.previous.next = NewNode;
            NewNode.previous = cur.previous;
        }

        NewNode.next = cur;
        cur.previous = NewNode;
        if (NewNode.previous == null) {
            first = NewNode;
        }
    }
}

public class DESimulationPRJ4 {

    private static Queue<Customer> queue;
    private static DoublyLinkedList FEL;
    private static Queue<Customer> systemQueue;
    private static double currentTime;
    private static double serviceTime;
    private static double arrivalEvent;
    private static double nextArrivalTime;
    private static double sumWaitingTimeInQueue;
    private static double sumWaitingTimeInSystem;
    private static double peopleWhoLeaveSystem;
    private static double countCust;

    public static double lambda;
    public static double mean;
    public static double capacity;
    static double capacityInf;
    static boolean continueLoop;
    private static Scanner sc;

    public static void main(String[] args) {
        sc = new Scanner(System.in);
        continueLoop = true;
        capacityInf = Double.POSITIVE_INFINITY;
        int customers = 100000000;

        do {
            try {
                System.out.println("Enter the average arrival rate λ (customers/min)");
                lambda = sc.nextDouble();
                System.out.println("Enter the average service rate µ (customers/min)");
                mean = sc.nextDouble();
                System.out.println("Enter the capacity of the system , if you want infinite capacity,please enter -1");
                capacity = sc.nextDouble();
                if (capacity == -1) {
                    capacity = capacityInf;
                }
                if (lambda < 0 || mean < 0 || capacity < -1) {
                    throw new MyInputException("Please Enter Positive Numbers");
                }
                if (lambda > mean) {
                    throw new MyInputException("Lambda should be less than mean");
                }
                continueLoop = false;
            } catch (MyInputException ex) {
                System.out.println(ex);
            } catch (InputMismatchException ex) {
                System.out.println(ex);
                sc.nextLine();
                System.out.println("You can not enter any string");
            }
        } while (continueLoop);

        StartTheSimulation(mean, lambda);// starts the system the system.
        while (countCust < customers) {
            timeAdvance();
            currentTime = FEL.showFirst().linkTime;// for only showing the clock

        }

        System.out.println("The average waiting time in the system: " + (sumWaitingTimeInSystem / customers + " minutes"));
        System.out.println("The average waiting time in the queue: " + (sumWaitingTimeInQueue / customers + " minutes"));
        System.out.println("The percentage of those who leaves the system: %" + ((peopleWhoLeaveSystem / customers * 100)));
        System.out.println("number of person in queue: " + queue.size());

    }

    public static void StartTheSimulation(double mean, double lambda) {
        // Initialise process
        queue = new LinkedList();
        systemQueue = new LinkedList();
        FEL = new DoublyLinkedList();
        currentTime = 0;
        serviceTime = 0;
        arrivalEvent = 1;
        nextArrivalTime = 0;
        sumWaitingTimeInQueue = 0;
        peopleWhoLeaveSystem = 0;
        sumWaitingTimeInSystem = 0;
        countCust = 0;

        //get the service time of the customer and find the next arrival (first arrives at 0)
        Customer firstCust = new Customer(currentTime, randNum(mean)); // first customer
        countCust++;

        serviceTime = firstCust.getServiceTime();
        sumWaitingTimeInSystem = serviceTime;
        FEL.insertSorted("departure", serviceTime);

        //next arrival
        nextArrivalTime = randNum(lambda);
        FEL.insertSorted("arrival", nextArrivalTime);

    }

    public static void timeAdvance() {
        //Calling the proper method according to the first element of the FEL
        Link link = FEL.deleteFirst();
        switch (link.type) {
            case "arrival":
                arrival(link.linkTime);
                break;
            case "departure":
                departure(link.linkTime);
                break;
            default:
                break;
        }
    }

    public static void arrival(double time) {
        // arrivalEvent = 1 means , someone is in process. so go to queue
        if (arrivalEvent == 1) {
            if (queue.size() == capacity - 1) {
                peopleWhoLeaveSystem++;
                countCust++;
            } else {
                Customer c = new Customer(time, randNum(mean));
                queue.add(c);
                countCust++;

            }
            //schedule next arrival
            nextArrivalTime = time + randNum(lambda);
            FEL.insertSorted("arrival", nextArrivalTime);
        } else { // arrivalEvent = 0 means no one in process
            arrivalEvent = 1;
            Customer c = new Customer(time, randNum(mean));
            countCust++;

            systemQueue.add(c);
            serviceTime = c.getServiceTime();
            FEL.insertSorted("departure", time + serviceTime);

            //schedule next arrival
            nextArrivalTime = time + randNum(lambda);
            FEL.insertSorted("arrival", nextArrivalTime);

        }
    }

    public static void departure(double time) {

        if (!queue.isEmpty()) {
            Customer r = queue.remove();
            FEL.insertSorted("departure", time + r.getServiceTime());
            sumWaitingTimeInQueue += time - r.getArrivalTime();
            sumWaitingTimeInSystem += (time - r.getArrivalTime() + r.getServiceTime());
            arrivalEvent = 1;

        } else {
            arrivalEvent = 0;
            if (!systemQueue.isEmpty()) {
                sumWaitingTimeInSystem += systemQueue.remove().getServiceTime();
            }

        }

    }

    public static double randNum(double num) {
        return Math.log(1 - Math.random()) / (-num);//Generate random numbers without using a library, this is faster
    }

}
