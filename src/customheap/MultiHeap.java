package customheap;

import java.util.ArrayList;
import java.util.Collection;

public class MultiHeap<T extends Comparable<T>> {

    private final ArrayList<T> storage;
    private final int d;

    //Konstruktor
    public MultiHeap(int d) {
        if (d < 2) {
            throw new IllegalArgumentException("d must be >= 2");
        }
        this.d = d;
        this.storage = new ArrayList<>();
    }

    public void insert(T value) {
        storage.add(value);  // 1. Lägg längst bak i listan
        bubbleUp(storage.size() - 1); // 2. Flytta upp det tills heap-egenskapen håller
    }

    public T removeMin() {
        if (storage.isEmpty()) {
            return null;
        }

        T smallest = storage.get(0);
        T lastItem = storage.remove(storage.size() - 1);

        if (!storage.isEmpty()) {
            storage.set(0, lastItem);
            bubbleDown(0);
        }

        return smallest;
    }

    public boolean isEmpty() {
        return storage.isEmpty();
    }

    public int count() {
        return storage.size();
    }

    public void insertAll(Collection<T> items) {
        for (T item : items) {
            insert(item);
        }
    }

    private void bubbleUp(int index) {
        while (index > 0) {
            int parent = (index - 1) / d;
            if (storage.get(index).compareTo(storage.get(parent)) < 0) {
                swap(index, parent);
                index = parent;
            } else {
                break;
            }
        }
    }

    private void bubbleDown(int index) {
        while (true) {
            int smallest = index;

            for (int i = 1; i <= d; i++) {
                int child = d * index + i;
                if (child < storage.size()
                        && storage.get(child).compareTo(storage.get(smallest)) < 0) {
                    smallest = child;
                }
            }

            if (smallest != index) {
                swap(index, smallest);
                index = smallest;
            } else {
                break;
            }
        }
    }

    private void swap(int i, int j) {
        T temp = storage.get(i);
        storage.set(i, storage.get(j));
        storage.set(j, temp);
    }
}
