package com.example.classificador.service;

import com.example.classificador.model.request.ClassificadorRequest;
import com.example.classificador.model.response.ClassificadorResponse;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

@Service
public class ClassificadorService {

    public ClassificadorResponse classificador(ClassificadorRequest classificadorRequest) throws FileNotFoundException {
        ClassificadorService classificadorService = new ClassificadorService();
        classificadorService.loadData("negociacoes.csv");
        double[] resultados = classificadorService.classify(classificadorRequest.getRendaMensal(), classificadorRequest.getValorDivida());

        ClassificadorResponse classificadorResponse = mapResultadosToResponse(resultados);
        return classificadorResponse;
    }

    private static ClassificadorResponse mapResultadosToResponse(double[] resultados) {
        ClassificadorResponse classificadorResponse = new ClassificadorResponse();
        classificadorResponse.setRendaMensal(resultados[0]);
        classificadorResponse.setValorDivida(resultados[1]);
        classificadorResponse.setNumParcelas((int) resultados[2]);
        classificadorResponse.setValorParcela(resultados[3]);
        classificadorResponse.setSaldoDevedor(classificadorResponse.getValorParcela() * classificadorResponse.getNumParcelas());
        return classificadorResponse;
    }

    private List<double[]> data = new ArrayList<>();

    private void loadData(String filename) throws FileNotFoundException {
        Scanner scanner = new Scanner(new File(filename));
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            double[] instance = new double[parts.length];
            for (int i = 0; i < parts.length; i++) {
                instance[i] = Double.parseDouble(parts[i]);
            }
            data.add(instance);
        }
        scanner.close();
    }

    private double[] classify(double rendaMensal, double valorDivida) {
        double[][] array = data.stream()
                .map(instance -> new double[]{instance[0], instance[1]})
                .toArray(double[][]::new);

        double[][] novoNo = {{rendaMensal, valorDivida}};
        int k = 3;
        int[] nnIndices = new int[k];
        double[] nnDistances = new double[k];
        nearestNeighbors(array, novoNo, k, nnIndices, nnDistances);

        double[] npValues = new double[k];
        double[] vpValues = new double[k];
        IntStream.range(0, k).forEach(i -> {
            double[] instance = data.get(nnIndices[i]);
            npValues[i] = instance[2];
            vpValues[i] = instance[3];
        });

        double npMean = mean(npValues);
        double vpMean = mean(vpValues);

        System.out.printf("Plano sugerido: RM = %.2f, VD = %.2f, NP = %.2f, VP = %.2f\n",
                arrayMean(array, nnIndices, 0), arrayMean(array, nnIndices, 1), npMean, vpMean);

        return new double[]{arrayMean(array, nnIndices, 0), arrayMean(array, nnIndices, 1), npMean, vpMean};
    }

    private void nearestNeighbors(double[][] data, double[][] query, int k, int[] nnIndices, double[] nnDistances) {
        Double[] distances = IntStream.range(0, data.length)
                .mapToDouble(i -> distance(data[i], query[0]))
                .boxed()
                .toArray(Double[]::new);

        for (int i = 0; i < k; i++) {
            int minIndex = minIndex(distances);
            nnIndices[i] = minIndex;
            nnDistances[i] = distances[minIndex];
            distances[minIndex] = Double.MAX_VALUE;
        }

    }

    private double distance(double[] a, double[] b) {
        double d1 = a[0] - b[0];
        double d2 = a[1] - b[1];
        return Math.sqrt(d1 * d1 + d2 * d2);
    }

    private int minIndex(Double[] array) {
        int minIndex = IntStream.range(0, array.length)
                .reduce((i, j) -> array[i] < array[j] ? i : j)
                .orElseThrow(() -> new IllegalArgumentException("Array is empty"));

        return minIndex;
    }

    private double arrayMean(double[][] array, int[] indices, int column) {
        double mean = IntStream.of(indices)
                .mapToDouble(i -> array[i][column])
                .average()
                .orElse(Double.NaN);

        return mean;
    }

    private double mean(double[] array) {
        double mean = DoubleStream.of(array)
                .average()
                .orElse(Double.NaN);

        return mean;
    }

}
