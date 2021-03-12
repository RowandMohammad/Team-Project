package cs2810;

import java.io.IOException;
import java.util.ArrayList;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.stage.Stage;

public class WaiterViewController {

    private ViewCustomerInterface parent;
    private static boolean isShowing;

    ArrayList<Order> pendingOrders;
    ArrayList<Order> ordersToCook;
    ArrayList<Order> ordersToDeliver;
    ArrayList<Order> ordersToPay;
    

    @FXML
    private ListView<PendingOrderViewItem> PendingOrdersView;

    @FXML
    private ListView<PendingOrderViewItem> OrdersToDeliverView;
    
    @FXML
    private ListView<PendingOrderViewItem> LeftToPayView;

    @FXML
    private Button BackToOrdering;

    @FXML
    private Label UserLabel;

    @FXML
    private Button CancelOrder; 

    @FXML
    void BackToOrderingPressed(ActionEvent event) throws IOException {
        ((Stage) BackToOrdering.getScene().getWindow()).close();
        isShowing = false;
    }
    
    public static void assistancePopup() {
      if (isShowing) {
        Alert alert = new Alert(AlertType.NONE, "Customer on table X is calling for assistance", ButtonType.OK);
        alert.showAndWait();
      }       
    }


    public void populatePending(ArrayList<Order> pendingOrders) {
        int index = 0;
        for (Order order : pendingOrders) {
            PendingOrderViewItem item = new PendingOrderViewItem(this, order.getOrder(), index, true, order.payed);
            PendingOrdersView.getItems().add(item);
            index++;
        }
    }


    public void populateOrdersToDeliver(ArrayList<Order> ordersToDeliver) {
        int index = 0;
        for (Order order : ordersToDeliver) {
            PendingOrderViewItem item = new PendingOrderViewItem(this, order.getOrder(), index, false, order.payed);
            OrdersToDeliverView.getItems().add(item);
            index++;
        }
    }
    
    public void populateLeftToPay(ArrayList<Order> ordersToPay) {
      int index = 0;
      for (Order order : ordersToPay) {
          PendingOrderViewItem item = new PendingOrderViewItem(this, order.getOrder(), index, false, order.payed);
          LeftToPayView.getItems().add(item);
          index++;
      }
  }
    

    @FXML
    void handleCancelOrder(ActionEvent event) {
        int index = PendingOrdersView.getSelectionModel().getSelectedIndex();
        if (index >= 0) {
            cancelConfirmation(index);
        }

    }

    void cancelConfirmation(int index) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancel");
        alert.setContentText("Are you sure you want to cancel this order?");
        ButtonType yesButton = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType cancelButton = new ButtonType("No", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(yesButton, cancelButton);
        alert.showAndWait().ifPresent(type -> {
            if (type == yesButton) {
                PendingOrderViewItem item = PendingOrdersView.getItems().remove(index);
                pendingOrders.remove(index);
                updateIndex(PendingOrdersView, pendingOrders, item.getIndex());
                this.parent.updatePendingOrders(pendingOrders);
                PendingOrdersView.refresh();
                updateOrderStatus("Canceled");
            } else {
            }
        });
    }

    /**
     * Utility function for moving order from pending state to deliverable state
     *
     * @param index of order which have to be moved to deliverable state
     */
    public void confirmOrder(int index) {
        PendingOrderViewItem item = PendingOrdersView.getItems().remove(index);
        ordersToCook.add(pendingOrders.get(index));
        Order order = pendingOrders.remove(index);
        updateIndex(PendingOrdersView, pendingOrders, index);
        this.parent.updatePendingOrders(pendingOrders);
        this.parent.updateOrdersToCook(ordersToCook);
        updateOrderStatus("In progress");
        PendingOrdersView.refresh();
        OrdersToDeliverView.refresh();
    }

    private void updateIndex(ListView<PendingOrderViewItem> View, ArrayList<Order> Orders, int currentIndex) {
        for (int i = 0; i < Orders.size(); i++) {
            if (View.getItems().get(i).index > currentIndex) {
                View.getItems().get(i).index--;
            }
        }
    }

    /**
     * Utility function for moving order from deliverable state to delivered
     *
     * @param index of order which have to be moved to delivered
     */
    public void deliverOrder(int index) {
      
      if(ordersToDeliver.get(index).payed == false) {
        ordersToPay.add(ordersToDeliver.get(index));
        this.parent.updateOrdersToPay(ordersToPay);
        populateLeftToPay(ordersToPay);
      }
      
      
      
        ordersToDeliver.remove(index);
        OrdersToDeliverView.getItems().remove(index);
		updateIndex(OrdersToDeliverView, ordersToDeliver, index);
        OrdersToDeliverView.refresh();
        updateOrderStatus("Delivered");
    }

    private void updateOrderStatus(String status) {
        parent.setOrderStatus(status);
    }

    public void setInitialData(ViewCustomerInterface parent, ArrayList<Order> pendingOrders, ArrayList<Order> ordersToDeliver, ArrayList<Order> ordersToCook, ArrayList<Order> ordersToPay) {
        this.parent = parent;
        this.pendingOrders = pendingOrders;
        this.ordersToDeliver = ordersToDeliver;
        this.ordersToCook = ordersToCook;
        this.ordersToPay = ordersToPay;
        populatePending(pendingOrders);
        populateOrdersToDeliver(ordersToDeliver);
        populateLeftToPay(ordersToPay);
    }
    
    public static void setIsShowing(boolean bool) {
      isShowing = bool;
    }


    public void addOrderToDeliver(PendingOrderViewItem order) {
        OrdersToDeliverView.getItems().add(order);
        OrdersToDeliverView.refresh();
    }
}
