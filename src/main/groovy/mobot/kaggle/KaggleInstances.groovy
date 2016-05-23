package mobot.kaggle

/**
 * Created by tlin1 on 5/20/16.
 */
class KaggleInstances {
    public enum Workclass {
        Private("Private"),
        SelfEmpNotInc("Self-emp-not-inc"),
        SelfEmpInc("Self-emp-inc"),
        FederalGov("Federal-gov"),
        LocalGov("Local-gov"),
        StateGov("State-gov"),
        WithoutPay("Without-pay"),
        NeverWorked("Never-worked");

        private final String key;
        Workclass(String key) {
            this.key = key;
        }

        public static Workclass fromKey(String key) {
            for (Workclass value: Workclass.values()) {
                if (value.key.toLowerCase().equals(key.toLowerCase())) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Cannot find " + key);
        }
    }

    public enum Education {
        Bachelors("Bachelors"),
        SomeCollege("Some-college"),
        G11th("11th"),
        HsGrad("HS-grad"),
        ProfSchool("Prof-school"),
        AssocAcdm("AssocAcdm"),
        AssocVoc("AssocVoc"),
        G9th("9th"),
        G7_8th("7th-8th"),
        G12th("12th"),
        Masters("Masters"),
        G1_4th("1st-4th"),
        G10th("10th"),
        Doctorate("Doctorate"),
        G5_6th("5th-6th"),
        Preschool("Preschool");

        private final String key;
        Education(String key) {
            this.key = key;
        }

        public static Education fromKey(String key) {
            for (Education value: Education.values()) {
                if (value.key.toLowerCase().equals(key.toLowerCase())) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Cannot find " + key);
        }
    }

    public enum MaritalStatus {
        MarriedCivSpouse("Married-civ-spouse"),
        NeverMarried("Never-married"),
        Divorced("Divorced"),
        Separated("Separated"),
        Widowed("Widowed"),
        MarriedSpouseAbsent("Married-spouse-absent"),
        MarriedAFSpouse("Married-AF-spouse");

        private final String key;
        MaritalStatus(String key) {
            this.key = key;
        }

        public static MaritalStatus fromKey(String key) {
            for (MaritalStatus value: MaritalStatus.values()) {
                if (value.key.toLowerCase().equals(key.toLowerCase())) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Cannot find " + key);
        }
    }

    public enum Occupation {
        TechSupport("Tech-support"),
        CraftRepair("Craft-repair"),
        OtherService("OtherService"),
        Sales("Sales"),
        ExecManagerial("Exec-managerial"),
        ProfSpecialty("Prof-specialty"),
        MachineOpInspct("Machine-op-inspct"),
        AdmClerical("Adm-clerical"),
        FarmingFishing("Farming-fishing"),
        TransportMoving("Transport-moving"),
        PrivHouseServ("Priv-house-serv"),
        ProtectiveServ("Protective-serv"),
        ArmedForces("Armed-Forces"),
        HandlersCleaners("Handlers-cleaners");

        private final String key;
        Occupation(String key) {
            this.key = key;
        }

        public static Occupation fromKey(String key) {
            for (Occupation value: Occupation.values()) {
                if (value.key.toLowerCase().equals(key.toLowerCase())) {
                    return value;
                }
            }
            throw new IllegalArgumentException("Cannot find " + key);
        }
    }

    public int age;
    public Workclass workclass;
    private int fnlwgt;
    public Education education;
    public int educationNum;

//    @ATTRIBUTE relationship     {Wife, Own-child, Husband, Not-in-family, Other-relative, Unmarried}
//    @ATTRIBUTE race             {White, Asian-Pac-Islander, Amer-Indian-Eskimo, Other, Black}
//    @ATTRIBUTE sex              {Female, Male}
//    @ATTRIBUTE capital-gain     NUMERIC
//    @ATTRIBUTE capital-loss     NUMERIC
//    @ATTRIBUTE hours-per-week   NUMERIC
//    @ATTRIBUTE native-country   {United-States, Cambodia, England, Puerto-Rico, Canada, Germany, Outlying-US(Guam-USVI-etc), India, Japan, Greece, South, China, Cuba, Iran, Honduras, Philippines, Italy, Poland, Jamaica, Vietnam, Mexico, Portugal, Ireland, France, Dominican-Republic, Laos, Ecuador, Taiwan, Haiti, Columbia, Hungary, Guatemala, Nicaragua, Scotland, Thailand, Yugoslavia, El-Salvador, Trinadad&Tobago, Peru, Hong, Holand-Netherlands}
//    @ATTRIBUTE INCOME           {>50K, <=50K}
}
