package mebede;

public class Order {
	boolean dark;
	double quantity;
	int ID;
	boolean isPlaced;

	public Order(int idd, double quant, boolean drk) {
		ID = idd;
		quantity = quant;
		dark = drk;
	}

	public Order() {
	}

}
