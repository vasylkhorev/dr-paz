package sk.upjs.drpaz;

import sk.upjs.drpaz.storage.DaoFactory;

public class DrPAZ {
	public static void main(String[] args) {


		System.out.println(DaoFactory.INSTANCE.getPurchaseDao().getProductsByPurchaseId(3));
		Launcher.main(args);

	}

}
