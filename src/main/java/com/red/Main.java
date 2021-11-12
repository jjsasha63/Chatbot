package com.red;

import opennlp.tools.doccat.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.lang.Math.sqrt;

public class Main {

    private static Map<String, Integer> categories = new HashMap<>();

    static {
        categories.put("name", 0);
        categories.put("age", 1);
        categories.put("occupation", 2);
        categories.put("hobby", 3);
        categories.put("habit",4);
        categories.put("time", 5);
        categories.put("change_time", 6);
        categories.put("greeting_casual", 7);
        categories.put("greeting_polite", 8);
        categories.put("describe", 9);
        categories.put("complete", 10);
        categories.put("question",11);
        categories.put("continue", 12);
        categories.put("answ_positive", 13);
        categories.put("answ_negative", 14);
        categories.put("ach_positive",21);
        categories.put("ach_negative",22);
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

    private static String response(int index,boolean mode) throws IOException {
        File file;
        String start;
        if(mode) file = new File("src/response.txt");
        else file = new File("src/questions.txt");
        boolean check = false;
        if(mode) start = getKey(categories,index);
        else start = Integer.toString(index);
        String line;
        Scanner scanner = new Scanner(new FileInputStream(file));
        List<String> resp = new ArrayList();
        while(scanner.hasNextLine()){
            line = scanner.nextLine();
            if(line.equals(start)){
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
        return resp.get(random.nextInt(resp.size()));
    }



    private static String transform(String date){
        return date.replaceAll(date.substring(0,Math.min(date.length(),2)),Integer.toString(Integer.parseInt(date.substring(0,Math.min(date.length(),2))) - 1));
    }

    private static String clean(String data) throws FileNotFoundException {
        File file = new File("src/clean.txt");
        Scanner scanner = new Scanner(new FileInputStream(file));
        while (scanner.hasNextLine()){
            data = data.replaceAll(scanner.nextLine(),"");
        }
        scanner.close();
        return data;
    }

    private static void chat() throws IOException {
        List<String> user_info = new ArrayList<>();
        System.out.println(LocalTime.now().toString().substring(0,Math.min(LocalTime.now().toString().length(),8)));
        Model model = new Model();
        int count = 0,k=-1,days=0;
        boolean end = false,free = true,change = false,wait=false;
        String user = new String(), answ, category = "";
        String[] text, tk, pos, lemma;
        Scanner scanner = new Scanner(System.in);
        DoccatModel doccatModel = model.model_train();
        while (true){
            System.out.println("###########################You###########################");
            if(!wait)user = scanner.nextLine();
            text = model.breaks(user);
            answ = "";
            for (String i : text) {
                tk = model.token(i);
                pos = model.pos(tk);
                lemma = model.lemma(pos, tk);
                category = model.category(doccatModel, lemma);
            }
            if(count == 0) free = false;
            else if (count == 20) free = true;
            if("answ_negative".equals(category)&&count==4) {
                count = 10;
                for (int i=0;i<4;i++) user_info.add(null);
            } else if(!free&&"answ_negative".equals(category)) user_info.add(null);

            if(!free&&(count==2||count==6||count==8||count==10||count==16||count==18)) {
                k++;
                user_info.add(k,clean(user));
            }
            if(change) {
                user_info.set(5,user);
                change = false;
            }
            if(count==20||count==21){
                if("answ_positive".equals(category)){
                    category = "ach_positive";
                    days++;
                }
                if("answ_negative".equals(category)){
                    category = "ach_negative";
                    if(count==20) days = 0;
                }
                count+=2;
            }
            if("change_time".equals(category))  change =true;
            if(categories.get(category)>7) {
                answ = response(categories.get(category),true);
            }else if(free){
                if(user_info.get(categories.get(category))==null) answ = "Sorry, i don't know what to answer";
                else answ = response(categories.get(category),true) + " " + user_info.get(categories.get(category));
            }
            if("answ_negative".equals(category)&&count==8) {
                free = true;
                answ = "I hope you'll change your mind...";
            }
            if(category == null) answ = "I don't know what to say";
            if(k==5){
            if(user_info.get(k)==LocalTime.now().toString().substring(0,Math.min(LocalTime.now().toString().length(),8))){
                count = 20;
                answ = response(count,false);
                wait = false;
            }else if(transform(user_info.get(k))==LocalTime.now().toString().substring(0,Math.min(LocalTime.now().toString().length(),8))){
                count=21;
                answ = response(count,false);
                wait = false;
            }}
            if(categories.get(category)>20) answ += days + " " + response(21,false) + " " + response(22,false) + sqrt(days*10+1) + "%" ;

            if(!free){
            if(!"answ_negative".equals(category)&&(count==2||count==6||count==8||count==10||count==16||count==18)) answ = answ + "\n" + user_info.get(k) + " " + response(count,false);
            else if(!"answ_negative".equals(category)) answ = answ + "\n" + response(count,false);
            System.out.println("########################Steve(bot)#######################" + "\n" + answ + "\n" + response(count+1,false));
            count+=2;} else {
                System.out.println("########################Steve(bot)#######################" + answ);
            }
            if ("complete".equals(category))  wait = true;
        }
    }


    public static void main(String[] args) throws IOException {
        chat();
    }
}
