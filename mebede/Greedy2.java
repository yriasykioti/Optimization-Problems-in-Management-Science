package mebede;

import java.util.ArrayList;
import java.util.Random;

public class Greedy2 {
	int totMachines = 5;
	int totOrders = 100;
	int bday = 19092000;
	double[][] transitionTime;
	double[][] finalTimes;
	ArrayList<Order> orders;
	Random ran = new Random(bday);
	Solution solution;

	public Greedy2() {
	}

	void GenerateNetworkRandomly() {
		createOrders();
		createTimes();
		finalTimes();
	}

	public void createOrders() {
		orders = new ArrayList<>();
		for (int i = 0; i < totOrders; i++) {
			int qq = 100 + ran.nextInt(401);
			boolean drk = false;
			if (ran.nextDouble() < 0.15) {
				drk = true;
			}
			Order o = new Order(i + 1, qq, drk);
			orders.add(o);
		}
	}

	public void createTimes() {
		transitionTime = new double[totOrders][totOrders];
		for (int i = 0; i < totOrders; i++) {
			for (int j = 0; j < totOrders; j++) {
				double randTime = 10 + 20 * ran.nextDouble();
				randTime = Math.round(randTime * 100.0) / 100.0;
				if (i == j) {
					randTime = 0;
				}
				transitionTime[i][j] = randTime;
			}
		}

	}

	public void finalTimes() {
		finalTimes = new double[totOrders][totOrders];
		double time;
		for (int i = 0; i < totOrders; i++) {
			for (int j = 0; j < totOrders; j++) {
				time = transitionTime[i][j];
				finalTimes[i][j] = time;
			}
		}

		for (int i = 0; i < totOrders; i++) {
			if (orders.get(i).dark) {
				for (int j = 0; j < totOrders; j++) {
					if (orders.get(j).dark == false) {
						finalTimes[i][j] += 15;
						finalTimes[i][j] = Math.round(finalTimes[i][j] * 100.0) / 100.0;
					}
				}
			}
		}

	}

	private void SetPlacedFlagToFalseForAllOrders() {
		for (int i = 0; i < orders.size(); i++) {
			orders.get(i).isPlaced = false;
		}
	}

	public void InitiateSolution() {
		solution = new Solution();
		for (int i = 0; i < totMachines; i++) {
			Machine machine = new Machine();
			solution.machines.add(machine);
		}
	}

	public void ApplyNearestNeighborMethod(Solution solution) {

		SetPlacedFlagToFalseForAllOrders();

		TwoOrdersInsertion two = new TwoOrdersInsertion();
		two.time = Double.MAX_VALUE;
		findMinFromFinalTimesArray(solution.machines.get(0), two);
		ApplyTwoOrdersInsertion(two);

		for (int insertions = 2; insertions < orders.size();) {
			int sum = 0;
			for (int i = 0; i < totMachines; i++) {
				if (!solution.machines.get(i).machineorders.isEmpty()) {
					sum += 1;
				}
			}

			if (sum == 5) {
				OrderInsertion bestInsertion = new OrderInsertion();
				bestInsertion.time = Double.MAX_VALUE;
				for (int i = 0; i < totMachines; i++) {
					findMinFromCertainOrder(solution.machines.get(i), bestInsertion);
				}
				ApplyOrderInsertion(bestInsertion);
				insertions++;
			} else {
				OrderInsertion bestInsertion = new OrderInsertion();
				bestInsertion.time = Double.MAX_VALUE;
				two = new TwoOrdersInsertion();
				two.time = Double.MAX_VALUE;
				boolean oneemptyarraylist = false;
				for (int i = 0; i < totMachines; i++) {
					if (!solution.machines.get(i).machineorders.isEmpty()) {
						findMinFromCertainOrder(solution.machines.get(i), bestInsertion);

					} else {
						if (oneemptyarraylist == false) {
							findMinFromFinalTimesArray(solution.machines.get(i), two);
							oneemptyarraylist = true;
						}
					}

				}

				if (two.time < bestInsertion.time) {
					ApplyTwoOrdersInsertion(two);
					insertions += 2;
				} else {
					ApplyOrderInsertion(bestInsertion);
					insertions++;
				}

			}

		}
	}

	public void findMinFromFinalTimesArray(Machine machine, TwoOrdersInsertion two) {
		double minValue = Double.MAX_VALUE;
		for (int i = 0; i < totOrders; i++) {
			Order candidate1 = orders.get(i);
			if (candidate1.isPlaced == false) {
				for (int j = 0; j < totOrders; j++) {
					Order candidate2 = orders.get(j);
					if (candidate2.isPlaced == false) {
						if (i != j) {
							minValue = finalTimes[candidate1.ID - 1][candidate2.ID - 1];
							if (minValue < two.time) {
								two.time = minValue;
								two.order1 = candidate1;
								two.order2 = candidate2;
								two.insertionMachine = machine;
							}
						}
					}
				}
			}
		}
	}

