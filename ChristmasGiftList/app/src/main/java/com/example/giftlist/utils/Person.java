package com.example.giftlist.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mshehab on 5/6/18.
 */

public class Person implements Serializable {
    public String name;
    public int totalBudget;
    public int totalBought;
    public int giftCount;
    public String id;
    public List<Gift> gifts = new ArrayList<>();

    public Person() {
    }

    public Person(String name, int totalBudget, int totalBought, int giftCount, String id) {
        this.name = name;
        this.totalBudget = totalBudget;
        this.totalBought = totalBought;
        this.giftCount = giftCount;
        this.id = id;
    }
}
