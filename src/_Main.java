import java.util.ArrayList;
import java.util.Random;

public class _Main {

    static int kGroupSize = 20;
    static int kGenSize = 100;
    //static double kSeenProb = 0.5;

    public static void main(String[] args) {
        System.out.println("Start ...");

        // Create and initialize the group
        ArrayList<Player> intialGeneration = new ArrayList<>();
        for (int i=0; i< kGroupSize; ++i) {
            intialGeneration.add(new Player("Gen0-" + Integer.toString(i)));
        }

        ArrayList<Player> nextGeneration = intialGeneration;
        for (int genIndex=0; genIndex<kGenSize; ++genIndex) {
            nextGeneration = generation(genIndex, nextGeneration);
//            _printGenInfo(nextGeneration);
        }

        System.out.println("Done ...");
    }

    public static ArrayList<Player> generation(int genIndex, ArrayList<Player> currentGeneration) {

        System.out.println(String.format(
                "******************** Generation %d  ********************",
                genIndex));

        int roundMax =4;

        // Let's play
        for (int round=0; round<roundMax; round++) {
            for (int i = 0; i < kGroupSize; ++i) {

                Player playerI = currentGeneration.get(i);
                System.out.println(String.format(
                        "============== Gen %d Round %d PlayerI %s Starts ================",
                        genIndex,
                        round,
                        playerI.getName()));

                if (!playerI.defect()) {
                    continue;
                }

                // add -1 to other players
                for (int j = 0; j < kGroupSize; ++j) {
                    if (j == i) continue;
                    currentGeneration.get(j).addScore(-1);
                }

                // probability of I being seen, from 0 ~ 1
                double seenProb_i = _getSeenProb();
                double rand = new Random().nextDouble();
                boolean beingSeen_i = rand < seenProb_i ? true : false;

                // no one sees i, continue
                if (!beingSeen_i) {
                    System.out.println(String.format(
                            "PlayerI %s's defection was not seen",
                            playerI.getName()));
                    continue;
                }

                // assign witness j who saw i's defection
                int witnessJIndex = _getRandomWithOneExclusion(0, kGroupSize - 1, i);
                Player PlayerJ = currentGeneration.get(witnessJIndex);

                System.out.println(String.format(
                        "PlayerI %s's defection was seen by PlayerJ %s",
                        playerI.getName(),
                        PlayerJ.getName()));

                boolean punished = PlayerJ.venge();
                if (punished) {
                    playerI.addScore(-9);
                    continue;
                }

                // probability of J's not punishing being seen, from 0 ~ 1
                double seenProb_j = _getSeenProb();
                rand = new Random().nextDouble();
                boolean beingSeen_j = rand < seenProb_j ? true : false;

                if (!beingSeen_j) {
                    System.out.println("PlayerJ's not punishing was not seen.");
                    continue;
                }

                // assign witness k who saw j's not punishing
                int witnessKIndex = _getRandomWithTwoExclusion(0, kGroupSize - 1, i, witnessJIndex);
                Player PlayerK = currentGeneration.get(witnessKIndex);

                System.out.println(String.format(
                        "PlayerJ %s's not punishing was seen by PlayerK %s",
                        PlayerJ.getName(),
                        PlayerK.getName()));

                punished = PlayerK.metaVenge();
                if (punished) {
                    PlayerJ.addScore(-3);
                    continue;
                }

                System.out.println("PlayerK did not punish PlayerJ");
            }
        }

        _printGenInfo(currentGeneration);

        ArrayList<Integer> scoreList = new ArrayList<>();
        for(int index=0; index<currentGeneration.size(); index++) {
            Player player = currentGeneration.get(index);
            scoreList.add(player.getScore());
        }

        double average = _getMean(scoreList);
        double standardDeviation = _getStandardDeviation(scoreList);
        System.out.println(String.format(
                "The average is %f, the standard deviation is %f",
                average,
                standardDeviation));


        // Creating the next generation
        if (genIndex == kGenSize-1) {
            return null;
        }
        System.out.println(String.format(
                "Creating the next generation... with upper bound %f, lower bound %f",
                average + standardDeviation,
                average - standardDeviation));

        ArrayList<Player> newGeneration = new ArrayList<>();
        int count = 0;
        for(int index=0; index<currentGeneration.size(); index++) {
            Player player = currentGeneration.get(index);
            int score = player.getScore();
            if (score > average + standardDeviation) {

                System.out.println(String.format(
                        "Player %s has two children... with score %d, upper bound %f",
                        player.getName(),
                        player.getScore(),
                        average + standardDeviation));

                newGeneration.add(
                        new Player(
                                "Gen" + Integer.toString(genIndex+1)+ "-" + Integer.toString(count++),
                                player.getBoldnessLevel(),
                                player.getVengefulnessLevel(),
                                player.getMetaVengefulnessLevel(),
                                true));
                newGeneration.add(
                        new Player(
                                "Gen" + Integer.toString(genIndex+1)+ "-" + Integer.toString(count++),
                                player.getBoldnessLevel(),
                                player.getVengefulnessLevel(),
                                player.getMetaVengefulnessLevel(),
                                true));
                continue;
            } else if (score < average - standardDeviation) {

                System.out.println(String.format(
                        "Player %s is diaosi... with score %d, lower bound %f",
                        player.getName(),
                        player.getScore(),
                        average - standardDeviation));

                continue;
            } else {
                // add one child

                System.out.println(String.format(
                        "Player %s has one child... with score %d, upper bound %f, lower bound %f",
                        player.getName(),
                        player.getScore(),
                        average + standardDeviation,
                        average - standardDeviation));

                newGeneration.add(
                        new Player(
                                "Gen" + Integer.toString(genIndex+1)+ "-" + Integer.toString(count++),
                                player.getBoldnessLevel(),
                                player.getVengefulnessLevel(),
                                player.getMetaVengefulnessLevel(),
                                true));
            }
        }

        // Adjust player number to groupSize for convenience
        if (newGeneration.size() >= kGroupSize) {
            return (new ArrayList<Player>(newGeneration.subList(0, kGroupSize)));
        }

        int supplementIndex = newGeneration.size();
        while(supplementIndex < kGroupSize) {
            newGeneration.add(new Player("Gen" + Integer.toString(genIndex+1) + "-" + Integer.toString(supplementIndex++)));
        }

        return newGeneration;
    }

