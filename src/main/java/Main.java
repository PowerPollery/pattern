
public class Main {
    private static final String Data = "C:\\Users\\power\\Desktop\\Data\\comments.csv";
    private static final String Training = "C:\\Users\\power\\Desktop\\Data\\training.txt";
    private static final String Testing = "C:\\Users\\power\\Desktop\\Data\\testing.txt";
    private static final String Stop = "C:\\Users\\power\\Desktop\\Data\\stop-gbk.txt";
    private static final String Model = "C:\\Users\\power\\Desktop\\Data\\model.txt";


    public static void main(String[] args) {

        for (int i=16; i<80; i+=16){
            System.out.println("======Init "+i+"=======");
            DataSetIniter dataSetIniter = new DataSetIniter(Data, Training, Testing, Stop);

            dataSetIniter.loadStopWords();
            dataSetIniter.setIncrement(1);
            dataSetIniter.setCharSet("utf-8");
            dataSetIniter.setCommentLengthMin(10);
            dataSetIniter.setInitPercent(i);
            dataSetIniter.setTraningPercent(100);
            dataSetIniter.init();

            ModelTrainer modelTrainer = new ModelTrainer(Training, Model);
            modelTrainer.train();

            Classifier classifier = new Classifier(Model);
            classifier.loadModel();

//            double acc = new ModelTester("C:\\Users\\power\\Desktop\\testing.txt", classifier).test();
            double acc = new ModelTester(Testing, classifier).test();
        }
    }
}
