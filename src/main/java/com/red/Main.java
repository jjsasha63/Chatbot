package com.red;

import opennlp.tools.doccat.*;
import opennlp.tools.lemmatizer.LemmatizerME;
import opennlp.tools.lemmatizer.LemmatizerModel;
import opennlp.tools.postag.POSModel;
import opennlp.tools.postag.POSTaggerME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.TokenizerME;
import opennlp.tools.tokenize.TokenizerModel;
import opennlp.tools.util.*;
import opennlp.tools.util.model.ModelUtil;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class Main {

    private static DoccatModel model_train() throws IOException{
        DoccatFactory factory = new DoccatFactory(new FeatureGenerator[]{new BagOfWordsFeatureGenerator()});
        InputStreamFactory inputStreamFactory = new MarkableFileInputStreamFactory(new File("src/categorizer.txt"));
        ObjectStream<String> stringObjectStream = new PlainTextByLineStream(inputStreamFactory, StandardCharsets.UTF_8);
        ObjectStream<DocumentSample> sampleObjectStream = new DocumentSampleStream(stringObjectStream);
        TrainingParameters trainingParameters = ModelUtil.createDefaultTrainingParameters();
        trainingParameters.put(TrainingParameters.CUTOFF_PARAM,0);
        DoccatModel doccatModel =DocumentCategorizerME.train("en",sampleObjectStream,trainingParameters,factory);
        return doccatModel;
    }


    private static String[] breaks(String val) throws IOException{
        try (InputStream inputStream = new FileInputStream("en-sent.bin")) {
            SentenceDetectorME sentenceDetectorME = new SentenceDetectorME(new SentenceModel(inputStream));
            String[] t_lines = sentenceDetectorME.sentDetect(val);
            return t_lines;
        }
    }

    private static String[] lemma(String[] pos,String[] tk)throws  IOException{
        try(InputStream inputStream = new FileInputStream("en-lemmatizer.bin")) {
            LemmatizerME lemmatizerME =new LemmatizerME(new LemmatizerModel(inputStream));
            String[] lemma_tk =lemmatizerME.lemmatize(tk,pos);
            return lemma_tk;
        }
    }


    private static String[] pos(String[] tk)throws IOException{
        try(InputStream inputStream = new FileInputStream("en-pos-maxent.bin")){
            POSTaggerME posTaggerME = new POSTaggerME(new POSModel(inputStream));
            String[] pos_tk = posTaggerME.tag(tk);
            return pos_tk;
        }
    }

    private static String[] token(String text)throws IOException{
        try(InputStream inputStream = new FileInputStream("en-token.bin")){
            TokenizerME tokenizerME = new TokenizerME(new TokenizerModel(inputStream));
            String[] tk = tokenizerME.tokenize(text);
            return tk;
        }
    }

    private static String category(DoccatModel doccatModel,String[] tk){
        DocumentCategorizerME documentCategorizerME = new DocumentCategorizerME(doccatModel);
        double[] out = documentCategorizerME.categorize(tk);
        String category = documentCategorizerME.getBestCategory(out);
        return category;
    }

    public static void main(String[] args) {

    }
}
