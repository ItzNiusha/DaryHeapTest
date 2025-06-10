package customheap;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        // Let VisualVM attach BEFORE benchmark starts
        // System.out.println("⏳ Waiting 30 seconds for VisualVM to attach...");
        // try {
        //     Thread.sleep(30_000); // wait for VisualVM attachment
        // } catch (InterruptedException e) {
        //     e.printStackTrace();
        // }

        int[] heapDegrees = new int[]{2, 3, 4};
        int[] sizes = new int[]{1000, 10000, 100000, 1000000};
        File file = getNextAvailableFile("heap_benchmark", "csv");
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.forLanguageTag("sv-SE"));
        nf.setMinimumFractionDigits(2);
        nf.setMaximumFractionDigits(3);

        try (FileWriter writer = new FileWriter(file)) {
            writer.write("d,size,avg_insert_time_ms,avg_extract_time_ms\n");

            for (int d : heapDegrees) {
                for (int size : sizes) {
                    double totalInsertTime = 0.0;
                    double totalExtractTime = 0.0;
                    List<Integer> data = generateRandomData(size);
                    MultiHeap<Integer> heap = new MultiHeap<>(d);

                    long insertStart = System.nanoTime();
                    for (int value : data) {
                        heap.insert(value);
                    }
                    long insertEnd = System.nanoTime();

                    totalInsertTime = (insertEnd - insertStart) / 1_000_000.0;

                    long extractStart = System.nanoTime();
                    while (!heap.isEmpty()) {
                        heap.removeMin();
                    }
                    long extractEnd = System.nanoTime();

                    totalExtractTime = (extractEnd - extractStart) / 1_000_000.0;

                    System.gc(); // optional, triggers garbage collection

                    try {
                        Thread.sleep(50L);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    System.out.printf("d=%d | size=%d | insert=%.2f ms | extract=%.2f ms%n",
                            d, size, totalInsertTime, totalExtractTime);
                    writer.write(String.format("%d;%d;%s;%s\n",
                            d, size, nf.format(totalInsertTime), nf.format(totalExtractTime)));
                }
            }

            System.out.printf("Benchmark results written to %s%n", file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Let VisualVM review post-execution state
        try {
            System.out.println("\n✅ Benchmark complete. Profiling window open for 30 seconds...");
            Thread.sleep(30_000); // wait 30 seconds
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // ⏸ Wait for manual user input before exit
        System.out.println("🔚 Press Enter to exit...");
        Scanner input = new Scanner(System.in);
        input.nextLine();
    }

    private static List<Integer> generateRandomData(int size) {
        Random rand = new Random();
        List<Integer> list = new ArrayList<>();

        for (int i = 0; i < size; ++i) {
            list.add(rand.nextInt(1_000_000));
        }

        return list;
    }

    private static File getNextAvailableFile(String baseName, String extension) {
        int index = 1;
        File file;
        do {
            file = new File(baseName + "_" + index + "." + extension);
            ++index;
        } while (file.exists());
        return file;
    }
}
