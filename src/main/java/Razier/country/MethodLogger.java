package Razier.country;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.Buffer;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MethodLogger {

    public static String time =turn(Country.time);
    public static String turn(long time){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS");
        return LocalDateTime.now().format(formatter);
    }
    public MethodLogger(){
        System.out.println("\n\n\n\n创建成功\n\n\n\n");
        add("Start",0,"创建成功");
    }
    public static void add(String name,int time){
        add(name,time,"");
    }
    public static void add(String name ,int time, String message){
        String currentTime = turn(System.currentTimeMillis());
        String s =currentTime+"("+time+ "):使用了" + name + "  "+message+"\n";
        try(BufferedWriter bw = new BufferedWriter(new FileWriter("D:\\code\\Country\\logs\\"+ MethodLogger.time +".log",true))) {

            bw.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        save(s);
        System.out.println(s);
    }

    public static void save(String message){


    }
}
