package sk.upjs.drpaz.controllers;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Purchase;
import sk.upjs.drpaz.biznis.ProductStatistics;
import sk.upjs.drpaz.biznis.ProductStatisticsImpl;
import sk.upjs.drpaz.biznis.ProductStatisticsManager;

public class StatisticsTabController {

	@FXML
	private LineChart<String, Number> dailyIncome;
	@FXML
	private MFXDatePicker fromDatePicker;
	@FXML
	private MFXDatePicker toDatePicker;
	@FXML
	private MFXLegacyTableView<ProductStatistics> allProductsTableView;
	@FXML
	private TableColumn<ProductStatistics, String> nameColumn;
	@FXML
	private TableColumn<ProductStatistics, Integer> quantityColumn;
	@FXML
	private TableColumn<ProductStatistics, Double> totalColumn;

	private ObservableList<ProductStatistics> productStatisticsModel;
	private ProductStatisticsManager productStatisticsManager = new ProductStatisticsImpl();

	@FXML
	void onFromDatePickerClick(ActionEvent event) {

	}

	@FXML
	void onToDatePickerClick(ActionEvent event) {

	}

	@FXML
	void initialize() {
		setAllColumns(null, null);

		CategoryAxis xAxis = new CategoryAxis();
		NumberAxis yAxis = new NumberAxis();

		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		series.setName("Last 30 days");

		List<Purchase> byDate = DaoFactory.INSTANCE.getPurchaseDao()
				.getByDate(LocalDateTime.now().toLocalDate().atStartOfDay().minusMonths(31), LocalDateTime.now());

		Map<LocalDate, Double> map = new HashMap<LocalDate, Double>();
		for (int i = 0; i < 30; i++) {
			map.put(LocalDateTime.now().minusDays(i).toLocalDate(), 0.0);
		}
		for (Purchase purchase : byDate) {
			double sum = DaoFactory.INSTANCE.getPurchaseDao().getTotalPriceById(purchase.getId());
			map.put(purchase.getCreatedAt().toLocalDate(), map.get(purchase.getCreatedAt().toLocalDate()) + sum);
		}

		for (Map.Entry<LocalDate, Double> entry : map.entrySet()) {
			series.getData().add(new XYChart.Data<String, Number>(entry.getKey().toString(), entry.getValue()));

		}

		series.getData().sort(new Comparator<Data<String, Number>>() {
			@Override
			public int compare(Data<String, Number> o1, Data<String, Number> o2) {
				return LocalDate.parse(o1.getXValue()).compareTo(LocalDate.parse(o2.getXValue()));
			}
		});
		dailyIncome.getData().add(series);
	}

	private void setAllColumns(LocalDateTime datetimeStart, LocalDateTime datetimeEnd) {
		totalColumn.setCellValueFactory(new PropertyValueFactory<>("total"));
		quantityColumn.setCellValueFactory(new PropertyValueFactory<>("count"));
		nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));

		List<ProductStatistics> productStatistics = productStatisticsManager.getProductStatistics(datetimeStart,
				datetimeEnd);
		productStatisticsModel = FXCollections.observableArrayList(productStatistics);

		allProductsTableView.setItems(productStatisticsModel);
	}

}
