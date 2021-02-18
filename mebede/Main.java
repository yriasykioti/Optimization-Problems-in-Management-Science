package mebede;

import java.util.ArrayList;

public class Main {

	public static void main(String[] args) {
		
		//Ερωτημα Α
		Greedy2 gr = new Greedy2();
		gr.GenerateNetworkRandomly();
		gr.InitiateSolution();
		
		
		//Ερώτημα Γ
		gr.ApplyNearestNeighborMethod(gr.solution);
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < gr.solution.machines.get(i).machineorders.size() ; j++) {
				System.out.print(gr.solution.machines.get(i).machineorders.get(j).ID + ",");
				
			}
			System.out.println();
		}
		
		//Τιμή αντικειμενικής Συνάρτησης
		double maxtime=gr.CalculateObjectiveFunction(gr.solution);
		System.out.println(maxtime);
		
		
		

	}

}
