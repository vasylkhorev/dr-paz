package sk.upjs.drpaz.biznis;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.dao.PurchaseDao;
import sk.upjs.drpaz.storage.entities.Product;
import sk.upjs.drpaz.storage.entities.Purchase;

public class ProductStatisticsImpl implements ProductStatisticsManager {

	private PurchaseDao purchaseDao = DaoFactory.INSTANCE.getPurchaseDao();

	@Override
	public List<ProductStatistics> getProductStatistics(LocalDateTime datetimeStart, LocalDateTime datetimeEnd) {
		List<Purchase> purchases;
		if(datetimeStart == null && datetimeEnd == null) {
			purchases = purchaseDao.getAll();
		}else {
			purchases = purchaseDao.getByDate(datetimeStart, datetimeEnd);
		}
		Map<Long, ProductStatistics> result = new HashMap<>();
		if (purchases.isEmpty())
			return new ArrayList<>();
		for (Purchase purchase: purchases) {
			List<Product> products = purchaseDao.getProductsByPurchaseId(purchase.getId());
			for (Product product: products) {
				if(result.containsKey(product.getId())) {
					ProductStatistics productStatistics = result.get(product.getId());
					
					productStatistics.setCount(productStatistics.getCount()+product.getQuantity());
					
					Double productTotal = product.getPrice() * product.getQuantity();
					productStatistics.setTotal(productStatistics.getTotal()+productTotal);
					
					result.put(product.getId(), productStatistics);
				}else {
					Double productTotal = product.getPrice() * product.getQuantity();
					ProductStatistics productStatistics = new ProductStatistics(product, product.getQuantity(), productTotal);
					result.put(product.getId(), productStatistics);
				}
			}
		}
		List<ProductStatistics> resultList = new ArrayList<>(result.values());
		return resultList;
	}

}
