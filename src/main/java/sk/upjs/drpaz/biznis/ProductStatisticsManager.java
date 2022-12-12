package sk.upjs.drpaz.biznis;

import java.time.LocalDateTime;
import java.util.List;

public interface ProductStatisticsManager {
	
	List<ProductStatistics> getProductStatistics(LocalDateTime datetimeStart, LocalDateTime datetimeEnd);
}
