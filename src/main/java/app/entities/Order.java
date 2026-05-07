package app.entities;

import java.util.List;

public class Order {
    int id;
    Customer customer;
    List<Material> materialList;
    Specifications specifications;
    Workshop workshop;
    String remarks;
}
