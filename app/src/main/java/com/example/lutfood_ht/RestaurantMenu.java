package com.example.lutfood_ht;

import java.util.ArrayList;

public class RestaurantMenu {
    ArrayList<MenuItem> menu = new ArrayList<>();

    RestaurantMenu(String id, String itemName, String priceNormal){
        /*MenuItem menuItem = new MenuItem(id, itemName, priceNormal);
        addMenuItem(menuItem);*/
    }

   void addMenuItem(MenuItem menuItem){
        menu.add(menuItem);
    }

    ArrayList<MenuItem> getMenu(){
        return menu;
    }

}
