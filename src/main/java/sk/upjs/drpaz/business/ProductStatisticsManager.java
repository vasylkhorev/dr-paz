package sk.upjs.drpaz.business;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductStatisticsManager {
	
	List<ProductStatistics> getProductStatistics(LocalDateTime datetimeStart, LocalDateTime datetimeEnd);
}