    // Select an int range from [min, max] excluding a particular int
    private static int _getRandomWithOneExclusion(int min, int max, int exclusion) {

        int rand = -1;
        do {
            rand = new Random().nextInt(max + 1) + min;
        } while(rand == exclusion);

        return rand;
    }

    // Select an int range from [min, max] excluding a particular int
    private static int _getRandomWithTwoExclusion(int min, int max, int exclusion1, int exclusion2) {

        int rand = -1;
        do {
            rand = new Random().nextInt(max + 1) + min;
        } while(rand == exclusion1 || rand == exclusion2);

        return rand;
    }

    private static double _getMean(ArrayList<Integer> list) {

        double totalScore = 0;
        for (int i = 0; i < list.size(); i++)
        {
            int val = list.get(i);

            // Step 2:
            totalScore += val;
        }

        return totalScore/(double)list.size();
    }

    private static double _getStandardDeviation(ArrayList<Integer> list) {
        // Step 1:
        double mean = _getMean(list);
        double temp = 0;

        for (int i = 0; i < list.size(); i++)
        {
            int val = list.get(i);

            // Step 2:
            double squrDiffToMean = Math.pow(val - mean, 2);

            // Step 3:
            temp += squrDiffToMean;
        }

        // Step 4:
        double meanOfDiffs = (double) temp / (double) (list.size());

        // Step 5:
        return Math.sqrt(meanOfDiffs);
    }

    private static void _printGenInfo(ArrayList<Player> generation) {

        System.out.println("================== Gen Info Begins ===================");
        int sumBoldnessLevel = 0;
        int sumVengefulnessLevel = 0;
        int sumMetaVengefullnessLevel = 0;
        for(int index=0; index<generation.size(); index++) {
            Player player = generation.get(index);
            sumBoldnessLevel += player.getBoldnessLevel();
            sumVengefulnessLevel += player.getVengefulnessLevel();
            sumMetaVengefullnessLevel += player.getMetaVengefulnessLevel();
            player.printInfo();
        }
        double averageBoldnessLevel = (double)sumBoldnessLevel/(double)generation.size();
        double averageVengefulnessLevel = (double)sumVengefulnessLevel/(double)generation.size();
        double averageMetaVengefulnessLevel = (double)sumMetaVengefullnessLevel/(double)generation.size();

        System.out.println(String.format(
                "* The average boldness level is %f, average vengefulness level is %f, average meta-vengefulness level is %f",
                averageBoldnessLevel,
                averageVengefulnessLevel,
                averageMetaVengefulnessLevel));

        System.out.println("================== Gen Info Ends ===================");
    }

    private static double _getSeenProb() {

        double seenProb = new Random().nextDouble();
    //    double seenProb = kSeenProb;

        return seenProb;
    }
}
