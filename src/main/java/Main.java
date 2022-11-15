import com.hankcs.hanlp.HanLP;

public class Main {
    private static final String Data = "C:\\Users\\power\\Desktop\\Data\\comments.csv";
//    private static final String Data = "C:\\Users\\power\\Desktop\\Data\\ratings.csv";
    private static final String Training = "C:\\Users\\power\\Desktop\\Data\\training.txt";
    private static final String Testing = "C:\\Users\\power\\Desktop\\Data\\testing.txt";
//    private static final String Stop = "C:\\Users\\power\\Desktop\\Data\\stop.txt";
    private static final String Stop = "C:\\Users\\power\\Desktop\\Data\\stop-gbk.txt";
    private static final String Model = "C:\\Users\\power\\Desktop\\Data\\model.txt";


    public static void main(String[] args) {
        DataSetIniter dataSetIniter = new DataSetIniter(Data, Training, Testing, Stop);

        dataSetIniter.loadStopWords();
        dataSetIniter.setIncrement(1);
        dataSetIniter.setInitPercent(50);
        dataSetIniter.setTraningPercent(70);
        dataSetIniter.init();

        ModelTrainer modelTrainer = new ModelTrainer(Training, Model);
        modelTrainer.train();

        Classifier classifier = new Classifier(Model);
        classifier.loadModel();

        double acc = new ModelTester(Testing, classifier).test();
//        System.out.println(acc);

//        String[] instance = dataSetIniter.toArray(dataSetIniter.filter(HanLP.segment("这个电影无聊")));
//        String label = classifier.predict(instance);
//        System.out.println(label);
    }
}
