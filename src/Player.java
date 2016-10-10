import java.util.Random;


public class Player {

    private int _levelBase = 7;
    private String _name;
    private int _boldnessLevel;
    private int _vengefulnessLevel;
    private int _metaVengefulnessLevel;
    private int _score = 0;

    public Player(String name) {

        _name = name;
        _boldnessLevel = _getRandomProbLevel();
        _vengefulnessLevel = _getRandomProbLevel();
        _metaVengefulnessLevel = _getRandomProbLevel();
    }

    public Player(String name, int boldnessLevel, int vengefulnessLevel, int metaVengefulnessLevel, boolean mutation) {

        _name = name;
        _boldnessLevel = boldnessLevel;
        _vengefulnessLevel = vengefulnessLevel;
        _metaVengefulnessLevel = metaVengefulnessLevel;

        if (mutation) {
            _mutate();
        }
    }

    public boolean defect() {

        float defectProb = _getBasedProb(_boldnessLevel);
        double rand = new Random().nextDouble();

        boolean defected = rand < defectProb? true : false;

        if (defected) {
            _score += 3;
        }

        System.out.println(String.format("** Player %s defected? %b score=%d", _name, defected, _score));
        return defected;
    }

    public boolean venge() {

        float vengeProb = _getBasedProb(_vengefulnessLevel);
        double rand = new Random().nextDouble();

        boolean punished = rand < vengeProb? true : false;

        if (punished) {
            _score += -2;
        }

        System.out.println(String.format("** Player %s venged? %b score=%d", _name, punished, _score));
        return punished;
    }

    public boolean metaVenge() {

        float metaVengeProb = _getBasedProb(_metaVengefulnessLevel);
        double rand = new Random().nextDouble();

        boolean punished = rand < metaVengeProb? true : false;

        if (punished) {
            _score += -2;
        }

        System.out.println(String.format("** Player %s meta-venged? %b score=%d", _name, punished, _score));
        return punished;
    }

    public int addScore(int change) {

        _score += change;

//        System.out.println(String.format("** Player %s'score=%d", _name, _score));
        return _score;
    }

    public String getName() {

        return _name;
    }

    public int getScore() {

        return _score;
    }

    public int getBoldnessLevel() {

        return _boldnessLevel;
    }

    public int getVengefulnessLevel() {

        return _vengefulnessLevel;
    }

    public int getMetaVengefulnessLevel() {

        return _metaVengefulnessLevel;
    }

    // Print out player information
    public String printInfo() {

        String info = String.format(
                "Player %s, boldness %d - %f, vengefulness %d - %f, meta-vengefulness %d - %f, score %d",
                _name,
                _boldnessLevel,
                _getBasedProb(_boldnessLevel),
                _vengefulnessLevel,
                _getBasedProb(_vengefulnessLevel),
                _metaVengefulnessLevel,
                _getBasedProb(_metaVengefulnessLevel),
                _score
        );

        System.out.println(info);
        return info;
    }

    // get random int ranges from 0 to 1
    private int _getRandomProbLevel() {

        Random rand = new Random();
        // max:kLevelBase, min:0
        return rand.nextInt(_levelBase + 1) + 0;
    }

    private float _getBasedProb(int level) {

        return (float)level/_levelBase;
    }

    private void _mutate() {

        int pre_boldnessLevel = _boldnessLevel;
        int pre_vengefulnessLevel = _vengefulnessLevel;
        int pre_metaVengefulnessLevel = _metaVengefulnessLevel;

        boolean mutated = false;

        char[] boldnessLevelBinary = Integer.toBinaryString(_boldnessLevel).toCharArray();
        for (int i=0; i<boldnessLevelBinary.length; ++i) {

            // max: 99, min: 0
            int rand = new Random().nextInt(99 + 1) + 0;
            if (rand == 0) {
                mutated = true;
                if (boldnessLevelBinary[i] == '1') {
                    boldnessLevelBinary[i] = '0';
                } else {
                    boldnessLevelBinary[i] = '1';
                }
            }
        }

        _boldnessLevel = Integer.parseInt(String.valueOf(boldnessLevelBinary), 2);

        char[] vengefulnessLevelBinary = Integer.toBinaryString(_vengefulnessLevel).toCharArray();
        for (int i=0; i<vengefulnessLevelBinary.length; ++i) {

            // max: 99, min: 0
            int rand = new Random().nextInt(99 + 1) + 0;
            if (rand == 0) {
                mutated = true;
                if (vengefulnessLevelBinary[i] == '1') {
                    vengefulnessLevelBinary[i] = '0';
                } else {
                    vengefulnessLevelBinary[i] = '1';
                }
            }
        }

        _vengefulnessLevel = Integer.parseInt(String.valueOf(vengefulnessLevelBinary), 2);

        char[] metaVengefulnessLevelBinary = Integer.toBinaryString(_metaVengefulnessLevel).toCharArray();
        for (int i=0; i<metaVengefulnessLevelBinary.length; ++i) {

            // max: 99, min: 0
            int rand = new Random().nextInt(99 + 1) + 0;
            if (rand == 0) {
                mutated = true;
                if (metaVengefulnessLevelBinary[i] == '1') {
                    metaVengefulnessLevelBinary[i] = '0';
                } else {
                    metaVengefulnessLevelBinary[i] = '1';
                }
            }
        }

        _metaVengefulnessLevel = Integer.parseInt(String.valueOf(vengefulnessLevelBinary), 2);


        if (mutated) {
            System.out.println(String.format(
                    "** !!Player %s, mutated! boldness %d -> %d, vengefulness %d -> %d, metavengefulnees %d -> %d, score %d",
                    _name,
                    pre_boldnessLevel,
                    _boldnessLevel,
                    pre_vengefulnessLevel,
                    _vengefulnessLevel,
                    pre_metaVengefulnessLevel,
                    _metaVengefulnessLevel,
                    _score
            ));
        }
    }
}
