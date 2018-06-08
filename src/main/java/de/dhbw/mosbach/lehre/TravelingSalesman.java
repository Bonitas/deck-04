package de.dhbw.mosbach.lehre;

import io.jenetics.*;
import io.jenetics.engine.*;
import io.jenetics.util.ISeq;
import io.jenetics.util.MSeq;
import io.jenetics.util.RandomRegistry;

import java.io.IOException;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.IntStream;

import static io.jenetics.engine.EvolutionResult.toBestPhenotype;
import static java.lang.Math.hypot;
import static java.util.Objects.requireNonNull;

public final class TravelingSalesman
        implements Problem<ISeq<WayPoint>, EnumGene<WayPoint>, Integer> {

    private final ISeq<WayPoint> _points;

    public TravelingSalesman(final ISeq<WayPoint> points) {
        _points = requireNonNull(points);
    }

    @Override
    public Codec<ISeq<WayPoint>, EnumGene<WayPoint>> codec() {
        return Codecs.ofPermutation(_points);
    }

    @Override
    public Function<ISeq<WayPoint>, Integer> fitness() {
        return p -> IntStream.range(0, p.length())
                .map(i -> {
                    final WayPoint p1 = p.get(i);
                    final WayPoint p2 = p.get((i+1)%p.size());
                    return (int) hypot(p1.getX()-p2.getX(), p1.getY()-p2.getY());
                })
                .sum();
    }

    public static void main(String[] args) throws IOException {
        // Initialisierung - Gesamt 130 (126 + 4 Ecken)
        final TravelingSalesman tsm = new TravelingSalesman(points(126));

        final Engine<EnumGene<WayPoint>, Integer> engine = Engine.builder(tsm)
                // je geringer die fitness, desto besser
                .optimize(Optimize.MINIMUM)
                .alterers(
                        // Mutation
                        new SwapMutator<>(0.20),
                        // Rekombination
                        new PartiallyMatchedCrossover<>(0.15))
                .selector(new TournamentSelector<>(3))
                .populationSize(606)
                .build();

        // Create evolution statistics consumer.
        final EvolutionStatistics<Integer, ?>
                statistics = EvolutionStatistics.ofNumber();

        final Phenotype<EnumGene<WayPoint>, Integer> best = engine.stream()
                .limit(1_000)
                .peek(statistics)
                .collect(toBestPhenotype());

        final ISeq<WayPoint> path = best.getGenotype()
                .getChromosome().toSeq()
                .map(Gene::getAllele);

        path.stream().forEach(System.out::println);

        final double km = tsm.fitness(best.getGenotype()) / 1_000.0;
        System.out.println(statistics);
        System.out.println("Length: " + km);
    }


    private static ISeq<WayPoint> points(int count) throws IOException {
        final MSeq<WayPoint> points = MSeq.ofLength(count);
        Random random = RandomRegistry.getRandom();
        Integer x1 = 0;
        Integer y1 = 0;
        Integer x2 = 500;
        Integer y2 = 500;
        points.set(0, new WayPoint(x1, y1));
        points.set(1, new WayPoint(x1, y2));
        points.set(2, new WayPoint(x2, y1));
        points.set(3, new WayPoint(x2, y2));
        for (int i = 4; i < count; i++){
            Integer rnd = random.nextInt(4);
            switch (rnd){
                case 0:
                    points.set(i, new WayPoint(x1, random.nextInt(500)));
                    break;
                case 1:
                    points.set(i, new WayPoint(x2, random.nextInt(500)));
                    break;
                case 2:
                    points.set(i, new WayPoint(random.nextInt(500), y1));
                    break;
                case 3:
                    points.set(i, new WayPoint(random.nextInt(500), y2));
                    break;
            }
        }
        return points.toISeq();
    }

}
