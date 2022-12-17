package sk.upjs.drpaz.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.github.palexdev.materialfx.controls.MFXDatePicker;
import io.github.palexdev.materialfx.controls.legacy.MFXLegacyTableView;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;
import sk.upjs.drpaz.business.ProductStatistics;
import sk.upjs.drpaz.business.ProductStatisticsImpl;
import sk.upjs.drpaz.business.ProductStatisticsManager;
import sk.upjs.drpaz.storage.dao.DaoFactory;
import sk.upjs.drpaz.storage.entities.Purchase;

public class StatisticsTabController {

	@FXML
    private SplitPane splitPane;
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
		refreshDates();
	}

	@FXML
	void onToDatePickerClick(ActionEvent event) {
		refreshDates();
	}

	private void refreshDates() {
		allProductsTableView.getItems().clear();
		
		if (fromDatePicker.getValue() != null && toDatePicker.getValue() == null) {
			allProductsTableView.getItems()
					.addAll(FXCollections.observableArrayList(productStatisticsManager.getProductStatistics(
								fromDatePicker.getValue().atStartOfDay(),
								null
							)));
			return;
		}
		
		if (fromDatePicker.getValue() == null && toDatePicker.getValue() != null) {
			allProductsTableView.getItems()
					.addAll(FXCollections.observableArrayList(productStatisticsManager.getProductStatistics(
							null,
							toDatePicker.getValue().plusDays(1).atStartOfDay()
						)));
			return;
		}
		
		if (fromDatePicker.getValue() != null && toDatePicker.getValue() != null) {
			allProductsTableView.getItems()
					.addAll(FXCollections.observableArrayList(productStatisticsManager.getProductStatistics(
							fromDatePicker.getValue().atStartOfDay(),
							toDatePicker.getValue().plusDays(1).atStartOfDay()
						)));
			return;
		}
	}

	@FXML
	void initialize() {
		
		splitPane.widthProperty().addListener((ChangeListener<Number>) (observable, oldValue, newValue) -> {
			setWidth();
		});
		
		setAllColumns(null, null);

		XYChart.Series<String, Number> series = new XYChart.Series<String, Number>();
		series.setName("Last 30 days");

		List<Purchase> byDate = DaoFactory.INSTANCE.getPurchaseDao()
				.getByDate(LocalDateTime.now().toLocalDate().atStartOfDay().minusDays(29), LocalDateTime.now());

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
		setWidth();
	}
	
	private void setWidth() {
		totalColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.3));
		quantityColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.3));
		nameColumn.prefWidthProperty().bind(allProductsTableView.widthProperty().multiply(0.401));
	}

}
