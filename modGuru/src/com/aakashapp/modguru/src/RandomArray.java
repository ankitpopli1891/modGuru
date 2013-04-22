package com.aakashapp.modguru.src;

import java.util.ArrayList;
import java.util.Random;

public class RandomArray {

    ArrayList<Integer> list;

    public int[] getArray(int length) {
        list = new ArrayList<Integer>();
        for (int i = 0; i < length; i++) {
            list.add(i);
        }
        shuffleList(list);
        int arr[] = new int[length];
        for (int i = 0; i < length; i++) {
            arr[i] = list.get(i);
        }
        return arr;
    }

    private void shuffleList(ArrayList<Integer> a) {
        int n = a.size();
        Random random = new Random();
        random.nextInt();
        for (int i = 0; i < n; i++) {
            int change = i + random.nextInt(n - i);
            swap(a, i, change);
        }
    }

    private void swap(ArrayList<Integer> a, int i, int change) {
        int helper = a.get(i);
        a.set(i, a.get(change));
        a.set(change, helper);
    }
}