/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package desimulationprj4;

/**
 *
 * @author gumus
 */
public class Customer {
    
    private double arrivalTime;
    private double serviceTime;

    public Customer(double arrivalTime , double serviceTime){
     this.arrivalTime = arrivalTime;
     this.serviceTime = serviceTime;
    }
    
    public double getArrivalTime(){
        return this.arrivalTime;
    }
    public double getServiceTime(){
        return this.serviceTime;
    }
}

