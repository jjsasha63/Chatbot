package com.red;

import opennlp.tools.doccat.*;

import java.io.*;
import java.util.*;

public class Main {

    private static Map<String, Integer> categories = new HashMap<>();

    static {
        categories.put("name", 0);
        categories.put("age", 1);
        categories.put("occupation", 2);
        categories.put("hobby", 3);
        categories.put("habit",4);
        categories.put("time", 5);
        categories.put("greeting_casual", 6);
        categories.put("greeting_polite", 7);
        categories.put("describe", 8);
        categories.put("complete", 9);
        categories.put("question",10);
        categories.put("continue", 11);
        categories.put("answ_positive", 12);
        categories.put("answ_negative", 13);
        categories.put("change_time", 14);
    }


    public static <K, V> K getKey(Map<K, V> map, V value)
    {
        for (Map.Entry<K, V> entry: map.entrySet())
        {
            if (value.equals(entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static String response(int index) throws IOException {
        File file = new File("src/response.txt");
        boolean check = false;
        String line;
        Scanner scanner = new Scanner(new FileInputStream(file));
        List<String> resp = new ArrayList();
        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            if(line.equals(getKey(categories,index))){
                check = true;
                line = scanner.nextLine();
            }
            while(check&&!line.equals("end")) {
                resp.add(line);
                line = scanner.nextLine();
            }
            check = false;
        }
        scanner.close();
        Random random = new Random();
        return resp.get(random.nextInt(random.nextInt(resp.size())));
    }

    private static String questions(int index) throws FileNotFoundException {
        File file  = new File("src/questions.txt");
        Scanner scanner = new Scanner(new FileInputStream(file));
        if(index == 0) return scanner.nextLine();
        for(int i=0;i<index;i++) scanner.nextLine();
        return scanner.nextLine();
    }


    private static void chat() throws IOException {
        List<String> user_info = new ArrayList<>();
        Model model = new Model();
        int count = 0;
        boolean end = false,free = true;
        String user, answ, category = "";
        String[] text, tk, pos, lemma;
        Scanner scanner = new Scanner(System.in);
        DoccatModel doccatModel = model.model_train();
        while (true){
            System.out.println("You: ");
            user = scanner.nextLine();
            text = model.breaks(user);
            answ = "";
            for (String i : text) {
                tk = model.token(i);
                pos = model.pos(tk);
                lemma = model.lemma(pos, tk);
                category = model.category(doccatModel, lemma);
            }
            if(!free&&count!=2&&count!=5&&count!=7&&count!=8) user_info.add(user);
            else if(count==2||count==5||count==7||count==8) user_info.add("0");
            if(count == 0) free = false;
            else if (count == 11) free = true;
            if("answ_negative".equals(category)&&count==2) {
                count = 4;
                user_info.add("0");
            }
            if(categories.get(category)>5) {
                answ = response(categories.get(category));
            }else if(!free) answ = response(categories.get(category)) + " " + user_info.get(categories.get(category));
            if("answ_negative".equals(category)&&count==8) {
                free = true;
                answ = "I'll hope you change your mind...";
            }
            if(category == null) answ = "I don't know what to say";
            if(!free&&count!=0&&count!=2&&count!=5&&count!=7&&count!=8) answ = answ + " " + user_info.get(count-1) + " " + questions(count);
            else answ = answ + questions(count);
            System.out.println("Bot: " + answ);
            count++;
            if ("complete".equals(category)) end = true;
            if (end) break;
        }
    }


    public static void main(String[] args) throws IOException {
        chat();
    }
}
