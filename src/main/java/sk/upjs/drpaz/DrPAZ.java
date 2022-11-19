package sk.upjs.drpaz;

import sk.upjs.drpaz.storage.DaoFactory;
import sk.upjs.drpaz.storage.Product;

public class DrPAZ {
	public static void main(String[] args) {
		
		
//		DaoFactory.INSTANCE.getJdbcTemplate().execute("INSERT INTO `mydb`.`product` (`name`, `price`, `quantity`, `alert_quantity`, `description`) VALUES ('Pharmaton GERIAVIT Vitality 50+', '21.16', '25', '10', 'Výživový doplnok obsahujúci vitamíny, minerály a štandardizovaný výťažok ženšena.')");
		System.out.println(DaoFactory.INSTANCE.getProductDao().getAll());
//		DaoFactory.INSTANCE.getProductDao().save(new Product(null, "Pharmaton GERIAVIT Vitality 50+", 21.16, 25, 10, "Výživový doplnok obsahujúci vitamíny, minerály a štandardizovaný výťažok ženšena."));
//		System.out.println(DaoFactory.INSTANCE.getProductDao().delete(7));

		
//		Launcher.main(args);

	}

}
