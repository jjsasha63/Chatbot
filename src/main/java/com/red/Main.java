package com.red;

import opennlp.tools.doccat.*;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static Map<String, String> questionAnswer = new HashMap<>();

    static {
        questionAnswer.put("greeting_casual", "greeting");
        questionAnswer.put("greeting_polite", "greetings2");
        questionAnswer.put("describe", "descr");
        questionAnswer.put("start", "start");
        questionAnswer.put("complete", "end");
        questionAnswer.put("question","quest");
        questionAnswer.put("continue", "cont");
        questionAnswer.put("answ_positive", "answ_pos");
        questionAnswer.put("answ_negative", "answ_neg");
    }

    private static void chat(){

    }
    public static void main(String[] args) throws IOException {
        Model model = new Model();
        boolean end = false;
        String user, answ, category;
        String[] text, tk, pos, lemma;
        Scanner scanner = new Scanner(System.in);
        DoccatModel doccatModel = model.model_train();
        for(;;){
            System.out.println("You: ");
            user = scanner.nextLine();
            text = model.breaks(user);
            answ = "";
            for (String i : text) {
                tk = model.token(i);
                pos = model.pos(tk);
                lemma = model.lemma(pos, tk);
                category = model.category(doccatModel, lemma);
                answ = answ + " " + questionAnswer.get(category);
                    if ("complete".equals(category)) {
                        end = true;
                    }
            }
        System.out.println("Bot: " + answ);
        if (end) {
            break;
        }
        }
    }
}