	public void findMinFromCertainOrder(Machine machine, OrderInsertion bestInsertion) {
		double value = Double.MAX_VALUE;
		for (int j = 0; j < orders.size(); j++) {
			Order candidate = orders.get(j);
			if (candidate.isPlaced == false) {
				ArrayList<Order> orderSequence = machine.machineorders;
				Order lastOrderInTheMachine = orderSequence.get(orderSequence.size() - 1);

				if (lastOrderInTheMachine.ID - 1 != candidate.ID - 1) {
					value = finalTimes[lastOrderInTheMachine.ID - 1][candidate.ID - 1];
				}

				if (value < bestInsertion.time) {
					bestInsertion.order = candidate;
					bestInsertion.insertionMachine = machine;
					bestInsertion.time = value;
				}
			}
		}
	}

	private void ApplyOrderInsertion(OrderInsertion bestInsertion) {
		Order insertedOrder = bestInsertion.order;
		Machine insertedMachine = bestInsertion.insertionMachine;

		insertedMachine.machineorders.add(insertedOrder);

		insertedOrder.isPlaced = true;

	}

	private void ApplyTwoOrdersInsertion(TwoOrdersInsertion two) {
		Order insertedOrder1 = two.order1;
		Order insertedOrder2 = two.order2;
		Machine insertedMachine = two.insertionMachine;

		insertedMachine.machineorders.add(insertedOrder1);
		insertedMachine.machineorders.add(insertedOrder2);

		insertedOrder1.isPlaced = true;
		insertedOrder2.isPlaced = true;

	}

	public double CalculateObjectiveFunction(Solution solution) {
		double sum = 0;
		double maxtime = Double.MIN_VALUE;
		int k = 0;
		for (int i = 0; i < totMachines; i++) {
			ArrayList<Machine> machineList = solution.machines;
			sum = 0;
			for (int j = 0; j < machineList.get(i).machineorders.size(); j++) {
				sum += machineList.get(i).machineorders.get(j).quantity;
			}

			sum = sum * 6 / 60;
			for (int j = 0; j < machineList.get(i).machineorders.size() - 1; j++) {
				sum += finalTimes[machineList.get(i).machineorders.get(j).ID
						- 1][machineList.get(i).machineorders.get(j + 1).ID - 1];
			}
			machineList.get(i).time = sum;
		}
		for (int i = 0; i < totMachines; i++) {
			if (solution.machines.get(i).time > maxtime) {
				maxtime = solution.machines.get(i).time;
				k = i;
			}
		}
		solution.maxmachine = solution.machines.get(k);
		solution.totaltime = maxtime;
		return maxtime;
	}

	public double CalculateChangedMachineTime(ArrayList<Order> list) {
		double sum = 0;
		sum = 0;
		for (int j = 0; j < list.size(); j++) {
			sum += list.get(j).quantity;
		}

		sum = sum * 6 / 60;
		for (int j = 0; j < list.size() - 1; j++) {
			sum += finalTimes[list.get(j).ID - 1][list.get(j + 1).ID - 1];
		}
		return sum;
	}

	public Solution cloneSolution(Solution sol) {
		Solution cloned = new Solution();
		cloned.totaltime = sol.totaltime;
		cloned.maxmachine = sol.maxmachine;
		for (int i = 0; i < sol.machines.size(); i++) {
			Machine machine = sol.machines.get(i);
			Machine clonedMachine = cloneMachine(machine);
			cloned.machines.add(clonedMachine);
		}

		return cloned;
	}

	private Machine cloneMachine(Machine machine) {
		Machine cloned = new Machine();
		cloned.time = machine.time;
		cloned.machineorders = new ArrayList<Order>();
		for (int i = 0; i < machine.machineorders.size(); i++) {
			Order order = machine.machineorders.get(i);
			cloned.machineorders.add(order);
		}
		return cloned;
	}

	

	public void SearchSwap(Machine searchMachine, CandidateSwapMove swap) {
		for (int i = 0; i < searchMachine.machineorders.size() - 1; i++) {
			Order order1 = searchMachine.machineorders.get(i);
			findBestSwap(searchMachine, swap, i, order1);
		}
	}

	public void findBestSwap(Machine searchMachine, CandidateSwapMove bestSwap, int i, Order order1) {
		double value = Double.MAX_VALUE;

		for (int j = i + 1; j <= searchMachine.machineorders.size() - (i + 1); j++) {
			ArrayList<Order> arrayorders = searchMachine.machineorders;
			arrayorders.set(i, searchMachine.machineorders.get(j));
			arrayorders.set(j, order1);
			value = CalculateChangedMachineTime(arrayorders);

			if (value < bestSwap.time) {
				bestSwap.newListOrders = arrayorders;
				bestSwap.time = value;
			}
		}

	}

}


