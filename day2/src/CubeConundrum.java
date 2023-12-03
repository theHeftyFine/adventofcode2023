import java.io.*;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class CubeConundrum {

    private static final int MAX_RED = 12;
    private static final int MAX_GREEN = 13;
    private static final int MAX_BLUE = 14;
    private static final Pattern HEADER_PATTERN = Pattern.compile("^Game \\d+: ");

    private static class Bag {
        private final int id;
        private final int red;
        private final int blue;
        private final int green;

        public Bag(int id, int red, int blue, int green) {
            this.id = id;
            this.red = red;
            this.blue = blue;
            this.green = green;
        }

        public Bag(int id) {
            this.id = id;
            this.red = 0;
            this.blue = 0;
            this.green = 0;
        }

        public int getRed() {
            return red;
        }

        public int getBlue() {
            return blue;
        }

        public int getGreen() {
            return green;
        }

        public int getId() {
            return id;
        }

        public Bag withRed(int red) {
            return new Bag(this.id, red, this.blue, this.green);
        }

        public Bag withBlue(int blue) {
            return new Bag(this.id, this.red, blue, this.green);
        }

        public Bag withGreen(int green) {
            return new Bag(this.id, this.red, this.blue, green);
        }

        public Bag clone(int red, int blue, int green) {
            return new Bag(this.id, red, blue, green);
        }

        @Override
        public String toString() {
            return "BagDraw{" +
                    "red=" + red +
                    ", blue=" + blue +
                    ", green=" + green +
                    '}';
        }
    }

    public static void main(String... args) {
        try (FileReader fr = new FileReader("input.txt"); BufferedReader br = new BufferedReader(fr)) {
            List<String> lines = br.lines().toList();

            // Part 1
            long correctLines = lines.stream()
                    .map(CubeConundrum::determineMostCubes)
                    .filter(bag -> bag.red <= MAX_RED)
                    .filter(bag -> bag.blue <= MAX_BLUE)
                    .filter(bag -> bag.green <= MAX_GREEN)
                    .map(Bag::getId)
                    .reduce(0, Integer::sum);

            System.out.println("part 1: " + correctLines);

            // Part 2
            long leastPower = lines.stream()
                    .map(CubeConundrum::determineMostCubes)
                    .map(bag -> bag.red * bag.blue * bag.green)
                    .reduce(0, Integer::sum);

            System.out.println("part 2: " + leastPower);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Could not read file");
        }
    }

    private static Stream<Bag> getPossibleGames(List<String> lines) {
        return lines.stream()
                .map(CubeConundrum::determineMostCubes)
                .filter(bag -> bag.red <= MAX_RED)
                .filter(bag -> bag.blue <= MAX_BLUE)
                .filter(bag -> bag.green <= MAX_GREEN);
    }

    private static Bag determineMostCubes(String line) {
        String header = getGameHeader(line);
        int round = getRound(header);
        String game = line.substring(header.length());

        return Stream.of(game.split("; "))
                .map(part -> CubeConundrum.determineDraw(round, part))
                .reduce(CubeConundrum::determineMaxBag)
                .orElseThrow(RuntimeException::new);
    }

    private static int getRound(String header) {
        String[] parts = header.split(" ");
        if (parts.length == 2) {
            return Integer.parseInt(parts[1].substring(0, parts[1].length() - 1));
        }
        return 0;
    }

    private static Bag determineMaxBag(Bag bag, Bag bag1) {
        // Mapping over all draws in a given game, determine the minimum amount of cubes for each color
        return bag.clone(
                Math.max(bag.getRed(), bag1.getRed()),
                Math.max(bag.getBlue(), bag1.getBlue()),
                Math.max(bag.getGreen(), bag1.getGreen()));
    }

    private static Bag determineDraw(int round, String s) {
        // determine the least possible amount of cubes for this draw
        Bag bag = new Bag(round);
        String[] draws = s.split(", ");
        if (draws.length > 0) {
            bag = determineBag(draws[0], bag);
            if (draws.length > 1) {
                bag = determineBag(draws[1], bag);
            }
            if (draws.length > 2) {
                bag = determineBag(draws[2], bag);
            }
        }
        return bag;
    }

    private static Bag determineBag(String drawLine, Bag bag) {
        String[] draw = drawLine.split(" ");
        if (draw.length == 2) {
            // the line is a correct "number color" line (should always be true)
            int value = Integer.parseInt(draw[0]);

            // set color value based on color marker (should only be set once for each color per draw)
            return switch (draw[1]) {
                case "red" -> bag.withRed(value);
                case "blue" -> bag.withBlue(value);
                case "green" -> bag.withGreen(value);
                default -> bag;
            };
        }
        return bag;
    }

    private static String getGameHeader(String line) {
        Matcher headMatcher = HEADER_PATTERN.matcher(line);
        if (headMatcher.find()) {
            return headMatcher.group();
        }
        return "";
    }
}
